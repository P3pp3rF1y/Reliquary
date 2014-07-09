package xreliquary.items;

import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import lib.enderwizards.sandstone.init.ContentInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.NBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ContentInit
public class ItemMidasTouchstone extends ItemBase {

	public ItemMidasTouchstone() {
		super(Names.midas_touchstone);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(257);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack, int pass) {
		return stack.getItemDamage() != 0;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		if (world.isRemote)
			return;
		EntityPlayer player = null;
		if (e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		} else
			return;
		if (ist.getItemDamage() == 0 || ist.getItemDamage() > 4) {
			if (findAndConsumeGlowstoneDust(ist, player)) {
				ist.setItemDamage(ist.getItemDamage() == 0 ? 251 : ist.getItemDamage() - 4);
			}
		}
		if (getCooldown(ist) == 0) {
			doRepairAndDamageTouchstone(ist, player);
		} else {
			decrementCooldown(ist);
		}
	}

	private void decrementCooldown(ItemStack ist) {
		NBTHelper.setShort("cooldown", ist, NBTHelper.getShort("cooldown", ist) - 1);
	}

	private int getCooldown(ItemStack ist) {
		return NBTHelper.getShort("cooldown", ist);
	}

	private boolean findAndConsumeGlowstoneDust(ItemStack ist, EntityPlayer player) {
		for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if (player.inventory.mainInventory[slot] == null) {
				continue;
			}
			if (player.inventory.mainInventory[slot].getItem() == Items.glowstone_dust) {
				player.inventory.decrStackSize(slot, 1);
				return true;
			}
		}
		return false;
	}

	private void doRepairAndDamageTouchstone(ItemStack ist, EntityPlayer player) {
		for (int slot = 0; slot < player.inventory.armorInventory.length; slot++) {
			if (player.inventory.armorInventory[slot] == null) {
				continue;
			}
			if (!(player.inventory.armorInventory[slot].getItem() instanceof ItemArmor)) {
				continue;
			}
			ItemArmor armor = (ItemArmor) player.inventory.armorInventory[slot].getItem();
			if (armor.getArmorMaterial() != ItemArmor.ArmorMaterial.GOLD) {
				continue;
			}
			if (player.inventory.armorInventory[slot].getItemDamage() <= 0) {
				continue;
			}
			if (decrementTouchStoneCharge(ist)) {
				player.inventory.armorInventory[slot].setItemDamage(player.inventory.armorInventory[slot].getItemDamage() - 1);
			}
		}
		for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if (player.inventory.mainInventory[slot] == null) {
				continue;
			}
			if (player.inventory.mainInventory[slot].getItem() instanceof ItemSword) {
				ItemSword sword = (ItemSword) player.inventory.mainInventory[slot].getItem();
				if (sword.getToolMaterialName() != "GOLD") {
					continue;
				}
				if (player.inventory.mainInventory[slot].getItemDamage() <= 0) {
					continue;
				}
				if (decrementTouchStoneCharge(ist)) {
					player.inventory.mainInventory[slot].setItemDamage(player.inventory.mainInventory[slot].getItemDamage() - 1);
				}
			} else if (player.inventory.mainInventory[slot].getItem() instanceof ItemTool) {
				ItemTool tool = (ItemTool) player.inventory.mainInventory[slot].getItem();
				if (tool.getToolMaterialName() != "GOLD") {
					continue;
				}
				if (player.inventory.mainInventory[slot].getItemDamage() <= 0) {
					continue;
				}
				if (decrementTouchStoneCharge(ist)) {
					player.inventory.mainInventory[slot].setItemDamage(player.inventory.mainInventory[slot].getItemDamage() - 1);
				}
			}
		}
		setCooldown(ist);
	}

	private void setCooldown(ItemStack ist) {
		NBTHelper.setShort("cooldown", ist, 4);
	}

	private boolean decrementTouchStoneCharge(ItemStack ist) {
		if (ist.getItemDamage() != 0 && ist.getItemDamage() < ist.getMaxDamage() - 1) {
			ist.setItemDamage(ist.getItemDamage() + 1);
			return true;
		}
		return false;
	}
}
