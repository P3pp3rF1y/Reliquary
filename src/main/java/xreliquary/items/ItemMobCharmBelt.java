package xreliquary.items;

import baubles.api.BaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

public class ItemMobCharmBelt extends ItemBauble {
	public ItemMobCharmBelt() {
		super(Names.mob_charm_belt);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	public BaubleType getBaubleType(ItemStack stack) {
		return BaubleType.BELT;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {

	}
}
