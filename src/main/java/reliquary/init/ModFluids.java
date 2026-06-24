package reliquary.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import reliquary.Reliquary;

import java.util.function.Supplier;

public class ModFluids {
	private ModFluids() {
	}

	private static BaseFlowingFluid.Properties fluidProperties() {
		return new BaseFlowingFluid.Properties(EXPERIENCE_FLUID_TYPE, XP_STILL, XP_FLOWING);
	}

	public static final Identifier EXPERIENCE_TAG_NAME = Identifier.fromNamespaceAndPath("c", "experience");
	public static final TagKey<Fluid> EXPERIENCE_TAG = TagKey.create(Registries.FLUID, EXPERIENCE_TAG_NAME);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, Reliquary.MOD_ID);
	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Reliquary.MOD_ID);
	public static final Supplier<FlowingFluid> XP_STILL = FLUIDS.register("xp_still", () -> new BaseFlowingFluid.Source(fluidProperties()));
	public static final Supplier<FlowingFluid> XP_FLOWING = FLUIDS.register("xp_flowing", () -> new BaseFlowingFluid.Flowing(fluidProperties()));

	public static final Supplier<FluidType> EXPERIENCE_FLUID_TYPE = FLUID_TYPES.register("experience",
			() -> new FluidType(FluidType.Properties.create().lightLevel(10).density(800).viscosity(1500)));

	public static void registerHandlers(IEventBus modBus) {
		FLUIDS.register(modBus);
		FLUID_TYPES.register(modBus);
	}
}
