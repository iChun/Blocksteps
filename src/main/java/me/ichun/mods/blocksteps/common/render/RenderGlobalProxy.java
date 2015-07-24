package me.ichun.mods.blocksteps.common.render;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.core.Waypoint;
import me.ichun.mods.blocksteps.common.entity.EntityWaypoint;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import us.ichun.mods.ichunutil.client.render.RendererHelper;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@SideOnly(Side.CLIENT)
public class RenderGlobalProxy extends RenderGlobal
{
    public boolean setupTerrain = false;

    private static final ResourceLocation texBeaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");

    public RenderGlobalProxy(Minecraft par1Minecraft)
    {
        super(par1Minecraft);
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
    public void loadRenderers()
    {
        if (this.theWorld != null && !setupTerrain)
        {
            this.displayListEntitiesDirty = true;
            Blocks.leaves.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
            Blocks.leaves2.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
            this.renderDistanceChunks = Blocksteps.config.renderDistance > 0 ? Blocksteps.config.renderDistance : this.mc.gameSettings.renderDistanceChunks;
            boolean flag = this.vboEnabled;
            this.vboEnabled = OpenGlHelper.useVbo();

            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactoryBlocksteps();

            if (flag != this.vboEnabled)
            {
                this.generateStars();
                this.generateSky();
                this.generateSky2();
            }

            if (this.viewFrustum != null)
            {
                this.viewFrustum.deleteGlResources();
            }

            this.stopChunkUpdates();
            this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);

            if (this.theWorld != null)
            {
                Entity entity = this.mc.getRenderViewEntity();

                if (entity != null)
                {
                    this.viewFrustum.updateChunkPositions(entity.posX, entity.posZ);
                }
            }

            this.renderEntitiesStartupCounter = 2;
        }
    }

    @Override
    public void generateSky2()
    {
        if (this.sky2VBO != null)
        {
            this.sky2VBO.deleteGlBuffers();
        }

        if (this.glSkyList2 >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSkyList2);
            this.glSkyList2 = -1;
        }

