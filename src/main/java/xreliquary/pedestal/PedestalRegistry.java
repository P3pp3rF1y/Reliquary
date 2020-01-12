package xreliquary.pedestal;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import xreliquary.api.IPedestalItemWrapper;
import xreliquary.reference.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PedestalRegistry {
	private static final PedestalRegistry INSTANCE = new PedestalRegistry();
	private static final Map<LocationKey, BlockPos> positions = new HashMap<>();

	private Map<Class<? extends Item>, Supplier<? extends IPedestalItemWrapper>> itemWrappers = new HashMap<>();
	private Map<Class<? extends Block>, Supplier<? extends IPedestalItemWrapper>> blockWrappers = new HashMap<>();

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
			if (item.getItem() instanceof BlockItem && blockClass.isInstance(((BlockItem) item.getItem()).getBlock())) {
				return Optional.of(INSTANCE.blockWrappers.get(blockClass).get());
			}
		}

		return Optional.empty();
	}

	public static void registerPosition(int dimensionId, BlockPos pos) {
		LocationKey key = new LocationKey(dimensionId, pos.toLong());
		if (!positions.containsKey(key)) {
			positions.put(key, pos);
		}
	}

	public static void unregisterPosition(int dimensionId, BlockPos pos) {
		positions.remove(new LocationKey(dimensionId, pos.toLong()));
	}

	private static void clearPositions() {
		positions.clear();
	}

	public static List<BlockPos> getPositionsInRange(int dimensionId, BlockPos startPos, int range) {
		return getPositionsInRange(dimensionId, startPos, range, range, range);
	}

	private static List<BlockPos> getPositionsInRange(int dimensionId, BlockPos startPos, int xRange, int yRange, int zRange) {
		List<BlockPos> positionsInRange = new ArrayList<>();
		for (Map.Entry<LocationKey, BlockPos> position : positions.entrySet()) {
			if (position.getKey().getDimensionId() != dimensionId) {
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

	@SubscribeEvent
	public void serverStopping(FMLServerStoppedEvent event) {
		PedestalRegistry.clearPositions();
	}

	private static class LocationKey {
		private int dimensionId;
		private long location;

		LocationKey(int dimensionId, long location) {
			this.dimensionId = dimensionId;
			this.location = location;
		}

		@Override
		public int hashCode() {
			//won't produce unique hash at all times, but it's very unlikely that there will be two keys with same hash and absolutely unlikely that will cause issues
			return Long.hashCode(location + dimensionId);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof LocationKey)) {
				return false;
			}

			LocationKey key2 = (LocationKey) o;

			return getDimensionId() == key2.getDimensionId() && getLocation() == key2.getLocation();
		}

		int getDimensionId() {
			return dimensionId;
		}

		public long getLocation() {
			return location;
		}
	}
}
