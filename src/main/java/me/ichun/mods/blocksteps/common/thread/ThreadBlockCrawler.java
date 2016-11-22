package me.ichun.mods.blocksteps.common.thread;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.blockaid.BlockStepHandler;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

public class ThreadBlockCrawler extends Thread
{
    public final Map<ChunkPos, HashSet<BlockPos>> crawler = new HashMap<>();
    public final Map<ChunkPos, HashSet<BlockPos>> surface = Collections.synchronizedMap(new HashMap<ChunkPos, HashSet<BlockPos>>());
    public final ArrayList<BlockPos> updatePoses = new ArrayList<>();
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
                    if(mc.thePlayer != null && mc.theWorld.provider.getDimension() != -1)
                    {
                        updatePoses.clear();

                        BlockPos ref = new BlockPos(mc.thePlayer.posX, 0, mc.thePlayer.posZ);
                        int rangeHori = Math.max((Blocksteps.config.renderDistance == 0 ? (mc.gameSettings.renderDistanceChunks) : (Blocksteps.config.renderDistance)), 1) * 16;

                        HashMap<ChunkPos, Boolean> hasInfo = new HashMap<>();

                        for(int i = -rangeHori; i <= rangeHori; i++)
                        {
                            for(int k = -rangeHori; k <= rangeHori; k++)
                            {
                                ChunkPos chunk = new ChunkPos((ref.getX() + i) >> 4, (ref.getZ() + k) >> 4);
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
                                    if(state.getBlock().isAir(state, mc.theWorld, pos) || !(state.getBlock().isNormalCube(state, mc.theWorld, pos) || BlockStepHandler.isAcceptableBlockType(state, state.getBlock()) || BlockLiquid.class.isInstance(state.getBlock())))
                                    {
                                        continue;
                                    }
                                    addPos(pos);
                                    if(finds == 0)
                                    {
                                        ArrayList<BlockPos> periphs = new ArrayList<>();
                                        BlockStepHandler.addPeripherals(mc.theWorld, pos, periphs, false);
                                        for(BlockPos pos1 : periphs)
                                        {
                                            addPos(pos1);
                                        }
                                    }
                                    finds++;
                                    updatePoses.add(pos);
                                }
                            }
                        }

                        synchronized(surface)
                        {
                            Iterator<Map.Entry<ChunkPos, HashSet<BlockPos>>> ite = surface.entrySet().iterator();
                            while(ite.hasNext())
                            {
                                Map.Entry<ChunkPos, HashSet<BlockPos>> e = ite.next();
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

                        for(BlockPos pos : updatePoses)
                        {
                            IBlockState state = mc.theWorld.getBlockState(pos);
                            Blocksteps.eventHandler.renderGlobalProxy.notifyBlockUpdate(mc.theWorld, pos, state, state, 3);
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
        ChunkPos coord = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        HashSet<BlockPos> poses = crawler.get(coord);
        if(poses == null)
        {
            poses = new HashSet<>();
            crawler.put(coord, poses);
        }
        poses.add(pos);
    }
}
