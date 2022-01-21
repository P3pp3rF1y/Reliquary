package reliquary.compat.jei;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import reliquary.crafting.FragmentRecipeHelper;
import reliquary.init.ModItems;
import reliquary.items.MobCharmRegistry;
import reliquary.reference.Reference;

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

			ResourceLocation id = new ResourceLocation(Reference.MOD_ID, "reliquary.fragment_to_spawn_egg." + output.getDescriptionId());
			recipes.add(new ShapelessRecipe(id, "reliquary.fragment_to_spawn_egg", output, inputs));
		}

		return recipes;
	}
}
