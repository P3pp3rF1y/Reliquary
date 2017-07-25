package xreliquary.init;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.items.util.HarvestRodCache;
import xreliquary.items.util.HarvestRodCacheStorage;
import xreliquary.items.util.IHarvestRodCache;
import xreliquary.items.util.handgun.HandgunData;
import xreliquary.items.util.handgun.HandgunDataStorage;
import xreliquary.items.util.handgun.IHandgunData;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
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
	public static void onItemStackConstruct(AttachCapabilitiesEvent<ItemStack> evt) {
		if (evt.getObject().getItem() == ModItems.harvestRod) {
			evt.addCapability(new ResourceLocation(Reference.MOD_ID, "IHarvestRodCache"), new ICapabilityProvider() {
				@SuppressWarnings("ConstantConditions")
				IHarvestRodCache instance = HARVEST_ROD_CACHE.getDefaultInstance();

				@Override
				public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
					//noinspection ConstantConditions
					return capability == HARVEST_ROD_CACHE;
				}

				@Override
				public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
					//noinspection ConstantConditions
					return capability == HARVEST_ROD_CACHE ? HARVEST_ROD_CACHE.<T>cast(instance) : null;
				}
			});
		}
	}
}
