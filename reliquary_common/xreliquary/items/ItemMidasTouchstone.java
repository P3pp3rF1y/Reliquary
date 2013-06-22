package xreliquary.items;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import xreliquary.lib.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMidasTouchstone extends ItemWithCapacity {
	public ItemMidasTouchstone(int par1) {
		super(par1);
		this.setUnlocalizedName(Names.TOUCHSTONE_NAME);
		this.DEFAULT_TARGET_ITEM = new ItemStack(Item.lightStoneDust, 1, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	protected boolean isActive(ItemStack ist) {
		return true;
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List infoList, boolean par4) {
		infoList.add("Repairs items made of gold;");
		infoList.add("consumes glowstone dust.");
		super.addInformation(ist, player, infoList, par4);
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		super.onUpdate(ist, world, e, i, f);
		if (!(e instanceof EntityPlayer)) return;
		if (getCooldown(ist) == 0) {
			doRepair(ist, (EntityPlayer)e);
		}
	}

	private void doRepair(ItemStack ist, EntityPlayer player) {
		// handles armor repair only in first for loop
		for (int slot = 0; slot < player.inventory.armorInventory.length; slot++) {
			ItemStack armorStack = player.inventory.armorInventory[slot];
			if (armorStack == null || !(armorStack.getItem() instanceof ItemArmor)) {
				continue;
			}
			ItemArmor armor = (ItemArmor)armorStack.getItem();
			if (armor.getArmorMaterial() != EnumArmorMaterial.GOLD || armorStack.getItemDamage() <= 3) {
				continue;
			}
			if (this.decreaseQuantity(ist)) {
				player.inventory.armorInventory[slot].setItemDamage(armorStack.getItemDamage() - 4);
			}
		}
		for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if (player.inventory.mainInventory[slot] == null) {
				continue;
			}
			if (player.inventory.mainInventory[slot].getItem() instanceof ItemSword) {
				ItemSword sword = (ItemSword)player.inventory.mainInventory[slot].getItem();
				if (sword.getToolMaterialName() != "GOLD") {
					continue;
				}
				if (player.inventory.mainInventory[slot].getItemDamage() <= 3) {
					continue;
				}
				if (this.decreaseQuantity(ist)) {
					player.inventory.mainInventory[slot].setItemDamage(player.inventory.mainInventory[slot].getItemDamage() - 4);
				}
			} else if (player.inventory.mainInventory[slot].getItem() instanceof ItemTool) {
				ItemTool tool = (ItemTool)player.inventory.mainInventory[slot].getItem();
				if (tool.getToolMaterialName() != "GOLD") {
					continue;
				}
				if (player.inventory.mainInventory[slot].getItemDamage() <= 3) {
					continue;
				}
				if (this.decreaseQuantity(ist)) {
					player.inventory.mainInventory[slot].setItemDamage(player.inventory.mainInventory[slot].getItemDamage() - 4);
				}
			}
		}
		setCooldown(ist, 4);
	}
}
