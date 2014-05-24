package blocksteps.client.render;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.util.RenderDistanceSorter;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IWorldAccess;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.Callable;

@SideOnly(Side.CLIENT)
public class RenderGlobalBlockstep extends RenderGlobal
{
    public RenderGlobalBlockstep(Minecraft par1Minecraft)
    {
        super(par1Minecraft);
    }

    @Override
    public void clipRenderersByFrustum(ICamera par1ICamera, float par2)
    {
//        for (int i = 0; i < this.worldRenderers.length; ++i)
//        {
//            this.worldRenderers[i].isInFrustum = true;
//        }
    }

    /**
     * Loads all the renderers and sets up the basic settings usage
     */
//    //TODO review this function
//    public void loadRenderers()
//    {
//        if (this.theWorld != null)
//        {
//            Blocks.leaves.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
//            Blocks.leaves2.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
//            this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
//            int i;
//
//            if (this.worldRenderers != null)
//            {
//                for (i = 0; i < this.worldRenderers.length; ++i)
//                {
//                    this.worldRenderers[i].stopRendering();
//                }
//            }
//
//            i = this.renderDistanceChunks * 2 + 1;
//            this.renderChunksWide = i;
//            this.renderChunksTall = 16;
//            this.renderChunksDeep = i;
//            this.worldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
//            this.sortedWorldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
//            int j = 0;
//            int k = 0;
//            this.minBlockX = 0;
//            this.minBlockY = 0;
//            this.minBlockZ = 0;
//            this.maxBlockX = this.renderChunksWide;
//            this.maxBlockY = this.renderChunksTall;
//            this.maxBlockZ = this.renderChunksDeep;
//            int l;
//
//            for (l = 0; l < this.worldRenderersToUpdate.size(); ++l)
//            {
//                ((WorldRenderer)this.worldRenderersToUpdate.get(l)).needsUpdate = false;
//            }
//
//            this.worldRenderersToUpdate.clear();
//            this.tileEntities.clear();
//
//            for (l = 0; l < this.renderChunksWide; ++l)
//            {
//                for (int i1 = 0; i1 < this.renderChunksTall; ++i1)
//                {
//                    for (int j1 = 0; j1 < this.renderChunksDeep; ++j1)
//                    {
//                        this.worldRenderers[(j1 * this.renderChunksTall + i1) * this.renderChunksWide + l] = new WorldRenderer(this.theWorld, this.tileEntities, l * 16, i1 * 16, j1 * 16, this.glRenderListBase + j);
//
//                        if (this.occlusionEnabled)
//                        {
//                            this.worldRenderers[(j1 * this.renderChunksTall + i1) * this.renderChunksWide + l].glOcclusionQuery = this.glOcclusionQueryBase.get(k);
//                        }
//
//                        this.worldRenderers[(j1 * this.renderChunksTall + i1) * this.renderChunksWide + l].isWaitingOnOcclusionQuery = false;
//                        this.worldRenderers[(j1 * this.renderChunksTall + i1) * this.renderChunksWide + l].isVisible = true;
//                        this.worldRenderers[(j1 * this.renderChunksTall + i1) * this.renderChunksWide + l].isInFrustum = true;
//                        this.worldRenderers[(j1 * this.renderChunksTall + i1) * this.renderChunksWide + l].chunkIndex = k++;
//                        this.worldRenderers[(j1 * this.renderChunksTall + i1) * this.renderChunksWide + l].markDirty();
//                        this.sortedWorldRenderers[(j1 * this.renderChunksTall + i1) * this.renderChunksWide + l] = this.worldRenderers[(j1 * this.renderChunksTall + i1) * this.renderChunksWide + l];
//                        this.worldRenderersToUpdate.add(this.worldRenderers[(j1 * this.renderChunksTall + i1) * this.renderChunksWide + l]);
//                        j += 3;
//                    }
//                }
//            }
//
//            if (this.theWorld != null)
//            {
//                EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
//
//                if (entitylivingbase != null)
//                {
//                    this.markRenderersForNewPosition(MathHelper.floor_double(entitylivingbase.posX), MathHelper.floor_double(entitylivingbase.posY), MathHelper.floor_double(entitylivingbase.posZ));
//                    Arrays.sort(this.sortedWorldRenderers, new EntitySorter(entitylivingbase));
//                }
//            }
//
//            this.renderEntitiesStartupCounter = 2;
//        }
//    }
//
//    //TODO this is the function you need to change
//    public int sortAndRender(EntityLivingBase par1EntityLivingBase, int par2, double par3)
//    {
//        this.theWorld.theProfiler.startSection("sortchunks");
//
//        for (int j = 0; j < 10; ++j)
//        {
//            this.worldRenderersCheckIndex = (this.worldRenderersCheckIndex + 1) % this.worldRenderers.length;
//            WorldRenderer worldrenderer = this.worldRenderers[this.worldRenderersCheckIndex];
//
//            if (worldrenderer.needsUpdate && !this.worldRenderersToUpdate.contains(worldrenderer))
//            {
//                this.worldRenderersToUpdate.add(worldrenderer);
//            }
//        }
//
//        if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks)
//        {
//            this.loadRenderers();
//        }
//
//        if (par2 == 0)
//        {
//            this.renderersLoaded = 0;
//            this.dummyRenderInt = 0;
//            this.renderersBeingClipped = 0;
//            this.renderersBeingOccluded = 0;
//            this.renderersBeingRendered = 0;
//            this.renderersSkippingRenderPass = 0;
//        }
//
//        double d9 = par1EntityLivingBase.lastTickPosX + (par1EntityLivingBase.posX - par1EntityLivingBase.lastTickPosX) * par3;
//        double d1 = par1EntityLivingBase.lastTickPosY + (par1EntityLivingBase.posY - par1EntityLivingBase.lastTickPosY) * par3;
//        double d2 = par1EntityLivingBase.lastTickPosZ + (par1EntityLivingBase.posZ - par1EntityLivingBase.lastTickPosZ) * par3;
//        double d3 = par1EntityLivingBase.posX - this.prevSortX;
//        double d4 = par1EntityLivingBase.posY - this.prevSortY;
//        double d5 = par1EntityLivingBase.posZ - this.prevSortZ;
//
//        if (this.prevChunkSortX != par1EntityLivingBase.chunkCoordX || this.prevChunkSortY != par1EntityLivingBase.chunkCoordY || this.prevChunkSortZ != par1EntityLivingBase.chunkCoordZ || d3 * d3 + d4 * d4 + d5 * d5 > 16.0D)
//        {
//            this.prevSortX = par1EntityLivingBase.posX;
//            this.prevSortY = par1EntityLivingBase.posY;
//            this.prevSortZ = par1EntityLivingBase.posZ;
//            this.prevChunkSortX = par1EntityLivingBase.chunkCoordX;
//            this.prevChunkSortY = par1EntityLivingBase.chunkCoordY;
//            this.prevChunkSortZ = par1EntityLivingBase.chunkCoordZ;
//            this.markRenderersForNewPosition(MathHelper.floor_double(par1EntityLivingBase.posX), MathHelper.floor_double(par1EntityLivingBase.posY), MathHelper.floor_double(par1EntityLivingBase.posZ));
//            Arrays.sort(this.sortedWorldRenderers, new EntitySorter(par1EntityLivingBase));
//        }
//
//        double d6 = par1EntityLivingBase.posX - this.prevRenderSortX;
//        double d7 = par1EntityLivingBase.posY - this.prevRenderSortY;
//        double d8 = par1EntityLivingBase.posZ - this.prevRenderSortZ;
//        int k;
//
//        if (d6 * d6 + d7 * d7 + d8 * d8 > 1.0D)
//        {
//            this.prevRenderSortX = par1EntityLivingBase.posX;
//            this.prevRenderSortY = par1EntityLivingBase.posY;
//            this.prevRenderSortZ = par1EntityLivingBase.posZ;
//
//            for (k = 0; k < 27; ++k)
//            {
//                this.sortedWorldRenderers[k].updateRendererSort(par1EntityLivingBase);
//            }
//        }
//
//        RenderHelper.disableStandardItemLighting();
//        byte b1 = 0;
//
//        if (this.occlusionEnabled && this.mc.gameSettings.advancedOpengl && !this.mc.gameSettings.anaglyph && par2 == 0)
//        {
//            byte b0 = 0;
//            int l = 16;
//            this.checkOcclusionQueryResult(b0, l);
//
//            for (int i1 = b0; i1 < l; ++i1)
//            {
//                this.sortedWorldRenderers[i1].isVisible = true;
//            }
//
//            this.theWorld.theProfiler.endStartSection("render");
//            k = b1 + this.renderSortedRenderers(b0, l, par2, par3);
//
//            do
//            {
//                this.theWorld.theProfiler.endStartSection("occ");
//                int l1 = l;
//                l *= 2;
//
//                if (l > this.sortedWorldRenderers.length)
//                {
//                    l = this.sortedWorldRenderers.length;
//                }
//
//                GL11.glDisable(GL11.GL_TEXTURE_2D);
//                GL11.glDisable(GL11.GL_LIGHTING);
//                GL11.glDisable(GL11.GL_ALPHA_TEST);
//                GL11.glDisable(GL11.GL_FOG);
//                GL11.glColorMask(false, false, false, false);
//                GL11.glDepthMask(false);
//                this.theWorld.theProfiler.startSection("check");
//                this.checkOcclusionQueryResult(l1, l);
//                this.theWorld.theProfiler.endSection();
//                GL11.glPushMatrix();
//                float f9 = 0.0F;
//                float f = 0.0F;
//                float f1 = 0.0F;
//
//                for (int j1 = l1; j1 < l; ++j1)
//                {
//                    if (this.sortedWorldRenderers[j1].skipAllRenderPasses())
//                    {
//                        this.sortedWorldRenderers[j1].isInFrustum = false;
//                    }
//                    else
//                    {
//                        if (!this.sortedWorldRenderers[j1].isInFrustum)
//                        {
//                            this.sortedWorldRenderers[j1].isVisible = true;
//                        }
//
//                        if (this.sortedWorldRenderers[j1].isInFrustum && !this.sortedWorldRenderers[j1].isWaitingOnOcclusionQuery)
//                        {
//                            float f2 = MathHelper.sqrt_float(this.sortedWorldRenderers[j1].distanceToEntitySquared(par1EntityLivingBase));
//                            int k1 = (int)(1.0F + f2 / 128.0F);
//
//                            if (this.cloudTickCounter % k1 == j1 % k1)
//                            {
//                                WorldRenderer worldrenderer1 = this.sortedWorldRenderers[j1];
//                                float f3 = (float)((double)worldrenderer1.posXMinus - d9);
//                                float f4 = (float)((double)worldrenderer1.posYMinus - d1);
//                                float f5 = (float)((double)worldrenderer1.posZMinus - d2);
//                                float f6 = f3 - f9;
//                                float f7 = f4 - f;
//                                float f8 = f5 - f1;
//
//                                if (f6 != 0.0F || f7 != 0.0F || f8 != 0.0F)
//                                {
//                                    GL11.glTranslatef(f6, f7, f8);
//                                    f9 += f6;
//                                    f += f7;
//                                    f1 += f8;
//                                }
//
//                                this.theWorld.theProfiler.startSection("bb");
//                                ARBOcclusionQuery.glBeginQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB, this.sortedWorldRenderers[j1].glOcclusionQuery);
//                                this.sortedWorldRenderers[j1].callOcclusionQueryList();
//                                ARBOcclusionQuery.glEndQueryARB(ARBOcclusionQuery.GL_SAMPLES_PASSED_ARB);
//                                this.theWorld.theProfiler.endSection();
//                                this.sortedWorldRenderers[j1].isWaitingOnOcclusionQuery = true;
//                            }
//                        }
//                    }
//                }
//
//                GL11.glPopMatrix();
//
//                if (this.mc.gameSettings.anaglyph)
//                {
//                    if (EntityRenderer.anaglyphField == 0)
//                    {
//                        GL11.glColorMask(false, true, true, true);
//                    }
//                    else
//                    {
//                        GL11.glColorMask(true, false, false, true);
//                    }
//                }
//                else
//                {
//                    GL11.glColorMask(true, true, true, true);
//                }
//
//                GL11.glDepthMask(true);
//                GL11.glEnable(GL11.GL_TEXTURE_2D);
//                GL11.glEnable(GL11.GL_ALPHA_TEST);
//                GL11.glEnable(GL11.GL_FOG);
//                this.theWorld.theProfiler.endStartSection("render");
//                k += this.renderSortedRenderers(l1, l, par2, par3);
//            }
//            while (l < this.sortedWorldRenderers.length);
//        }
//        else
//        {
//            this.theWorld.theProfiler.endStartSection("render");
//            k = b1 + this.renderSortedRenderers(0, this.sortedWorldRenderers.length, par2, par3);
//        }
//
//        this.theWorld.theProfiler.endSection();
//        return k;
//    }
//
//    /**
//     * Renders the sorted renders for the specified render pass. Args: startRenderer, numRenderers, renderPass,
//     * partialTickTime
//     */
//    //TODO review this function as well
//    private int renderSortedRenderers(int par1, int par2, int par3, double par4)
//    {
//        this.glRenderLists.clear();
//        int l = 0;
//        int i1 = par1;
//        int j1 = par2;
//        byte b0 = 1;
//
//        if (par3 == 1)
//        {
//            i1 = this.sortedWorldRenderers.length - 1 - par1;
//            j1 = this.sortedWorldRenderers.length - 1 - par2;
//            b0 = -1;
//        }
//
//        for (int k1 = i1; k1 != j1; k1 += b0)
//        {
//            if (par3 == 0)
//            {
//                ++this.renderersLoaded;
//
//                if (this.sortedWorldRenderers[k1].skipRenderPass[par3])
//                {
//                    ++this.renderersSkippingRenderPass;
//                }
//                else if (!this.sortedWorldRenderers[k1].isInFrustum)
//                {
//                    ++this.renderersBeingClipped;
//                }
//                else if (this.occlusionEnabled && !this.sortedWorldRenderers[k1].isVisible)
//                {
//                    ++this.renderersBeingOccluded;
//                }
//                else
//                {
//                    ++this.renderersBeingRendered;
//                }
//            }
//
//            if (!this.sortedWorldRenderers[k1].skipRenderPass[par3] && this.sortedWorldRenderers[k1].isInFrustum && (!this.occlusionEnabled || this.sortedWorldRenderers[k1].isVisible))
//            {
//                int l1 = this.sortedWorldRenderers[k1].getGLCallListForPass(par3);
//
//                if (l1 >= 0)
//                {
//                    this.glRenderLists.add(this.sortedWorldRenderers[k1]);
//                    ++l;
//                }
//            }
//        }
//
//        EntityLivingBase entitylivingbase = this.mc.renderViewEntity;
//        double d3 = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * par4;
//        double d1 = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * par4;
//        double d2 = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * par4;
//        int i2 = 0;
//        int j2;
//
//        for (j2 = 0; j2 < this.allRenderLists.length; ++j2)
//        {
//            this.allRenderLists[j2].resetList();
//        }
//
//        for (j2 = 0; j2 < this.glRenderLists.size(); ++j2)
//        {
//            WorldRenderer worldrenderer = (WorldRenderer)this.glRenderLists.get(j2);
//            int k2 = -1;
//
//            for (int l2 = 0; l2 < i2; ++l2)
//            {
//                if (this.allRenderLists[l2].rendersChunk(worldrenderer.posXMinus, worldrenderer.posYMinus, worldrenderer.posZMinus))
//                {
//                    k2 = l2;
//                }
//            }
//
//            if (k2 < 0)
//            {
//                k2 = i2++;
//                this.allRenderLists[k2].setupRenderList(worldrenderer.posXMinus, worldrenderer.posYMinus, worldrenderer.posZMinus, d3, d1, d2);
//            }
//
//            this.allRenderLists[k2].addGLRenderList(worldrenderer.getGLCallListForPass(par3));
//        }
//
//        Arrays.sort(this.allRenderLists, new RenderDistanceSorter());
//        this.renderAllRenderLists(par3, par4);
//        return l;
//    }
//
//    /**
//     * Checks all renderers that previously weren't in the frustum and 1/16th of those that previously were in the
//     * frustum for frustum clipping Args: frustum, partialTickTime
//     */
//    //TODO think about this.
//    public void clipRenderersByFrustum(ICamera par1ICamera, float par2)
//    {
//        for (int i = 0; i < this.worldRenderers.length; ++i)
//        {
//            if (!this.worldRenderers[i].skipAllRenderPasses() && (!this.worldRenderers[i].isInFrustum || (i + this.frustumCheckOffset & 15) == 0))
//            {
//                this.worldRenderers[i].updateInFrustum(par1ICamera);
//            }
//        }
//
//        ++this.frustumCheckOffset;
//    }

