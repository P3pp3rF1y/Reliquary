package reliquary.compat.tconstruct;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import reliquary.pedestal.PedestalRegistry;
import reliquary.pedestal.wrappers.PedestalMeleeWeaponWrapper;
import reliquary.reference.Compatibility;

public class TConstructCompat {
	public TConstructCompat() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
	}

	private void setup(FMLCommonSetupEvent event) {
		PedestalRegistry.registerItemWrapper(getRL("sword"), PedestalMeleeWeaponWrapper::new);
		PedestalRegistry.registerItemWrapper(getRL("cleaver"), PedestalMeleeWeaponWrapper::new);
		PedestalRegistry.registerItemWrapper(getRL("dagger"), PedestalMeleeWeaponWrapper::new);
		PedestalRegistry.registerItemWrapper(getRL("scythe"), PedestalMeleeWeaponWrapper::new);
	}

	private @NotNull ResourceLocation getRL(String itemName) {
		return new ResourceLocation(Compatibility.ModIds.TINKERS_CONSTRUCT, itemName);
	}
}
