package me.ichun.mods.blocksteps.common.thread;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.blockaid.BlockStepHandler;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.*;

public class ThreadBlockCrawler extends Thread
{
    public final Map<ChunkCoordIntPair, HashSet<BlockPos>> crawler = new HashMap<ChunkCoordIntPair, HashSet<BlockPos>>();
    public final Map<ChunkCoordIntPair, HashSet<BlockPos>> surface = Collections.synchronizedMap(new HashMap<ChunkCoordIntPair, HashSet<BlockPos>>());
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
                    Minecraft mc = Minecraft.getMinecraft();
                    if(mc.thePlayer != null && mc.theWorld.provider.getDimensionId() != -1)
                    {
                        BlockPos ref = new BlockPos(mc.thePlayer.posX, 0, mc.thePlayer.posZ);
                        int rangeHori = Math.max((Blocksteps.config.renderDistance == 0 ? (mc.gameSettings.renderDistanceChunks) : (Blocksteps.config.renderDistance)), 1) * 16;

                        HashMap<ChunkCoordIntPair, Boolean> hasInfo = new HashMap<ChunkCoordIntPair, Boolean>();

                        for(int i = -rangeHori; i <= rangeHori; i++)
                        {
                            for(int k = -rangeHori; k <= rangeHori; k++)
                            {
                                ChunkCoordIntPair chunk = new ChunkCoordIntPair((ref.getX() + i) >> 4, (ref.getZ() + k) >> 4);
                                if(!hasInfo.containsKey(chunk))
                                {
                                    hasInfo.put(chunk, surface.containsKey(chunk));
                                }
                                if((rangeHori - Math.abs(i) > 18 && rangeHori - Math.abs(k) > 18) && hasInfo.get(chunk))
                                {
                                    continue;
                                }

                                int j = mc.theWorld.getActualHeight() + 1;
                                int finds = 0;
                                while(j > 0 && finds < Blocksteps.config.surfaceDepth && mc.theWorld != null)
                                {
                                    j--;
                                    BlockPos pos = ref.add(i, j, k);
                                    IBlockState state = mc.theWorld.getBlockState(pos);
                                    if(BlockStepHandler.isBlockTypePeripheral(mc.theWorld, pos, state.getBlock(), state, BlockStepHandler.DUMMY_AVAILABLES))
                                    {
                                        addPos(pos);
                                        continue;
                                    }
                                    if(state.getBlock().isAir(mc.theWorld, pos) || !(state.getBlock().isNormalCube(mc.theWorld, pos) || BlockStepHandler.isAcceptableBlockType(state.getBlock()) || BlockLiquid.class.isInstance(state.getBlock())))
                                    {
                                        continue;
                                    }
                                    addPos(pos);
                                    if(finds == 0)
                                    {
                                        ArrayList<BlockPos> periphs = new ArrayList<BlockPos>();
                                        BlockStepHandler.addPeripherals(mc.theWorld, pos, periphs, false);
                                        for(BlockPos pos1 : periphs)
                                        {
                                            addPos(pos1);
                                        }
                                    }
                                    finds++;
                                    Blocksteps.eventHandler.renderGlobalProxy.markBlockForUpdate(pos);
                                }
                            }
                        }

                        synchronized(surface)
                        {
                            Iterator<Map.Entry<ChunkCoordIntPair, HashSet<BlockPos>>> ite = surface.entrySet().iterator();
                            while(ite.hasNext())
                            {
                                Map.Entry<ChunkCoordIntPair, HashSet<BlockPos>> e = ite.next();
                                double dx = e.getKey().chunkXPos << 4 - ref.getX();
                                double dz = e.getKey().chunkZPos << 4 - ref.getZ();
                                double dist = Math.sqrt(dx * dx + dz * dz);
                                if(dist > ((Blocksteps.config.renderDistance == 0 ? (mc.gameSettings.renderDistanceChunks) : (Blocksteps.config.renderDistance)) + 4) * 16) //if the cache is >4 chunks away from the range, clear it off.
                                {
                                    ite.remove();
                                }
                            }

                            surface.putAll(crawler);
                            crawler.clear();
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

    private void addPos(BlockPos pos)
    {
        ChunkCoordIntPair coord = new ChunkCoordIntPair(pos.getX() >> 4, pos.getZ() >> 4);
        HashSet<BlockPos> poses = crawler.get(coord);
        if(poses == null)
        {
            poses = new HashSet<BlockPos>();
            crawler.put(coord, poses);
        }
        poses.add(pos);
    }
}
