package reliquary;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import reliquary.init.ModItems;
import reliquary.reference.Reference;

class ReliquaryItemGroup extends CreativeModeTab {
	private ItemStack tabIcon;

	ReliquaryItemGroup() {
		super(Reference.MOD_ID);
	}

	@Override
	public ItemStack makeIcon() {
		if (tabIcon == null) {
			tabIcon = new ItemStack(ModItems.MERCY_CROSS.get());
		}
		return tabIcon;
	}
}
