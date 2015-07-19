package me.ichun.mods.blocksteps.common.blockaid.handler.periphs;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.blockaid.handler.BlockPeripheralHandler;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public class LogHandler extends BlockPeripheralHandler
{
    @Override
    public boolean isValidPeripheral(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        return true;
    }

    @Override
    public List<BlockPos> getRelativeBlocks(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        ArrayList<BlockPos> poses = new ArrayList<BlockPos>();
        ArrayList<BlockPos> tried = new ArrayList<BlockPos>();


        int tries = Blocksteps.config.treeDetection == 1 ? 100 : 0;
        int j = -2;
        while(tries > 0)
        {
            tries = getLeavesAndWood(world, pos.add(0, j, 0), poses, tried, tries);
            tries--;
            j++;
        }

        if(!poses.contains(pos))
        {
            poses.add(pos);
        }

        return poses;
    }

    public int getLeavesAndWood(IBlockAccess world, BlockPos pos, ArrayList<BlockPos> poses, ArrayList<BlockPos> tried, int triesLeft)
    {
        tried.add(pos);

        for(int i = -5; i <= 5; i++)
        {
            for(int k = -5; k <= 5; k++)
            {
                BlockPos newPos = pos.add(i, 0, k);
                if(BlockLog.class.isInstance(world.getBlockState(newPos).getBlock()))
                {
                    if(!poses.contains(newPos))
                    {
                        poses.add(newPos);
                    }
                    if(!tried.contains(newPos))
                    {
                        triesLeft = getLeavesAndWood(world, newPos, poses, tried, triesLeft);
                    }
                }
                else if(BlockLeaves.class.isInstance(world.getBlockState(newPos).getBlock()))
                {
                    if(!poses.contains(newPos))
                    {
                        poses.add(newPos);
                    }
                }
            }
        }
        return triesLeft - 1;
    }

    @Override
    public boolean requireThread()
    {
        return true;
    }
}
