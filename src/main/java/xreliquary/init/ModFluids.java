package xreliquary.init;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.reference.Reference;
import xreliquary.util.LogHelper;

public class ModFluids {
	private static final String XP_JUICE_NAME = "xpjuice";

	public static Fluid fluidXpJuice;

	public static void init() {
		FluidContainerRegistry.registerFluidContainer(FluidRegistry.WATER, new ItemStack(ModItems.emperorChalice), new ItemStack(ModItems.emperorChalice));

		if(!Loader.isModLoaded("OpenBlocks")) {
			LogHelper.info("XP Juice registered by Reliquary.");
			fluidXpJuice = new Fluid(XP_JUICE_NAME, new ResourceLocation(Reference.MOD_ID, "fluids/xpjuicestill"), new ResourceLocation(Reference.MOD_ID, "fluids/xpjuiceflowing")).setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("xreliquary.xpjuice");
			FluidRegistry.registerFluid(fluidXpJuice);

			//TODO figure out if there needs to a bucket added here as well
		}
		else {
			LogHelper.info("XP Juice registration left to Open Blocks / Ender IO.");
		}

	}

	public static void postInit() {
		if(fluidXpJuice == null) { //should have been registered by open blocks
			fluidXpJuice = FluidRegistry.getFluid(XP_JUICE_NAME);
			if(fluidXpJuice == null) {
				LogHelper.error("Liquid XP Juice registration left to open blocks / Ender IO but could not be found.");
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onIconLoad(TextureStitchEvent.Pre event) {
		if(fluidXpJuice != null) {
			event.map.registerSprite(fluidXpJuice.getStill());
			event.map.registerSprite(fluidXpJuice.getFlowing());
		}
	}
}
