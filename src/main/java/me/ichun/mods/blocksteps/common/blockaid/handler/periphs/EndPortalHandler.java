package me.ichun.mods.blocksteps.common.blockaid.handler.periphs;

import me.ichun.mods.blocksteps.api.BlockPeripheralHandler;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public class EndPortalHandler extends BlockPeripheralHandler
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

        for(int i = -4; i <= 4; i++)
        {
            for(int k = -4; k <= 4; k++)
            {
                BlockPos newPos = pos.add(i, 0, k);
                if(BlockEndPortalFrame.class.isInstance(world.getBlockState(newPos).getBlock()) || BlockEndPortal.class.isInstance(world.getBlockState(newPos).getBlock()))
                {
                    poses.add(newPos);
                }
            }
        }
        return poses;
    }
}
