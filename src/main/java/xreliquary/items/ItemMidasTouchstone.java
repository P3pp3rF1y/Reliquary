package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

import java.util.List;

public class ItemMidasTouchstone extends ItemToggleable {

	public ItemMidasTouchstone() {
		super(Names.midas_touchstone);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List<String> list, boolean par4) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;
		this.formatTooltip(ImmutableMap.of("charge", Integer.toString(NBTHelper.getInteger("glowstone", ist))), ist, list);
		if(this.isEnabled(ist))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.YELLOW + Items.GLOWSTONE_DUST.getItemStackDisplayName(new ItemStack(Items.GLOWSTONE_DUST))), ist, list);
		LanguageHelper.formatTooltip("tooltip.absorb", null, ist, list);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
		if(world.isRemote)
			return;
		EntityPlayer player;
		if(e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		} else
			return;

		//don't drain glowstone if it isn't activated.
		if(this.isEnabled(ist)) {
			if(NBTHelper.getInteger("glowstone", ist) + getGlowStoneWorth() <= getGlowstoneLimit()) {
				if(InventoryHelper.consumeItem(new ItemStack(Items.GLOWSTONE_DUST), player)) {
					NBTHelper.setInteger("glowstone", ist, NBTHelper.getInteger("glowstone", ist) + getGlowStoneWorth());
				}
			}
		}

		if(world.getWorldTime() % 4 == 0) {
			doRepairAndDamageTouchstone(ist, player);
		}
	}

	private void doRepairAndDamageTouchstone(ItemStack ist, EntityPlayer player) {
		//list of customizable items added through configs that can be repaired by the touchstone.
		List<String> goldItems = Settings.MidasTouchstone.goldItems;

		for(int slot = 0; slot < player.inventory.armorInventory.length; slot++) {
			if(player.inventory.armorInventory[slot] == null) {
				continue;
			}
			if(!(player.inventory.armorInventory[slot].getItem() instanceof ItemArmor)) {
				continue;
			}
			ItemArmor armor = (ItemArmor) player.inventory.armorInventory[slot].getItem();
			if(armor.getArmorMaterial() != ItemArmor.ArmorMaterial.GOLD && !goldItems.contains(RegistryHelper.getItemRegistryName(armor))) {
				continue;
			}
			if(player.inventory.armorInventory[slot].getItemDamage() <= 0) {
				continue;
			}
			if(decrementTouchStoneCharge(ist, player)) {
				player.inventory.armorInventory[slot].setItemDamage(player.inventory.armorInventory[slot].getItemDamage() - 1);
			}
		}
		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if(player.inventory.mainInventory[slot] == null) {
				continue;
			}
			if(player.inventory.mainInventory[slot].getItem() instanceof ItemSword) {
				ItemSword sword = (ItemSword) player.inventory.mainInventory[slot].getItem();
				if(sword.getToolMaterialName() != ItemSword.ToolMaterial.GOLD.name() && !goldItems.contains(RegistryHelper.getItemRegistryName(sword))) {
					continue;
				}
				if(player.inventory.mainInventory[slot].getItemDamage() <= 0) {
					continue;
				}
				if(decrementTouchStoneCharge(ist, player)) {
					player.inventory.mainInventory[slot].setItemDamage(player.inventory.mainInventory[slot].getItemDamage() - 1);
				}
			} else if(player.inventory.mainInventory[slot].getItem() instanceof ItemTool) {
				ItemTool tool = (ItemTool) player.inventory.mainInventory[slot].getItem();
				if(tool.getToolMaterialName() != ItemSword.ToolMaterial.GOLD.name() && !goldItems.contains(RegistryHelper.getItemRegistryName(tool))) {
					continue;
				}
				if(player.inventory.mainInventory[slot].getItemDamage() <= 0) {
					continue;
				}
				if(decrementTouchStoneCharge(ist, player)) {
					player.inventory.mainInventory[slot].setItemDamage(player.inventory.mainInventory[slot].getItemDamage() - 1);
				}
			} else {
				Item item = player.inventory.mainInventory[slot].getItem();
				if(!goldItems.contains(RegistryHelper.getItemRegistryName(item))) {
					continue;
				}
				if(player.inventory.mainInventory[slot].getItemDamage() <= 0 || !item.isDamageable()) {
					continue;
				}
				if(decrementTouchStoneCharge(ist, player)) {
					player.inventory.mainInventory[slot].setItemDamage(player.inventory.mainInventory[slot].getItemDamage() - 1);
				}
			}
		}
	}

	private boolean decrementTouchStoneCharge(ItemStack ist, EntityPlayer player) {
		if(NBTHelper.getInteger("glowstone", ist) - getGlowStoneCost() >= 0 || player.capabilities.isCreativeMode) {
			if(!player.capabilities.isCreativeMode)
				NBTHelper.setInteger("glowstone", ist, NBTHelper.getInteger("glowstone", ist) - getGlowStoneCost());
			return true;
		}
		return false;
	}

	private int getGlowStoneCost() {
		return Settings.MidasTouchstone.glowstoneCost;
	}

	private int getGlowStoneWorth() {
		return Settings.MidasTouchstone.glowstoneWorth;
	}

	private int getGlowstoneLimit() {
		return Settings.MidasTouchstone.glowstoneLimit;
	}
}
