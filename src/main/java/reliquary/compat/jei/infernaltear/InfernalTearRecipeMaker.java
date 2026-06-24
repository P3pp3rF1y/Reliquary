package reliquary.compat.jei.infernaltear;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import reliquary.crafting.InfernalTearValueHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InfernalTearRecipeMaker {
	private InfernalTearRecipeMaker() {
	}

	public static List<InfernalTearRecipe> getRecipes() {
		ArrayList<InfernalTearRecipe> recipes = new ArrayList<>();

		InfernalTearValueHelper.getClientItemExperiences().entrySet().stream()
				.sorted(Comparator.comparing(entry -> BuiltInRegistries.ITEM.getKey(entry.getKey()).toString()))
				.forEach(entry -> recipes.add(new InfernalTearRecipe(new ItemStack(entry.getKey()), entry.getValue())));

		return recipes;
	}
}
