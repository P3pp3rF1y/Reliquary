package xreliquary.items.util.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import xreliquary.init.ModFluids;
import xreliquary.init.ModItems;
import xreliquary.util.XpHelper;

import javax.annotation.Nullable;

public class FluidHandlerHeroMedallion implements IFluidHandlerItem, ICapabilityProvider {
	private static final int MAX_CAPACITY = Integer.MAX_VALUE;
	private final ItemStack heroMedallion;

	public FluidHandlerHeroMedallion(ItemStack heroMedallion) {
		this.heroMedallion = heroMedallion;
	}

	@Override
	public ItemStack getContainer() {
		return heroMedallion;
	}


	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this));
	}

	@Override
	public int getTanks() {
		return 1;
	}


	@Override
	public FluidStack getFluidInTank(int tank) {
		return new FluidStack(ModFluids.XP_JUICE_STILL.get(), getMedallionXp());
	}

	@Override
	public int getTankCapacity(int tank) {
		return MAX_CAPACITY;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return tank == 0 && isXpJuiceFluid(stack);
	}

	private boolean isXpJuiceFluid(FluidStack stack) {
		return FluidTags.getCollection().getTagByID(ModFluids.XP_JUICE_TAG).contains(stack.getFluid());
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (!isXpJuiceFluid(resource)) {
			return 0;
		}

		int currentXp = getMedallionXp();
		int toFill = Math.min(MAX_CAPACITY - XpHelper.experienceToLiquid(currentXp), resource.getAmount());

		if (action == FluidAction.EXECUTE) {
			ModItems.HERO_MEDALLION.setExperience(heroMedallion, currentXp + XpHelper.liquidToExperience(toFill));
		}

		return toFill;
	}

	private int getMedallionXp() {
		return ModItems.HERO_MEDALLION.getExperience(heroMedallion);
	}


	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (!isXpJuiceFluid(resource)) {
			return FluidStack.EMPTY;
		}

		int currentXp = getMedallionXp();
		int currentLiquidXp = XpHelper.experienceToLiquid(currentXp);
		int toDrain = Math.min(currentLiquidXp, resource.getAmount());

		if (action == FluidAction.EXECUTE) {
			ModItems.HERO_MEDALLION.setExperience(heroMedallion, currentXp - XpHelper.liquidToExperience(toDrain));
		}

		return new FluidStack(resource.getFluid(), toDrain);
	}


	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return drain(new FluidStack(ModFluids.XP_JUICE_STILL.get(), maxDrain), action);
	}
}
