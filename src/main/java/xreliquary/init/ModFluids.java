package xreliquary.init;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

public class ModFluids {
    public static void init() {
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.WATER, new ItemStack(ModItems.emperorChalice), new ItemStack(ModItems.emperorChalice));
    }
}
