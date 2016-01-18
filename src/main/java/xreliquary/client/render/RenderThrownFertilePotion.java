package xreliquary.client.render;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.ItemStack;
import xreliquary.entities.potion.EntityAttractionPotion;
import xreliquary.entities.potion.EntityFertilePotion;
import xreliquary.init.ModItems;

public class RenderThrownFertilePotion extends RenderSnowball<EntityFertilePotion> {
    public RenderThrownFertilePotion(RenderManager renderManager, RenderItem itemRenderer)
    {
        super( renderManager, ModItems.fertilePotion, itemRenderer );
    }

    public ItemStack func_177082_d(EntityFertilePotion entityIn)
    {
        ItemStack potion = new ItemStack(this.field_177084_a, 1);

        return potion;
    }

}
