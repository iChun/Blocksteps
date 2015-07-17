package me.ichun.mods.blocksteps.common.render;

import me.ichun.mods.blocksteps.common.Blocksteps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class ListedRenderChunkBlocksteps extends ListedRenderChunk
{
    public ListedRenderChunkBlocksteps(World worldIn, RenderGlobal renderGlobalIn, BlockPos pos, int indexIn)
    {
        super(worldIn, renderGlobalIn, pos, indexIn);
    }

    @Override
    public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator)
    {
        CompiledChunk compiledchunk = new CompiledChunk();
        boolean flag = true;
        BlockPos blockpos = this.position;
        BlockPos blockpos1 = blockpos.add(15, 15, 15);
        generator.getLock().lock();
        RegionRenderCache regionrendercache;

        try
        {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING)
            {
                return;
            }

            regionrendercache = new RegionRenderCacheBlocksteps(this.world, blockpos.add(-1, -1, -1), blockpos1.add(1, 1, 1), 1);
            generator.setCompiledChunk(compiledchunk);
        }
        finally
        {
            generator.getLock().unlock();
        }

        VisGraph visgraph = new VisGraph();

        Minecraft mc = Minecraft.getMinecraft();
        List<BlockPos> steps = Blocksteps.eventHandler.getSteps(mc.theWorld.provider.getDimensionId());

        if (!regionrendercache.extendedLevelsInChunkCache())
        {
            ++renderChunksUpdated;
            Iterator iterator = BlockPos.getAllInBoxMutable(blockpos, blockpos1).iterator();

            while (iterator.hasNext())
            {
                BlockPos.MutableBlockPos mutableblockpos = (BlockPos.MutableBlockPos)iterator.next();
                IBlockState iblockstate = regionrendercache.getBlockState(mutableblockpos);
                Block block = iblockstate.getBlock();

                if (block.isOpaqueCube())
                {
                    visgraph.func_178606_a(mutableblockpos);
                }

                if (steps.contains(mutableblockpos) && block.hasTileEntity(iblockstate))
                {
                    TileEntity tileentity = regionrendercache.getTileEntity(new BlockPos(mutableblockpos));

                    if (tileentity != null && TileEntityRendererDispatcher.instance.hasSpecialRenderer(tileentity))
                    {
                        compiledchunk.addTileEntity(tileentity);
                    }
                }

                for(EnumWorldBlockLayer enumworldblocklayer1 : EnumWorldBlockLayer.values()) {
                    if(!block.canRenderInLayer(enumworldblocklayer1)) continue;
                    net.minecraftforge.client.ForgeHooksClient.setRenderLayer(enumworldblocklayer1);
                    int i = enumworldblocklayer1.ordinal();

                    if (block.getRenderType() != -1)
                    {
                        WorldRenderer worldrenderer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(i);

                        if (!compiledchunk.isLayerStarted(enumworldblocklayer1))
                        {
                            compiledchunk.setLayerStarted(enumworldblocklayer1);
                            this.preRenderBlocks(worldrenderer, blockpos);
                        }

                        if (steps.contains(mutableblockpos) && mc.getBlockRendererDispatcher().renderBlock(iblockstate, mutableblockpos, regionrendercache, worldrenderer))
                        {
                            compiledchunk.setLayerUsed(enumworldblocklayer1);
                        }
                    }
                }
            }

            EnumWorldBlockLayer[] aenumworldblocklayer = EnumWorldBlockLayer.values();
            int j = aenumworldblocklayer.length;

            for (int k = 0; k < j; ++k)
            {
                EnumWorldBlockLayer enumworldblocklayer = aenumworldblocklayer[k];

                if (compiledchunk.isLayerStarted(enumworldblocklayer))
                {
                    this.postRenderBlocks(enumworldblocklayer, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumworldblocklayer), compiledchunk);
                }
            }
        }

        compiledchunk.setVisibility(visgraph.computeVisibility());
    }
}
