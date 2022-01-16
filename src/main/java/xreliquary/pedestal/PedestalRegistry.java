package xreliquary.pedestal;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.server.ServerStoppedEvent;
import xreliquary.api.IPedestalItemWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class PedestalRegistry {
	private static final PedestalRegistry INSTANCE = new PedestalRegistry();
	private static final Map<LocationKey, BlockPos> positions = new HashMap<>();

	private final Map<Class<? extends Item>, Supplier<? extends IPedestalItemWrapper>> itemWrappers = new HashMap<>();
	private final Map<Class<? extends Block>, Supplier<? extends IPedestalItemWrapper>> blockWrappers = new HashMap<>();

	public static void registerItemWrapper(Class<? extends Item> itemClass, Supplier<? extends IPedestalItemWrapper> wrapperClass) {
		INSTANCE.itemWrappers.put(itemClass, wrapperClass);
	}

	public static void registerItemBlockWrapper(Class<? extends Block> blockClass, Supplier<? extends IPedestalItemWrapper> wrapperClass) {
		INSTANCE.blockWrappers.put(blockClass, wrapperClass);
	}

	public static Optional<IPedestalItemWrapper> getItemWrapper(ItemStack item) {
		for (Class<? extends Item> itemClass : INSTANCE.itemWrappers.keySet()) {
			if (itemClass.isInstance(item.getItem())) {
				return Optional.of(INSTANCE.itemWrappers.get(itemClass).get());
			}
		}

		for (Class<? extends Block> blockClass : INSTANCE.blockWrappers.keySet()) {
			if (item.getItem() instanceof BlockItem blockItem && blockClass.isInstance(blockItem.getBlock())) {
				return Optional.of(INSTANCE.blockWrappers.get(blockClass).get());
			}
		}

		return Optional.empty();
	}

	public static void registerPosition(ResourceLocation dimension, BlockPos pos) {
		LocationKey key = new LocationKey(dimension, pos.asLong());
		positions.putIfAbsent(key, pos);
	}

	public static void unregisterPosition(ResourceLocation dimension, BlockPos pos) {
		positions.remove(new LocationKey(dimension, pos.asLong()));
	}

	private static void clearPositions() {
		positions.clear();
	}

	public static List<BlockPos> getPositionsInRange(ResourceLocation dimension, BlockPos startPos, int range) {
		return getPositionsInRange(dimension, startPos, range, range, range);
	}

	private static List<BlockPos> getPositionsInRange(ResourceLocation dimension, BlockPos startPos, int xRange, int yRange, int zRange) {
		List<BlockPos> positionsInRange = new ArrayList<>();
		for (Map.Entry<LocationKey, BlockPos> position : positions.entrySet()) {
			if (!position.getKey().getDimension().equals(dimension)) {
				continue;
			}
			BlockPos pos = position.getValue();
			if (pos.getX() < startPos.getX() - xRange || pos.getX() > startPos.getX() + xRange
					|| pos.getY() < startPos.getY() - yRange || pos.getY() > startPos.getY() + yRange
					|| pos.getZ() < startPos.getZ() - zRange || pos.getZ() > startPos.getZ() + zRange) {
				continue;
			}

			positionsInRange.add(pos);
		}
		return positionsInRange;
	}

	@SuppressWarnings("unused") //need to have event type here for reflection to call this during correct event
	public static void serverStopping(ServerStoppedEvent event) {
		PedestalRegistry.clearPositions();
	}

	private record LocationKey(ResourceLocation dimension, long location) {
		@Override
		public int hashCode() {
			return Objects.hash(dimension, location);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof LocationKey key2)) {
				return false;
			}

			return getDimension().equals(key2.getDimension()) && getLocation() == key2.getLocation();
		}

		ResourceLocation getDimension() {
			return dimension;
		}

		public long getLocation() {
			return location;
		}
	}
}
