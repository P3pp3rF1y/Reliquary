package xreliquary.init;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.items.util.HarvestRodCache;
import xreliquary.items.util.HarvestRodCacheStorage;
import xreliquary.items.util.IHarvestRodCache;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Reference;

public class ModCapabilities {
	@CapabilityInject(IHarvestRodCache.class)
	public final static Capability<IHarvestRodCache> HARVEST_ROD_CACHE = null;

	public static void init() {
		CapabilityManager.INSTANCE.register(IHarvestRodCache.class, new HarvestRodCacheStorage(), HarvestRodCache.class);
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
}
