package me.ichun.mods.blocksteps.common.blockaid.handler.periphs;

import me.ichun.mods.blocksteps.api.BlockPeripheralHandler;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public class MushroomHandler extends BlockPeripheralHandler
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


        int tries = 100;
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
                if(BlockHugeMushroom.class.isInstance(world.getBlockState(newPos).getBlock()))
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
            }
        }
        return triesLeft - 1;
    }

    @Override
    public boolean isAlsoSolidBlock()
    {
        return true;
    }

    @Override
    public boolean requireThread()
    {
        return true;
    }

}
