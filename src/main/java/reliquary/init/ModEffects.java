package reliquary.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import reliquary.Reliquary;
import reliquary.potions.CureEffect;
import reliquary.potions.FlightEffect;
import reliquary.potions.PacificationEffect;

public class ModEffects {
	private ModEffects() {
	}

	private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Reliquary.MOD_ID);

	public static Holder<MobEffect> FLIGHT = MOB_EFFECTS.register("flight", FlightEffect::new);
	public static Holder<MobEffect> PACIFICATION = MOB_EFFECTS.register("pacification", PacificationEffect::new);
	public static Holder<MobEffect> CURE = MOB_EFFECTS.register("cure", CureEffect::new);

	public static void registerListeners(IEventBus modBus) {
		MOB_EFFECTS.register(modBus);
	}
}
