package me.ichun.mods.blocksteps.common.core;

import com.google.common.collect.Ordering;
import me.ichun.mods.blocksteps.common.Blocksteps;
import net.minecraft.util.BlockPos;

import java.util.Collection;
import java.util.TreeMap;

public class MapSaveFile
{
    private MapSaveFile(){}

    public TreeMap<Integer, Collection<BlockPos>> stepPoints;

    public static MapSaveFile create()
    {
        MapSaveFile file = new MapSaveFile();

        file.stepPoints = new TreeMap<Integer, Collection<BlockPos>>(Ordering.natural());
        file.stepPoints.putAll(Blocksteps.eventHandler.steps.asMap());

        return file;
    }
}
