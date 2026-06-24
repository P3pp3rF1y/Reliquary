package reliquary.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PlayerInventoryProvider {
	public static final String MAIN_INVENTORY = "main";
	public static final String OFFHAND_INVENTORY = "offhand";
	public static final String ARMOR_INVENTORY = "armor";

	private final Map<String, PlayerInventoryHandler> playerInventoryHandlers = new LinkedHashMap<>();
	private final List<String> renderedHandlers = new ArrayList<>();

	private static final PlayerInventoryProvider serverProvider = new PlayerInventoryProvider();
	private static final PlayerInventoryProvider clientProvider = new PlayerInventoryProvider();

	public static PlayerInventoryProvider get() {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			return clientProvider;
		} else {
			return serverProvider;
		}
	}

	private PlayerInventoryProvider() {
		addPlayerInventoryHandler(MAIN_INVENTORY, () -> PlayerInventoryHandler.SINGLE_IDENTIFIER,
				(player, identifier) -> player.getInventory().getNonEquipmentItems().size(),
				(player, identifier, slot) -> player.getInventory().getNonEquipmentItems().get(slot),
				(player, identifier, slot, stack) -> player.getInventory().setItem(slot, stack), false);
		addPlayerInventoryHandler(OFFHAND_INVENTORY, () -> PlayerInventoryHandler.SINGLE_IDENTIFIER, (player, identifier) -> 1,
				(player, identifier, slot) -> player.getOffhandItem(),
				(player, identifier, slot, stack) -> player.setItemInHand(InteractionHand.OFF_HAND, stack), false);
		addPlayerInventoryHandler(ARMOR_INVENTORY, () -> PlayerInventoryHandler.SINGLE_IDENTIFIER, (player, identifier) -> 4,
				(player, identifier, slot) -> player.getInventory().getItem(slot + 36),
				(player, identifier, slot, stack) -> player.getInventory().setItem(slot + 36, stack), true);
	}

	public void addPlayerInventoryHandler(String name, Supplier<Set<String>> identifiersGetter, PlayerInventoryHandler.SlotCountGetter slotCountGetter,
			PlayerInventoryHandler.SlotStackGetter slotStackGetter, PlayerInventoryHandler.SlotStackSetter slotStackSetter, boolean rendered) {
		Map<String, PlayerInventoryHandler> temp = new LinkedHashMap<>(playerInventoryHandlers);
		playerInventoryHandlers.clear();
		playerInventoryHandlers.put(name, new PlayerInventoryHandler(identifiersGetter, slotCountGetter, slotStackGetter, slotStackSetter));
		playerInventoryHandlers.putAll(temp);

		if (rendered) {
			ArrayList<String> tempRendered = new ArrayList<>(renderedHandlers);
			renderedHandlers.clear();
			renderedHandlers.add(name);
			renderedHandlers.addAll(tempRendered);
		}
	}

	private Map<String, PlayerInventoryHandler> getPlayerInventoryHandlers() {
		return playerInventoryHandlers;
	}

	public void runOnPlayerInventoryHandlers(Player player, Consumer<ItemStack> run) {
		getFromPlayerInventoryHandlers(player, (stack, result) -> {
			run.accept(stack);
			return result;
		}, result -> false, () -> true);
	}

	public void swapFirstFoundItemInPlayerInventoryHandlers(Player player, Item filter, ItemStack replacement) {
		for (var handler : playerInventoryHandlers.values()) {
			Set<String> identifiers = handler.getIdentifiers();
			for (String identifier : identifiers) {
				int slots = handler.getSlotCount(player, identifier);
				for (int slot = 0; slot < slots; slot++) {
					ItemStack stack = handler.getStackInSlot(player, identifier, slot);
					if (stack.getItem() == filter) {
						handler.setStackInSlot(player, identifier, slot, replacement);
						return;
					}
				}
			}
		}
	}

	public <T> T getFromPlayerInventoryHandlers(Player player, BiFunction<ItemStack, T, T> get, Predicate<T> shouldExit, Supplier<T> defaultValue) {
		T result = defaultValue.get();
		for (var handler : playerInventoryHandlers.values()) {
			Set<String> identifiers = handler.getIdentifiers();
			for (String identifier : identifiers) {
				int slots = handler.getSlotCount(player, identifier);
				for (int slot = 0; slot < slots; slot++) {
					result = get.apply(handler.getStackInSlot(player, identifier, slot), result);
					if (shouldExit.test(result)) {
						return result;
					}
				}
			}
		}
		return result;
	}
}
