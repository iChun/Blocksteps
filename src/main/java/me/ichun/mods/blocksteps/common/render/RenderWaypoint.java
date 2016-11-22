package me.ichun.mods.blocksteps.common.render;

import me.ichun.mods.blocksteps.common.entity.EntityWaypoint;
import me.ichun.mods.blocksteps.common.model.ModelWaypoint;
import me.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderWaypoint extends Render<EntityWaypoint>
{
    private ModelBase modelWaypoint = new ModelWaypoint();

    public RenderWaypoint(RenderManager manager)
    {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityWaypoint entity)
    {
        return ResourceHelper.texEnderCrystal;
    }

    @Override
    public void doRender(EntityWaypoint entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        float f2 = (float)entity.getEntityId() + Minecraft.getMinecraft().thePlayer.ticksExisted + partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        this.bindTexture(ResourceHelper.texEnderCrystal);
        float f3 = MathHelper.sin(f2 * 0.2F) / 2.0F + 0.5F;
        f3 += f3 * f3;
        this.modelWaypoint.render(entity, 0.0F, f2 * 3.0F, f3 * 0.2F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public static class RenderFactory implements IRenderFactory<EntityWaypoint>
    {
        @Override
        public Render<? super EntityWaypoint> createRenderFor(RenderManager manager)
        {
            return new RenderWaypoint(manager);
        }
    }
}
