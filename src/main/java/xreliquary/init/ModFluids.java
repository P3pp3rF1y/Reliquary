package xreliquary.init;

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.reference.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModFluids {
	private ModFluids() {}

	public static final ResourceLocation XP_JUICE_TAG = new ResourceLocation("forge:xp_juice");

	private static final ResourceLocation XP_JUICE_STILL_TEXTURE = new ResourceLocation(Reference.MOD_ID, "fluids/xp_juice_still");
	private static final ResourceLocation XP_JUICE_FLOWING_TEXTURE = new ResourceLocation(Reference.MOD_ID, "fluids/xp_juice_flowing");
	private static final ResourceLocation MILK_STILL_TEXTURE = new ResourceLocation(Reference.MOD_ID, "fluids/milk_still");
	private static final ResourceLocation MILK_FLOWING_TEXTURE = new ResourceLocation(Reference.MOD_ID, "fluids/milk_flowing");

	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Reference.MOD_ID);

	public static final RegistryObject<FlowingFluid> XP_JUICE_STILL = FLUIDS.register("xp_juice_still", () -> new ForgeFlowingFluid.Source(ModFluids.XP_JUICE_PROPERTIES));
	public static final RegistryObject<FlowingFluid> XP_JUICE_FLOWING = FLUIDS.register("xp_juice_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.XP_JUICE_PROPERTIES));

	public static final ForgeFlowingFluid.Properties XP_JUICE_PROPERTIES = new ForgeFlowingFluid.Properties(XP_JUICE_STILL, XP_JUICE_FLOWING, FluidAttributes.builder(XP_JUICE_STILL_TEXTURE, XP_JUICE_FLOWING_TEXTURE).luminosity(10).density(800).viscosity(1500));

	public static final RegistryObject<FlowingFluid> MILK_STILL = FLUIDS.register("milk_still", () -> new ForgeFlowingFluid.Source(ModFluids.MILK_PROPERTIES));
	public static final RegistryObject<FlowingFluid> MILK_FLOWING = FLUIDS.register("milk_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.MILK_PROPERTIES));

	public static final ForgeFlowingFluid.Properties MILK_PROPERTIES = new ForgeFlowingFluid.Properties(MILK_STILL, MILK_FLOWING, FluidAttributes.builder(MILK_STILL_TEXTURE, MILK_FLOWING_TEXTURE).temperature(320)).bucket(() -> Items.MILK_BUCKET);
}
