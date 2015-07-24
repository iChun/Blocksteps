package me.ichun.mods.blocksteps.common.render;

import me.ichun.mods.blocksteps.common.entity.EntityWaypoint;
import me.ichun.mods.blocksteps.common.model.ModelWaypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderWaypoint extends Render
{
    private static final ResourceLocation texEnderCrystal = new ResourceLocation("textures/entity/endercrystal/endercrystal.png");
    private ModelBase modelWaypoint = new ModelWaypoint();

    public RenderWaypoint()
    {
        super(Minecraft.getMinecraft().getRenderManager());
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return texEnderCrystal;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        float f2 = (float)entity.getEntityId() + Minecraft.getMinecraft().thePlayer.ticksExisted + partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        this.bindTexture(texEnderCrystal);
        float f3 = MathHelper.sin(f2 * 0.2F) / 2.0F + 0.5F;
        f3 += f3 * f3;
        this.modelWaypoint.render(entity, 0.0F, f2 * 3.0F, f3 * 0.2F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}
