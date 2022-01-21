package reliquary.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import reliquary.reference.Reference;

public class ModSounds {
	private ModSounds() {}

	private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Reference.MOD_ID);

	public static RegistryObject<SoundEvent> BOOK = SOUND_EVENTS.register("book", () -> new SoundEvent(new ResourceLocation(Reference.MOD_ID, "book")));
	public static RegistryObject<SoundEvent> HANDGUN_LOAD = SOUND_EVENTS.register("handgun_load", () -> new SoundEvent(new ResourceLocation(Reference.MOD_ID, "handgun_load")));
	public static RegistryObject<SoundEvent> HANDGUN_SHOT = SOUND_EVENTS.register("handgun_shot", () -> new SoundEvent(new ResourceLocation(Reference.MOD_ID, "handgun_shot")));

	public static void registerListeners(IEventBus modBus) {
		SOUND_EVENTS.register(modBus);
	}
}
