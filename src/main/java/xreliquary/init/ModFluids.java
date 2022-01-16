package xreliquary.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xreliquary.reference.Reference;

public class ModFluids {
	private ModFluids() {}

	public static final ResourceLocation EXPERIENCE_TAG = new ResourceLocation("forge:experience");

	private static final ResourceLocation XP_JUICE_STILL_TEXTURE = new ResourceLocation(Reference.MOD_ID, "fluids/xp_juice_still");
	private static final ResourceLocation XP_JUICE_FLOWING_TEXTURE = new ResourceLocation(Reference.MOD_ID, "fluids/xp_juice_flowing");

	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Reference.MOD_ID);

	public static final RegistryObject<FlowingFluid> XP_JUICE_STILL = FLUIDS.register("xp_juice_still", () -> new ForgeFlowingFluid.Source(ModFluids.XP_JUICE_PROPERTIES));
	public static final RegistryObject<FlowingFluid> XP_JUICE_FLOWING = FLUIDS.register("xp_juice_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.XP_JUICE_PROPERTIES));

	public static final ForgeFlowingFluid.Properties XP_JUICE_PROPERTIES = new ForgeFlowingFluid.Properties(XP_JUICE_STILL, XP_JUICE_FLOWING, FluidAttributes.builder(XP_JUICE_STILL_TEXTURE, XP_JUICE_FLOWING_TEXTURE).luminosity(10).density(800).viscosity(1500));
}
