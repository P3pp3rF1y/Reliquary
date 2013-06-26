package xreliquary.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemXR extends Item {
	protected ItemXR(int par1) {
		super(par1);
		this.setCreativeTab(Reliquary.tabsXR);
	}

	public int getShort(String s, ItemStack ist) {
		if (ist.getTagCompound() == null) {
			ist.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tagCompound = ist.getTagCompound();
		if (tagCompound.getShort(s) == 0) {
			tagCompound.setShort(s, (short)0);
		}
		return tagCompound.getShort(s);
	}

	public void setShort(String s, ItemStack ist, int i) {
		if (ist.getTagCompound() == null) {
			ist.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tagCompound = ist.getTagCompound();
		tagCompound.setShort(s, (short)i);
	}

	public boolean getBoolean(String s, ItemStack ist) {
		if (ist.getTagCompound() == null) {
			ist.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tagCompound = ist.getTagCompound();
		if (tagCompound.getBoolean(s) == false) {
			tagCompound.setBoolean(s, false);
		}
		return tagCompound.getBoolean(s);
	}

	public void setBoolean(String s, ItemStack ist, boolean b) {
		if (ist.getTagCompound() == null) {
			ist.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tagCompound = ist.getTagCompound();
		tagCompound.setBoolean(s, b);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
	}

	protected boolean isOnCooldown(ItemStack ist) {
		return getCooldown(ist) > 0;
	}

	protected void decrementCooldown(ItemStack ist) {
		if (!isOnCooldown(ist)) return;
		setCooldown(ist, getCooldown(ist) - 1);
	}

	protected void setCooldown(ItemStack ist, int i) {
		setShort("cooldown", ist, (short)i);
	}

	protected int getCooldown(ItemStack ist) {
		return this.getShort("cooldown", ist);
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		decrementCooldown(ist);
	}
}
