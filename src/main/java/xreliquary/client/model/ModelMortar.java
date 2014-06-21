package xreliquary.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMortar extends ModelBase {
  //fields
    ModelRenderer backAngle;
    ModelRenderer bottom;
    ModelRenderer back;
    ModelRenderer leftAngle;
    ModelRenderer frontAngle;
    ModelRenderer leg3;
    ModelRenderer left;
    ModelRenderer front;
    ModelRenderer leg1;
    ModelRenderer leg4;
    ModelRenderer leg2;
    ModelRenderer right;
    ModelRenderer rightAngle;
    public ModelRenderer pestleKnob;
    public ModelRenderer pestle;
  
  public ModelMortar()
  {
    textureWidth = 32;
    textureHeight = 32;
    
      backAngle = new ModelRenderer(this, 14, 18);
      backAngle.addBox(0F, 0F, -1F, 6, 2, 1);
      backAngle.setRotationPoint(-3F, 22.3F, 4F);
      backAngle.setTextureSize(32, 32);
      backAngle.mirror = true;
      setRotation(backAngle, -0.5235988F, 0F, 0F);
      bottom = new ModelRenderer(this, 0, 0);
      bottom.addBox(0F, 0F, 0F, 6, 1, 6);
      bottom.setRotationPoint(-3F, 23F, -3F);
      bottom.setTextureSize(32, 32);
      bottom.mirror = true;
      setRotation(bottom, 0F, 0F, 0F);
      back = new ModelRenderer(this, 14, 14);
      back.addBox(0F, 0F, 0F, 6, 3, 1);
      back.setRotationPoint(-3F, 19.3F, 3F);
      back.setTextureSize(32, 32);
      back.mirror = true;
      setRotation(back, 0F, 0F, 0F);
      leftAngle = new ModelRenderer(this, 0, 16);
      leftAngle.mirror = true;
      leftAngle.addBox(-1F, 0F, 0F, 1, 2, 6);
      leftAngle.setRotationPoint(4F, 22.3F, -3F);
      leftAngle.setTextureSize(32, 32);
      setRotation(leftAngle, 0F, 0F, 0.5235988F);
      leftAngle.mirror = false;
      frontAngle = new ModelRenderer(this, 14, 11);
      frontAngle.addBox(0F, 0F, 0F, 6, 2, 1);
      frontAngle.setRotationPoint(-3F, 22.3F, -4F);
      frontAngle.setTextureSize(32, 32);
      frontAngle.mirror = true;
      setRotation(frontAngle, 0.5235988F, 0F, 0F);
      leg3 = new ModelRenderer(this, 0, 0);
      leg3.addBox(0F, 0F, 0F, 1, 5, 1);
      leg3.setRotationPoint(3F, 19.3F, 3F);
      leg3.setTextureSize(32, 32);
      leg3.mirror = true;
      setRotation(leg3, 0F, 0F, 0F);
      left = new ModelRenderer(this, 0, 7);
      left.mirror = true;
      left.addBox(0F, 0F, 0F, 1, 3, 6);
      left.setRotationPoint(3F, 19.3F, -3F);
      left.setTextureSize(32, 32);
      left.mirror = true;
      setRotation(left, 0F, 0F, 0F);
      left.mirror = false;
      front = new ModelRenderer(this, 14, 7);
      front.addBox(0F, 0F, 0F, 6, 3, 1);
      front.setRotationPoint(-3F, 19.3F, -4F);
      front.setTextureSize(32, 32);
      front.mirror = true;
      setRotation(front, 0F, 0F, 0F);
      leg1 = new ModelRenderer(this, 0, 0);
      leg1.addBox(0F, 0F, 0F, 1, 5, 1);
      leg1.setRotationPoint(3F, 19.3F, -4F);
      leg1.setTextureSize(32, 32);
      leg1.mirror = true;
      setRotation(leg1, 0F, 0F, 0F);
      leg4 = new ModelRenderer(this, 0, 0);
      leg4.addBox(0F, 0F, 0F, 1, 5, 1);
      leg4.setRotationPoint(-4F, 19.3F, 3F);
      leg4.setTextureSize(32, 32);
      leg4.mirror = true;
      setRotation(leg4, 0F, 0F, 0F);
      leg2 = new ModelRenderer(this, 0, 0);
      leg2.addBox(0F, 0F, 0F, 1, 5, 1);
      leg2.setRotationPoint(-4F, 19.3F, -4F);
      leg2.setTextureSize(32, 32);
      leg2.mirror = true;
      setRotation(leg2, 0F, 0F, 0F);
      right = new ModelRenderer(this, 0, 7);
      right.addBox(0F, 0F, 0F, 1, 3, 6);
      right.setRotationPoint(-4F, 19.3F, -3F);
      right.setTextureSize(32, 32);
      right.mirror = true;
      setRotation(right, 0F, 0F, 0F);
      rightAngle = new ModelRenderer(this, 0, 16);
      rightAngle.addBox(0F, 0F, 0F, 1, 2, 6);
      rightAngle.setRotationPoint(-4F, 22.3F, -3F);
      rightAngle.setTextureSize(32, 32);
      rightAngle.mirror = true;
      setRotation(rightAngle, 0F, 0F, -0.5235988F);
      pestleKnob = new ModelRenderer(this, 20, 0);
      pestleKnob.addBox(-0.5F, -1.5F, -0.5F, 2, 2, 2);
      pestleKnob.setRotationPoint(1F, 23F, -2F);
      pestleKnob.setTextureSize(32, 32);
      pestleKnob.mirror = true;
      setRotation(pestleKnob, 0.4363323F, -0.9424778F, 0F);
      pestle = new ModelRenderer(this, 24, 0);
      pestle.addBox(0F, -6F, 0F, 1, 6, 1);
      pestle.setRotationPoint(1F, 23F, -2F);
      pestle.setTextureSize(32, 32);
      pestle.mirror = true;
      setRotation(pestle, 0.4363323F, -0.9424778F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    backAngle.render(f5);
    bottom.render(f5);
    back.render(f5);
    leftAngle.render(f5);
    frontAngle.render(f5);
    leg3.render(f5);
    left.render(f5);
    front.render(f5);
    leg1.render(f5);
    leg4.render(f5);
    leg2.render(f5);
    right.render(f5);
    rightAngle.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
  }

}
