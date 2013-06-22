package xreliquary.items;

import java.util.List;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSlug extends ItemXR {
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.SLUG_NAME);
	}

	protected ItemSlug(int par1) {
		super(par1);
		this.setMaxStackSize(64);
		canRepair = false;
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("A standard stone");
		par3List.add("slug, for killing.");
	}
}
