package reliquary.item.util.fluid;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import reliquary.init.ModFluids;
import reliquary.init.ModItems;
import reliquary.util.XpHelper;

public class FluidHandlerHeroMedallion implements IFluidHandlerItem {
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
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return new FluidStack(ModFluids.XP_STILL.get(), XpHelper.experienceToLiquid(getMedallionXp()));
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
		return stack.getFluid().is(ModFluids.EXPERIENCE_TAG);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (!isXpJuiceFluid(resource)) {
			return 0;
		}

		int currentXp = getMedallionXp();
		int toFill = Math.min(MAX_CAPACITY - XpHelper.experienceToLiquid(currentXp), resource.getAmount());

		if (action == FluidAction.EXECUTE) {
			ModItems.HERO_MEDALLION.get().setExperience(heroMedallion, currentXp + XpHelper.liquidToExperience(toFill));
		}

		return toFill;
	}

	private int getMedallionXp() {
		return ModItems.HERO_MEDALLION.get().getExperience(heroMedallion);
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
			ModItems.HERO_MEDALLION.get().setExperience(heroMedallion, currentXp - XpHelper.liquidToExperience(toDrain));
		}

		return new FluidStack(resource.getFluid(), toDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return drain(new FluidStack(ModFluids.XP_STILL.get(), maxDrain), action);
	}
}
