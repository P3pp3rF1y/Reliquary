package reliquary.compat.jei.infernaltear;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.world.item.ItemStack;

public class InfernalTearRecipe {
	private final ItemStack input;
	private final int experiencePoints;

	InfernalTearRecipe(ItemStack input, int experiencePoints) {
		this.input = input;
		this.experiencePoints = experiencePoints;
	}

	public void setIngredients(IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, input);
	}

	public int getExperiencePoints() {
		return experiencePoints;
	}
}
