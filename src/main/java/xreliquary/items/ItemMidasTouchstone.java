package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemMidasTouchstone extends ItemToggleable {

	public ItemMidasTouchstone() {
		super(Names.Items.MIDAS_TOUCHSTONE);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	protected void addMoreInformation(ItemStack touchstone, @Nullable World world, List<String> tooltip) {
		LanguageHelper.formatTooltip(getUnlocalizedNameInefficiently(touchstone) + ".tooltip2", ImmutableMap.of("charge", Integer.toString(NBTHelper.getInteger("glowstone", touchstone))), tooltip);
		if(this.isEnabled(touchstone))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.YELLOW + Items.GLOWSTONE_DUST.getItemStackDisplayName(new ItemStack(Items.GLOWSTONE_DUST))), tooltip);
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Nonnull
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
		String[] goldItems = Settings.Items.MidasTouchstone.goldItems;

		for(int slot = 0; slot < player.inventory.armorInventory.size(); slot++) {
			ItemStack armorStack = player.inventory.armorInventory.get(slot);
			if(armorStack.isEmpty() || !(armorStack.getItem() instanceof ItemArmor))
				continue;
			ItemArmor armor = (ItemArmor) armorStack.getItem();
			if(armor.getArmorMaterial() != ItemArmor.ArmorMaterial.GOLD && !ArrayUtils.contains(goldItems, RegistryHelper.getItemRegistryName(armor))) {
				continue;
			}
			if(armorStack.getItemDamage() <= 0) {
				continue;
			}
			if(decrementTouchStoneCharge(ist, player)) {
				armorStack.setItemDamage(armorStack.getItemDamage() - 1);
			}
		}
		for(int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			ItemStack stack = player.inventory.mainInventory.get(slot);
			Item item = stack.getItem();

			if(item instanceof ItemSword) {
				ItemSword sword = (ItemSword) item;
				if(!ItemSword.ToolMaterial.GOLD.name().equals(sword.getToolMaterialName()) && !ArrayUtils.contains(goldItems, RegistryHelper.getItemRegistryName(sword))) {
					continue;
				}
				if(stack.getItemDamage() <= 0) {
					continue;
				}
				if(decrementTouchStoneCharge(ist, player)) {
					stack.setItemDamage(stack.getItemDamage() - 1);
				}
			} else if(item instanceof ItemTool) {
				ItemTool tool = (ItemTool) item;
				if(!ItemSword.ToolMaterial.GOLD.name().equals(tool.getToolMaterialName()) && !ArrayUtils.contains(goldItems, RegistryHelper.getItemRegistryName(tool))) {
					continue;
				}
				if(stack.getItemDamage() <= 0) {
					continue;
				}
				if(decrementTouchStoneCharge(ist, player)) {
					stack.setItemDamage(stack.getItemDamage() - 1);
				}
			} else {
				if(!ArrayUtils.contains(goldItems, RegistryHelper.getItemRegistryName(item))) {
					continue;
				}
				if(stack.getItemDamage() <= 0 || !item.isDamageable()) {
					continue;
				}
				if(decrementTouchStoneCharge(ist, player)) {
					stack.setItemDamage(stack.getItemDamage() - 1);
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
		return Settings.Items.MidasTouchstone.glowstoneCost;
	}

	private int getGlowStoneWorth() {
		return Settings.Items.MidasTouchstone.glowstoneWorth;
	}

	private int getGlowstoneLimit() {
		return Settings.Items.MidasTouchstone.glowstoneLimit;
	}
}
