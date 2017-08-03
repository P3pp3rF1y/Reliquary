package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

import javax.annotation.Nonnull;

public class ItemGunPart extends ItemBase {

	public ItemGunPart() {
		super(Names.Items.GUN_PART);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(4);
		this.setHasSubtypes(true);
		canRepair = false;
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
		if (!isInCreativeTab(tab))
			return;

		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
		list.add(new ItemStack(this, 1, 2));
	}

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if(stack.getItemDamage() > 2) {
			return "item." + Names.Items.GUN_PART + "_" + "0";
		}
		return "item." + Names.Items.GUN_PART + "_" + String.valueOf(stack.getItemDamage());
	}
}
