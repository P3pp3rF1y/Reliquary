package reliquary.compat.jei.infernaltear;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import reliquary.crafting.InfernalTearValueHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InfernalTearRecipeMaker {
	private InfernalTearRecipeMaker() {}

	public static List<InfernalTearRecipe> getRecipes() {
		ArrayList<InfernalTearRecipe> recipes = new ArrayList<>();
		RecipeManager recipeManager = getRecipeManager();
		if (recipeManager == null) {
			return recipes;
		}

		InfernalTearValueHelper.getItemExperiences(recipeManager).entrySet().stream()
				.sorted(Comparator.comparing(entry -> BuiltInRegistries.ITEM.getKey(entry.getKey()).toString()))
				.forEach(entry -> recipes.add(new InfernalTearRecipe(new ItemStack(entry.getKey()), entry.getValue())));

		return recipes;
	}

	private static RecipeManager getRecipeManager() {
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		if (connection != null) {
			return connection.getRecipeManager();
		}

		return Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getRecipeManager() : null;
	}
}
