package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.util.LanguageHelper;

import java.util.List;

public class ItemMagazine extends ItemBase {

	public ItemMagazine() {
		super(Names.magazine);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(64);
		canRepair = false;
		this.setHasSubtypes(true);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
		//TODO get rid of this, obviously at some point magazines were part of this class
		if(stack.getItemDamage() < 2) {
			//list.add(LanguageHelper.getLocalization("item." + Names.magazine + "_" + stack.getItemDamage() + ".tooltip"));
		} else {
			list.add(LanguageHelper.getLocalization("item." + Names.bullet + "_" + stack.getItemDamage() + ".tooltip"));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item." + Names.magazine + "_" + ist.getItemDamage();
	}

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 1));
		par3List.add(new ItemStack(par1, 1, 2));
		par3List.add(new ItemStack(par1, 1, 3));
		par3List.add(new ItemStack(par1, 1, 4));
		par3List.add(new ItemStack(par1, 1, 5));
		par3List.add(new ItemStack(par1, 1, 6));
		par3List.add(new ItemStack(par1, 1, 7));
		par3List.add(new ItemStack(par1, 1, 8));
		par3List.add(new ItemStack(par1, 1, 9));
	}

}
