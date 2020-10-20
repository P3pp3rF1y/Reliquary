package xreliquary.compat.jei;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import xreliquary.crafting.FragmentRecipeHelper;
import xreliquary.init.ModItems;
import xreliquary.items.MobCharmRegistry;
import xreliquary.reference.Reference;

import java.util.ArrayList;
import java.util.List;

public class SpawnEggRecipeMaker {
	private SpawnEggRecipeMaker() {}

	public static List<ShapelessRecipe> getRecipes() {
		List<ShapelessRecipe> recipes = new ArrayList<>();

		for (String regName : MobCharmRegistry.getRegisteredNames()) {
			Ingredient fragmentIngredient = Ingredient.fromStacks(ModItems.MOB_CHARM_FRAGMENT.getStackFor(regName));
			Ingredient eggIngredient = Ingredient.fromItems(Items.EGG);

			NonNullList<Ingredient> inputs = NonNullList.create();
			inputs.add(fragmentIngredient);
			inputs.add(fragmentIngredient);
			inputs.add(eggIngredient);

			ItemStack output = FragmentRecipeHelper.getSpawnEggStack(regName);

			ResourceLocation id = new ResourceLocation(Reference.MOD_ID, "xreliquary.fragment_to_spawn_egg." + output.getTranslationKey());
			recipes.add(new ShapelessRecipe(id, "xreliquary.fragment_to_spawn_egg", output, inputs));
		}

		return recipes;
	}
}