        if (this.vboEnabled)
        {
            this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
        }
        else
        {
            this.glSkyList2 = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
            GL11.glEndList();
        }
    }

    @Override
    public void generateSky()
    {
        if (this.skyVBO != null)
        {
            this.skyVBO.deleteGlBuffers();
        }

        if (this.glSkyList >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSkyList);
            this.glSkyList = -1;
        }

        if (this.vboEnabled)
        {
            this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
        }
        else
        {
            this.glSkyList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
            GL11.glEndList();
        }
    }

    @Override
    public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks)
    {
        int pass = net.minecraftforge.client.MinecraftForgeClient.getRenderPass();
        if (this.renderEntitiesStartupCounter > 0)
        {
            if (pass > 0) return;
            --this.renderEntitiesStartupCounter;
        }
        else
        {
            double d0 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double)partialTicks;
            double d1 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double)partialTicks;
            double d2 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double)partialTicks;
            this.theWorld.theProfiler.startSection("prepare");
            TileEntityRendererDispatcher.instance.cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), partialTicks);
            this.renderManager.cacheActiveRenderInfo(this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
            if (pass == 0) // no indentation to shrink patch
            {
                this.countEntitiesTotal = 0;
                this.countEntitiesRendered = 0;
                this.countEntitiesHidden = 0;
            }
            Entity entity1 = this.mc.getRenderViewEntity();
            double d3 = entity1.lastTickPosX + (entity1.posX - entity1.lastTickPosX) * (double)partialTicks;
            double d4 = entity1.lastTickPosY + (entity1.posY - entity1.lastTickPosY) * (double)partialTicks;
            double d5 = entity1.lastTickPosZ + (entity1.posZ - entity1.lastTickPosZ) * (double)partialTicks;
            TileEntityRendererDispatcher.staticPlayerX = d3;
            TileEntityRendererDispatcher.staticPlayerY = d4;
            TileEntityRendererDispatcher.staticPlayerZ = d5;
            this.renderManager.setRenderPosition(d3, d4, d5);
            this.mc.entityRenderer.enableLightmap();
            this.theWorld.theProfiler.endStartSection("global");
            List list = this.theWorld.getLoadedEntityList();
            if (pass == 0) // no indentation to shrink patch
            {
                this.countEntitiesTotal = list.size();
            }

            RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
            float viewY = rendermanager.playerViewY;
            rendermanager.setPlayerViewY(Blocksteps.eventHandler.angleY + 180F);

            int i;
            Entity entity2;

            for (i = 0; i < this.theWorld.weatherEffects.size(); ++i)
            {
                entity2 = (Entity)this.theWorld.weatherEffects.get(i);
                if (!entity2.shouldRenderInPass(pass)) continue;
                ++this.countEntitiesRendered;

                if (shouldRenderEntity(entity2) && entity2.isInRangeToRender3d(d0, d1, d2))
                {
                    this.renderManager.renderEntitySimple(entity2, partialTicks);
                }
            }

            this.theWorld.theProfiler.endStartSection("entities");
            Iterator iterator = this.renderInfos.iterator();
            RenderGlobal.ContainerLocalRenderInformation containerlocalrenderinformation;

            while (iterator.hasNext())
            {
                containerlocalrenderinformation = (RenderGlobal.ContainerLocalRenderInformation)iterator.next();
                Chunk chunk = this.theWorld.getChunkFromBlockCoords(containerlocalrenderinformation.renderChunk.getPosition());
                Iterator iterator2 = chunk.getEntityLists()[containerlocalrenderinformation.renderChunk.getPosition().getY() / 16].iterator();

                while (iterator2.hasNext())
                {
                    Entity entity3 = (Entity)iterator2.next();
                    if (!entity3.shouldRenderInPass(pass)) continue;
                    boolean flag2 = this.renderManager.shouldRender(entity3, camera, d0, d1, d2) || entity3.riddenByEntity == this.mc.thePlayer;

                    if (flag2)
                    {
                        if (!shouldRenderEntity(entity3) || entity3.posY >= 0.0D && entity3.posY < 256.0D && !this.theWorld.isBlockLoaded(new BlockPos(entity3)))
                        {
                            continue;
                        }

                        ++this.countEntitiesRendered;
                        this.renderManager.renderEntitySimple(entity3, partialTicks);
                    }

                    if (!flag2 && entity3 instanceof EntityWitherSkull && shouldRenderEntity(entity3))
                    {
                        this.mc.getRenderManager().renderWitherSkull(entity3, partialTicks);
                    }
                }
            }

            if(!Blocksteps.eventHandler.hideWaypoints)
            {
                ArrayList<Waypoint> points = Blocksteps.eventHandler.getWaypoints(mc.theWorld.provider.getDimensionId());
                for(Waypoint wp : points)
                {
                    if(wp.visible)
                    {
                        int clr = wp.colour;
                        if(Blocksteps.config.easterEgg == 1)
                        {
                            if(wp.name.equalsIgnoreCase("home") || wp.name.equalsIgnoreCase(mc.getSession().getUsername()) || wp.name.equalsIgnoreCase("blocksteps") || wp.name.equalsIgnoreCase("ichun") || wp.name.equalsIgnoreCase("fusionlord") || wp.name.equalsIgnoreCase("sheeppig") || wp.name.equalsIgnoreCase("sheepig"))
                            {
                                int ii = mc.thePlayer.ticksExisted / 25 + Math.abs(wp.name.hashCode());
                                int j = EnumDyeColor.values().length;
                                int k = ii % j;
                                int l = (ii + 1) % j;
                                float f7 = ((float)(mc.thePlayer.ticksExisted % 25) + partialTicks) / 25.0F;
                                float[] afloat1 = EntitySheep.func_175513_a(EnumDyeColor.byMetadata(k));
                                float[] afloat2 = EntitySheep.func_175513_a(EnumDyeColor.byMetadata(l));
                                clr = ((int)((afloat1[0] * (1.0F - f7) + afloat2[0] * f7) * 255F) << 16) + ((int)((afloat1[1] * (1.0F - f7) + afloat2[1] * f7) * 255F) << 8) + ((int)((afloat1[2] * (1.0F - f7) + afloat2[2] * f7) * 255F));
                            }
                        }
                        if(Blocksteps.config.waypointIndicator == 1)
                        {
                            if(wp.beam)
                            {
                                Tessellator tessellator = Tessellator.getInstance();
                                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                                double j = 0;
                                double l = 256;
                                if(Blocksteps.config.waypointBeamHeightAdjust > 0)
                                {
                                    double dist = mc.thePlayer.getDistance(wp.pos.getX() + 0.5D, wp.pos.getY(), wp.pos.getZ() + 0.5D);
                                    if(dist < 256D)
                                    {
                                        l = Math.max(Blocksteps.config.waypointBeamHeightAdjust, dist);
                                    }
                                }
                                double x = wp.pos.getX() - renderManager.renderPosX;
                                double y = wp.pos.getY() - (l / 2D) - renderManager.renderPosY;
                                double z = wp.pos.getZ() - renderManager.renderPosZ;
                                Minecraft.getMinecraft().getTextureManager().bindTexture(texBeaconBeam);
                                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
                                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
                                GlStateManager.disableLighting();
                                GlStateManager.disableCull();
                                GlStateManager.disableBlend();
                                GlStateManager.depthMask(true);
                                GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
                                float f2;
                                if(wp.entityInstance != null)
                                {
                                    f2 = (wp.entityInstance.getEntityId() + mc.thePlayer.ticksExisted + partialTicks) * 0.4F;
                                }
                                else
                                {
                                    f2 = (float)mc.theWorld.getTotalWorldTime() + partialTicks;
                                }
                                float f3 = -f2 * 0.2F - (float)MathHelper.floor_float(-f2 * 0.1F);
                                double dd3 = (double)f2 * 0.025D * -1.5D;
                                worldrenderer.startDrawingQuads();
                                double dd4 = 0.1D;
                                double dd5 = 0.5D + Math.cos(dd3 + 2.356194490192345D) * dd4;
                                double d6 = 0.5D + Math.sin(dd3 + 2.356194490192345D) * dd4;
                                double d7 = 0.5D + Math.cos(dd3 + (Math.PI / 4D)) * dd4;
                                double d8 = 0.5D + Math.sin(dd3 + (Math.PI / 4D)) * dd4;
                                double d9 = 0.5D + Math.cos(dd3 + 3.9269908169872414D) * dd4;
                                double d10 = 0.5D + Math.sin(dd3 + 3.9269908169872414D) * dd4;
                                double d11 = 0.5D + Math.cos(dd3 + 5.497787143782138D) * dd4;
                                double d12 = 0.5D + Math.sin(dd3 + 5.497787143782138D) * dd4;
                                double d13 = 0.0D;
                                double d14 = 1.0D;
                                double d15 = (double)(-1.0F + f3);
                                double d16 = (double)((float)256) * (0.5D / dd4) + d15;
                                float r = (clr >> 16 & 0xff) / 255F;
                                float g = (clr >> 8 & 0xff) / 255F;
                                float b = (clr & 0xff) / 255F;
                                worldrenderer.setColorRGBA_F(r, g, b, 0.125F);
                                worldrenderer.addVertexWithUV(x + dd5, y + (double)l, z + d6, d14, d16);
                                worldrenderer.addVertexWithUV(x + dd5, y + (double)j, z + d6, d14, d15);
                                worldrenderer.addVertexWithUV(x + d7, y + (double)j, z + d8, d13, d15);
                                worldrenderer.addVertexWithUV(x + d7, y + (double)l, z + d8, d13, d16);
                                worldrenderer.addVertexWithUV(x + d11, y + (double)l, z + d12, d14, d16);
                                worldrenderer.addVertexWithUV(x + d11, y + (double)j, z + d12, d14, d15);
                                worldrenderer.addVertexWithUV(x + d9, y + (double)j, z + d10, d13, d15);
                                worldrenderer.addVertexWithUV(x + d9, y + (double)l, z + d10, d13, d16);
                                worldrenderer.addVertexWithUV(x + d7, y + (double)l, z + d8, d14, d16);
                                worldrenderer.addVertexWithUV(x + d7, y + (double)j, z + d8, d14, d15);
                                worldrenderer.addVertexWithUV(x + d11, y + (double)j, z + d12, d13, d15);
                                worldrenderer.addVertexWithUV(x + d11, y + (double)l, z + d12, d13, d16);
                                worldrenderer.addVertexWithUV(x + d9, y + (double)l, z + d10, d14, d16);
                                worldrenderer.addVertexWithUV(x + d9, y + (double)j, z + d10, d14, d15);
                                worldrenderer.addVertexWithUV(x + dd5, y + (double)j, z + d6, d13, d15);
                                worldrenderer.addVertexWithUV(x + dd5, y + (double)l, z + d6, d13, d16);
                                tessellator.draw();
                                GlStateManager.enableBlend();
                                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                                GlStateManager.depthMask(false);
                                worldrenderer.startDrawingQuads();
                                worldrenderer.setColorRGBA_F(r, g, b, 0.125F);
                                dd3 = 0.3D;
                                dd4 = 0.3D;
                                dd5 = 0.7D;
                                d6 = 0.3D;
                                d7 = 0.3D;
                                d8 = 0.7D;
                                d9 = 0.7D;
                                d10 = 0.7D;
                                d11 = 0.0D;
                                d12 = 1.0D;
                                d13 = (double)(-1.0F + f3);
                                d14 = (double)((float)256) + d13;
                                worldrenderer.addVertexWithUV(x + dd3, y + (double)l, z + dd4, d12, d14);
                                worldrenderer.addVertexWithUV(x + dd3, y + (double)j, z + dd4, d12, d13);
                                worldrenderer.addVertexWithUV(x + dd5, y + (double)j, z + d6, d11, d13);
                                worldrenderer.addVertexWithUV(x + dd5, y + (double)l, z + d6, d11, d14);
                                worldrenderer.addVertexWithUV(x + d9, y + (double)l, z + d10, d12, d14);
                                worldrenderer.addVertexWithUV(x + d9, y + (double)j, z + d10, d12, d13);
                                worldrenderer.addVertexWithUV(x + d7, y + (double)j, z + d8, d11, d13);
                                worldrenderer.addVertexWithUV(x + d7, y + (double)l, z + d8, d11, d14);
                                worldrenderer.addVertexWithUV(x + dd5, y + (double)l, z + d6, d12, d14);
                                worldrenderer.addVertexWithUV(x + dd5, y + (double)j, z + d6, d12, d13);
                                worldrenderer.addVertexWithUV(x + d9, y + (double)j, z + d10, d11, d13);
                                worldrenderer.addVertexWithUV(x + d9, y + (double)l, z + d10, d11, d14);
                                worldrenderer.addVertexWithUV(x + d7, y + (double)l, z + d8, d12, d14);
                                worldrenderer.addVertexWithUV(x + d7, y + (double)j, z + d8, d12, d13);
                                worldrenderer.addVertexWithUV(x + dd3, y + (double)j, z + dd4, d11, d13);
                                worldrenderer.addVertexWithUV(x + dd3, y + (double)l, z + dd4, d11, d14);
                                tessellator.draw();
                                GlStateManager.enableLighting();
                                GlStateManager.enableTexture2D();
                                GlStateManager.depthMask(true);
                            }
                            if(wp.entityInstance == null)
                            {
                                try
                                {
                                    if(wp.name.equalsIgnoreCase("sheeppig") || wp.name.equalsIgnoreCase("sheepig"))
                                    {
                                        wp.entityInstance = new EntityPig(mc.theWorld);
                                        EntityPig pig = (EntityPig)wp.entityInstance;
                                        pig.setCustomNameTag("iChun");
                                        pig.enablePersistence();
                                    }
                                    else if(!wp.entityType.isEmpty())
                                    {
                                        wp.entityInstance = (Entity)Class.forName(wp.entityType).getConstructor(World.class).newInstance(mc.theWorld);
                                    }
                                    else
                                    {
                                        wp.entityInstance = new EntityWaypoint(mc.theWorld);
                                    }
                                    wp.entityInstance.setLocationAndAngles(wp.pos.getX() + 0.5D, wp.pos.getY(), wp.pos.getZ() + 0.5D, 0F, 0F);
                                }
                                catch(Exception e)
                                {
                                    Blocksteps.logger.warn("Error creating waypoint indicator for waypoint " + wp.name + " with type " + wp.entityType);
                                    e.printStackTrace();
                                }
                            }
                            if(wp.entityInstance != null)
                            {
                                double dd0 = wp.entityInstance.lastTickPosX + (wp.entityInstance.posX - wp.entityInstance.lastTickPosX) * (double)partialTicks;
                                double dd1 = wp.entityInstance.lastTickPosY + (wp.entityInstance.posY - wp.entityInstance.lastTickPosY) * (double)partialTicks;
                                double dd2 = wp.entityInstance.lastTickPosZ + (wp.entityInstance.posZ - wp.entityInstance.lastTickPosZ) * (double)partialTicks;
                                float ff1 = wp.entityInstance.prevRotationYaw + (wp.entityInstance.rotationYaw - wp.entityInstance.prevRotationYaw) * partialTicks;

                                RendererHelper.setColorFromInt(clr);
                                GlStateManager.disableLighting();
                                int j = 15728880 % 65536;
                                int k = 15728880 / 65536;
                                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);

                                GlStateManager.pushMatrix();
                                GlStateManager.translate(dd0 - renderManager.renderPosX, dd1 - renderManager.renderPosY + 0.2F, dd2 - renderManager.renderPosZ);
                                if(wp.beam && (wp.name.equalsIgnoreCase("sheeppig") || wp.name.equalsIgnoreCase("sheepig") || !wp.entityType.isEmpty()))
                                {
                                    GlStateManager.rotate((wp.entityInstance.getEntityId() + mc.thePlayer.ticksExisted + partialTicks) % 400F / 400F * 360F, 0F, 1F, 0F);
                                    if(!(wp.entityInstance instanceof EntityDragon))
                                    {
                                        GlStateManager.translate(0F, 0F, wp.entityInstance.width / 2F);
                                    }
                                    GlStateManager.rotate(-10F, 1F, 0F, 0F);
                                }
                                GlStateManager.scale(0.5D, 0.5D, 0.5D);

                                if(Blocksteps.config.waypointIndicatorThroughBlocks == 1)
                                {
                                    GlStateManager.depthMask(false);
                                    GlStateManager.disableDepth();
                                }

                                EntityHelperBase.storeBossStatus();

                                if(wp.entityInstance instanceof EntityDragon)
                                {
                                    GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);
                                }
                                try
                                {
                                    this.renderManager.doRenderEntity(wp.entityInstance, 0D, 0D, 0D, ff1, partialTicks, false);
                                }
                                catch(Exception ignored){}
                                if(wp.entityInstance instanceof EntityDragon)
                                {
                                    GlStateManager.rotate(180F, 0.0F, -1.0F, 0.0F);
                                }

                                EntityHelperBase.restoreBossStatus();

                                if(Blocksteps.config.waypointIndicatorThroughBlocks == 1)
                                {
                                    GlStateManager.enableDepth();
                                    GlStateManager.depthMask(true);
                                }

                                GlStateManager.popMatrix();

                                GlStateManager.enableLighting();
                            }
                        }
                        String str = wp.name;
                        FontRenderer fontrenderer = renderManager.getFontRenderer();
                        float f = 1.6F;
                        float f1 = 0.016666668F * f;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(wp.pos.getX() + 0.5D - renderManager.renderPosX, wp.pos.getY() - renderManager.renderPosY, wp.pos.getZ() + 0.5D - renderManager.renderPosZ);
                        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(Blocksteps.eventHandler.angleX, 1.0F, 0.0F, 0.0F);
                        GlStateManager.scale(-f1, -f1, f1);
                        GlStateManager.disableLighting();
                        if(Blocksteps.config.waypointLabelThroughBlocks == 1)
                        {
                            GlStateManager.depthMask(false);
                            GlStateManager.disableDepth();
                        }
                        GlStateManager.enableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        Tessellator tessellator = Tessellator.getInstance();
                        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                        byte b0 = 0;
                        GlStateManager.disableTexture2D();
                        worldrenderer.startDrawingQuads();
                        int j = fontrenderer.getStringWidth(str) / 2;
                        worldrenderer.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                        worldrenderer.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
                        worldrenderer.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
                        worldrenderer.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
                        worldrenderer.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
                        tessellator.draw();
                        GlStateManager.enableTexture2D();
                        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, b0, clr);
                        if(wp.showDistance)
                        {
                            b0 = -9;
                            str = String.format(Locale.ENGLISH, "%.2f", mc.thePlayer.getDistance(wp.pos.getX() + 0.5D, wp.pos.getY(), wp.pos.getZ() + 0.5D)) + "m";
                            GlStateManager.disableTexture2D();
                            worldrenderer.startDrawingQuads();
                            j = fontrenderer.getStringWidth(str) / 2;
                            worldrenderer.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                            worldrenderer.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
                            worldrenderer.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
                            worldrenderer.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
                            worldrenderer.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
                            tessellator.draw();
                            GlStateManager.enableTexture2D();
                            fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, b0, clr);
                        }
                        if(Blocksteps.config.waypointLabelThroughBlocks == 1)
                        {
                            GlStateManager.enableDepth();
                            GlStateManager.depthMask(true);
                        }
                        GlStateManager.enableLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.popMatrix();
                    }
                }
            }

            rendermanager.setPlayerViewY(viewY);

            this.theWorld.theProfiler.endStartSection("blockentities");
            RenderHelper.enableStandardItemLighting();
            iterator = this.renderInfos.iterator();
            TileEntity tileentity;

            while (iterator.hasNext())
            {
                containerlocalrenderinformation = (RenderGlobal.ContainerLocalRenderInformation)iterator.next();
                Iterator iterator1 = containerlocalrenderinformation.renderChunk.getCompiledChunk().getTileEntities().iterator();

                while (iterator1.hasNext())
                {
                    tileentity = (TileEntity)iterator1.next();
                    if (!tileentity.shouldRenderInPass(pass) || !camera.isBoundingBoxInFrustum(tileentity.getRenderBoundingBox())) continue;
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileentity, partialTicks, -1);
                }
            }

            this.preRenderDamagedBlocks();
            iterator = this.damagedBlocks.values().iterator();

            while (iterator.hasNext())
            {
                DestroyBlockProgress destroyblockprogress = (DestroyBlockProgress)iterator.next();
                BlockPos blockpos = destroyblockprogress.getPosition();
                tileentity = this.theWorld.getTileEntity(blockpos);

                if (tileentity instanceof TileEntityChest)
                {
                    TileEntityChest tileentitychest = (TileEntityChest)tileentity;

                    if (tileentitychest.adjacentChestXNeg != null)
                    {
                        blockpos = blockpos.offset(EnumFacing.WEST);
                        tileentity = this.theWorld.getTileEntity(blockpos);
                    }
                    else if (tileentitychest.adjacentChestZNeg != null)
                    {
                        blockpos = blockpos.offset(EnumFacing.NORTH);
                        tileentity = this.theWorld.getTileEntity(blockpos);
                    }
                }

                Block block = this.theWorld.getBlockState(blockpos).getBlock();

                if (tileentity != null && tileentity.shouldRenderInPass(pass) && tileentity.canRenderBreaking() && camera.isBoundingBoxInFrustum(tileentity.getRenderBoundingBox()))
                {
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileentity, partialTicks, destroyblockprogress.getPartialBlockDamage());
                }
            }

            this.postRenderDamagedBlocks();
            this.mc.entityRenderer.disableLightmap();
            this.mc.mcProfiler.endSection();
        }
    }

    public boolean shouldRenderEntity(Entity entity)
    {
        BlockPos pos = new BlockPos(entity);

        Minecraft mc = Minecraft.getMinecraft();

        if(entity == mc.getRenderViewEntity() || entity instanceof EntityPlayer && Blocksteps.config.trackOtherPlayers == 1 || mc.getRenderViewEntity() != null && ((entity == mc.getRenderViewEntity().riddenByEntity || entity == mc.getRenderViewEntity().ridingEntity) || mc.getRenderViewEntity().ridingEntity != null && entity == mc.getRenderViewEntity().ridingEntity.ridingEntity))
        {
            return true;
        }

        return Blocksteps.config.mapShowEntities == 1 && (Blocksteps.eventHandler.blocksToRender.contains(pos) || Blocksteps.eventHandler.blocksToRender.contains(pos.add(0, -1, 0)) || Blocksteps.eventHandler.blocksToRender.contains(pos.add(0, -2, 0)));
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
