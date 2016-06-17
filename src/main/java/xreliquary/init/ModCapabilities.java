package xreliquary.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.items.util.HarvestRodCache;
import xreliquary.items.util.HarvestRodCacheStorage;
import xreliquary.items.util.IHarvestRodCache;
import xreliquary.items.util.handgun.HandgunData;
import xreliquary.items.util.handgun.HandgunDataStorage;
import xreliquary.items.util.handgun.IHandgunData;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Reference;

public class ModCapabilities {
	@CapabilityInject(IHarvestRodCache.class)
	public final static Capability<IHarvestRodCache> HARVEST_ROD_CACHE = null;
	@CapabilityInject(IHandgunData.class)
	public final static Capability<IHandgunData> HANDGUN_DATA_CAPABILITY = null;

	public static void init() {
		CapabilityManager.INSTANCE.register(IHarvestRodCache.class, new HarvestRodCacheStorage(), HarvestRodCache.class);
		CapabilityManager.INSTANCE.register(IHandgunData.class, new HandgunDataStorage(), HandgunData.class);
	}

	@SubscribeEvent
	public void onItemStackConstruct(AttachCapabilitiesEvent.Item evt) {
		if (evt.getItem() == ModItems.harvestRod) {
			evt.addCapability(new ResourceLocation(Reference.MOD_ID, "IHarvestRodCache"), new ICapabilityProvider() {
				IHarvestRodCache instance = HARVEST_ROD_CACHE.getDefaultInstance();

				@Override
				public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
					return capability == HARVEST_ROD_CACHE;
				}

				@Override
				public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
					return capability == HARVEST_ROD_CACHE ? HARVEST_ROD_CACHE.<T>cast(instance) : null;
				}
			});
		}
	}

	@SubscribeEvent
	public void onEntityConstruct(AttachCapabilitiesEvent.Entity evt) {
		if (evt.getEntity() instanceof EntityPlayer) {
			evt.addCapability(new ResourceLocation(Reference.MOD_ID, "IHandgunData"), new ICapabilityProvider() {

				private IHandgunData mainHandgunData = new HandgunData();
				private IHandgunData offHandgunData = new HandgunData();

				@Override
				public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
					if(capability == ModCapabilities.HANDGUN_DATA_CAPABILITY && (facing == EnumFacing.EAST || facing == EnumFacing.WEST))
						return true;
					return false;
				}

				@Override
				public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
					if (capability != ModCapabilities.HANDGUN_DATA_CAPABILITY)
						return null;

					if(facing == EnumFacing.EAST)
						return ModCapabilities.HANDGUN_DATA_CAPABILITY.cast(mainHandgunData);
					else if (facing == EnumFacing.WEST)
						return ModCapabilities.HANDGUN_DATA_CAPABILITY.cast(offHandgunData);

					return null;
				}
			});
		}
	}
}
