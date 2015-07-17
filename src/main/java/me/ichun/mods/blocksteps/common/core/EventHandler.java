package me.ichun.mods.blocksteps.common.core;

import me.ichun.mods.blocksteps.common.Blocksteps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
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
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;

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

                double posX = reso.getScaledWidth() * (float)Blocksteps.config.camPosX / 100F;
                double posY = reso.getScaledHeight() * (float)Blocksteps.config.camPosY / 100F;
                float aScale = EntityHelperBase.interpolateValues(prevScale, scale, event.renderTickTime) / 10F;

                if(aScale > 0F)
                {
                    EntityLivingBase ent = mc.thePlayer;
                    GlStateManager.enableColorMaterial();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float)posX, (float)posY, 50.0F);
                    GlStateManager.scale(-aScale, aScale, aScale);
                    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

                    GlStateManager.rotate(EntityHelperBase.interpolateRotation(prevAngleX, angleX, event.renderTickTime), 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(EntityHelperBase.interpolateRotation(prevAngleY, angleY, event.renderTickTime), 0.0F, 1.0F, 0.0F);

                    renderWorld(mc, event.renderTickTime);

                    RenderHelper.enableStandardItemLighting();

                    if(ent instanceof EntityDragon)
                    {
                        GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);
                    }
                    RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
                    float viewY = rendermanager.playerViewY;
                    rendermanager.setPlayerViewY(180.0F);
                    rendermanager.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, event.renderTickTime);
                    if(ent instanceof EntityDragon)
                    {
                        GlStateManager.rotate(180F, 0.0F, -1.0F, 0.0F);
                    }

                    Entity ridden = ent.riddenByEntity;
                    while(ridden != null)
                    {
                        rendermanager.renderEntityWithPosYaw(ridden, ridden.posX - ent.posX, ridden.posY - ent.posY, ridden.posZ - ent.posZ, 0.0F, event.renderTickTime);
                        ridden = ridden.riddenByEntity;
                    }

                    ridden = ent.ridingEntity;
                    while(ridden != null)
                    {
                        rendermanager.renderEntityWithPosYaw(ridden, ridden.posX - ent.posX, ridden.posY - ent.posY, ridden.posZ - ent.posZ, 0.0F, event.renderTickTime);
                        ridden = ridden.ridingEntity;
                    }

                    rendermanager.setPlayerViewY(viewY);

                    GlStateManager.popMatrix();
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.disableRescaleNormal();
                    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                    GlStateManager.disableTexture2D();
                    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                }
            }
        }
        else
        {
            if(renderGlobalProxy != null && renderGlobalProxy.theWorld != null && mc.theWorld == null)
            {
                setNewWorld(null);
            }
        }
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
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            Frustum frustum = new Frustum();
            double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
            double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
            double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
            frustum.setPosition(d0, d1, d2);
            renderglobal.setupTerrain(entity, (double)partialTicks, frustum, frameCount++, mc.thePlayer.isSpectator());
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

                angleX = angleX + (targetAngleX - angleX) * 0.4F;
                angleY = angleY + (targetAngleY - angleY) * 0.4F;
                scale = scale + (targetScale - scale) * 0.4F;
            }
        }
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        Blocksteps.eventHandler.targetAngleX = Blocksteps.eventHandler.prevAngleX = Blocksteps.eventHandler.angleX = Blocksteps.config.camStartVertical;
        Blocksteps.eventHandler.targetAngleY = Blocksteps.eventHandler.prevAngleY = Blocksteps.eventHandler.angleY = Blocksteps.config.camStartHorizontal;
        Blocksteps.eventHandler.targetScale = Blocksteps.eventHandler.prevScale = Blocksteps.eventHandler.scale = Blocksteps.config.camStartScale;

    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if(event.world.isRemote && event.world instanceof WorldClient)
        {
            setNewWorld((WorldClient)event.world);
        }
    }

    public void setNewWorld(WorldClient world)
    {
        if (renderGlobalProxy == null)
        {
            renderGlobalProxy = new RenderGlobalProxy(Minecraft.getMinecraft());
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
                if(event.keyBind.equals(Blocksteps.config.keyCamRight))
                {
                    targetAngleY -= Blocksteps.config.camPanHorizontal;
                }
                else if(event.keyBind.equals(Blocksteps.config.keyCamLeft))
                {
                    targetAngleY += Blocksteps.config.camPanHorizontal;
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
                }
                else if(event.keyBind.equals(Blocksteps.config.keyCamZoomOut))
                {
                    targetScale -= Blocksteps.config.camZoom;
                    if(targetScale < 0F)
                    {
                        targetScale = 0F;
                    }
                }
            }
        }
    }

    public RenderGlobalProxy renderGlobalProxy;

    public float prevAngleY;
    public float prevAngleX;
    public float prevScale;

    public float angleY = 45F;
    public float angleX = 30F;
    public float scale = 100F;

    public float targetAngleX = 30F;
    public float targetAngleY = 45F;
    public float targetScale = 100F;

    public int frameCount = 0;
}
