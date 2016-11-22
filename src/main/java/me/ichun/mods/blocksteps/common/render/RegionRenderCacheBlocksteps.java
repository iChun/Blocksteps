package me.ichun.mods.blocksteps.common.render;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.core.ChunkStore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
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
        if(Blocksteps.config.mapType == 4)
        {
            Minecraft mc = Minecraft.getMinecraft();
            int rangeHori = (Blocksteps.config.renderDistance == 0 ? (mc.gameSettings.renderDistanceChunks - 1) : (Blocksteps.config.renderDistance - 1)) * 16;
            double dx = pos.getX() - mc.thePlayer.posX;
            double dz = pos.getZ() - mc.thePlayer.posZ;
            double dist = Math.sqrt(dx * dx + dz * dz);
            if(dist > rangeHori)
            {
                return Blocks.AIR.getDefaultState();
            }
        }
        else if(Blocksteps.config.mapType != 3 && !ChunkStore.contains(pos))
        {
            return Blocks.AIR.getDefaultState();
        }
        return super.getBlockState(pos);
    }
}
