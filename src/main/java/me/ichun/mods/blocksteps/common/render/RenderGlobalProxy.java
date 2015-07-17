package me.ichun.mods.blocksteps.common.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGlobalProxy extends RenderGlobal
{
    private boolean renderSky;

    public RenderGlobalProxy(Minecraft par1Minecraft)
    {
        super(par1Minecraft);
        renderSky = true;
    }

    public RenderGlobalProxy setRenderSky(boolean flag)
    {
        renderSky = flag;
        return this;
    }

    @Override
    public void updateClouds()
    {
        if(renderSky)
        {
            super.updateClouds();
        }
    }

    @Override
    public void deleteAllDisplayLists()
    {
        super.deleteAllDisplayLists();
        if (starGLCallList >= 0)
        {
            GLAllocation.deleteDisplayLists(starGLCallList);
        }
    }

    @Override
    public void renderSky(float par1, int pass)
    {
        if(renderSky)
        {
            super.renderSky(par1, pass);
        }
    }

    @Override
    public void renderClouds(float par1, int pass)
    {
        if(renderSky)
        {
            super.renderClouds(par1, pass);
        }
    }

    @Override
    public boolean hasCloudFog(double par1, double par3, double par5, float par7)
    {
        if(renderSky)
        {
            return super.hasCloudFog(par1, par3, par5, par7);
        }
        return false;
    }

    @Override
    public void renderCloudsFancy(float par1, int pass)
    {
        if(renderSky)
        {
            super.renderCloudsFancy(par1, pass);
        }
    }

    @Override
    public void loadRenderers()
    {
        super.loadRenderers();
        if (this.theWorld != null)
        {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactoryBlocksteps();

            if (this.viewFrustum != null)
            {
                this.viewFrustum.deleteGlResources();
            }

            this.stopChunkUpdates();
            this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);

            Entity entity = this.mc.getRenderViewEntity();

            if (entity != null)
            {
                this.viewFrustum.updateChunkPositions(entity.posX, entity.posZ);
            }

            this.renderEntitiesStartupCounter = 2;
        }
    }

    /**
     * Plays the specified record. Arg: recordName, x, y, z
     */
    @Override
    public void playRecord(String par1Str, BlockPos pos)
    {
    }

    /**
     * Plays the specified sound. Arg: soundName, x, y, z, volume, pitch
     */
    @Override
    public void playSound(String par1Str, double par2, double par4, double par6, float par8, float par9) {}

    /**
     * Plays sound to all near players except the player reference given
     */
    @Override
    public void playSoundToNearExcept(EntityPlayer par1EntityPlayer, String par2Str, double par3, double par5, double par7, float par9, float par10) {}

    @Override
    public void broadcastSound(int par1, BlockPos pos, int par5)
    {
    }

    /**
     * Plays a pre-canned sound effect along with potentially auxiliary data-driven one-shot behaviour (particles, etc).
     */
    @Override
    public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int p_180439_4_)
    {
    }
}
