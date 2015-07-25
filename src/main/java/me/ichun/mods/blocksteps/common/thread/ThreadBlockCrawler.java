package me.ichun.mods.blocksteps.common.thread;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.blockaid.BlockStepHandler;
import me.ichun.mods.blocksteps.common.blockaid.CheckBlockInfo;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

import java.util.*;

public class ThreadBlockCrawler extends Thread
{
    public final Set<BlockPos> surface = Collections.synchronizedSet(new HashSet<BlockPos>());
    public boolean needChecks = false;

    public ThreadBlockCrawler()
    {
        this.setName("Blocksteps Block Crawler Thread");
        this.setDaemon(true);
    }

    @Override
    public void run()
    {
        try
        {
            boolean execute = true;
            while(execute)
            {
                if(needChecks)
                {
                    synchronized(surface)
                    {
                        Minecraft mc = Minecraft.getMinecraft();
                        if(mc.thePlayer != null)
                        {
                            surface.clear();
                            BlockPos ref = new BlockPos(mc.thePlayer.posX, 0, mc.thePlayer.posZ);
                            int rangeHori = Math.max((Blocksteps.config.renderDistance == 0 ? (mc.gameSettings.renderDistanceChunks - 1) : (Blocksteps.config.renderDistance - 1)), 1) * 16;
                            int minX = ref.getX();
                            int minY = mc.theWorld.getActualHeight();
                            int minZ = ref.getZ();
                            int maxX = ref.getX();
                            int maxY = 0;
                            int maxZ = ref.getZ();

                            for(int i = -rangeHori; i <= rangeHori; i++)
                            {
                                for(int k = -rangeHori; k <= rangeHori; k++)
                                {
                                    int j = mc.theWorld.getActualHeight() + 1;
                                    int finds = 0;
                                    while(j > 0 && finds < Blocksteps.config.surfaceDepth && mc.theWorld != null)
                                    {
                                        j--;
                                        BlockPos pos = ref.add(i, j, k);
                                        IBlockState state = mc.theWorld.getBlockState(pos);
                                        if(BlockStepHandler.isBlockTypePeripheral(mc.theWorld, pos, state.getBlock(), state, BlockStepHandler.DUMMY_AVAILABLES))
                                        {
                                            surface.add(pos);
                                            continue;
                                        }
                                        if(state.getBlock().isAir(mc.theWorld, pos) || !(state.getBlock().isNormalCube(mc.theWorld, pos) || BlockStepHandler.isAcceptableBlockType(state.getBlock()) || BlockLiquid.class.isInstance(state.getBlock())))
                                        {
                                            continue;
                                        }
                                        surface.add(pos);
                                        if(pos.getX() < minX)
                                        {
                                            minX = pos.getX();
                                        }
                                        if(pos.getY() < minY)
                                        {
                                            minY = pos.getY();
                                        }
                                        if(pos.getZ() < minZ)
                                        {
                                            minZ = pos.getZ();
                                        }
                                        if(pos.getX() > maxX)
                                        {
                                            maxX = pos.getX();
                                        }
                                        if(pos.getY() > maxY)
                                        {
                                            maxY = pos.getY();
                                        }
                                        if(pos.getZ() > maxZ)
                                        {
                                            maxZ = pos.getZ();
                                        }
                                        if(finds == 0)
                                        {
                                            ArrayList<BlockPos> periphs = new ArrayList<BlockPos>();
                                            BlockStepHandler.addPeripherals(mc.theWorld, pos, periphs, false);
                                            surface.addAll(periphs);
                                        }
                                        finds++;
                                    }
                                }
                            }

                            Blocksteps.eventHandler.renderGlobalProxy.markBlocksForUpdate(minX, minY, minZ, maxX, maxY, maxZ);
                        }
                    }
                    needChecks = false;
                }
                Thread.sleep(50L);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
