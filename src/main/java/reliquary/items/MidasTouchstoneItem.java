package reliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import reliquary.init.ModDataComponents;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;
import reliquary.util.RegistryHelper;
import reliquary.util.TooltipBuilder;

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
				Holder<ArmorMaterial> material = ((ArmorItem) item).getMaterial();
				return material.equals(ArmorMaterials.GOLD) || material.equals(ArmorMaterials.NETHERITE);
			})
			.build();

	public MidasTouchstoneItem() {
		super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	protected void addMoreInformation(ItemStack touchstone, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		tooltipBuilder.charge(this, ".tooltip2", getGlowstoneCharge(touchstone));
		if (isEnabled(touchstone)) {
			tooltipBuilder.absorbActive(Items.GLOWSTONE_DUST.getName(new ItemStack(Items.GLOWSTONE_DUST)).getString());
		} else {
			tooltipBuilder.absorb();
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean f) {
		if (level.isClientSide || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % 10 != 0) {
			return;
		}

		if (isEnabled(stack)) {
			int glowstoneCharge = getGlowstoneCharge(stack);
			consumeAndCharge(player, getGlowstoneLimit() - glowstoneCharge, getGlowStoneWorth(), Items.GLOWSTONE_DUST, 16,
					chargeToAdd -> addGlowstoneCharge(stack, chargeToAdd));
		}

		doRepairAndDamageTouchstone(stack, player);
	}

	public static int getGlowstoneCharge(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.GLOWSTONE, 0);
	}

	private void addGlowstoneCharge(ItemStack stack, int chargeToAdd) {
		stack.set(ModDataComponents.GLOWSTONE, Math.max(getGlowstoneCharge(stack) + chargeToAdd, 0));
	}

	private void doRepairAndDamageTouchstone(ItemStack touchstone, Player player) {
		List<String> goldItems = Config.COMMON.items.midasTouchstone.getGoldItems();

		IItemHandler playerInventory = InventoryHelper.getItemHandlerFrom(player);
		if (playerInventory == null) {
			return;
		}
		for (int slot = 0; slot < playerInventory.getSlots(); slot++) {
			ItemStack stack = playerInventory.getStackInSlot(slot);
			Item item = stack.getItem();

			if (stack.getDamageValue() <= 0 || !stack.has(DataComponents.DAMAGE)) {
				continue;
			}

			tryRepairingItem(touchstone, player, goldItems, stack, item);
		}
	}

	private void tryRepairingItem(ItemStack touchstone, Player player, List<String> goldItems, ItemStack stack, Item item) {
		Optional<IRepairableItem> repairableItem = getRepairableItem(item.getClass());
		if ((repairableItem.isPresent() && repairableItem.get().materialMatches(item)) || goldItems.contains(RegistryHelper.getItemRegistryName(item))) {
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
		if (getGlowstoneCharge(stack) - getGlowStoneCost() >= 0 || player.isCreative()) {
			if (!player.isCreative()) {
				addGlowstoneCharge(stack, -getGlowStoneCost());
			}
			return true;
		}
		return false;
	}

	private int getGlowStoneCost() {
		return Config.COMMON.items.midasTouchstone.glowstoneCost.get();
	}

	private int getGlowStoneWorth() {
		return Config.COMMON.items.midasTouchstone.glowstoneWorth.get();
	}

	private int getGlowstoneLimit() {
		return Config.COMMON.items.midasTouchstone.glowstoneLimit.get();
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
