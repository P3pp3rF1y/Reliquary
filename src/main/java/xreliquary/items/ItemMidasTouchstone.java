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

import javax.annotation.Nonnull;
import java.util.List;

public class ItemMidasTouchstone extends ItemToggleable {

	public ItemMidasTouchstone() {
		super(Names.Items.MIDAS_TOUCHSTONE);
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
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.YELLOW + Items.GLOWSTONE_DUST.getItemStackDisplayName(new ItemStack(Items.GLOWSTONE_DUST))), list);
		LanguageHelper.formatTooltip("tooltip.absorb", null, list);
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
		List<String> goldItems = Settings.MidasTouchstone.goldItems;

		for(int slot = 0; slot < player.inventory.armorInventory.size(); slot++) {
			ItemStack armorStack = player.inventory.armorInventory.get(slot);
			if (armorStack.isEmpty())
				continue;
			ItemArmor armor = (ItemArmor) armorStack.getItem();
			if(armor.getArmorMaterial() != ItemArmor.ArmorMaterial.GOLD && !goldItems.contains(RegistryHelper.getItemRegistryName(armor))) {
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
				if(!ItemSword.ToolMaterial.GOLD.name().equals(sword.getToolMaterialName()) && !goldItems.contains(RegistryHelper.getItemRegistryName(sword))) {
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
				if(!ItemSword.ToolMaterial.GOLD.name().equals(tool.getToolMaterialName()) && !goldItems.contains(RegistryHelper.getItemRegistryName(tool))) {
					continue;
				}
				if(stack.getItemDamage() <= 0) {
					continue;
				}
				if(decrementTouchStoneCharge(ist, player)) {
					stack.setItemDamage(stack.getItemDamage() - 1);
				}
			} else {
				if(!goldItems.contains(RegistryHelper.getItemRegistryName(item))) {
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
		return Settings.MidasTouchstone.glowstoneCost;
	}

	private int getGlowStoneWorth() {
		return Settings.MidasTouchstone.glowstoneWorth;
	}

	private int getGlowstoneLimit() {
		return Settings.MidasTouchstone.glowstoneLimit;
	}
}
