package me.ichun.mods.blocksteps.common.blockaid.handler.periphs;

import me.ichun.mods.blocksteps.common.blockaid.handler.BlockPeripheralHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public class HorizontalGenericHandler extends BlockPeripheralHandler
{
    public final Class<? extends Block> blockType;

    public HorizontalGenericHandler(Class<? extends Block> blockType)
    {
        this.blockType = blockType;
    }

    @Override
    public List<BlockPos> getRelativeBlocks(IBlockAccess world, BlockPos pos, IBlockState state, List<BlockPos> availableBlocks)
    {
        ArrayList<BlockPos> poses = new ArrayList<BlockPos>();
        poses.add(pos);

        boolean flag = false;
        for(int i = -1; i <= 1; i++)
        {
            if(!flag)
            {
                for(int k = -1; k <= 1; k++)
                {
                    if(!(i == 0 && k == 0))
                    {
                        BlockPos newPos = pos.add(i, 0, k);
                        if(blockType.isInstance(world.getBlockState(newPos).getBlock()))
                        {
                            poses.add(newPos);
                            poses.add(newPos.add(0, -1, 0));
                            flag = true;
                            break;
                        }
                    }
                }
            }
        }
        return poses;
    }
}
