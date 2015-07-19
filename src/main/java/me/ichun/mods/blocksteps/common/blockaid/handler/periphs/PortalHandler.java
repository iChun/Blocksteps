package me.ichun.mods.blocksteps.common.blockaid.handler.periphs;

import me.ichun.mods.blocksteps.common.blockaid.handler.BlockPeripheralHandler;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public class PortalHandler extends BlockPeripheralHandler
{
    @Override
    public boolean isValidPeripheral(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        return true;
    }

    @Override
    public List<BlockPos> getRelativeBlocks(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        EnumFacing.Axis axis = (EnumFacing.Axis)state.getValue(BlockPortal.AXIS);
        ArrayList<BlockPos> poses = new ArrayList<BlockPos>();
        poses.add(pos);

        for(int j = -1; j <= 21; j++)
        {
            boolean hasPortal = false;
            for(int i = 0; i <= 21; i++)
            {
                if(!(i == 0 && j == 0))
                {
                    BlockPos newPos;
                    if(axis == EnumFacing.Axis.X)
                    {
                        newPos = pos.add(-i, j, 0);
                    }
                    else
                    {
                        newPos = pos.add(0, j, -i);
                    }
                    IBlockState newState = world.getBlockState(newPos);
                    if(newState.getBlock() == Blocks.obsidian)
                    {
                        poses.add(newPos);
                        if(hasPortal && j != -1)
                        {
                            break;
                        }
                    }
                    else if(newState.getBlock() == Blocks.portal)
                    {
                        poses.add(newPos);
                        hasPortal = true;
                    }
                }
            }
            for(int i = 0; i <= 21; i++)
            {
                if(!(i == 0 && j == 0))
                {
                    BlockPos newPos;
                    if(axis == EnumFacing.Axis.X)
                    {
                        newPos = pos.add(i, j, 0);
                    }
                    else
                    {
                        newPos = pos.add(0, j, i);
                    }
                    IBlockState newState = world.getBlockState(newPos);
                    if(newState.getBlock() == Blocks.obsidian)
                    {
                        poses.add(newPos);
                        if(hasPortal && j != -1)
                        {
                            break;
                        }
                    }
                    else if(newState.getBlock() == Blocks.portal)
                    {
                        poses.add(newPos);
                        hasPortal = true;
                    }
                }
            }
            if(!hasPortal && j != -1)
            {
                break;
            }
        }

        return poses;
    }
}
