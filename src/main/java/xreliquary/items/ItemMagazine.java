package xreliquary.items;

import java.util.List;

import mods.themike.core.item.ItemBase;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import xreliquary.Reliquary;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMagazine extends ItemBase {

	protected ItemMagazine(int par1) {
		super(par1, Reference.MOD_ID, Names.MAGAZINE_NAME);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(64);
		canRepair = false;
		this.setHasSubtypes(true);
	}

	@SideOnly(Side.CLIENT)
	private Icon iconOverlay;

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		super.registerIcons(iconRegister);
		iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.MAGAZINE_OVERLAY_NAME);
	}

	@Override
	public Icon getIcon(ItemStack itemStack, int renderPass) {
		if (itemStack.getItemDamage() == 0 || renderPass != 1)
			return this.itemIcon;
		else
			return iconOverlay;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
		if (itemStack.getItemDamage() == 0 || renderPass != 1)
			return Integer.parseInt(Colors.DARKER, 16);
		else
			return getColor(itemStack);
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
		return Integer.parseInt(Colors.DARKEST, 16);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		if (stack.getItemDamage() == 0) {
			list.add(LanguageRegistry.instance().getStringLocalization("item." + Names.MAGAZINE_NAME + ".tooltip"));
		} else {
			list.add(LanguageRegistry.instance().getStringLocalization("item." + Names.BULLET_NAME + stack.getItemDamage() + ".tooltip"));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item.reliquaryMagazine" + ist.getItemDamage();
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
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
