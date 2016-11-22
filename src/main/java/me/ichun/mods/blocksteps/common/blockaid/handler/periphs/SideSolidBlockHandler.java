package me.ichun.mods.blocksteps.common.blockaid.handler.periphs;

import me.ichun.mods.blocksteps.api.BlockPeripheralHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public class SideSolidBlockHandler extends BlockPeripheralHandler
{
    public final Class<? extends Block> blockType;
    public final int checkHeight;

    public SideSolidBlockHandler(Class<? extends Block> blockType, int checkHeight)
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
            if(blockType.isInstance(world.getBlockState(highPos).getBlock()))
            {
                poses.add(highPos);
                for(EnumFacing face : EnumFacing.Plane.HORIZONTAL.facings())
                {
                    BlockPos sidePos = highPos.offset(face);
                    IBlockState sideState = world.getBlockState(sidePos);
                    if(!sideState.getBlock().isAir(sideState, world, sidePos) && sideState.getBlock().isNormalCube(sideState, world, sidePos))
                    {
                        poses.add(sidePos);
                    }
                }
                height--;
                highPos = highPos.add(0, 1, 0);
            }
            else
            {
                break;
            }
        }
        return poses;
    }
}
