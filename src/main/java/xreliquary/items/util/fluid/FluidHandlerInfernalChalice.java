package xreliquary.items.util.fluid;

import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;

public class FluidHandlerInfernalChalice extends FluidHandlerItemStack {
	private static final String FLUID_STACKS_TAG = "fluidStacks";

	public FluidHandlerInfernalChalice(ItemStack chalice) {
		super(chalice, Settings.COMMON.items.infernalChalice.fluidLimit.get());
	}

	@Override
	public boolean canDrainFluidType(FluidStack fluid) {
		return !ModItems.INFERNAL_CHALICE.get().isEnabled(container) && FluidTags.LAVA.contains(fluid.getFluid()) && getFluid().getAmount() >= fluid.getAmount();
	}

	@Override
	protected void setContainerToEmpty() {
		setFluid(new FluidStack(Fluids.LAVA, 0));
	}

	@Override
	public boolean canFillFluidType(FluidStack fluid) {
		return ModItems.INFERNAL_CHALICE.get().isEnabled(container) && fluid.getFluid() == Fluids.LAVA;
	}

	@Override
	protected void setFluid(FluidStack fluid) {
		if (!container.hasTag()) {
			container.setTag(new CompoundNBT());
		}

		//noinspection ConstantConditions
		container.getTag().putInt(FLUID_STACKS_TAG, fluid.getAmount());
	}

	@Override
	public FluidStack getFluid() {
		CompoundNBT tagCompound = container.getTag();
		if (tagCompound == null || !tagCompound.contains(FLUID_STACKS_TAG)) {
			return new FluidStack(Fluids.LAVA, 0);
		}
		return new FluidStack(Fluids.LAVA, tagCompound.getInt(FLUID_STACKS_TAG));
	}
}
