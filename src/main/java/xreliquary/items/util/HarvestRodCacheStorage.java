package xreliquary.items.util;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class HarvestRodCacheStorage implements Capability.IStorage<IHarvestRodCache> {
	@Override
	public INBT writeNBT(Capability<IHarvestRodCache> capability, IHarvestRodCache instance, Direction side) {
		return null;
	}

	@Override
	public void readNBT(Capability<IHarvestRodCache> capability, IHarvestRodCache instance, Direction side, INBT nbt) {

	}
}
