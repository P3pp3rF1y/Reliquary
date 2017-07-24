package xreliquary.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
	public void addInformation(ItemStack fragment, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs creativeTab, @Nonnull NonNullList<ItemStack> list) {
		for(int i = 0; i < Reference.MOB_CHARM.COUNT_TYPES; i++)
			list.add(new ItemStack(this, 1, i));
	}
}
