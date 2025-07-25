package reliquary.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class CustomShapelessRecipe implements CraftingRecipe {
	final String group;
	final CraftingBookCategory category;
	final ItemStack result;
	final List<Ingredient> ingredients;
	@Nullable
	private PlacementInfo placementInfo;
	private final boolean isSimple;

	public CustomShapelessRecipe(String group, CraftingBookCategory category, ItemStack result, List<Ingredient> ingredients) {
		this.group = group;
		this.category = category;
		this.result = result;
		this.ingredients = ingredients;
		isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
	}

	public String group() {
		return group;
	}

	public CraftingBookCategory category() {
		return category;
	}

	public PlacementInfo placementInfo() {
		if (placementInfo == null) {
			placementInfo = PlacementInfo.create(ingredients);
		}

		return placementInfo;
	}

	public ItemStack result() {
		return result;
	}

	public boolean matches(CraftingInput craftingInput, Level level) {
		if (craftingInput.ingredientCount() != ingredients.size()) {
			return false;
		} else if (!isSimple) {
			ArrayList<ItemStack> nonEmptyItems = new ArrayList<>(craftingInput.ingredientCount());

			for (ItemStack item : craftingInput.items()) {
				if (!item.isEmpty()) {
					nonEmptyItems.add(item);
				}
			}

			return RecipeMatcher.findMatches(nonEmptyItems, ingredients) != null;
		} else {
			return craftingInput.size() == 1 && ingredients.size() == 1 ? ingredients.getFirst().test(craftingInput.getItem(0)) : craftingInput.stackedContents().canCraft(this, null);
		}
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider registries) {
		return result.copy();
	}

	public List<RecipeDisplay> display() {
		return List.of(new ShapelessCraftingRecipeDisplay(ingredients.stream().map(Ingredient::display).toList(), new SlotDisplay.ItemStackSlotDisplay(result), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
	}
}
