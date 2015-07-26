package me.ichun.mods.blocksteps.common.blockaid.handler.periphs;

import me.ichun.mods.blocksteps.api.BlockPeripheralHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class GenericHandler extends BlockPeripheralHandler
{
    @Override
    public boolean isValidPeripheral(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        return true;
    }
}
