package reliquary.crafting;

import net.minecraftforge.event.AddReloadListenerEvent;

import javax.annotation.Nullable;
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

	@SuppressWarnings("unused") //parameter needed for addListener to recognize which event to subscribe this to
	public static void onResourceReload(AddReloadListenerEvent event) {
		drainRecipe = null;
		craftingRecipes.clear();
		chargingRecipes.clear();
	}
}
