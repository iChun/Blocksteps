package me.ichun.mods.blocksteps.common.model;

import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelRenderer;

/**
 * ModelPig - Either Mojang or a mod author
 * Created using Tabula 5.1.0
 */
public class ModelSheepPig extends ModelPig
{

    public ModelSheepPig()
    {
        super(0.0F);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.leg4 = new ModelRenderer(this, 0, 48);
        this.leg4.setRotationPoint(3.0F, 18.0F, -5.0F);
        this.leg4.addBox(-2.0F, 0.5F, -2.0F, 4, 4, 4, 0.5F);
        this.leg2 = new ModelRenderer(this, 0, 48);
        this.leg2.setRotationPoint(3.0F, 18.0F, 7.0F);
        this.leg2.addBox(-2.0F, 0.48F, -2.0F, 4, 4, 4, 0.5F);
        this.body = new ModelRenderer(this, 24, 38);
        this.body.setRotationPoint(-0.5F, 11.5F, 1.5F);
        this.body.addBox(-5.0F, -10.0F, -7.0F, 11, 17, 9, 0.0F);
        this.setRotateAngle(body, 1.5707963267948966F, 0.0F, 0.0F);
        this.leg3 = new ModelRenderer(this, 0, 48);
        this.leg3.setRotationPoint(-3.0F, 18.0F, -5.0F);
        this.leg3.addBox(-2.0F, 0.5F, -2.0F, 4, 4, 4, 0.5F);
        this.leg1 = new ModelRenderer(this, 0, 48);
        this.leg1.setRotationPoint(-3.0F, 18.0F, 7.0F);
        this.leg1.addBox(-2.0F, 0.48F, -2.0F, 4, 4, 4, 0.5F);
        this.head = new ModelRenderer(this, 22, 33);
        this.head.setRotationPoint(0.0F, 12.0F, -6.0F);
        this.head.addBox(-4.5F, -4.5F, -6.5F, 9, 9, 7, 0.0F);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
