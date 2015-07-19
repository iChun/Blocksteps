package me.ichun.mods.blocksteps.common.blockaid;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.blockaid.handler.BlockPeripheralHandler;
import me.ichun.mods.blocksteps.common.blockaid.handler.BlockSteppedHandler;
import me.ichun.mods.blocksteps.common.blockaid.handler.periphs.GenericDenyHandler;
import me.ichun.mods.blocksteps.common.blockaid.handler.periphs.GenericHandler;
import me.ichun.mods.blocksteps.common.blockaid.handler.periphs.HorizontalGenericHandler;
import me.ichun.mods.blocksteps.common.blockaid.handler.periphs.VerticalGenericHandler;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockStepHandler
{
    public static void handleStep(Entity entity, List<BlockPos> steps)
    {
        if(entity == Minecraft.getMinecraft().thePlayer && !entity.onGround || !(entity.riddenByEntity instanceof EntityPlayer))
        {
            return;
        }

        BlockPos pos;
        if(entity.getEntityBoundingBox().minY % 1 == 0)
        {
            pos = new BlockPos(entity.posX, entity.posY - 1, entity.posZ);
        }
        else
        {
            pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
        }
        pos = adjustIfBlockIsPeripheral(entity.worldObj, pos);
        boolean add = true;
        if(!steps.isEmpty())
        {
            BlockPos lastPos = steps.get(steps.size() - 1);
            if(lastPos.equals(pos))
            {
                add = false;
            }
        }
        if(add)
        {
            IBlockState state = entity.worldObj.getBlockState(pos);
            if(state.getBlock().isAir(entity.worldObj, pos) || !(state.getBlock().isNormalCube(entity.worldObj, pos) || isAcceptableBlockType(state.getBlock())))
            {
                add = false;
            }
        }
        if(add)
        {
            if(Blocksteps.eventHandler.renderGlobalProxy != null && !steps.contains(pos))
            {
                Blocksteps.eventHandler.renderGlobalProxy.markBlockForUpdate(pos);
                Blocksteps.eventHandler.repopulateBlocksToRender = true;
            }
            steps.add(pos);
            BlockStepHandler.getBlocksToRender(true, pos);
        }
    }

    public static BlockPos adjustIfBlockIsPeripheral(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        if(isBlockTypePeripheral(world, pos, state.getBlock(), state, DUMMY_AVAILABLES))
        {
            return pos.add(0, -1, 0);
        }
        return pos;
    }

    public static void getBlocksToRender(boolean markUpdate, BlockPos...poses)
    {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.theWorld;
        for(BlockPos pos : poses)
        {
            List<BlockPos> renderPos = Blocksteps.eventHandler.blocksToRenderByStep.get(pos);
            renderPos.clear();

            if(Blocksteps.config.stepRadius > 1)
            {
                int radius = Blocksteps.config.stepRadius - 1;
                for(int i = -radius; i <= radius; i++)
                {
                    for(int j = -radius; j <= radius; j++)
                    {
                        for(int k = -radius; k <= radius; k++)
                        {
                            if(!(i == 0 && j == 0 && k == 0))
                            {
                                BlockPos newPos = pos.add(i, j, k);
                                IBlockState state = world.getBlockState(newPos);
                                if(!(state.getBlock().isAir(world, newPos) || !(state.getBlock().isNormalCube(world, newPos) || isAcceptableBlockType(state.getBlock()))))
                                {
                                    renderPos.add(newPos);
                                }
                            }
                        }
                    }
                }
            }

            if(!renderPos.contains(pos))
            {
                renderPos.add(pos);
            }

            addPeripherals(world, renderPos);

            if(markUpdate)
            {
                for(BlockPos bpos : renderPos)
                {
                    Blocksteps.eventHandler.renderGlobalProxy.markBlockForUpdate(bpos);
                }
            }
        }
    }

    public static void addPeripherals(World world, List<BlockPos> renderPos)
    {
        if(Blocksteps.config.stepPeripherals == 1)
        {
            ArrayList<BlockPos> periphs = new ArrayList<BlockPos>();
            for(BlockPos pos : renderPos)
            {
                BlockPos periph = pos.add(0, 1, 0);
                IBlockState state = world.getBlockState(periph);
                if(isBlockTypePeripheral(world, periph, state.getBlock(), state, renderPos))
                {
                    List<BlockPos> poses = getBlockPeripheralHandler(state.getBlock()).getRelativeBlocks(world, periph, state, renderPos);
                    for(BlockPos pos1 : poses)
                    {
                        if(!periphs.contains(pos1) && !renderPos.contains(pos1))
                        {
                            periphs.add(pos1);
                        }
                    }
                }
            }
            renderPos.addAll(periphs);
        }
    }

    public static boolean isAcceptableBlockType(Block block)
    {
        return (block.getRenderType() == 2 || block.getMaterial() == Material.glass || block == Blocks.glowstone || block == Blocks.waterlily || block == Blocks.farmland || block == Blocks.tnt || block == Blocks.ice || block instanceof BlockSlab || block instanceof BlockStairs) && !(block instanceof IPlantable);
    }

    public static boolean isBlockTypePeripheral(World world, BlockPos pos, Block block, IBlockState state, List<BlockPos> availableBlocks)
    {
        BlockPeripheralHandler handler = getBlockPeripheralHandler(block);
        return handler != null && handler.isValidPeripheral(world, pos, state, availableBlocks);
    }

    public static BlockPeripheralHandler getBlockPeripheralHandler(Block block)
    {
        Class clz = block.getClass();
        while(clz != Block.class)
        {
            if(blockPeripheralRegistry.containsKey(clz))
            {
                return blockPeripheralRegistry.get(clz);
            }
            clz = clz.getSuperclass();
        }
        return null;
    }

    public static HashMap<Class<? extends Block>, BlockPeripheralHandler> blockPeripheralRegistry = new HashMap<Class<? extends Block>, BlockPeripheralHandler>();
    public static HashMap<Class<? extends Block>, BlockSteppedHandler> blockSteppedRegistry = new HashMap<Class<? extends Block>, BlockSteppedHandler>();

    public static final ArrayList<BlockPos> DUMMY_AVAILABLES = new ArrayList<BlockPos>();
    public static final GenericHandler DEFAULT_GENERIC_HANDLER = new GenericHandler();
    public static final GenericDenyHandler DEFAULT_GENERIC_DENY_HANDLER = new GenericDenyHandler();

    static
    {
        blockPeripheralRegistry.put(BlockAnvil.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockBasePressurePlate.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockBanner.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockBeacon.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockBrewingStand.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockCake.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockCarpet.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockCauldron.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockDragonEgg.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockDaylightDetector.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockEnderChest.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockEnchantmentTable.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockFence.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockFenceGate.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockFire.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockFlowerPot.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockHopper.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockJukebox.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockMelon.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockNote.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockPumpkin.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockRailBase.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockRedstoneDiode.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockRedstoneWire.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockSapling.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockSign.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockSkull.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockSnow.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockTripWire.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockWall.class, DEFAULT_GENERIC_HANDLER);
        blockPeripheralRegistry.put(BlockWorkbench.class, DEFAULT_GENERIC_HANDLER);

        blockPeripheralRegistry.put(BlockLilyPad.class, DEFAULT_GENERIC_DENY_HANDLER);

        blockPeripheralRegistry.put(BlockBush.class, new VerticalGenericHandler(BlockBush.class, 2));
        blockPeripheralRegistry.put(BlockCactus.class, new VerticalGenericHandler(BlockCactus.class, 5));
        blockPeripheralRegistry.put(BlockCrops.class, new VerticalGenericHandler(BlockCrops.class, 2));
        blockPeripheralRegistry.put(BlockDoor.class, new VerticalGenericHandler(BlockDoor.class, 2));
        blockPeripheralRegistry.put(BlockPane.class, new VerticalGenericHandler(BlockPane.class, 5));
        blockPeripheralRegistry.put(BlockReed.class, new VerticalGenericHandler(BlockReed.class, 5));

        blockPeripheralRegistry.put(BlockBed.class, new HorizontalGenericHandler(BlockBed.class));
        blockPeripheralRegistry.put(BlockChest.class, new HorizontalGenericHandler(BlockChest.class));
    }

//    addPeripheralBlock(BlockTorch.class, BlockLever.class
//                       BlockLadder.class
//                       BlockPane.class,
//    );
//    addPeripheralBlock(BlockDynamicLiquid.class);//TODO think about this.
//    addPeripheralBlock(BlockButton.class); //TODO think about this
//    addPeripheralBlock(BlockLilyPad.class);//TODO think about these:
    //huge mushrooms
    //trees
    //Portals

}
