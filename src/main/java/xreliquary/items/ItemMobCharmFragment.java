package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import java.util.List;

/**
 * Created by Xeno on 10/11/2014.
 */
public class ItemMobCharmFragment extends ItemBase {
	public ItemMobCharmFragment() {
		super(Names.Items.MOB_CHARM_FRAGMENT);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		this.setHasSubtypes(true);
		canRepair = false;
	}

	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item.mob_charm_fragment_" + ist.getItemDamage();
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean whatDoesThisEvenDo) {
	}

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		for(int i = 0; i < Reference.MOB_CHARM.COUNT_TYPES; i++)
			par3List.add(new ItemStack(par1, 1, i));
	}
}