    @Override
    public void renderEntities(EntityLivingBase p_147589_1_, ICamera p_147589_2_, float p_147589_3_)
    {
    }

    @Override
    public void updateClouds()
    {
    }

    @Override
    public void renderSky(float par1)
    {
    }

    @Override
    public void renderClouds(float par1)
    {
    }

    @Override
    public void renderCloudsFancy(float par1)
    {
    }

    @Override
    public void drawSelectionBox(EntityPlayer par1EntityPlayer, MovingObjectPosition par2MovingObjectPosition, int par3, float par4)
    {
    }

    @Override
    public void playRecord(String par1Str, int par2, int par3, int par4)
    {
    }

    @Override
    public void playSound(String par1Str, double par2, double par4, double par6, float par8, float par9) {}

    @Override
    public void playSoundToNearExcept(EntityPlayer par1EntityPlayer, String par2Str, double par3, double par5, double par7, float par9, float par10) {}

    @Override
    public void spawnParticle(String par1Str, final double par2, final double par4, final double par6, double par8, double par10, double par12)
    {
    }

    @Override
    public EntityFX doSpawnParticle(String par1Str, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        return null;
    }

    @Override
    public void onEntityCreate(Entity par1Entity) {}

    @Override
    public void onEntityDestroy(Entity par1Entity) {}

    @Override
    public void broadcastSound(int par1, int par2, int par3, int par4, int par5)
    {
    }

    @Override
    public void playAuxSFX(EntityPlayer par1EntityPlayer, int par2, int par3, int par4, int par5, int par6)
    {
    }
}