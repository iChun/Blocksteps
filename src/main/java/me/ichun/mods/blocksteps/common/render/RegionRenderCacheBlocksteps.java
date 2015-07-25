package me.ichun.mods.blocksteps.common.render;

import me.ichun.mods.blocksteps.common.Blocksteps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class RegionRenderCacheBlocksteps extends RegionRenderCache
{
    public RegionRenderCacheBlocksteps(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn)
    {
        super(worldIn, posFromIn, posToIn, subIn);
    }

    @Override
    public IBlockState getBlockState(BlockPos pos)
    {
        if(!Blocksteps.eventHandler.getBlocksToRender().contains(pos))
        {
            return Blocks.air.getDefaultState();
        }
        return super.getBlockState(pos);
    }
}
