package xreliquary.items.util.handgun;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class HandgunDataStorage implements Capability.IStorage<IHandgunData> {
	@Override
	public NBTBase writeNBT(Capability<IHandgunData> capability, IHandgunData instance, EnumFacing side) {
		return instance.serializeNBT();
	}

	@Override
	public void readNBT(Capability<IHandgunData> capability, IHandgunData instance, EnumFacing side, NBTBase nbt) {
		instance.deserializeNBT((NBTTagCompound) nbt);
	}
}
