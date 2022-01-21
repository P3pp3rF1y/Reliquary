package reliquary.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AlkahestryRecipeRegistry {
	private AlkahestryRecipeRegistry() {}

	@Nullable
	private static AlkahestryDrainRecipe drainRecipe = null;
	private static final Map<ResourceLocation, AlkahestryCraftingRecipe> craftingRecipes = new LinkedHashMap<>();
	private static final Map<ResourceLocation, AlkahestryChargingRecipe> chargingRecipes = new LinkedHashMap<>();

	static void setDrainRecipe(AlkahestryDrainRecipe drainRecipe) {
		AlkahestryRecipeRegistry.drainRecipe = drainRecipe;
	}

	public static Optional<AlkahestryDrainRecipe> getDrainRecipe() {
		return Optional.ofNullable(drainRecipe);
	}

	public static Collection<AlkahestryCraftingRecipe> getCraftingRecipes() {
		return craftingRecipes.values();
	}

	static void registerCraftingRecipe(AlkahestryCraftingRecipe alkahestryCraftingRecipe) {
		craftingRecipes.put(alkahestryCraftingRecipe.getId(), alkahestryCraftingRecipe);
	}

	static void registerChargingRecipe(AlkahestryChargingRecipe alkahestryChargingRecipe) {
		chargingRecipes.put(alkahestryChargingRecipe.getId(), alkahestryChargingRecipe);
	}

	public static Collection<AlkahestryChargingRecipe> getChargingRecipes() {
		return chargingRecipes.values();
	}

	@SuppressWarnings("unused") //parameter needed for addListener to recognize which event to subscribe this to
	public static void onResourceReload(AddReloadListenerEvent event) {
		drainRecipe = null;
		craftingRecipes.clear();
		chargingRecipes.clear();
	}
}
