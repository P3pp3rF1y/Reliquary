package xreliquary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.init.ModItems;

class CreativeTabXR extends CreativeTabs {

	public CreativeTabXR(int ID, String langName) {
		super(ID, langName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return ModItems.mercyCross;
	}

}
