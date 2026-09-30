package reliquary.crafting;

import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlkahestryRecipeRegistry {
	private AlkahestryRecipeRegistry() {
	}

	@Nullable
	private static AlkahestryDrainRecipe drainRecipe = null;
	private static final List<AlkahestryCraftingRecipe> craftingRecipes = new ArrayList<>();
	private static final List<AlkahestryChargingRecipe> chargingRecipes = new ArrayList<>();

	static synchronized void setDrainRecipe(AlkahestryDrainRecipe drainRecipe) {
		AlkahestryRecipeRegistry.drainRecipe = drainRecipe;
	}

	public static synchronized Optional<AlkahestryDrainRecipe> getDrainRecipe() {
		return Optional.ofNullable(drainRecipe);
	}

	public static synchronized List<AlkahestryCraftingRecipe> getCraftingRecipes() {
		return List.copyOf(craftingRecipes);
	}

	static synchronized void registerCraftingRecipe(AlkahestryCraftingRecipe alkahestryCraftingRecipe) {
		if (craftingRecipes.stream()
				.noneMatch(recipe -> recipe.getCraftingIngredient().equals(alkahestryCraftingRecipe.getCraftingIngredient())
						&& recipe.getChargeNeeded() == alkahestryCraftingRecipe.getChargeNeeded()
						&& recipe.getResultCount() == alkahestryCraftingRecipe.getResultCount())) {
			craftingRecipes.add(alkahestryCraftingRecipe);
		}
	}

	static synchronized void registerChargingRecipe(AlkahestryChargingRecipe alkahestryChargingRecipe) {
		if (chargingRecipes.stream().noneMatch(recipe -> recipe.getChargingIngredient().equals(alkahestryChargingRecipe.getChargingIngredient())
				&& recipe.getChargeToAdd() == alkahestryChargingRecipe.getChargeToAdd())) {
			chargingRecipes.add(alkahestryChargingRecipe);
		}
	}

	public static synchronized List<AlkahestryChargingRecipe> getChargingRecipes() {
		return List.copyOf(chargingRecipes);
	}

	@SuppressWarnings("unused") // parameter needed for addListener to recognize which event to subscribe this to
	public static synchronized void onResourceReload(AddServerReloadListenersEvent event) {
		drainRecipe = null;
		craftingRecipes.clear();
		chargingRecipes.clear();
	}
}
