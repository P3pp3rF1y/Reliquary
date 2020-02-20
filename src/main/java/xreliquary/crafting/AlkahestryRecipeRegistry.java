package xreliquary.crafting;

import com.google.common.collect.Lists;

import java.util.List;

public class AlkahestryRecipeRegistry {
	private AlkahestryRecipeRegistry() {}

	private static AlkahestryDrainRecipe drainRecipe;
	private static List<AlkahestryCraftingRecipe> craftingRecipes = Lists.newArrayList();
	private static List<AlkahestryChargingRecipe> chargingRecipes = Lists.newArrayList();

	static void setDrainRecipe(AlkahestryDrainRecipe drainRecipe) {
		AlkahestryRecipeRegistry.drainRecipe = drainRecipe;
	}

	public static AlkahestryDrainRecipe getDrainRecipe() {
		return drainRecipe;
	}

	public static List<AlkahestryCraftingRecipe> getCraftingRecipes() {
		return craftingRecipes;
	}

	static void registerCraftingRecipe(AlkahestryCraftingRecipe alkahestryCraftingRecipe) {
		craftingRecipes.add(alkahestryCraftingRecipe);
	}

	static void registerChargingRecipe(AlkahestryChargingRecipe alkahestryChargingRecipe) {
		chargingRecipes.add(alkahestryChargingRecipe);
	}

	public static List<AlkahestryChargingRecipe> getChargingRecipes() {
		return chargingRecipes;
	}
}
