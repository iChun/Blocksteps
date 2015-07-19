package me.ichun.mods.blocksteps.common.blockaid.handler.periphs;

import me.ichun.mods.blocksteps.common.blockaid.handler.BlockPeripheralHandler;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class TorchHandler extends BlockPeripheralHandler
{
    @Override
    public boolean isValidPeripheral(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(BlockTorch.FACING);

        return availableBlocks.contains(pos.offset(enumfacing.getOpposite()));
    }
}
