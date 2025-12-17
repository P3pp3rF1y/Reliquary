package reliquary.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import reliquary.Reliquary;

import java.util.function.Supplier;

public class ModSounds {
	private ModSounds() {
	}

	private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Reliquary.MOD_ID);

	public static final Supplier<SoundEvent> BOOK = SOUND_EVENTS.register("book", () -> SoundEvent.createVariableRangeEvent(Reliquary.getIdentifier("book")));
	public static final Supplier<SoundEvent> HANDGUN_LOAD = SOUND_EVENTS.register("handgun_load", () -> SoundEvent.createVariableRangeEvent(Reliquary.getIdentifier("handgun_load")));
	public static final Supplier<SoundEvent> HANDGUN_SHOT = SOUND_EVENTS.register("handgun_shot", () -> SoundEvent.createVariableRangeEvent(Reliquary.getIdentifier("handgun_shot")));

	public static void registerListeners(IEventBus modBus) {
		SOUND_EVENTS.register(modBus);
	}
}
