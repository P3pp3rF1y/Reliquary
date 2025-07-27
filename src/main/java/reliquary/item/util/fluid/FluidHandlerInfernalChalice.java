package reliquary.item.util.fluid;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import reliquary.init.ModItems;
import reliquary.reference.Config;

import java.util.function.Supplier;

public class FluidHandlerInfernalChalice extends FluidHandlerItemStack {
	public FluidHandlerInfernalChalice(Supplier<DataComponentType<SimpleFluidContent>> componentType, ItemStack chalice) {
		super(componentType, chalice, Config.COMMON.items.infernalChalice.fluidLimit.get());
	}

	@Override
	public boolean canDrainFluidType(FluidStack fluid) {
		return !ModItems.INFERNAL_CHALICE.get().isEnabled(container) && fluid.getFluid().is(FluidTags.LAVA) && getFluid().getAmount() >= fluid.getAmount();
	}

	@Override
	public boolean canFillFluidType(FluidStack fluid) {
		return ModItems.INFERNAL_CHALICE.get().isEnabled(container) && fluid.getFluid() == Fluids.LAVA;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return stack.getFluid() == Fluids.LAVA;
	}
}
