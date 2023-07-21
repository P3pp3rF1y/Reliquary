package reliquary.compat.jei;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import reliquary.crafting.MobCharmRecipe;
import reliquary.init.ModItems;
import reliquary.items.MobCharmFragmentItem;
import reliquary.items.MobCharmRegistry;
import reliquary.reference.Reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MobCharmRecipeMaker {
	private MobCharmRecipeMaker() {
	}

	public static List<CraftingRecipe> getRecipes() {
		List<CraftingRecipe> recipes = new ArrayList<>();
		MobCharmRecipe.REGISTERED_RECIPES.forEach(baseRecipe -> addMobCharmRecipes(recipes, baseRecipe));
		return recipes;
	}

	private static void addMobCharmRecipes(List<CraftingRecipe> recipes, MobCharmRecipe baseRecipe) {

		for (String regName : MobCharmRegistry.getRegisteredNames()) {
			NonNullList<Ingredient> inputs = NonNullList.create();
			baseRecipe.getIngredients().forEach(i -> {
				if (Arrays.stream(i.getItems()).anyMatch(stack -> stack.getItem() instanceof MobCharmFragmentItem)) {
					ItemStack[] items = new ItemStack[i.getItems().length];
					for (int j = 0; j < i.getItems().length; j++) {
						ItemStack item = i.getItems()[j];
						if (item.getItem() instanceof MobCharmFragmentItem) {
							items[j] = ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(regName);
						} else {
							items[j] = item;
						}
					}
					inputs.add(Ingredient.of(items));
				} else {
					inputs.add(i);
				}

			});
			ItemStack output = ModItems.MOB_CHARM.get().getStackFor(regName);

			ResourceLocation id = new ResourceLocation(Reference.MOD_ID, "mob_charm_" + regName.replace(':', '_'));
			recipes.add(new ShapedRecipe(id, "reliquary.mob_charm", 3, 3, inputs, output));
		}
	}
}
