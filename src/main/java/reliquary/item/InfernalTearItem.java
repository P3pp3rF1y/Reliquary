package reliquary.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;
import reliquary.Reliquary;
import reliquary.crafting.InfernalTearValueHelper;
import reliquary.init.ModDataComponents;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;
import reliquary.util.TooltipBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class InfernalTearItem extends ToggleableItem {
	private static final int COOLDOWN = 4;
	private static final int NOTHING_FOUND_COOLDOWN = COOLDOWN * 5;

	public InfernalTearItem(Properties properties) {
		super(properties.stacksTo(1).setNoCombineRepair().rarity(Rarity.EPIC));
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		return super.getName(stack).withStyle(ChatFormatting.RED);
	}

	@Override
	public void inventoryTick(ItemStack tear, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
		if (level.isClientSide() || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % COOLDOWN != 0 || !isEnabled(tear) || isInCooldown(tear, level)) {
			return;
		}

		ItemStack tearStack = getStackFromTear(tear);
		if (tearStack.isEmpty()) {
			resetTear(tear);
			return;
		}

		Optional<Integer> experience = InfernalTearValueHelper.getItemExperience(level, tearStack.getItem());
		if (experience.isEmpty()) {
			resetTear(tear);
			return;
		}

		int countConsumed = InventoryHelper.consumeItemStack(resource -> ItemStack.isSameItemSameComponents(tearStack, resource.toStack()), player, 4).getCount();
		if (countConsumed > 0) {
			player.giveExperiencePoints(experience.get() * countConsumed);
		} else {
			setCooldown(tear, level, NOTHING_FOUND_COOLDOWN);
		}
	}

	private void resetTear(ItemStack stack) {
		stack.remove(DataComponents.CONTAINER);
		stack.remove(ModDataComponents.ENABLED);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltipDisplay, tooltip, flag);
		if (getStackFromTear(stack).isEmpty()) {
			TooltipBuilder.of(tooltip, context).description("tooltip.reliquary.tear_empty");
		}
	}

	@Override
	protected void addMoreInformation(ItemStack stack, HolderLookup.@Nullable Provider registries, TooltipBuilder tooltipBuilder) {
		ItemStack contents = getStackFromTear(stack);
		String itemName = contents.getHoverName().getString();

		tooltipBuilder.data("tooltip." + Reliquary.MOD_ID + ".tear", itemName);

		if (isEnabled(stack)) {
			tooltipBuilder.absorbActive(itemName);
		}
		tooltipBuilder.description("tooltip." + Reliquary.MOD_ID + ".absorb");
		tooltipBuilder.description(this, ".infernal_tear.absorb_unset");
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return !getStackFromTear(stack).isEmpty();
	}

	public static ItemStack getStackFromTear(ItemStack tear) {
		ItemContainerContents contents = tear.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
		return contents.getSlots() > 0 ? contents.getStackInSlot(0) : ItemStack.EMPTY;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		InteractionResult actionResult = super.use(level, player, hand);
		ItemStack tear = player.getItemInHand(hand);
		if (player.isShiftKeyDown() && !isEnabled(tear)) {
			return actionResult;
		}


		//empty the tear if player is not sneaking and the tear is not empty
		if (!player.isShiftKeyDown() && !getStackFromTear(tear).isEmpty()) {
			resetTear(tear);
			return actionResult;
		}

		//nothing more to do with a filled tear here
		if (!getStackFromTear(tear).isEmpty()) {
			return actionResult;
		}

		//if user is sneaking or just enabled the tear, let's fill it
		if (player.isShiftKeyDown() || !isEnabled(tear)) {
			ResourceHandler<ItemResource> playerInventory = InventoryHelper.getMainInventoryItemHandlerFrom(player);
			ItemStack returnStack = buildTear(tear, playerInventory, player.level());
			if (!returnStack.isEmpty()) {
				return InteractionResult.SUCCESS.heldItemTransformedTo(returnStack);
			}
		}

		//by this time the tear is still empty and there wasn't anything to put in it
		// so let's disable it if it got enabled
		if (isEnabled(tear)) {
			toggleEnabled(tear);
		}
		return actionResult;
	}

	private ItemStack buildTear(ItemStack stack, ResourceHandler<ItemResource> inventory, Level level) {
		ItemStack tear = new ItemStack(this, 1);

		ItemStack target = getTargetAlkahestItem(stack, inventory, level);
		if (target.isEmpty()) {
			return ItemStack.EMPTY;
		}

		setTearTarget(tear, target);

		if (Config.COMMON.items.infernalTear.absorbWhenCreated.get()) {
			stack.set(ModDataComponents.ENABLED, true);
		}

		return tear;
	}

	public static void setTearTarget(ItemStack tear, ItemStack target) {
		tear.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(target)));
	}

	private ItemStack getTargetAlkahestItem(ItemStack self, ResourceHandler<ItemResource> inventory, Level level) {
		ItemStack targetItem = ItemStack.EMPTY;
		int itemQuantity = 0;
		Map<Item, Integer> itemExperiences = InfernalTearValueHelper.getItemExperiences(level);
		for (int slot = 0; slot < inventory.size(); slot++) {
			ItemResource resource = inventory.getResource(slot);

			if (resource.isEmpty() || self.getItem() == resource.getItem() || resource.getMaxStackSize() == 1 || !resource.getComponentsPatch().isEmpty()
					|| !itemExperiences.containsKey(resource.getItem())) {
				continue;
			}
			ItemStack stack = resource.toStack();
			if (InventoryHelper.getItemQuantity(stack, inventory) > itemQuantity) {
				itemQuantity = InventoryHelper.getItemQuantity(stack, inventory);
				targetItem = stack.copy();
			}
		}
		return targetItem;
	}
}
