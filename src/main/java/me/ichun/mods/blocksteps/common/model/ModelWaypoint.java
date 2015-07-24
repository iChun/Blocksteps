package me.ichun.mods.blocksteps.common.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelWaypoint extends ModelBase
{
    private ModelRenderer glass = new ModelRenderer(this, "glass");

    public ModelWaypoint()
    {
        this.glass.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
    }

    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GlStateManager.translate(0.0F, -0.5F, 0.0F);
        GlStateManager.rotate(p_78088_3_, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.8F, 0.0F);
        GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
        this.glass.render(scale);
        float f6 = 0.875F;
        GlStateManager.scale(f6, f6, f6);
        GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
        GlStateManager.rotate(p_78088_3_, 0.0F, 1.0F, 0.0F);
        this.glass.render(scale);
        GlStateManager.scale(f6, f6, f6);
        GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
        GlStateManager.rotate(p_78088_3_, 0.0F, 1.0F, 0.0F);
        this.glass.render(scale);
        GlStateManager.popMatrix();
    }
}