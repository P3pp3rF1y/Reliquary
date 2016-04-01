package xreliquary.items.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class HarvestRodCacheStorage implements Capability.IStorage<IHarvestRodCache> {
	@Override
	public NBTBase writeNBT(Capability<IHarvestRodCache> capability, IHarvestRodCache instance, EnumFacing side) {
		return null;
	}

	@Override
	public void readNBT(Capability<IHarvestRodCache> capability, IHarvestRodCache instance, EnumFacing side, NBTBase nbt) {

	}
}
