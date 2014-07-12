package xreliquary.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelHandgun extends ModelBase {
	// fields
	public ModelRenderer Bottom;
	public ModelRenderer Gunmain;
	public ModelRenderer Top;
	public ModelRenderer Topback;
	public ModelRenderer Hammerblock;
	public ModelRenderer Grip;
	public ModelRenderer Trigger;
	public ModelRenderer Sight;
	public ModelRenderer Hammer_thingy;

	public ModelHandgun() {
		textureWidth = 64;
		textureHeight = 32;

		Bottom = new ModelRenderer(this, 0, 10);
		Bottom.addBox(-3F, -6F, -2F, 13, 1, 3);
		Bottom.setRotationPoint(0F, 0F, 0F);
		Bottom.setTextureSize(textureWidth, textureHeight);
		Bottom.mirror = true;
		setRotation(Bottom, 0F, 0F, 0F);
		Gunmain = new ModelRenderer(this, 0, 0);
		Gunmain.addBox(-3F, -9F, -2F, 15, 3, 3);
		Gunmain.setRotationPoint(0F, 0F, 0F);
		Gunmain.setTextureSize(textureWidth, textureHeight);
		Gunmain.mirror = true;
		setRotation(Gunmain, 0F, 0F, 0F);
		Top = new ModelRenderer(this, 0, 6);
		Top.addBox(-2F, -10F, -2F, 14, 1, 3);
		Top.setRotationPoint(0F, 0F, 0F);
		Top.setTextureSize(textureWidth, textureHeight);
		Top.mirror = true;
		setRotation(Top, 0F, 0F, 0F);
		Topback = new ModelRenderer(this, 12, 21);
		Topback.addBox(-3F, -10F, -1.5F, 1, 1, 2);
		Topback.setRotationPoint(0F, 0F, 0F);
		Topback.setTextureSize(textureWidth, textureHeight);
		Topback.mirror = true;
		setRotation(Topback, 0F, 0F, 0F);
		Hammerblock = new ModelRenderer(this, 12, 16);
		Hammerblock.addBox(-4F, -9F, -1.5F, 1, 3, 2);
		Hammerblock.setRotationPoint(0F, 0F, 0F);
		Hammerblock.setTextureSize(textureWidth, textureHeight);
		Hammerblock.mirror = true;
		setRotation(Hammerblock, 0F, 0F, 0F);
		Grip = new ModelRenderer(this, 0, 14);
		Grip.addBox(-5F, -5.5F, -1.5F, 4, 9, 2);
		Grip.setRotationPoint(0F, 0F, 0F);
		Grip.setTextureSize(textureWidth, textureHeight);
		Grip.mirror = true;
		setRotation(Grip, 0F, 0F, 0.1919862F);
		Trigger = new ModelRenderer(this, 36, 0);
		Trigger.addBox(-1F, -5F, -0.5F, 5, 4, 0);
		Trigger.setRotationPoint(0F, 0F, 0F);
		Trigger.setTextureSize(textureWidth, textureHeight);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
		Sight = new ModelRenderer(this, 12, 14);
		Sight.addBox(10F, -11F, -1F, 2, 1, 1);
		Sight.setRotationPoint(0F, 0F, 0F);
		Sight.setTextureSize(textureWidth, textureHeight);
		Sight.mirror = true;
		setRotation(Sight, 0F, 0F, 0F);
		Hammer_thingy = new ModelRenderer(this, 0, 25);
		Hammer_thingy.addBox(-4.693333F, -10F, -0.5F, 2, 2, 0);
		Hammer_thingy.setRotationPoint(0F, 0F, 0F);
		Hammer_thingy.setTextureSize(textureWidth, textureHeight);
		Hammer_thingy.mirror = true;
		setRotation(Hammer_thingy, 0F, 0F, -0.1047198F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		Bottom.render(f5);
		Gunmain.render(f5);
		Top.render(f5);
		Topback.render(f5);
		Hammerblock.render(f5);
		Grip.render(f5);
		Trigger.render(f5);
		Sight.render(f5);
		Hammer_thingy.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity ent) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, ent);
	}

}