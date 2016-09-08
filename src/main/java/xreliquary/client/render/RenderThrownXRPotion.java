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

	@Override
	public ItemStack getStackToRender(EntityThrownXRPotion entityIn) {
		ItemStack potion = new ItemStack(this.item, 1);
		NBTHelper.setBoolean("splash", potion, !entityIn.getLingering());
		NBTHelper.setBoolean("lingering", potion, entityIn.getLingering());
		NBTHelper.setInteger("renderColor", potion, entityIn.getRenderColor());
		return potion;
	}

}
