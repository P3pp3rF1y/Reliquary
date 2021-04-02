package xreliquary.crafting;

import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class AlkahestryRecipeRegistry {
	private AlkahestryRecipeRegistry() {}

	@Nullable
	private static AlkahestryDrainRecipe drainRecipe = null;
	private static final List<AlkahestryCraftingRecipe> craftingRecipes = Lists.newArrayList();
	private static final List<AlkahestryChargingRecipe> chargingRecipes = Lists.newArrayList();

	static void setDrainRecipe(AlkahestryDrainRecipe drainRecipe) {
		AlkahestryRecipeRegistry.drainRecipe = drainRecipe;
	}

	public static Optional<AlkahestryDrainRecipe> getDrainRecipe() {
		return Optional.ofNullable(drainRecipe);
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
