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
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.ItemStack;
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

                //                double posX = reso.getScaledWidth() * 0.9D;
                //                double posY = reso.getScaledHeight() * 0.85D;
                double posX = reso.getScaledWidth() * 0.7D;
                double posY = reso.getScaledHeight() * 0.7D;
                double scale = 10D;
                EntityLivingBase ent = mc.thePlayer;
                GlStateManager.enableColorMaterial();
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)posX, (float)posY, 50.0F);
                GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
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

                Entity ridden = ent.ridingEntity;
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
            //            RenderGlobal ori = mc.renderGlobal;
            //            mc.renderGlobal = renderGlobalProxy;
            //            mc.entityRenderer.renderWorld(partialTicks, 0L);
            //            mc.renderGlobal = ori;
            //
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
            renderglobal.setupTerrain(entity, (double)partialTicks, frustum, 0, mc.thePlayer.isSpectator());
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

                angleX = angleX + (targetAngleX - angleX) * 0.4F;
                angleY = angleY + (targetAngleY - angleY) * 0.4F;
            }
        }
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        Blocksteps.eventHandler.targetAngleX = Blocksteps.eventHandler.prevAngleX = Blocksteps.eventHandler.angleX = Blocksteps.config.camStartVertical;
        Blocksteps.eventHandler.targetAngleY = Blocksteps.eventHandler.prevAngleY = Blocksteps.eventHandler.angleY = Blocksteps.config.camStartHorizontal;
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

        renderGlobalProxy.updateDestroyBlockIcons();
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
            }
        }
    }

    public RenderGlobalProxy renderGlobalProxy;

    public float prevAngleY;
    public float prevAngleX;

    public float angleY = 45F;
    public float angleX = 30F;

    public float targetAngleX = 30F;
    public float targetAngleY = 45F;
}
