package me.ichun.mods.blocksteps.common.core;

import me.ichun.mods.blocksteps.common.Blocksteps;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.TreeMap;

public class MapSaveFile
{
    private MapSaveFile(){}

    public TreeMap<Integer, ArrayList<BlockPos>> stepPoints;

    public static MapSaveFile create()
    {
        MapSaveFile file = new MapSaveFile();

        file.stepPoints = new TreeMap<Integer, ArrayList<BlockPos>>(Blocksteps.eventHandler.steps);

        return file;
    }

    public void load()
    {
        Blocksteps.eventHandler.steps = stepPoints;
    }
}
