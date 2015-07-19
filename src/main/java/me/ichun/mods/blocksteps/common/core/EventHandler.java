package me.ichun.mods.blocksteps.common.core;

import com.google.common.collect.ArrayListMultimap;
import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.blockaid.BlockStepHandler;
import me.ichun.mods.blocksteps.common.blockaid.CheckBlockInfo;
import me.ichun.mods.blocksteps.common.blockaid.ThreadCheckBlocks;
import me.ichun.mods.blocksteps.common.render.RenderGlobalProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class EventHandler
{
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(event.phase == TickEvent.Phase.END)
        {
            if(mc.thePlayer != null && !mc.gameSettings.hideGUI /*&& (mc.currentScreen == null || mc.currentScreen instanceof GuiChat)*/)
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
                    if(fullscreen)
                    {
                        x = (int)(reso.getScaledWidth_double() * 0.01D);
                        y = (int)(reso.getScaledHeight_double() * 0.02D);
                        width = (int)(reso.getScaledWidth_double() * 0.98D);
                        height = (int)(reso.getScaledHeight_double() * 0.96D);
                    }
                    float alphaAmp = MathHelper.clamp_float(aScale / 0.1F, 0F, 1F);

                    GlStateManager.disableDepth();
                    GlStateManager.depthMask(false);

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

                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();

                    RendererHelper.startGlScissor(x, y, width, height);
                    drawMap(mc, reso, x, y, width, height, aScale, event.renderTickTime);
                    RendererHelper.endGlScissor();
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
                setNewWorld(null);
                steps.clear();
                blocksToRenderByStep.clear();
                blocksToRender.clear();
                synchronized(Blocksteps.eventHandler.threadCheckBlocks.checks)
                {
                    Blocksteps.eventHandler.threadCheckBlocks.checks.clear();
                }
                //TODO set up saving here
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

                if(Blocksteps.config.lockMapToHeadYaw == 1)
                {
                    targetAngleY = angleY = mc.getRenderViewEntity().rotationYaw + 180F;
                }
                if(Blocksteps.config.lockMapToHeadPitch == 1)
                {
                    targetAngleX = angleX = mc.getRenderViewEntity().rotationPitch;
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
                        BlockStepHandler.getBlocksToRender(true, steps.toArray(new BlockPos[steps.size()]));
                    }
                    for(BlockPos pos : steps)
                    {
                        blocksToRender.addAll(blocksToRenderByStep.get(pos));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        Blocksteps.eventHandler.targetAngleX = Blocksteps.eventHandler.prevAngleX = Blocksteps.eventHandler.angleX = Blocksteps.config.camStartVertical;
        Blocksteps.eventHandler.targetAngleY = Blocksteps.eventHandler.prevAngleY = Blocksteps.eventHandler.angleY = Blocksteps.eventHandler.oriAngleY = Blocksteps.config.camStartHorizontal;
        Blocksteps.eventHandler.targetScale = Blocksteps.config.camStartScale;
        Blocksteps.eventHandler.prevScale = Blocksteps.eventHandler.scale = 0;

        System.out.println(event.manager.getRemoteAddress());
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if(event.world.isRemote && event.world instanceof WorldClient)
        {
            setNewWorld((WorldClient)event.world);
            blocksToRenderByStep.clear();
            blocksToRender.clear();
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
        }

        renderGlobalProxy.setWorldAndLoadRenderers(world);
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
                }
                else if(event.keyBind.equals(Blocksteps.config.keyToggleFullscreen))
                {
                    if(targetScale > 0F)
                    {
                        fullscreen = !fullscreen;
                        targetOffsetX = targetOffsetY = prevOffsetX = prevOffsetY = offsetX = offsetY = 0F;
                    }
                }
                else if(event.keyBind.equals(Blocksteps.config.keyPurgeRerender))
                {
                    purgeRerender = true;
                }
            }
        }
    }

    public List<BlockPos> getSteps(int dimension)
    {
        return steps.get(dimension);
    }

    public RenderGlobalProxy renderGlobalProxy;
    public ThreadCheckBlocks threadCheckBlocks;

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
    public float offsetX = 0F;
    public float offsetY = 0F;
    public float prevOffsetX = 0F;
    public float prevOffsetY = 0F;
    public float targetOffsetX = 0F;
    public float targetOffsetY = 0F;

    public int frameCount = 0;

    public ArrayListMultimap<Integer, BlockPos> steps = ArrayListMultimap.create();

    public ArrayListMultimap<BlockPos, BlockPos> blocksToRenderByStep = ArrayListMultimap.create();
    public boolean repopulateBlocksToRender = false;
    public HashSet<BlockPos> blocksToRender = new HashSet<BlockPos>();
    public final List<CheckBlockInfo> blocksToAdd = Collections.synchronizedList(new ArrayList<CheckBlockInfo>());
    public boolean purgeRerender;

}
