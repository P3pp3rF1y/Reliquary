package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

import java.util.List;

/**
 * Created by Xeno on 10/11/2014.
 */
public class ItemHeartZhu extends ItemBase {
	public ItemHeartZhu() {
		super(Names.heart_zhu);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		canRepair = false;
	}

	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item.heart_zhu_" + ist.getItemDamage();
	}

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for(int i = 0; i < 4; i++)
			par3List.add(new ItemStack(par1, 1, i));
	}
}
