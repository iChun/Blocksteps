package me.ichun.mods.blocksteps.common.blockaid.handler.periphs;

import me.ichun.mods.blocksteps.api.BlockPeripheralHandler;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class TripWireHookHandler extends BlockPeripheralHandler
{
    @Override
    public boolean isValidPeripheral(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        EnumFacing enumfacing = state.getValue(BlockTripWireHook.FACING);

        return availableBlocks.contains(pos.offset(enumfacing.getOpposite()));
    }
}
