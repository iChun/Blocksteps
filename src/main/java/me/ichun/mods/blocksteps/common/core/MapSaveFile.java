package me.ichun.mods.blocksteps.common.core;

import com.google.common.collect.Ordering;
import me.ichun.mods.blocksteps.common.Blocksteps;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class MapSaveFile
{
    private MapSaveFile(){}

    public TreeMap<Integer, ArrayList<BlockPos>> stepPoints;

    public static MapSaveFile create()
    {
        MapSaveFile file = new MapSaveFile();

        file.stepPoints = Blocksteps.eventHandler.steps;

        return file;
    }

    public void load()
    {
        Blocksteps.eventHandler.steps = stepPoints;
    }
}
