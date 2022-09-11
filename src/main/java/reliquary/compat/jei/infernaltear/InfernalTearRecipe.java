package reliquary.compat.jei.infernaltear;

import net.minecraft.world.item.ItemStack;

public class InfernalTearRecipe {
	private final ItemStack input;
	private final int experiencePoints;

	InfernalTearRecipe(ItemStack input, int experiencePoints) {
		this.input = input;
		this.experiencePoints = experiencePoints;
	}

	public int getExperiencePoints() {
		return experiencePoints;
	}

	public ItemStack getInput() {
		return input;
	}
}
