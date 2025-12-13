package reliquary.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.*;
import java.util.function.*;

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
		addPlayerInventoryHandler(MAIN_INVENTORY, player -> PlayerInventoryHandler.SINGLE_IDENTIFIER, (player, identifier) -> player.getInventory().items.size(),
				(player, identifier, slot) -> player.getInventory().items.get(slot), (player, identifier, slot, stack) -> player.getInventory().setItem(slot, stack), false);
		addPlayerInventoryHandler(OFFHAND_INVENTORY, player -> PlayerInventoryHandler.SINGLE_IDENTIFIER, (player, identifier) -> player.getInventory().offhand.size(),
				(player, identifier, slot) -> player.getInventory().offhand.get(slot), (player, identifier, slot, stack) -> player.getInventory().offhand.set(slot, stack), false);
		addPlayerInventoryHandler(ARMOR_INVENTORY, player -> PlayerInventoryHandler.SINGLE_IDENTIFIER, (player, identifier) -> player.getInventory().armor.size(),
				(player, identifier, slot) -> player.getInventory().armor.get(slot), (player, identifier, slot, stack) -> player.getInventory().armor.set(slot, stack), true);
	}

	public void addPlayerInventoryHandler(String name, Function<Player, Set<String>> identifiersGetter, PlayerInventoryHandler.SlotCountGetter slotCountGetter, PlayerInventoryHandler.SlotStackGetter slotStackGetter, PlayerInventoryHandler.SlotStackSetter slotStackSetter, boolean rendered) {
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
			Set<String> identifiers = handler.getIdentifiers(player);
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
		return getFromPlayerInventoryHandlers(player, (stack, handler, handlerName, identifier, slot, result) -> get.apply(stack, result), shouldExit, defaultValue);
	}

	public <T> T getFromPlayerInventoryHandlers(Player player, IHandlerSlotValueGetter<T> getter, Predicate<T> shouldExit, Supplier<T> defaultValue) {
		T result = defaultValue.get();
		for (Map.Entry<String, PlayerInventoryHandler> entry : playerInventoryHandlers.entrySet()) {
			String handlerName = entry.getKey();
			PlayerInventoryHandler handler = entry.getValue();
			Set<String> identifiers = handler.getIdentifiers(player);
			for (String identifier : identifiers) {
				int slots = handler.getSlotCount(player, identifier);
				for (int slot = 0; slot < slots; slot++) {
					result = getter.get(handler.getStackInSlot(player, identifier, slot), handler, handlerName, identifier, slot, result);
					if (shouldExit.test(result)) {
						return result;
					}
				}
			}
		}
		return result;
	}

	public ItemStack getStack(Player player, String handlerName, String identifier, int slot) {
		PlayerInventoryHandler handler = playerInventoryHandlers.get(handlerName);
		if (handler != null) {
			return handler.getStackInSlot(player, identifier, slot);
		}
		return ItemStack.EMPTY;
	}

	public void setStack(Player player, String handlerName, String identifier, int slot, ItemStack stack) {
		PlayerInventoryHandler handler = playerInventoryHandlers.get(handlerName);
		if (handler != null) {
			handler.setStackInSlot(player, identifier, slot, stack);
		}
	}

	public interface IHandlerSlotValueGetter<T> {
		T get(ItemStack slotStack, PlayerInventoryHandler handler, String handlerName, String identifier, int slot, T ret);
	}
}
