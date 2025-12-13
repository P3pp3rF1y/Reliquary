package reliquary.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class PlayerInventoryHandler {
	public static final Set<String> SINGLE_IDENTIFIER = Collections.singleton("");
	private final Function<Player, Set<String>> identifiersGetter;
	private final SlotCountGetter slotCountGetter;
	private final SlotStackGetter slotStackGetter;
	private final SlotStackSetter slotStackSetter;

	public PlayerInventoryHandler(Function<Player, Set<String>> identifiersGetter, SlotCountGetter slotCountGetter, SlotStackGetter slotStackGetter, SlotStackSetter slotStackSetter) {
		this.identifiersGetter = identifiersGetter;
		this.slotCountGetter = slotCountGetter;
		this.slotStackGetter = slotStackGetter;
		this.slotStackSetter = slotStackSetter;
	}

	public int getSlotCount(Player player, String identifier) {
		return slotCountGetter.getSlotCount(player, identifier);
	}

	public ItemStack getStackInSlot(Player player, String identifier, int slot) {
		return slotStackGetter.getStackInSlot(player, identifier, slot);
	}

	public Set<String> getIdentifiers(Player player) {
		return identifiersGetter.apply(player);
	}

	public void setStackInSlot(Player player, String identifier, int slot, ItemStack stack) {
		slotStackSetter.setStackInSlot(player, identifier, slot, stack);
	}

	public interface SlotCountGetter {
		int getSlotCount(Player player, String identifier);
	}

	public interface SlotStackGetter {
		ItemStack getStackInSlot(Player player, String identifier, int slot);
	}

	public interface SlotStackSetter {
		void setStackInSlot(Player player, String identifier, int slot, ItemStack stack);
	}
}
