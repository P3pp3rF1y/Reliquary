package reliquary.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import reliquary.reference.Reference;

import java.util.function.Consumer;

public class ModFluids {
	private ModFluids() {}

	private static ForgeFlowingFluid.Properties fluidProperties() {
		return new ForgeFlowingFluid.Properties(XP_JUICE_FLUID_TYPE, XP_JUICE_STILL, XP_JUICE_FLOWING);
	}
	public static final ResourceLocation EXPERIENCE_TAG_NAME = new ResourceLocation("forge:experience");
	public static final TagKey<Fluid> EXPERIENCE_TAG = TagKey.create(Registry.FLUID_REGISTRY, EXPERIENCE_TAG_NAME);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Reference.MOD_ID);
	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Reference.MOD_ID);
	public static final RegistryObject<FlowingFluid> XP_JUICE_STILL = FLUIDS.register("xp_juice_still", () -> new ForgeFlowingFluid.Source(fluidProperties()));
	public static final RegistryObject<FlowingFluid> XP_JUICE_FLOWING = FLUIDS.register("xp_juice_flowing", () -> new ForgeFlowingFluid.Flowing(fluidProperties()));

	public static final RegistryObject<FluidType> XP_JUICE_FLUID_TYPE = FLUID_TYPES.register("xp_juice", () -> new FluidType(FluidType.Properties.create().lightLevel(10).density(800).viscosity(1500)) {
		@Override
		public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
			consumer.accept(new IClientFluidTypeExtensions() {
				private static final ResourceLocation XP_STILL_TEXTURE = new ResourceLocation(Reference.MOD_ID, "fluids/xp_juice_still");
				private static final ResourceLocation XP_FLOWING_TEXTURE = new ResourceLocation(Reference.MOD_ID, "fluids/xp_juice_flowing");

				@Override
				public ResourceLocation getStillTexture() {
					return XP_STILL_TEXTURE;
				}

				@Override
				public ResourceLocation getFlowingTexture() {
					return XP_FLOWING_TEXTURE;
				}
			});
		}
	});

	public static void registerHandlers(IEventBus modBus) {
		FLUIDS.register(modBus);
		FLUID_TYPES.register(modBus);
	}
}
