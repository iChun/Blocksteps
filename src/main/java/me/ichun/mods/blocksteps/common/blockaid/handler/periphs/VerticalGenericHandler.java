package me.ichun.mods.blocksteps.common.blockaid.handler.periphs;

import me.ichun.mods.blocksteps.common.blockaid.handler.BlockPeripheralHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockReed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public class VerticalGenericHandler extends BlockPeripheralHandler
{
    public final Class<? extends Block> blockType;
    public final int checkHeight;

    public VerticalGenericHandler(Class<? extends Block> blockType, int checkHeight)
    {
        this.blockType = blockType;
        this.checkHeight = checkHeight;
    }

    @Override
    public List<BlockPos> getRelativeBlocks(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        ArrayList<BlockPos> poses = new ArrayList<BlockPos>();
        poses.add(pos);

        int height = checkHeight;
        BlockPos highPos = pos;
        while(height > 1)
        {
            highPos = highPos.add(0, 1, 0);
            if(blockType.isInstance(world.getBlockState(highPos).getBlock()))
            {
                poses.add(highPos);
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
