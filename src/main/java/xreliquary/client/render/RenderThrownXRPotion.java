package xreliquary.client.render;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.init.ModItems;
import xreliquary.util.NBTHelper;

@SideOnly(Side.CLIENT)
public class RenderThrownXRPotion extends RenderSnowball<EntityThrownXRPotion> {
	public RenderThrownXRPotion(RenderManager renderManager, RenderItem itemRenderer) {
		super(renderManager, ModItems.potion, itemRenderer);
	}

	public ItemStack getPotion(EntityThrownXRPotion entityIn) {
		ItemStack potion = new ItemStack(this.field_177084_a, 1);
		NBTHelper.setBoolean("splash", potion, true);
		NBTHelper.setInteger("renderColor", potion, entityIn.getRenderColor());
		return potion;
	}

}
