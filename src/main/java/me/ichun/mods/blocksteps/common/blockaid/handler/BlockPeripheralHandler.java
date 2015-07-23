package me.ichun.mods.blocksteps.common.blockaid.handler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockPeripheralHandler
{
    public boolean isValidPeripheral(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        return !getRelativeBlocks(world, pos, state, availableBlocks).isEmpty();
    }

    public List<BlockPos> getRelativeBlocks(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        ArrayList<BlockPos> poses = new ArrayList<BlockPos>();
        poses.add(pos);
        return poses;
    }

    public boolean isAlsoSolidBlock()
    {
        return false;
    }

    public boolean requireThread()
    {
        return false;
    }
}
