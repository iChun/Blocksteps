package me.ichun.mods.blocksteps.common.core;

import me.ichun.mods.blocksteps.common.Blocksteps;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ChunkStore
{
    public static HashMap<ChunkCoordIntPair, HashSet<BlockPos>> chunkCache = new HashMap<ChunkCoordIntPair, HashSet<BlockPos>>();

    public static void addBlocks(List<BlockPos> blockPoses)
    {
        for(BlockPos pos : blockPoses)
        {
            ChunkCoordIntPair coord = new ChunkCoordIntPair(pos.getX() >> 4, pos.getZ() >> 4);
            HashSet<BlockPos> poses = chunkCache.get(coord);
            if(poses == null)
            {
                poses = new HashSet<BlockPos>();
                chunkCache.put(coord, poses);
            }
            poses.add(pos);
        }
    }

    public static boolean contains(BlockPos pos)
    {
        ChunkCoordIntPair coord = new ChunkCoordIntPair(pos.getX() >> 4, pos.getZ() >> 4);
        if(Blocksteps.config.mapType == 2)
        {
            synchronized(Blocksteps.eventHandler.threadCrawlBlocks.surface)
            {
                HashSet<BlockPos> poses = chunkCache.get(coord);
                HashSet<BlockPos> surface = Blocksteps.eventHandler.threadCrawlBlocks.surface.get(coord);
                return poses != null && poses.contains(pos) || surface != null && surface.contains(pos);
            }
        }
        else
        {
            HashSet<BlockPos> poses = chunkCache.get(coord);
            if(poses != null)
            {
                return poses.contains(pos);
            }
        }
        return false;
    }

    public static void clear()
    {
        chunkCache.clear();
    }
}
