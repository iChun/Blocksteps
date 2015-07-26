package me.ichun.mods.blocksteps.common.core;

import com.google.gson.annotations.SerializedName;
import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.entity.EntityWaypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.opengl.GL11;
import us.ichun.mods.ichunutil.client.gui.window.element.IIdentifiable;
import us.ichun.mods.ichunutil.client.gui.window.element.IListable;
import us.ichun.mods.ichunutil.client.render.RendererHelper;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;
import us.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import us.ichun.mods.ichunutil.common.module.tabula.common.project.ProjectInfo;

import java.util.Locale;
import java.util.Random;

public class Waypoint
        implements Comparable, IIdentifiable, IListable
{
    @SerializedName("i")
    public String ident;
    @SerializedName("n")
    public String name;
    @SerializedName("p")
    public BlockPos pos;
    @SerializedName("c")
    public int colour;
    @SerializedName("v")
    public boolean visible;
    @SerializedName("s")
    public boolean showDistance;
    @SerializedName("b")
    public boolean beam;
    @SerializedName("e")
    public String entityType;
    @SerializedName("r")
    public int renderRange;

    public transient Entity entityInstance;
    public transient boolean errored;

    public Waypoint(BlockPos wpPos)
    {
        ident = RandomStringUtils.randomAscii(ProjectInfo.IDENTIFIER_LENGTH);
        name = "New Waypoint";
        pos = wpPos;
        int r = 255;
        int g = 255;
        int b = 255;
        if(Minecraft.getMinecraft().theWorld != null)
        {
            Random rand = Minecraft.getMinecraft().theWorld.rand;
            r = rand.nextInt(256);
            g = rand.nextInt(256);
            b = rand.nextInt(256);
        }
        colour = (r << 16) + (g << 8) + (b);
        visible = true;
        showDistance = true;
        beam = true;
        entityType = "";
        renderRange = 1000;
    }

    public Waypoint setName(String name)
    {
        this.name = name;
        return this;
    }

    @Override
    public int compareTo(Object arg0)
    {
        if(arg0 instanceof Waypoint)
        {
            Waypoint comp = (Waypoint)arg0;
            return name.compareTo(comp.name);
        }
        return 0;
    }

    @Override
    public String getIdentifier()
    {
        return ident;
    }

    @Override
    public String getName()
    {
        return "   " + name;
    }

    public void render(double d, double d1, double d2, float partialTicks, boolean inWorld)
    {
        if(this.visible)
        {
            Minecraft mc = Minecraft.getMinecraft();
            int clr = this.colour;
            if(Blocksteps.config.easterEgg == 1)
            {
                if(this.name.equalsIgnoreCase("home") || this.name.equalsIgnoreCase(mc.getSession().getUsername()) || this.name.equalsIgnoreCase("blocksteps") || this.name.equalsIgnoreCase("ichun") || this.name.equalsIgnoreCase("fusionlord") || this.name.equalsIgnoreCase("sheeppig") || this.name.equalsIgnoreCase("sheepig"))
                {
                    int ii = mc.thePlayer.ticksExisted / 25 + Math.abs(this.name.hashCode());
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
                if(this.beam)
                {
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    double j = 0;
                    double l = 256;
                    if(Blocksteps.config.waypointBeamHeightAdjust > 0)
                    {
                        double dist = mc.thePlayer.getDistance(this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.5D);
                        if(dist < 256D)
                        {
                            l = Math.max(Blocksteps.config.waypointBeamHeightAdjust, dist);
                        }
                    }
                    double x = this.pos.getX() - d;
                    double y = this.pos.getY() - (l / 2D) - d1;
                    double z = this.pos.getZ() - d2;
                    int jj = 15728880 % 65536;
                    int kk = 15728880 / 65536;
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)jj / 1.0F, (float)kk / 1.0F);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceHelper.texBeaconBeam);
                    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
                    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
                    GlStateManager.disableLighting();
                    GlStateManager.disableCull();
                    GlStateManager.disableBlend();
                    GlStateManager.depthMask(true);
                    GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
                    float f2;
                    if(this.entityInstance != null)
                    {
                        f2 = (this.entityInstance.getEntityId() + mc.thePlayer.ticksExisted + partialTicks) * 0.4F;
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
                if(this.entityInstance == null && !errored)
                {
                    try
                    {
                        if(this.name.equalsIgnoreCase("sheeppig") || this.name.equalsIgnoreCase("sheepig"))
                        {
                            this.entityInstance = new EntityPig(mc.theWorld);
                            EntityPig pig = (EntityPig)this.entityInstance;
                            pig.setCustomNameTag("iChun");
                            pig.enablePersistence();
                        }
                        else if(!this.entityType.isEmpty())
                        {
                            this.entityInstance = (Entity)Class.forName(this.entityType).getConstructor(World.class).newInstance(mc.theWorld);
                        }
                        else
                        {
                            this.entityInstance = new EntityWaypoint(mc.theWorld);
                        }
                        this.entityInstance.setLocationAndAngles(this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.5D, 0F, 0F);
                    }
                    catch(Exception e)
                    {
                        errored = true;
                        Blocksteps.logger.warn("Error creating waypoint indicator for waypoint " + this.name + " with type " + this.entityType);
                        e.printStackTrace();
                    }
                }
                if(this.entityInstance != null && !errored && mc.thePlayer.getDistanceToEntity(entityInstance) < 150D)
                {
                    double dd0 = this.entityInstance.lastTickPosX + (this.entityInstance.posX - this.entityInstance.lastTickPosX) * (double)partialTicks;
                    double dd1 = this.entityInstance.lastTickPosY + (this.entityInstance.posY - this.entityInstance.lastTickPosY) * (double)partialTicks;
                    double dd2 = this.entityInstance.lastTickPosZ + (this.entityInstance.posZ - this.entityInstance.lastTickPosZ) * (double)partialTicks;
                    float ff1 = this.entityInstance.prevRotationYaw + (this.entityInstance.rotationYaw - this.entityInstance.prevRotationYaw) * partialTicks;

                    RendererHelper.setColorFromInt(clr);
                    GlStateManager.disableLighting();
                    int j = 15728880 % 65536;
                    int k = 15728880 / 65536;
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);

                    GlStateManager.pushMatrix();
                    GlStateManager.translate(dd0 - d, dd1 - d1 + 0.2F, dd2 - d2);
                    if(this.beam && (this.name.equalsIgnoreCase("sheeppig") || this.name.equalsIgnoreCase("sheepig") || !this.entityType.isEmpty()))
                    {
                        GlStateManager.rotate((this.entityInstance.getEntityId() + mc.thePlayer.ticksExisted + partialTicks) % 400F / 400F * 360F, 0F, 1F, 0F);
                        if(!(this.entityInstance instanceof EntityDragon))
                        {
                            GlStateManager.translate(0F, 0F, this.entityInstance.width / 2F);
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

                    if(this.entityInstance instanceof EntityDragon)
                    {
                        GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);
                    }
                    try
                    {
                        mc.getRenderManager().doRenderEntity(this.entityInstance, 0D, 0D, 0D, ff1, 1F, false);
                    }
                    catch(Exception e)
                    {
                        errored = true;
                    }
                    if(this.entityInstance instanceof EntityDragon)
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
            String str = this.name;
            FontRenderer fontrenderer = mc.fontRendererObj;
            float f = Blocksteps.config.waypointLabelSize / 10F;
            float f1 = 0.016666668F * f;
            GlStateManager.pushMatrix();
            double dx = (this.pos.getX() + 0.5D) - d;
            double dy = (this.pos.getY()) - d1;
            double dz = (this.pos.getZ() + 0.5D) - d2;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if(dist < 116D || !inWorld)
            {
                GlStateManager.translate(this.pos.getX() + 0.5D - d, this.pos.getY() - d1, this.pos.getZ() + 0.5D - d2);
            }
            else
            {
                double distt = 116D / dist;
                GlStateManager.translate((this.pos.getX() + 0.5D - d) * distt, ((this.pos.getY() - d1) * distt), (this.pos.getZ() + 0.5D - d2) * distt);
            }
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            if(inWorld)
            {
                if(dist > 16D)
                {
                    f1 *= Math.min(100D, dist) / 16D;
                }
                GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
            }
            else
            {
                GlStateManager.rotate(Blocksteps.eventHandler.angleX, 1.0F, 0.0F, 0.0F);
            }
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
            if(this.showDistance)
            {
                b0 = -9;
                str = String.format(Locale.ENGLISH, "%.2f", mc.thePlayer.getDistance(this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.5D)) + "m";
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
            if(mc.gameSettings.showDebugInfo)
            {
                b0 = 9;
                str = "X:" + pos.getX() + " Y:" + pos.getY() + " Z:" + pos.getZ();
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
