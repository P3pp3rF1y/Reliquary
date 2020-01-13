package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.item.TieredItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MidasTouchstoneItem extends ToggleableItem {
	private static final Map<Class<? extends Item>, IRepairableItem> REPAIRABLE_ITEMS = new ImmutableMap.Builder<Class<? extends Item>, IRepairableItem>()
			.put(TieredItem.class, item -> ((TieredItem) item).getTier().equals(ItemTier.GOLD))
			.put(ArmorItem.class, item -> ((ArmorItem) item).getArmorMaterial().equals(ArmorMaterial.GOLD))
			.build();
	private static final String GLOWSTONE_TAG = "glowstone";

	public MidasTouchstoneItem() {
		super(Names.Items.MIDAS_TOUCHSTONE, new Properties().maxStackSize(1));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack touchstone, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2", ImmutableMap.of("charge", Integer.toString(NBTHelper.getInt(GLOWSTONE_TAG, touchstone))), tooltip);
		if (isEnabled(touchstone)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.YELLOW + Items.GLOWSTONE_DUST.getDisplayName(new ItemStack(Items.GLOWSTONE_DUST)).getString()), tooltip);
		}
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity e, int i, boolean f) {
		if (world.isRemote) {
			return;
		}
		PlayerEntity player;
		if (e instanceof PlayerEntity) {
			player = (PlayerEntity) e;
		} else {
			return;
		}

		//don't drain glowstone if it isn't activated.
		if (isEnabled(stack) && NBTHelper.getInt(GLOWSTONE_TAG, stack) + getGlowStoneWorth() <= getGlowstoneLimit() && InventoryHelper.consumeItem(new ItemStack(Items.GLOWSTONE_DUST), player)) {
			NBTHelper.putInt(GLOWSTONE_TAG, stack, NBTHelper.getInt(GLOWSTONE_TAG, stack) + getGlowStoneWorth());
		}

		if (world.getGameTime() % 4 == 0) {
			doRepairAndDamageTouchstone(stack, player);
		}
	}

	private void doRepairAndDamageTouchstone(ItemStack touchstone, PlayerEntity player) {
		//list of customizable items added through configs that can be repaired by the touchstone.
		List<String> goldItems = Settings.COMMON.items.midasTouchstone.goldItems.get();

		InventoryHelper.getItemHandlerFrom(player, null).ifPresent(itemHandler -> {
			for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
				ItemStack stack = itemHandler.getStackInSlot(slot);
				Item item = stack.getItem();

				if (stack.getDamage() <= 0 || !stack.getItem().isDamageable()) {
					continue;
				}

				tryRepairingItem(touchstone, player, goldItems, stack, item);
			}
		});
	}

	private void tryRepairingItem(ItemStack touchstone, PlayerEntity player, List<String> goldItems, ItemStack stack, Item item) {
		Optional<IRepairableItem> repairableItem = getRepairableItem(item.getClass());
		if (repairableItem.isPresent()) {
			if (!repairableItem.get().materialMatches(item)) {
				return;
			}
			repairItem(stack, touchstone, player);
		} else if (goldItems.contains(RegistryHelper.getItemRegistryName(item))) {
			repairItem(stack, touchstone, player);
		}
	}

	private void repairItem(ItemStack stack, ItemStack touchstone, PlayerEntity player) {
		if (reduceTouchStoneCharge(touchstone, player)) {
			stack.setDamage(stack.getDamage() - 1);
		}
	}

	private boolean reduceTouchStoneCharge(ItemStack stack, PlayerEntity player) {
		if (NBTHelper.getInt(GLOWSTONE_TAG, stack) - getGlowStoneCost() >= 0 || player.isCreative()) {
			if (!player.isCreative()) {
				NBTHelper.putInt(GLOWSTONE_TAG, stack, NBTHelper.getInt(GLOWSTONE_TAG, stack) - getGlowStoneCost());
			}
			return true;
		}
		return false;
	}

	private int getGlowStoneCost() {
		return Settings.COMMON.items.midasTouchstone.glowstoneCost.get();
	}

	private int getGlowStoneWorth() {
		return Settings.COMMON.items.midasTouchstone.glowstoneWorth.get();
	}

	private int getGlowstoneLimit() {
		return Settings.COMMON.items.midasTouchstone.glowstoneLimit.get();
	}

	private Optional<IRepairableItem> getRepairableItem(Class<? extends Item> item) {
		for (Map.Entry<Class<? extends Item>, IRepairableItem> repairableItem : REPAIRABLE_ITEMS.entrySet()) {
			if (repairableItem.getKey().isAssignableFrom(item)) {
				return Optional.of(repairableItem.getValue());
			}
		}
		return Optional.empty();
	}

	private interface IRepairableItem {
		boolean materialMatches(Item item);
	}
}
