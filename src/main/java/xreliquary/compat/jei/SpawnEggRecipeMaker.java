package xreliquary.compat.jei;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
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
			Ingredient fragmentIngredient = Ingredient.of(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(regName));
			Ingredient eggIngredient = Ingredient.of(Items.EGG);

			NonNullList<Ingredient> inputs = NonNullList.create();
			inputs.add(fragmentIngredient);
			inputs.add(fragmentIngredient);
			inputs.add(eggIngredient);

			ItemStack output = FragmentRecipeHelper.getSpawnEggStack(regName);

			ResourceLocation id = new ResourceLocation(Reference.MOD_ID, "xreliquary.fragment_to_spawn_egg." + output.getDescriptionId());
			recipes.add(new ShapelessRecipe(id, "xreliquary.fragment_to_spawn_egg", output, inputs));
		}

		return recipes;
	}
}
