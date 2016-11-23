package xreliquary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;

class CreativeTabXR extends CreativeTabs {

	private static final ItemStack TAB_ICON = new ItemStack(ModItems.mercyCross);

	CreativeTabXR(int ID) {
		super(ID, Reference.MOD_ID);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getTabIconItem() {
		return TAB_ICON;
	}

}
