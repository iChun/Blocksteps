package me.ichun.mods.blocksteps.common.blockaid.handler.periphs;

import me.ichun.mods.blocksteps.api.BlockPeripheralHandler;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public class LadderHandler extends BlockPeripheralHandler
{
    @Override
    public boolean isValidPeripheral(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(BlockLadder.FACING);

        return availableBlocks.contains(pos.offset(enumfacing.getOpposite()));
    }

    @Override
    public List<BlockPos> getRelativeBlocks(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(BlockLadder.FACING);
        ArrayList<BlockPos> poses = new ArrayList<BlockPos>();
        poses.add(pos);
        poses.add(pos.offset(enumfacing.getOpposite()));

        int height = 100;
        BlockPos highPos = pos;
        while(height > 1)
        {
            highPos = highPos.add(0, 1, 0);
            IBlockState state1 = world.getBlockState(highPos);
            if(BlockLadder.class.isInstance(state1.getBlock()))
            {
                EnumFacing enumfacing1 = (EnumFacing)state1.getValue(BlockLadder.FACING);
                poses.add(highPos);
                poses.add(highPos.offset(enumfacing1.getOpposite()));
                height--;
            }
            else
            {
                break;
            }
        }
        return poses;
    }
}
