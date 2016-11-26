package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;

public class ItemMobCharmFragment extends ItemBase {
	public ItemMobCharmFragment() {
		super(Names.Items.MOB_CHARM_FRAGMENT);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		this.setHasSubtypes(true);
		canRepair = false;
	}

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.mob_charm_fragment_" + stack.getItemDamage();
	}

	@Override
	public void getSubItems(@Nonnull Item item, CreativeTabs creativeTab, NonNullList<ItemStack> list) {
		for(int i = 0; i < Reference.MOB_CHARM.COUNT_TYPES; i++)
			list.add(new ItemStack(item, 1, i));
	}
}
