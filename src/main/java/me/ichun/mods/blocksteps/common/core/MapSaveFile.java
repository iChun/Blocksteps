package me.ichun.mods.blocksteps.common.core;

import me.ichun.mods.blocksteps.common.Blocksteps;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.TreeMap;

public class MapSaveFile
{
    private MapSaveFile(){}

    public TreeMap<Integer, ArrayList<BlockPos>> stepPoints;
    public TreeMap<Integer, ArrayList<Waypoint>> waypoints;

    public static MapSaveFile create()
    {
        MapSaveFile file = new MapSaveFile();

        file.stepPoints = new TreeMap<Integer, ArrayList<BlockPos>>(Blocksteps.eventHandler.steps);
        file.waypoints = new TreeMap<Integer, ArrayList<Waypoint>>(Blocksteps.eventHandler.waypoints);

        return file;
    }

    public void load()
    {
        Blocksteps.eventHandler.steps.clear();
        if(stepPoints != null)
        {
            Blocksteps.eventHandler.steps = stepPoints;
        }
        Blocksteps.eventHandler.waypoints.clear();
        if(waypoints != null)
        {
            Blocksteps.eventHandler.waypoints = waypoints;
        }
    }
}
