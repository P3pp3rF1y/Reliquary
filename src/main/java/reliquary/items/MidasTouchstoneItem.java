package reliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reliquary.reference.Settings;
import reliquary.util.InventoryHelper;
import reliquary.util.LanguageHelper;
import reliquary.util.NBTHelper;
import reliquary.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MidasTouchstoneItem extends ToggleableItem {
	private static final Map<Class<? extends Item>, IRepairableItem> REPAIRABLE_ITEMS = new ImmutableMap.Builder<Class<? extends Item>, IRepairableItem>()
			.put(TieredItem.class, item -> {
				Tier tier = ((TieredItem) item).getTier();
				return tier.equals(Tiers.GOLD) || tier.equals(Tiers.NETHERITE);
			})
			.put(ArmorItem.class, item -> {
				ArmorMaterial material = ((ArmorItem) item).getMaterial();
				return material.equals(ArmorMaterials.GOLD) || material.equals(ArmorMaterials.NETHERITE);
			})
			.build();
	private static final String GLOWSTONE_TAG = "glowstone";

	public MidasTouchstoneItem() {
		super(new Properties().stacksTo(1));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack touchstone, @Nullable Level world, List<Component> tooltip) {
		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip2", Map.of("charge", Integer.toString(NBTHelper.getInt(GLOWSTONE_TAG, touchstone))), tooltip);
		if (isEnabled(touchstone)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", Map.of("item", ChatFormatting.YELLOW + Items.GLOWSTONE_DUST.getName(new ItemStack(Items.GLOWSTONE_DUST)).getString()), tooltip);
		}
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity e, int i, boolean f) {
		if (world.isClientSide || world.getGameTime() % 10 != 0 || !(e instanceof Player player)) {
			return;
		}

		if (isEnabled(stack)) {
			int glowstoneCharge = NBTHelper.getInt(GLOWSTONE_TAG, stack);
			consumeAndCharge(player, getGlowstoneLimit() - glowstoneCharge, getGlowStoneWorth(), Items.GLOWSTONE_DUST, 16,
					chargeToAdd -> NBTHelper.putInt(GLOWSTONE_TAG, stack, glowstoneCharge + chargeToAdd));
		}

		doRepairAndDamageTouchstone(stack, player);
	}

	private void doRepairAndDamageTouchstone(ItemStack touchstone, Player player) {
		List<String> goldItems = Settings.COMMON.items.midasTouchstone.goldItems.get();

		InventoryHelper.getItemHandlerFrom(player, null).ifPresent(itemHandler -> {
			for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
				ItemStack stack = itemHandler.getStackInSlot(slot);
				Item item = stack.getItem();

				if (stack.getDamageValue() <= 0 || !stack.getItem().canBeDepleted()) {
					continue;
				}

				tryRepairingItem(touchstone, player, goldItems, stack, item);
			}
		});
	}

	private void tryRepairingItem(ItemStack touchstone, Player player, List<String> goldItems, ItemStack stack, Item item) {
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

	private void repairItem(ItemStack stack, ItemStack touchstone, Player player) {
		if (reduceTouchStoneCharge(touchstone, player)) {
			int damage = stack.getDamageValue();
			stack.setDamageValue(damage - Math.min(damage, 10));
		}
	}

	private boolean reduceTouchStoneCharge(ItemStack stack, Player player) {
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
