package xreliquary.util.pedestal;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import xreliquary.api.IPedestalItemWrapper;
import xreliquary.util.LogHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedestalRegistry {
	private static final PedestalRegistry INSTANCE = new PedestalRegistry();
	//TODO figure out if this stays here and if storing BlocPos in addition to pos long is required
	private static final Map<LocationKey, BlockPos> positions = new HashMap<>();

	private Map<Class<? extends Item>, Class<? extends IPedestalItemWrapper>> itemWrappers = new HashMap<>();
	private Map<Class<? extends Block>, Class<? extends IPedestalItemWrapper>> blockWrappers = new HashMap<>();

	public static void registerItemWrapper(Class<? extends Item> itemClass, Class<? extends IPedestalItemWrapper> wrapperClass) {
		INSTANCE.itemWrappers.put(itemClass, wrapperClass);
	}

	public static void registerItemBlockWrapper(Class<? extends Block> blockClass, Class<? extends IPedestalItemWrapper> wrapperClass) {
		INSTANCE.blockWrappers.put(blockClass, wrapperClass);
	}

	public static IPedestalItemWrapper getItemWrapper(ItemStack item) {
		for(Class<? extends Item> itemClass : INSTANCE.itemWrappers.keySet()) {
			if(itemClass.isInstance(item.getItem()))
				try {
					return INSTANCE.itemWrappers.get(itemClass).newInstance();
				}
				catch(InstantiationException | IllegalAccessException e) {
					LogHelper.error("Error instantiating pedestal action item wrapper for " + itemClass.getName());
				}

		}

		for(Class<? extends Block> blockClass:INSTANCE.blockWrappers.keySet()) {
			if (item.getItem() instanceof ItemBlock) {
				if (blockClass.isInstance(((ItemBlock) item.getItem()).getBlock()))
					try {
						return INSTANCE.blockWrappers.get(blockClass).newInstance();
					}
					catch(InstantiationException | IllegalAccessException e) {
						LogHelper.error("Error instantiating pedestal action item wrapper for " + blockClass.getName());
					}
			}
		}

		return null;
	}

	public static void registerPosition(int dimensionId, BlockPos pos) {
		LocationKey key = new LocationKey(dimensionId, pos.toLong());
		if(!positions.containsKey(key))
			positions.put(key, pos);
	}

	public static void unregisterPosition(int dimensionId, BlockPos pos) {
		positions.remove(new LocationKey(dimensionId, pos.toLong()));
	}

	public static void clearPositions() {
		positions.clear();
	}

	public static List<BlockPos> getPositionsInRange(int dimensionId, BlockPos startPos, int range) {
		return getPositionsInRange(dimensionId, startPos, range, range, range);
	}

	public static List<BlockPos> getPositionsInRange(int dimensionId, BlockPos startPos, int xRange, int yRange, int zRange) {
		List<BlockPos> positionsInRange = new ArrayList<>();
		for(Map.Entry<LocationKey, BlockPos> position : positions.entrySet()) {
			if(position.getKey().getDimensionId() != dimensionId)
				continue;
			BlockPos pos = position.getValue();
			if(pos.getX() < startPos.getX() - xRange || pos.getX() > startPos.getX() + xRange)
				continue;
			if(pos.getY() < startPos.getY() - yRange || pos.getY() > startPos.getY() + yRange)
				continue;
			if(pos.getZ() < startPos.getZ() - zRange || pos.getZ() > startPos.getZ() + zRange)
				continue;

			positionsInRange.add(pos);
		}
		return positionsInRange;
	}

	private static class LocationKey {
		private int dimensionId;
		private long location;

		public LocationKey(int dimensionId, long location) {
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
			LocationKey key2 = (LocationKey) o;

			return this.getDimensionId() == key2.getDimensionId() && this.getLocation() == key2.getLocation();
		}

		public int getDimensionId() {
			return dimensionId;
		}

		public long getLocation() {
			return location;
		}
	}
}
