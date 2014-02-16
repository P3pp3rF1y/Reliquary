package xreliquary.items;

import java.util.List;

import mods.themike.core.item.ItemBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import xreliquary.Reliquary;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBullet extends ItemBase {

	// 0 = Empty, 1 = Neutral, 2 = Exorcism, 3 = Blaze
	// 4 = Ender, 5 = Concussive, 6 = Buster, 7 = Seeker
	// 8 = Sand, 9 = Storm

	public ItemBullet() {
		super(Reference.MOD_ID, Names.BULLET_NAME);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(64);
		this.setHasSubtypes(true);
		canRepair = false;
	}

	@SideOnly(Side.CLIENT)
	private IIcon iconOverlay;

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		super.registerIcons(iconRegister);
		iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.BULLET_OVERLAY_NAME);
	}

	@Override
	public IIcon getIcon(ItemStack itemStack, int renderPass) {
		if (itemStack.getItemDamage() == 0)
			return this.itemIcon;
		if (renderPass != 1)
			return this.itemIcon;
		else
			return iconOverlay;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {

		if (renderPass == 1)
			return getColor(itemStack);
		else
			return Integer.parseInt(Colors.PURE, 16);
	}

	public int getColor(ItemStack itemStack) {

		switch (itemStack.getItemDamage()) {
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

	// public boolean hasColor(ItemStack itemStack) {
	//
	// return itemStack.getItemDamage() != 0;
	// }

	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item.reliquaryBullet" + ist.getItemDamage();
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
