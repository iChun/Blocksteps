package me.ichun.mods.blocksteps.common.render;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.World;

public class ListChunkFactoryBlocksteps implements IRenderChunkFactory
{
    @Override
    public RenderChunk create(World worldIn, RenderGlobal globalRenderer, int index)
    {
        return new ListedRenderChunkBlocksteps(worldIn, globalRenderer, index);
    }
}
