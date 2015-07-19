package me.ichun.mods.blocksteps.common.core;

import com.google.common.collect.Ordering;
import me.ichun.mods.blocksteps.common.Blocksteps;
import net.minecraft.util.BlockPos;

import java.util.Collection;
import java.util.Map;
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

    public void load()
    {
        Blocksteps.eventHandler.steps.clear();
        for(Map.Entry<Integer, Collection<BlockPos>> e : stepPoints.entrySet())
        {
            Blocksteps.eventHandler.steps.get(e.getKey()).addAll(e.getValue());
        }
    }
}
