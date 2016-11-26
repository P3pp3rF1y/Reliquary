package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

import javax.annotation.Nonnull;

/**
 * Created by Xeno on 10/11/2014.
 * This stores all the "ingredient" items used by the mod that don't actually do anything.
 * Right now I've dumped them all into meta and used the shared name "mob_ingredient" even though
 * they're not all technically mob drops. It was mostly just a way for me to clean up our items folder,
 * and make the creative tabs better organized.
 */
public class ItemMobIngredient extends ItemBase {

	public ItemMobIngredient() {
		super(Names.Items.MOB_INGREDIENT);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		canRepair = false;
		this.setHasSubtypes(true);
	}

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item.mob_ingredient_" + ist.getItemDamage();
	}

	@Override
	public void getSubItems(@Nonnull Item item, CreativeTabs creativeTab, NonNullList<ItemStack> list) {
		for(int i = 0; i <= 16; i++)
			list.add(new ItemStack(item, 1, i));
	}
}
