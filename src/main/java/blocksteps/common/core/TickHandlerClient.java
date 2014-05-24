package blocksteps.common.core;

import blocksteps.client.render.RenderGlobalBlockstep;
import blocksteps.common.Blocksteps;
import blocksteps.common.block.BlockInfo;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.ArrayList;

public class TickHandlerClient
{
    @SubscribeEvent
    public void worldTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().theWorld != null)
        {
            Minecraft mc = Minecraft.getMinecraft();
            WorldClient world = mc.theWorld;

            BlockInfo info = createBlockInfoFromPlayer(mc.thePlayer);

            if(info != null)
            {
                boolean add = true;
                if(!blocks.isEmpty())
                {
                    BlockInfo info1 = blocks.get(blocks.size() - 1); //newest
                    if(info.equals(info1))
                    {
                        add = false;
                    }
                }
                if(add)
                {
                    blocks.add(info);
                }
            }

            while(blocks.size() > Blocksteps.config.getInt("maxTracked"))
            {
                blocks.remove(0);//Remove oldest;
            }
            if(lastIndex >= blocks.size())
            {
                lastIndex = 0;
            }
            //TODO checks
        }
    }

    public BlockInfo createBlockInfoFromPlayer(EntityPlayer player)
    {
        int i = (int)Math.floor(player.posX);
        int j = (int)Math.floor(player.boundingBox.minY) - 1;
        int k = (int)Math.floor(player.posZ);

        Block blk = player.worldObj.getBlock(i, j, k);

        if(blk.isAir(player.worldObj, i, j, k))
        {
            return null;
        }

        BlockInfo periph = null;

        if(Blocksteps.isPeripheralBlock(blk.getClass()))
        {
            periph = new BlockInfo(blk, i, j, k, true);
            //TODO get periphs above.
            //TODO get meta and tile entities too

            blk = player.worldObj.getBlock(i, --j, k);
            if(Blocksteps.isPeripheralBlock(blk.getClass()))
            {
                return null;
            }
        }

        BlockInfo info = new BlockInfo(blk, i, j, k, false);

        info.metadata = player.worldObj.getBlockMetadata(i, j, k);
        info.tileEntity = player.worldObj.getTileEntity(i, j, k);

        //TODO get light level //getMixedBrightnessForBlock

        //TODO if black... get render level from previous block or something.

        return info;
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START)
        {
            Minecraft mc = Minecraft.getMinecraft();
            WorldClient world = mc.theWorld;

            if(worldInstance != world)
            {
                worldInstance = world;
                blocks.clear();
                //TODO clear blocks

                if (renderGlobalProxy == null)
                {
                    renderGlobalProxy = new RenderGlobalBlockstep(mc);
                }

                renderGlobalProxy.setWorldAndLoadRenderers(world);

                if(iconRegister != null)
                {
                    renderGlobalProxy.registerDestroyBlockIcons(iconRegister);
                    iconRegister = null;
                }
            }

        }
    }

    @SubscribeEvent
    public void onTextureStitched(TextureStitchEvent.Pre event)
    {
        if(event.map.getTextureType() == 0)
        {
            iconRegister = event.map;
        }
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event)
    {

    }


    public ArrayList<BlockInfo> blocks = new ArrayList<BlockInfo>();
    public int lastIndex = 0;

    public WorldClient worldInstance;
    public RenderGlobalBlockstep renderGlobalProxy;
    public IIconRegister iconRegister;

}
