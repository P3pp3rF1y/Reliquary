package xreliquary.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMortar extends ModelBase
{
  //fields
    ModelRenderer mortar;
    ModelRenderer pestle;
  
  public ModelMortar()
  {
    textureWidth = 64;
    textureHeight = 32;
    setTextureOffset("mortar.bottom", 0, 0);
    setTextureOffset("mortar.sideBack", 14, 16);
    setTextureOffset("mortar.sideRight", 14, 7);
    setTextureOffset("mortar.sideLeft", 0, 7);
    setTextureOffset("mortar.sideFront", 0, 16);
    
    mortar = new ModelRenderer(this, "mortar");
    mortar.setRotationPoint(-4F, 20F, -4F);
    setRotation(mortar, 0F, 0F, 0F);
    mortar.mirror = true;
      mortar.addBox("bottom", 1F, 3F, 1F, 6, 1, 6);
      mortar.addBox("sideBack", 1F, 0F, 7F, 6, 3, 1);
      mortar.addBox("sideRight", 0F, 0F, 1F, 1, 3, 6);
      mortar.addBox("sideLeft", 7F, 0F, 1F, 1, 3, 6);
      mortar.addBox("sideFront", 1F, 0F, 0F, 6, 3, 1);
      pestle = new ModelRenderer(this, 24, 0);
      pestle.addBox(0F, 0F, 0F, 6, 1, 1);
      pestle.setRotationPoint(4F, 19F, 0F);
      pestle.setTextureSize(64, 32);
      pestle.mirror = true;
      setRotation(pestle, 0F, -0.2974289F, 2.41661F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    mortar.render(f5);
    pestle.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity ent)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, ent);
  }

}
