package xreliquary.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xreliquary.potions.CurePotion;
import xreliquary.potions.FlightPotion;
import xreliquary.potions.PacificationPotion;
import xreliquary.reference.Reference;

public class ModPotions {
	private ModPotions() {}

	private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Reference.MOD_ID);

	public static RegistryObject<MobEffect> FLIGHT_POTION = MOB_EFFECTS.register("flight", FlightPotion::new);
	public static RegistryObject<MobEffect> PACIFICATION_POTION = MOB_EFFECTS.register("pacification", PacificationPotion::new);
	public static RegistryObject<MobEffect> CURE_POTION = MOB_EFFECTS.register("cure", CurePotion::new);

	public static void registerListeners(IEventBus modBus) {
		MOB_EFFECTS.register(modBus);
	}
}
