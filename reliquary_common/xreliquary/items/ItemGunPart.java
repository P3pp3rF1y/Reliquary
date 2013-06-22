package xreliquary.items;

import java.util.List;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGunPart extends ItemXR {
	@SideOnly(Side.CLIENT)
	private Icon[] icons;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		icons = new Icon[3];
		for (int i = 0; i < 3; i++) {
			icons[i] = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.GUNPART_NAME + i);
		}
	}

	@Override
	public Icon getIconFromDamage(int meta) {
		if (meta < 3) return icons[meta];
		return icons[0];
	}

	protected ItemGunPart(int par1) {
		super(par1);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		canRepair = false;
		this.setHasSubtypes(true);
		this.setUnlocalizedName(Names.GUNPART_NAME);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("It's one of three gun parts.");
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 1));
		par3List.add(new ItemStack(par1, 1, 2));
	}

	public String getItemNameIS(ItemStack ist) {
		switch (ist.getItemDamage()) {
			case 0:
				return "gunPart0";
			case 1:
				return "gunPart1";
			case 2:
				return "gunPart2";
			default:
				return "gunPart0";
		}
	}
}
