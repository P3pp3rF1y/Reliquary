package xreliquary;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

class ReliquaryItemGroup extends ItemGroup {
	private ItemStack tabIcon;

	ReliquaryItemGroup() {
		super(Reference.MOD_ID);
	}

	@Override
	public ItemStack createIcon() {
		if (tabIcon == null) {
			tabIcon = new ItemStack(ModItems.MERCY_CROSS);
		}
		return tabIcon;
	}
}
