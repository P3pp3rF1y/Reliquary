package reliquary.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Repairable;
import org.jspecify.annotations.Nullable;
import reliquary.init.ModDataComponents;
import reliquary.item.util.ICuriosItem;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;
import reliquary.util.RegistryHelper;
import reliquary.util.TooltipBuilder;

import java.util.List;

public class MidasTouchstoneItem extends ChargeableItem implements ICuriosItem {
	private ItemStack goldRepairItem = ItemStack.EMPTY;
	private ItemStack netheriteRepairItem = ItemStack.EMPTY;

	public MidasTouchstoneItem(Properties properties) {
		super(properties.stacksTo(1).rarity(Rarity.EPIC));
	}

	@Override
	protected void addMoreInformation(ItemStack touchstone, HolderLookup.@Nullable Provider registries, TooltipBuilder tooltipBuilder) {
		tooltipBuilder.charge(this, ".tooltip2", getTotalGlowstoneCharge(touchstone));
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
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
		if (level.isClientSide() || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % 10 != 0) {
			return;
		}

		// TODO legacy support, remove in future
		if (!stack.has(ModDataComponents.PARTIAL_CHARGES)) {
			getMigratedStoredCharge(stack, FIRST_SLOT);
		}

		if (isEnabled(stack)) {
			int freeGlowstoneCapacity = Math.max(getGlowstoneLimit() - getTotalGlowstoneCharge(stack), 0) / getGlowStoneWorth();
			consumeAndCharge(stack, 0, player, freeGlowstoneCapacity, 1, 16);
		}

		doRepairAndDamageTouchstone(stack, player);
	}

	@Override
	protected boolean isItemValidForContainerSlot(ItemStack containerStack, int slot, ItemStack stack) {
		return stack.is(Items.GLOWSTONE_DUST);
	}

	public static int getGlowstoneCharge(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.GLOWSTONE, 0);
	}

	public static int getTotalGlowstoneCharge(ItemStack stack) {
		return ((MidasTouchstoneItem) stack.getItem()).getTotalCharge(stack);
	}

	private void doRepairAndDamageTouchstone(ItemStack touchstone, Player player) {
		if (getTotalGlowstoneCharge(touchstone) <= 0) {
			return;
		}

		List<String> goldItems = Config.COMMON.items.midasTouchstone.getGoldItems();

		InventoryHelper.iteratePlayerInventory(player, (slot, stack) -> {
			if (!stack.has(DataComponents.DAMAGE) || stack.getOrDefault(DataComponents.DAMAGE, 0) <= 0) {
				return;
			}

			tryRepairingItem(touchstone, player, goldItems, stack);
		});
	}

	private void tryRepairingItem(ItemStack touchstone, Player player, List<String> goldItems, ItemStack stack) {
		if (isRepairableWithGoldOrNetherite(stack) || goldItems.contains(RegistryHelper.getItemRegistryName(stack.getItem()))) {
			repairItem(stack, touchstone, player);
		}
	}

	private boolean isRepairableWithGoldOrNetherite(ItemStack stack) {
		Repairable repairable = stack.get(DataComponents.REPAIRABLE);
		if (repairable == null) {
			return false;
		}

		return repairable.isValidRepairItem(getGoldRepairItem()) || repairable.isValidRepairItem(getNetheriteRepairItem());
	}

	private ItemStack getGoldRepairItem() {
		if (goldRepairItem.isEmpty()) {
			goldRepairItem = new ItemStack(Items.GOLD_INGOT);
		}
		return goldRepairItem;
	}

	private ItemStack getNetheriteRepairItem() {
		if (netheriteRepairItem.isEmpty()) {
			netheriteRepairItem = new ItemStack(Items.NETHERITE_INGOT);
		}
		return netheriteRepairItem;
	}

	private void repairItem(ItemStack stack, ItemStack touchstone, Player player) {
		if (reduceTouchStoneCharge(touchstone, player)) {
			int damage = stack.getDamageValue();
			stack.setDamageValue(damage - Math.min(damage, 10));
		}
	}

	private boolean reduceTouchStoneCharge(ItemStack stack, Player player) {
		return player.isCreative() || useCharge(stack, getGlowStoneCost());
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

	@Override
	public Type getCuriosType() {
		return Type.CHARM;
	}

	@Override
	public void onWornServerTick(ItemStack stack, ServerLevel serverLevel, LivingEntity player) {
		inventoryTick(stack, serverLevel, player, null);
	}

	@Override
	public void addStoredCharge(ItemStack containerStack, int slot, int chargeToAdd, @Nullable ItemStack chargeStack) {
		containerStack.set(ModDataComponents.GLOWSTONE, Math.max(getGlowstoneCharge(containerStack) + chargeToAdd, 0));
	}

	@Override
	protected int getSlotWorth(int slot) {
		return slot == 0 ? getGlowStoneWorth() : 0;
	}

	@Override
	public int getStoredCharge(ItemStack containerStack, int slot) {
		return getGlowstoneCharge(containerStack);
	}
}
