package xreliquary.util.pedestal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.util.LogHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PedestalRegistry {
	private static final PedestalRegistry INSTANCE = new PedestalRegistry();
	//TODO figure out if this stays here and if storing BlocPos in addition to pos long is required
	private static final Map<LocationKey, BlockPos> positions = new HashMap<>();

	private Map<Class<? extends Item>, Class<? extends IPedestalActionItemWrapper>> itemWrappers = new HashMap<>();

	public static void registerItemWrapper(Class<? extends Item> itemClass, Class<? extends IPedestalActionItemWrapper> wrapperClass) {
		INSTANCE.itemWrappers.put(itemClass, wrapperClass);
	}

	public static IPedestalActionItemWrapper getItemWrapper(ItemStack item) {
		for(Class<? extends Item> itemClass : INSTANCE.itemWrappers.keySet()) {
			if(itemClass.isInstance(item.getItem()))
				try {
					return INSTANCE.itemWrappers.get(itemClass).newInstance();
				}
				catch(InstantiationException | IllegalAccessException e) {
					LogHelper.error("Error instantiating pedestal action item wrapper for " + itemClass.getName());
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
