package xreliquary.client.render;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.init.ModItems;


@SideOnly(Side.CLIENT)
public class RenderThrownXRPotion extends RenderSnowball<EntityThrownXRPotion> {
    public RenderThrownXRPotion(RenderManager renderManager, RenderItem itemRenderer)
    {
        super( renderManager, ModItems.potionEssence, itemRenderer );
    }

    public ItemStack func_177082_d(EntityThrownXRPotion entityIn)
    {
        ItemStack potion = new ItemStack(this.field_177084_a, 1);
        potion.readFromNBT(entityIn.getNBTTagCompound());

        return potion;
    }

}
