package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.reference.Colors;
import xreliquary.reference.Names;

import java.util.List;

public class ItemBullet extends ItemBase {

	// 0 = Empty, 1 = Neutral, 2 = Exorcism, 3 = Blaze
	// 4 = Ender, 5 = Concussive, 6 = Buster, 7 = Seeker
	// 8 = Sand, 9 = Storm

	public ItemBullet() {
		super(Names.bullet);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(64);
		this.setHasSubtypes(true);
		canRepair = false;
	}

	public int getColor(ItemStack itemStack) {

		switch(itemStack.getItemDamage()) {
			case 1:
				return Integer.parseInt(Colors.NEUTRAL_SHOT_COLOR, 16);
			case 2:
				return Integer.parseInt(Colors.EXORCISM_SHOT_COLOR, 16);
			case 3:
				return Integer.parseInt(Colors.BLAZE_SHOT_COLOR, 16);
			case 4:
				return Integer.parseInt(Colors.ENDER_SHOT_COLOR, 16);
			case 5:
				return Integer.parseInt(Colors.CONCUSSIVE_SHOT_COLOR, 16);
			case 6:
				return Integer.parseInt(Colors.BUSTER_SHOT_COLOR, 16);
			case 7:
				return Integer.parseInt(Colors.SEEKER_SHOT_COLOR, 16);
			case 8:
				return Integer.parseInt(Colors.SAND_SHOT_COLOR, 16);
			case 9:
				return Integer.parseInt(Colors.STORM_SHOT_COLOR, 16);
		}
		return Integer.parseInt(Colors.PURE, 16);
	}

	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item." + Names.bullet + "_" + ist.getItemDamage();
	}

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
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
