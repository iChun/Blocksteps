package me.ichun.mods.blocksteps.common.layer;

import me.ichun.mods.blocksteps.common.Blocksteps;
import me.ichun.mods.blocksteps.common.model.ModelSheepPig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

public class LayerSheepPig implements LayerRenderer
{
    public ModelSheepPig modelSheepPig = new ModelSheepPig();
    private static final ResourceLocation texSheepPig = new ResourceLocation("blocksteps","textures/model/sheeppig.png");
    private final RenderPig renderPig;

    public LayerSheepPig(RenderPig renderPig)
    {
        this.renderPig = renderPig;
    }

    //func_177093_a(entity, f8, f7, partialTicks, f5, f4, f9, 0.0625F);
    public void doRenderLayer(EntityPig pig, float f, float f1, float renderTick, float f2, float f3, float f4, float f5)
    {
        if(Blocksteps.config.easterEgg == 1 && Blocksteps.eventHandler.renderingMinimap && !pig.isInvisible())
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(texSheepPig);

            if (pig.hasCustomName() && "jeb_".equals(pig.getCustomNameTag()))
            {
                boolean flag = true;
                int i = pig.ticksExisted / 25 + pig.getEntityId();
                int j = EnumDyeColor.values().length;
                int k = i % j;
                int l = (i + 1) % j;
                float f7 = ((float)(pig.ticksExisted % 25) + renderTick) / 25.0F;
                float[] afloat1 = EntitySheep.func_175513_a(EnumDyeColor.byMetadata(k));
                float[] afloat2 = EntitySheep.func_175513_a(EnumDyeColor.byMetadata(l));
                GlStateManager.color(afloat1[0] * (1.0F - f7) + afloat2[0] * f7, afloat1[1] * (1.0F - f7) + afloat2[1] * f7, afloat1[2] * (1.0F - f7) + afloat2[2] * f7);
            }
            else
            {
                float[] afloat = EntitySheep.func_175513_a(EnumDyeColor.byMetadata(pig.getEntityId() & 15));
                GlStateManager.color(afloat[0], afloat[1], afloat[2]);
            }

            modelSheepPig.setModelAttributes(renderPig.getMainModel());
            modelSheepPig.setLivingAnimations(pig, f, f1, renderTick);
            modelSheepPig.render(pig, f, f1, f2, f3, f4, f5);
        }
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float f, float f1, float renderTick, float f2, float f3, float f4, float f5)
    {
        doRenderLayer((EntityPig)entitylivingbaseIn, f, f1, renderTick, f2, f3, f4, f5);
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}
