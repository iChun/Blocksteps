package me.ichun.mods.blocksteps.common.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.blockaid.BlockStepHandler;
import me.ichun.mods.blocksteps.common.blockaid.CheckBlockInfo;
import me.ichun.mods.blocksteps.common.gui.GuiWaypoints;
import me.ichun.mods.blocksteps.common.layer.LayerSheepPig;
import me.ichun.mods.blocksteps.common.render.RenderGlobalProxy;
import me.ichun.mods.blocksteps.common.thread.ThreadBlockCrawler;
import me.ichun.mods.blocksteps.common.thread.ThreadCheckBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import us.ichun.mods.ichunutil.client.keybind.KeyEvent;
import us.ichun.mods.ichunutil.client.render.RendererHelper;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;
import us.ichun.mods.ichunutil.common.core.event.RenderAtPlayerEvent;
import us.ichun.mods.ichunutil.common.core.event.RendererSafeCompatibilityEvent;
import us.ichun.mods.ichunutil.common.core.util.IOUtil;
import us.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class EventHandler
{
    @SubscribeEvent
    public void onRendererSafeCompatibility(RendererSafeCompatibilityEvent event)
    {
        RenderPig renderPig = (RenderPig)Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(EntityPig.class);
        renderPig.addLayer(new LayerSheepPig(renderPig));
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(event.phase == TickEvent.Phase.END)
        {
            if(mc.thePlayer != null && !mc.gameSettings.hideGUI && (mc.currentScreen == null || mc.currentScreen instanceof GuiChat))
            {
                ScaledResolution reso = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                float aScale = EntityHelperBase.interpolateValues(prevScale, scale, event.renderTickTime) / 100F;

                if(aScale > 0.0001F)
                {
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GlStateManager.enableAlpha();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.00625F);
                    int x = (int)(reso.getScaledWidth_double() * (Blocksteps.config.mapStartX / 100D));
                    int y = (int)(reso.getScaledHeight_double() * (Blocksteps.config.mapStartY / 100D));
                    int width = (int)(reso.getScaledWidth_double() * ((Blocksteps.config.mapEndX - Blocksteps.config.mapStartX) / 100D));
                    int height = (int)(reso.getScaledHeight_double() * ((Blocksteps.config.mapEndY - Blocksteps.config.mapStartY) / 100D));

                    float fsProg = MathHelper.clamp_float((fullscreenTimeout - event.renderTickTime) / 3F, 0F, 1F);
                    if(fullscreen)
                    {
                        fsProg = 1.0F - fsProg;
                    }
                    fsProg = (float)Math.pow(fsProg, 0.5D);
                    double fsX = (reso.getScaledWidth_double() * 0.01D) - x;
                    double fsY = (reso.getScaledHeight_double() * 0.02D) - y;
                    double fsWidth = (reso.getScaledWidth_double() * 0.98D) - width;
                    double fsHeight = (reso.getScaledHeight_double() * 0.96D) - height;

                    x += fsX * fsProg;
                    y += fsY * fsProg;
                    width += fsWidth * fsProg;
                    height += fsHeight * fsProg;

                    float alphaAmp = MathHelper.clamp_float(aScale / 0.1F, 0F, 1F);

                    GlStateManager.depthMask(false);
                    GlStateManager.disableDepth();

                    if(Blocksteps.config.mapBackgroundOpacity > 0)
                    {
                        RendererHelper.drawColourOnScreen(Blocksteps.config.mapBackgroundColour.getColour(), (int)((float)Blocksteps.config.mapBackgroundOpacity / 100F * 255F * alphaAmp), x, y, width, height, -200D);
                    }
                    if(mc.thePlayer.ticksExisted < Blocksteps.config.mapLoad)
                    {
                        float prog = MathHelper.clamp_float((mc.thePlayer.ticksExisted + event.renderTickTime) / (float)Blocksteps.config.mapLoad, 0F, 1F);
                        RendererHelper.drawColourOnScreen(170, 170, 170, (int)((float)Blocksteps.config.mapBackgroundOpacity / 100F * 255F * alphaAmp), x, y + (height * 0.95D), width * prog, (height * 0.05D), -200D);
                    }
                    if(Blocksteps.config.mapBorderOpacity > 0)
                    {
                        int borderOpacity = (int)((float)Blocksteps.config.mapBorderOpacity / 100F * 255F * alphaAmp);
                        int size = Blocksteps.config.mapBorderSize;
                        if(Blocksteps.config.mapBorderOutline == 1)
                        {
                            size += 1;
                            RendererHelper.drawColourOnScreen(Blocksteps.config.mapBorderOutlineColour.getColour(), borderOpacity, x - size, y - size, size, height + (size * 2D), 0D);
                            RendererHelper.drawColourOnScreen(Blocksteps.config.mapBorderOutlineColour.getColour(), borderOpacity, x, y - size, width, size, 0D);
                            RendererHelper.drawColourOnScreen(Blocksteps.config.mapBorderOutlineColour.getColour(), borderOpacity, x + width, y - size, size, height + (size * 2D), 0D);
                            RendererHelper.drawColourOnScreen(Blocksteps.config.mapBorderOutlineColour.getColour(), borderOpacity, x, y + height, width, size, 0D);
                            size -= 1;
                            RendererHelper.drawColourOnScreen(Blocksteps.config.mapBorderColour.getColour(), borderOpacity, x - size - 0.5D, y - size - 0.5D, size, height + (size * 2D) + 1, 0D);
                            RendererHelper.drawColourOnScreen(Blocksteps.config.mapBorderColour.getColour(), borderOpacity, x - 0.5D, y - size - 0.5D, width + 1D, size, 0D);
                            RendererHelper.drawColourOnScreen(Blocksteps.config.mapBorderColour.getColour(), borderOpacity, x + width + 0.5D, y - size - 0.5D, size, height + (size * 2D) + 1, 0D);
                            RendererHelper.drawColourOnScreen(Blocksteps.config.mapBorderColour.getColour(), borderOpacity, x - 0.5D, y + height + 0.5D, width + 1D, size, 0D);
                        }
                        else
                        {
                            RendererHelper.drawColourOnScreen(Blocksteps.config.mapBorderColour.getColour(), borderOpacity, x - size, y - size, size, height + (size * 2D), 0D);
                            RendererHelper.drawColourOnScreen(Blocksteps.config.mapBorderColour.getColour(), borderOpacity, x, y - size, width, size, 0D);
                            RendererHelper.drawColourOnScreen(Blocksteps.config.mapBorderColour.getColour(), borderOpacity, x + width, y - size, size, height + (size * 2D), 0D);
                            RendererHelper.drawColourOnScreen(Blocksteps.config.mapBorderColour.getColour(), borderOpacity, x, y + height, width, size, 0D);
                        }
                    }
                    GlStateManager.enableDepth();
                    GlStateManager.depthMask(true);

                    RendererHelper.startGlScissor(x, y, width, height);
                    if(framebuffer == null || Blocksteps.config.mapFreq == 0)
                    {
                        renderingMinimap = true;
                        drawMap(mc, reso, x, y, width, height, aScale, event.renderTickTime);
                        renderingMinimap = false;
                    }
                    else
                    {
                        GlStateManager.enableTexture2D();
                        GlStateManager.disableLighting();
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                        GlStateManager.enableColorMaterial();

                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                        framebuffer.bindFramebufferTexture();
                        double zLevel = 900D;
                        Tessellator tessellator = Tessellator.getInstance();
                        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                        worldrenderer.startDrawingQuads();
                        worldrenderer.addVertexWithUV(0, reso.getScaledHeight_double(), zLevel, 0.0D, 0.0D);
                        worldrenderer.addVertexWithUV(reso.getScaledWidth_double(), reso.getScaledHeight_double(), zLevel, 1.0D, 0.0D);
                        worldrenderer.addVertexWithUV(reso.getScaledWidth_double(), 0, zLevel, 1.0D, 1.0D);
                        worldrenderer.addVertexWithUV(0, 0, zLevel, 0.0D, 1.0D);
                        tessellator.draw();
                        framebuffer.unbindFramebufferTexture();

                        GlStateManager.enableAlpha();

                        if((float)rendersThisSecond / (float)Blocksteps.config.mapFreq < ((float)(iChunUtil.proxy.tickHandlerClient.ticks - clock) + event.renderTickTime) / 20F)
                        {
                            lastRenderTick = event.renderTickTime;
                            rendersThisSecond++;

                            framebuffer.bindFramebuffer(true);

                            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

                            renderingMinimap = true;
                            drawMap(mc, reso, x, y, width, height, aScale, event.renderTickTime);
                            renderingMinimap = false;

                            mc.getFramebuffer().bindFramebuffer(true);
                        }
                    }
                    RendererHelper.endGlScissor();

                    if(Blocksteps.config.mapShowCoordinates == 1 || mc.gameSettings.showDebugInfo || mapTypeTimeout > 0 || waypointTimeout > 0)
                    {
                        GlStateManager.depthMask(false);
                        GlStateManager.disableDepth();
                        if(Blocksteps.config.mapShowCoordinates == 1 || mapTypeTimeout > 0 || waypointTimeout > 0)
                        {
                            GlStateManager.pushMatrix();
                            float scale = 0.5F;
                            GlStateManager.scale(scale, scale, scale);
                            int count = 0;
                            if(mapTypeTimeout > 0)
                            {
                                mc.fontRendererObj.drawString(StatCollector.translateToLocal("blocksteps.mapType.type") + StatCollector.translateToLocal(Blocksteps.config.mapType == 1 ? "blocksteps.mapType.blocksteps" : Blocksteps.config.mapType == 2 ? "blocksteps.mapType.surface" : "blocksteps.mapType.threedee"), (int)((x + 2) / scale), (int)((y + 2 + (7 * count++)) / scale), 0xffffff);
                            }
                            if(waypointTimeout > 0)
                            {
                                mc.fontRendererObj.drawString(StatCollector.translateToLocal(hideWaypoints ? "blocksteps.waypoint.hide" : "blocksteps.waypoint.show"), (int)((x + 2) / scale), (int)((y + 2 + (7 * count++)) / scale), 0xffffff);
                            }
                            if(Blocksteps.config.mapShowCoordinates == 1)
                            {
                                mc.fontRendererObj.drawString("X:" + String.format(Locale.ENGLISH, "%.2f", mc.thePlayer.posX) + " Y:" + String.format(Locale.ENGLISH, "%.2f", mc.thePlayer.posY) + " Z:" + String.format(Locale.ENGLISH, "%.2f", mc.thePlayer.posZ), (int)((x + 2) / scale), (int)((y + height - 1) / scale) - mc.fontRendererObj.FONT_HEIGHT, 0xffffff);
                            }
                            GlStateManager.popMatrix();
                        }
                        if(mc.gameSettings.showDebugInfo)
                        {
                            int count = 0;
                            mc.fontRendererObj.drawString("Steps loaded: " + getSteps(mc.theWorld.provider.getDimensionId()).size(), x + 2, y + 2 + (10 * count++), 0xffffff);
                            mc.fontRendererObj.drawString("Waypoints: " + getWaypoints(mc.theWorld.provider.getDimensionId()).size(), x + 2, y + 2 + (10 * count++), 0xffffff);
                            mc.fontRendererObj.drawString(!hideWaypoints ? "Showing waypoints" : "Hiding waypoints", x + 2, y + 2 + (10 * count++), 0xffffff);
                            mc.fontRendererObj.drawString("Map Type: " + Blocksteps.config.mapType, x + 2, y + 2 + (10 * count++), 0xffffff);


                            //                        RendererHelper.drawGradientOnScreen(0xff000000, 0xff000000, 0xffffffff, Color.HSBtoRGB((mc.thePlayer.ticksExisted + event.renderTickTime % 100) / 100F, 1F, 1F), x, y, height, height, 0D);
                            //                        RendererHelper.drawGradientOnScreen(0xff0000ff, 0xff0000ff, 0xffff0000, 0xffff0000, x, y, height, height / 3D, 0D);
                            //                        RendererHelper.drawGradientOnScreen(0xff00ff00, 0xff00ff00, 0xff0000ff, 0xff0000ff, x, y + height / 3D, height, height / 3D, 0D);
                            //                        RendererHelper.drawGradientOnScreen(0xffff0000, 0xffff0000, 0xff00ff00, 0xff00ff00, x, y + height / 3D + height / 3D, height, height / 3D, 0D);
                            //                        RendererHelper.drawHueStripOnScreen(255, x, y, height, height, 0D);
                        }
                        GlStateManager.enableDepth();
                        GlStateManager.depthMask(true);
                    }

                    if(Blocksteps.config.renderCompass == 1)
                    {
                        GlStateManager.depthMask(false);
                        GlStateManager.disableDepth();
                        GlStateManager.disableLighting();

                        GlStateManager.enableColorMaterial();
                        GlStateManager.pushMatrix();
                        GlStateManager.translate((float)x + width - 6F, (float)y + height - 6F, 50.0F);
                        float arrowScale = 8F * alphaAmp;
                        GlStateManager.scale(-arrowScale, arrowScale, arrowScale);
                        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                        GlStateManager.rotate(EntityHelperBase.interpolateRotation(prevAngleX, angleX, event.renderTickTime), 1.0F, 0.0F, 0.0F);
                        GlStateManager.rotate(EntityHelperBase.interpolateRotation(prevAngleY, angleY, event.renderTickTime), 0.0F, 1.0F, 0.0F);
                        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
                        rendermanager.renderEntityWithPosYaw(arrowCompass, 0.0D, 0.0D, 0.0D, 0.0F, event.renderTickTime);
                        GlStateManager.popMatrix();
                        GlStateManager.disableRescaleNormal();
                        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                        GlStateManager.disableTexture2D();
                        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

                        GlStateManager.enableLighting();
                        GlStateManager.enableDepth();
                        GlStateManager.depthMask(true);
                    }

                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                    GlStateManager.disableAlpha();
                }
            }

            synchronized(blocksToAdd)
            {
                if(!blocksToAdd.isEmpty())
                {
                    for(CheckBlockInfo info : blocksToAdd)
                    {
                        if(info.world == mc.theWorld)
                        {
                            if(renderGlobalProxy != null)
                            {
                                for(BlockPos pos : info.blocksToRender)
                                {
                                    renderGlobalProxy.markBlockForUpdate(pos);
                                }
                                repopulateBlocksToRender = true;
                            }
                            blocksToRenderByStep.get(info.oriPos).addAll(info.blocksToRender);
                        }
                    }
                    blocksToAdd.clear();
                }
            }
        }
        else
        {
            if(renderGlobalProxy != null && renderGlobalProxy.theWorld != null && mc.theWorld == null)
            {
                if(saveLocation != null)
                {
                    saveLocation.getParentFile().mkdirs();
                    try
                    {
                        FileOutputStream stream = new FileOutputStream(saveLocation);
                        stream.write(IOUtil.compress((new Gson()).toJson(MapSaveFile.create())));
                        stream.close();
                    }
                    catch(Exception e)
                    {
                        Blocksteps.logger.warn("Error saving file: " + saveLocation);
                    }

                    saveLocation = null;
                }
                setNewWorld(null);
                steps.clear();
                blocksToRenderByStep.clear();
                ChunkStore.clear();
                synchronized(Blocksteps.eventHandler.threadCheckBlocks.checks)
                {
                    Blocksteps.eventHandler.threadCheckBlocks.checks.clear();
                }
            }
            if(mc.theWorld != null)
            {
                if(mc.currentScreen instanceof GuiIngameMenu && fullscreen)
                {
                    mc.displayGuiScreen(null);
                    fullscreen = false;
                    fullscreenTimeout = 3;
                    targetOffsetX = targetOffsetY = prevOffsetX = prevOffsetY = offsetX = offsetY = 0F;
                }

                if(attemptLocalLoad && mc.theWorld.getSpawnPoint().hashCode() != 9680 && saveLocation == null)//9680 default world spawn.
                {
                    attemptLocalLoad = false;
                    saveLocation = new File(new File(ResourceHelper.getModsFolder(), "/blocksteps/local/"), mc.theWorld.getSpawnPoint().hashCode() + ".bsv");
                    if(saveLocation.exists())
                    {
                        try
                        {
                            byte[] data = new byte[(int)saveLocation.length()];
                            FileInputStream stream = new FileInputStream(saveLocation);
                            stream.read(data);
                            stream.close();
                            MapSaveFile save = (new Gson()).fromJson(IOUtil.decompress(data), MapSaveFile.class);
                            save.load();
                        }
                        catch(Exception e)
                        {
                            Blocksteps.logger.warn("Error loading save file: " + saveLocation);
                        }
                        ;
                    }
                }
            }
        }
    }

    public void drawMap(Minecraft mc, ScaledResolution reso, int x, int y, int width, int height, float aScale, float partialTicks)
    {
        double posX = x + width * (float)Blocksteps.config.camPosX / 100F;
        double posY = y + height * (float)Blocksteps.config.camPosY / 100F;

        EntityLivingBase ent = mc.thePlayer;
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 300.0F);
        if(fullscreen)
        {
            GlStateManager.translate(EntityHelperBase.interpolateValues(prevOffsetX, offsetX, partialTicks), EntityHelperBase.interpolateValues(prevOffsetY, offsetY, partialTicks), 0F);
        }
        GlStateManager.scale(-aScale, aScale, aScale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        GlStateManager.rotate(EntityHelperBase.interpolateRotation(prevAngleX, angleX, partialTicks), 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(EntityHelperBase.interpolateRotation(prevAngleY, angleY, partialTicks), 0.0F, 1.0F, 0.0F);

        GlStateManager.translate(0, -mc.thePlayer.getEyeHeight(), 0F);

        boolean hideGui = mc.gameSettings.hideGUI;
        mc.gameSettings.hideGUI = Blocksteps.config.hideNamePlates == 1;
        renderWorld(mc, partialTicks);
        mc.gameSettings.hideGUI = hideGui;

        GlStateManager.enableLighting();
        GlStateManager.enableNormalize();

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public void renderWorld(Minecraft mc, float partialTicks)
    {
        if(renderGlobalProxy != null)
        {
            //            RendererHelper.startGlScissor(0, 0, 1, 1);
            //                        RenderGlobal ori = mc.renderGlobal;
            //                        mc.renderGlobal = renderGlobalProxy;
            //                        mc.entityRenderer.renderWorldPass(2, partialTicks, 0L);
            //                        mc.renderGlobal = ori;
            //            RendererHelper.endGlScissor();

            RenderGlobal renderglobal = renderGlobalProxy;
            Entity entity = mc.getRenderViewEntity();
            int pass = 2;

            PotionEffect effect = null;

            if(Blocksteps.config.brightMap == 1)
            {
                effect = mc.thePlayer.getActivePotionEffect(Potion.nightVision);
                mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 10, 1000));
                mc.entityRenderer.updateTorchFlicker();
                mc.entityRenderer.updateLightmap(1.0F);
            }

            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            Frustum frustum = new Frustum();
            double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
            double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
            double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
            frustum.setPosition(d0, d1, d2);

            if(Blocksteps.config.renderSky == 1)
            {
                float aScale = 0.5F;
                GlStateManager.disableCull();
                GlStateManager.pushMatrix();
                GlStateManager.scale(aScale, aScale, aScale);
                renderglobal.renderSky(partialTicks, pass);
                GlStateManager.disableFog();
                GlStateManager.popMatrix();
                GlStateManager.enableCull();
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            }

            GlStateManager.color(1F, 1F, 1F, 1F);
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            ((RenderGlobalProxy)renderglobal).setupTerrain = renderglobal.renderDistanceChunks == Blocksteps.config.renderDistance;
            renderglobal.setupTerrain(entity, (double)partialTicks, frustum, frameCount++, mc.thePlayer.isSpectator());
            ((RenderGlobalProxy)renderglobal).setupTerrain = false;
            int i = Math.max(Minecraft.getDebugFPS(), 30);
            renderglobal.updateChunks(System.nanoTime() + (long)(1000000000 / i));
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            renderglobal.renderBlockLayer(EnumWorldBlockLayer.SOLID, (double)partialTicks, pass, entity);
            GlStateManager.enableAlpha();
            renderglobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, (double)partialTicks, pass, entity);
            mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
            renderglobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, (double)partialTicks, pass, entity);
            mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();

            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
            renderglobal.renderEntities(entity, frustum, partialTicks);
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
            RenderHelper.disableStandardItemLighting();
            mc.entityRenderer.disableLightmap();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();

            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(GL11.GL_FLAT);

            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            renderglobal.renderWorldBorder(entity, partialTicks);
            GlStateManager.enableCull();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);

            if (mc.gameSettings.fancyGraphics)
            {
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                renderglobal.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, (double)partialTicks, pass, entity);
                GlStateManager.disableBlend();
            }
            else
            {
                renderglobal.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, (double)partialTicks, pass, entity);
            }

            GlStateManager.shadeModel(GL11.GL_FLAT);

            if(Blocksteps.config.brightMap == 1)
            {
                mc.thePlayer.removePotionEffectClient(Potion.nightVision.getId());
                mc.entityRenderer.updateTorchFlicker();
                mc.entityRenderer.updateLightmap(1.0F);
                if(effect != null)
                {
                    mc.thePlayer.addPotionEffect(effect);
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.theWorld != null)
            {
                prevAngleX = angleX;
                prevAngleY = angleY;
                prevScale = scale;
                prevOffsetX = offsetX;
                prevOffsetY = offsetY;

                angleX = angleX + (targetAngleX - angleX) * 0.4F;
                angleY = angleY + (targetAngleY - angleY) * 0.4F;
                scale = scale + (targetScale - scale) * 0.4F;
                offsetX = offsetX + (targetOffsetX - offsetX) * 0.4F;
                offsetY = offsetY + (targetOffsetY - offsetY) * 0.4F;

                if(!fullscreen)
                {
                    if(Blocksteps.config.lockMapToHeadYaw == 1)
                    {
                        targetAngleY = angleY = mc.getRenderViewEntity().rotationYaw + 180F;
                    }
                    if(Blocksteps.config.lockMapToHeadPitch == 1)
                    {
                        targetAngleX = angleX = mc.getRenderViewEntity().rotationPitch;
                    }
                }

                if(renderGlobalProxy != null && (Math.abs(targetAngleX - angleX) > 0.01D || Math.abs(targetAngleY - angleY) > 0.01D || Math.abs(targetScale - scale) > 0.01D))
                {
                    renderGlobalProxy.lastViewEntityPitch += 0.001F;
                }

                List<BlockPos> steps = getSteps(mc.theWorld.provider.getDimensionId());
                while(steps.size() > Blocksteps.config.renderBlockCount)
                {
                    BlockPos pos = steps.get(0);
                    steps.remove(0);
                    if(renderGlobalProxy != null && !steps.contains(pos))
                    {
                        renderGlobalProxy.markBlockForUpdate(pos);
                        repopulateBlocksToRender = true;
                        blocksToRenderByStep.removeAll(pos);
                    }
                }
                ArrayList<Entity> entitiesToTrack = new ArrayList<Entity>();
                for(int i = 0; i < mc.theWorld.playerEntities.size(); i++)
                {
                    EntityPlayer player = (EntityPlayer)mc.theWorld.playerEntities.get(i);
                    if(player == mc.thePlayer || Blocksteps.config.trackOtherPlayers == 1)
                    {
                        if(player.ridingEntity != null)
                        {
                            entitiesToTrack.add(player.ridingEntity);
                        }
                        else
                        {
                            entitiesToTrack.add(player);
                        }
                    }
                }
                for(Entity ent : entitiesToTrack)
                {
                    BlockStepHandler.handleStep(ent, steps);
                }

                if(repopulateBlocksToRender && mc.thePlayer.ticksExisted > Blocksteps.config.mapLoad)
                {
                    repopulateBlocksToRender = false;
                    if(mc.thePlayer.ticksExisted == Blocksteps.config.mapLoad + 1 || purgeRerender)
                    {
                        purgeRerender = false;
                        purgeX = mc.thePlayer.posX;
                        purgeY = mc.thePlayer.posY;
                        purgeZ = mc.thePlayer.posZ;
                        BlockStepHandler.getBlocksToRender(true, steps.toArray(new BlockPos[steps.size()]));
                    }
                    for(BlockPos pos : steps)
                    {
                        ChunkStore.addBlocks(blocksToRenderByStep.get(pos));
                    }
                }

                if(--saveTimeout == 0)
                {
                    saveTimeout = Blocksteps.config.saveInterval;

                    if(saveLocation != null)
                    {
                        saveLocation.getParentFile().mkdirs();
                        try
                        {
                            FileOutputStream stream = new FileOutputStream(saveLocation);
                            stream.write(IOUtil.compress((new Gson()).toJson(MapSaveFile.create())));
                            stream.close();
                        }
                        catch(Exception e)
                        {
                            Blocksteps.logger.warn("Error saving file: " + saveLocation);
                        }
                    }
                }
                if(mc.getRenderViewEntity().getDistance(purgeX, purgeY, purgeZ) > (mc.gameSettings.renderDistanceChunks * 16D) * 0.75D)
                {
                    repopulateBlocksToRender = purgeRerender = true;
                }
                if(mc.getRenderViewEntity().ticksExisted > Blocksteps.config.mapLoad && mc.getRenderViewEntity().getDistance(surfaceX, surfaceY, surfaceZ) > Blocksteps.config.surfaceHorizontalUpdateDistance)
                {
                    surfaceX = mc.getRenderViewEntity().posX;
                    surfaceY = mc.getRenderViewEntity().posY;
                    surfaceZ = mc.getRenderViewEntity().posZ;
                    threadCrawlBlocks.needChecks = true;
                }

                if(iChunUtil.proxy.tickHandlerClient.ticks % 20L == 0)
                {
                    clock = iChunUtil.proxy.tickHandlerClient.ticks;
                    rendersThisSecond = 0;
                }
            }
            fullscreenTimeout--;
            mapTypeTimeout--;
            waypointTimeout--;
        }
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        Blocksteps.eventHandler.targetAngleX = Blocksteps.eventHandler.prevAngleX = Blocksteps.eventHandler.angleX = Blocksteps.config.camStartVertical;
        Blocksteps.eventHandler.targetAngleY = Blocksteps.eventHandler.prevAngleY = Blocksteps.eventHandler.angleY = Blocksteps.eventHandler.oriAngleY = Blocksteps.config.camStartHorizontal;
        Blocksteps.eventHandler.targetScale = Blocksteps.config.camStartScale;
        Blocksteps.eventHandler.prevScale = Blocksteps.eventHandler.scale = 0;
        saveTimeout = Blocksteps.config.saveInterval;

        String connectionName = event.manager.getRemoteAddress().toString();
        if(connectionName.contains("/") && !connectionName.startsWith("/192.168.")) //probably a public server
        {
            saveLocation = new File(new File(ResourceHelper.getModsFolder(), "/blocksteps/"), connectionName.substring(0, connectionName.indexOf("/")) + "_" + connectionName.substring(connectionName.indexOf(":") + 1, connectionName.length()) + ".bsv");
            if(saveLocation.exists())
            {
                try
                {
                    byte[] data = new byte[(int)saveLocation.length()];
                    FileInputStream stream = new FileInputStream(saveLocation);
                    stream.read(data);
                    stream.close();
                    MapSaveFile save = (new Gson()).fromJson(IOUtil.decompress(data), MapSaveFile.class);
                    save.load();
                }
                catch(Exception e)
                {
                    Blocksteps.logger.warn("Error loading save file: " + saveLocation);
                };
            }
        }
        else if(connectionName.startsWith("local") || connectionName.startsWith("/192.168."))
        {
            attemptLocalLoad = true;
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if(event.world.isRemote && event.world instanceof WorldClient)
        {
            setNewWorld((WorldClient)event.world);
            blocksToRenderByStep.clear();
            ChunkStore.clear();
            synchronized(Blocksteps.eventHandler.threadCheckBlocks.checks)
            {
                Blocksteps.eventHandler.threadCheckBlocks.checks.clear();
            }
            repopulateBlocksToRender = true;
        }
    }

    public void setNewWorld(WorldClient world)
    {
        if (renderGlobalProxy == null)
        {
            renderGlobalProxy = new RenderGlobalProxy(Minecraft.getMinecraft());
            threadCheckBlocks = new ThreadCheckBlocks();
            threadCheckBlocks.start();

            threadCrawlBlocks = new ThreadBlockCrawler();
            threadCrawlBlocks.start();

            if(OpenGlHelper.isFramebufferEnabled())
            {
                framebuffer = RendererHelper.createFrameBuffer(Blocksteps.MODNAME, true);
                framebuffer.enableStencil();
            }

            arrowCompass.prevRotationYaw = arrowCompass.rotationYaw = 180F;
        }

        renderGlobalProxy.setWorldAndLoadRenderers(world);

        cleanWaypoints();
    }

    public void cleanWaypoints()
    {
        for(Map.Entry<Integer, ArrayList<Waypoint>> e : waypoints.entrySet())
        {
            for(Waypoint wp : e.getValue())
            {
                wp.entityInstance = null; //prevents memleaks
                wp.errored = false;
            }
        }
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyEvent(KeyEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.currentScreen == null || mc.currentScreen instanceof GuiChat)
        {
            if(event.keyBind.isPressed())
            {
                if(fullscreen)
                {
                    if(event.keyBind.equals(Blocksteps.config.keyCamRightFS))
                    {
                        targetOffsetX -= 36F;
                    }
                    else if(event.keyBind.equals(Blocksteps.config.keyCamLeftFS))
                    {
                        targetOffsetX += 36F;
                    }
                    else if(event.keyBind.equals(Blocksteps.config.keyCamUpFS))
                    {
                        targetOffsetY += 36F;
                    }
                    else if(event.keyBind.equals(Blocksteps.config.keyCamDownFS))
                    {
                        targetOffsetY -= 36F;
                    }
                }
                if(event.keyBind.equals(Blocksteps.config.keyCamRight))
                {
                    targetAngleY -= Blocksteps.config.camPanHorizontal;
                    oriAngleY = targetAngleY;
                }
                else if(event.keyBind.equals(Blocksteps.config.keyCamLeft))
                {
                    targetAngleY += Blocksteps.config.camPanHorizontal;
                    oriAngleY = targetAngleY;
                }
                else if(event.keyBind.equals(Blocksteps.config.keyCamUp))
                {
                    targetAngleX -= Blocksteps.config.camPanVertical;
                    targetAngleX = MathHelper.clamp_float(targetAngleX, -90F, 90F);
                }
                else if(event.keyBind.equals(Blocksteps.config.keyCamDown))
                {
                    targetAngleX += Blocksteps.config.camPanVertical;
                    targetAngleX = MathHelper.clamp_float(targetAngleX, -90F, 90F);
                }
                else if(event.keyBind.equals(Blocksteps.config.keyCamZoomIn))
                {
                    targetScale += Blocksteps.config.camZoom;
                    oriScale = targetScale;
                    targetAngleY = oriAngleY;
                }
                else if(event.keyBind.equals(Blocksteps.config.keyCamZoomOut))
                {
                    targetScale -= Blocksteps.config.camZoom;
                    if(targetScale <= 0F)
                    {
                        if(fullscreen)
                        {
                            targetScale = Blocksteps.config.camZoom;
                        }
                        else
                        {
                            targetScale = 0F;
                            oriAngleY = targetAngleY;
                            targetAngleY += 270F;
                        }
                    }
                    oriScale = targetScale;
                }
                else if(event.keyBind.equals(Blocksteps.config.keyToggle))
                {
                    if(targetScale == 0F)
                    {
                        if(oriScale == 0F)
                        {
                            oriScale = Blocksteps.config.camZoom;
                        }
                        targetScale = oriScale;
                        targetAngleY = oriAngleY;
                    }
                    else if(!fullscreen)
                    {
                        targetScale = 0F;
                        oriAngleY = targetAngleY;
                        targetAngleY += 270F;
                    }
                    else
                    {
                        hideWaypoints = !hideWaypoints;
                        waypointTimeout = 100;
                    }
                }
                else if(event.keyBind.equals(Blocksteps.config.keyToggleFullscreen))
                {
                    if(targetScale > 0F)
                    {
                        fullscreen = !fullscreen;
                        fullscreenTimeout = 3;
                        targetOffsetX = targetOffsetY = prevOffsetX = prevOffsetY = offsetX = offsetY = 0F;
                    }
                }
                else if(event.keyBind.equals(Blocksteps.config.keySwitchMapMode))
                {
                    if(!GuiScreen.isShiftKeyDown())
                    {
                        Blocksteps.config.mapType++;
                        if(Blocksteps.config.mapType == 2)
                        {
                            synchronized(threadCrawlBlocks.surface)
                            {
                                threadCrawlBlocks.surface.clear();
                            }
                            threadCrawlBlocks.needChecks = true;
                        }
                        if(Blocksteps.config.mapType > 3)
                        {
                            Blocksteps.config.mapType = 1;
                        }
                        mapTypeTimeout = 100;
                        Blocksteps.config.save();
                        if(renderGlobalProxy != null)
                        {
                            renderGlobalProxy.markAllForUpdateFromPos(new BlockPos(mc.thePlayer));
                        }
                    }
                    repopulateBlocksToRender = purgeRerender = true;
                }
                else if(event.keyBind.equals(Blocksteps.config.keyWaypoints))
                {
                    mc.displayGuiScreen(new GuiWaypoints());
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if(event.entityLiving.worldObj.isRemote && Blocksteps.config.waypointOnDeath == 1 && event.entityLiving == Minecraft.getMinecraft().thePlayer)
        {
            ArrayList<Waypoint> waypoints = Blocksteps.eventHandler.getWaypoints(event.entityLiving.worldObj.provider.getDimensionId());
            waypoints.add((new Waypoint(new BlockPos(event.entityLiving))).setName("Death Location"));
        }
    }

    @SubscribeEvent
    public void onRenderAtPlayer(RenderAtPlayerEvent event)
    {
        if(Blocksteps.config.waypointRenderInWorld == 1 && !Blocksteps.eventHandler.hideWaypoints)
        {
            Minecraft mc = Minecraft.getMinecraft();
            RenderManager renderManager = mc.getRenderManager();
            ArrayList<Waypoint> points = Blocksteps.eventHandler.getWaypoints(mc.theWorld.provider.getDimensionId());
            Blocksteps.eventHandler.renderingMinimap = true;
            GlStateManager.disableFog();
            for(Waypoint wp : points)
            {
                double dx = mc.thePlayer.posX - (wp.pos.getX() + 0.5D);
                double dz = mc.thePlayer.posZ - (wp.pos.getZ() + 0.5D);
                double dist = Math.sqrt(dx * dx + dz * dz);
                if(wp.renderRange == 0 || dist < wp.renderRange)
                {
                    wp.render(renderManager.renderPosX, renderManager.renderPosY, renderManager.renderPosZ, event.renderTick, true);
                }
            }
            GlStateManager.enableFog();
            Blocksteps.eventHandler.renderingMinimap = false;
        }
    }

    public ArrayList<BlockPos> getSteps(int dimension)
    {
        ArrayList<BlockPos> dimSteps = steps.get(dimension);
        if(dimSteps == null)
        {
            dimSteps = new ArrayList<BlockPos>();
            steps.put(dimension, dimSteps);
        }
        return dimSteps;
    }

    public ArrayList<Waypoint> getWaypoints(int dimension)
    {
        ArrayList<Waypoint> dimPoints = waypoints.get(dimension);
        if(dimPoints == null)
        {
            dimPoints = new ArrayList<Waypoint>();
            waypoints.put(dimension, dimPoints);
        }
        return dimPoints;
    }

    public RenderGlobalProxy renderGlobalProxy;
    public ThreadCheckBlocks threadCheckBlocks;
    public ThreadBlockCrawler threadCrawlBlocks;
    public Framebuffer framebuffer;

    public boolean renderingMinimap;
    public boolean hideWaypoints;

    public float prevAngleY;
    public float prevAngleX;
    public float prevScale;

    public float angleY = 45F;
    public float oriAngleY = 30F;
    public float angleX = 30F;
    public float scale = 1000F;

    public float targetAngleX = 30F;
    public float targetAngleY = 45F;
    public float targetScale = 1000F;
    public float oriScale = 1000F;

    public boolean fullscreen = false;
    public int fullscreenTimeout = 0;
    public float offsetX = 0F;
    public float offsetY = 0F;
    public float prevOffsetX = 0F;
    public float prevOffsetY = 0F;
    public float targetOffsetX = 0F;
    public float targetOffsetY = 0F;

    public int frameCount = 0;

    public TreeMap<Integer, ArrayList<BlockPos>> steps = new TreeMap<Integer, ArrayList<BlockPos>>(Ordering.natural()); //newest = last index. oldest = index 0
    public TreeMap<Integer, ArrayList<Waypoint>> waypoints = new TreeMap<Integer, ArrayList<Waypoint>>(Ordering.natural()); //newest = last index. oldest = index 0

    public EntityArrow arrowCompass = new EntityArrow(null);

    public ArrayListMultimap<BlockPos, BlockPos> blocksToRenderByStep = ArrayListMultimap.create();
    public boolean repopulateBlocksToRender = false;
    public final List<CheckBlockInfo> blocksToAdd = Collections.synchronizedList(new ArrayList<CheckBlockInfo>());
    public boolean purgeRerender;
    public double purgeX;
    public double purgeY;
    public double purgeZ;

    public int waypointTimeout;
    public int mapTypeTimeout;
    public double surfaceX;
    public double surfaceY;
    public double surfaceZ;

    public boolean attemptLocalLoad;

    public File saveLocation;
    public int saveTimeout;

    public float lastRenderTick;
    public int rendersThisSecond;
    public long clock;
}
