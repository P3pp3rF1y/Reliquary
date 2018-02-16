package xreliquary.init;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.reference.Reference;
import xreliquary.util.LogHelper;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModFluids {
	private static final String XP_JUICE_FLUID_NAME = "xpjuice";
	private static final String MILK_FLUID_NAME = "milk";

	private static boolean registeredXpJuice = false;
	private static boolean registeredMilk = false;

	public static Fluid milk() {
		return FluidRegistry.getFluid(MILK_FLUID_NAME);
	}

	public static Fluid xpJuice() {
		return FluidRegistry.getFluid(XP_JUICE_FLUID_NAME);
	}


	public static void preInit() {
		if(!FluidRegistry.isFluidRegistered(XP_JUICE_FLUID_NAME)) {
			LogHelper.info("XP Juice registered by Reliquary.");
			Fluid fluidXpJuice = new Fluid(XP_JUICE_FLUID_NAME, new ResourceLocation(Reference.MOD_ID, "fluids/xpjuice_still"), new ResourceLocation(Reference.MOD_ID, "fluids/xpjuice_flowing")).setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("xreliquary.xpjuice");
			FluidRegistry.registerFluid(fluidXpJuice);
			registeredXpJuice = true;
		}

		if(!FluidRegistry.isFluidRegistered(MILK_FLUID_NAME)) {
			Fluid milk = new Fluid(MILK_FLUID_NAME, new ResourceLocation(Reference.MOD_ID, "fluids/milk_still"), new ResourceLocation(Reference.MOD_ID, "fluids/milk_flowing")).setTemperature(320);
			FluidRegistry.registerFluid(milk);
			registeredMilk = true;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onIconLoad(TextureStitchEvent.Pre event) {
		TextureMap textureMap = event.getMap();
		if (registeredXpJuice) {
			registerFluidSprites(textureMap, xpJuice());
		}
		if (registeredMilk) {
			registerFluidSprites(textureMap, milk());
		}
	}

	@SideOnly(Side.CLIENT)
	private static void registerFluidSprites(TextureMap textureMap, Fluid fluid) {
		if (fluid != null) {
			textureMap.registerSprite(fluid.getStill());
			textureMap.registerSprite(fluid.getFlowing());
		}
	}
}
