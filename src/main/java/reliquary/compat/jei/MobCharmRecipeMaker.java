package reliquary.compat.jei;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import reliquary.init.ModItems;
import reliquary.items.MobCharmRegistry;
import reliquary.reference.Reference;

import java.util.ArrayList;
import java.util.List;

public class MobCharmRecipeMaker {
	private MobCharmRecipeMaker() {}

	public static List<ShapedRecipe> getRecipes() {
		List<ShapedRecipe> recipes = new ArrayList<>();

		for (String regName : MobCharmRegistry.getRegisteredNames()) {
			Ingredient fragmentIngredient = Ingredient.of(ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(regName));
			Ingredient leatherIngredient = Ingredient.of(Items.LEATHER);
			Ingredient stringIngredient = Ingredient.of(Items.STRING);

			NonNullList<Ingredient> inputs = NonNullList.create();
			inputs.add(fragmentIngredient);
			inputs.add(leatherIngredient);
			inputs.add(fragmentIngredient);
			inputs.add(fragmentIngredient);
			inputs.add(stringIngredient);
			inputs.add(fragmentIngredient);
			inputs.add(fragmentIngredient);
			inputs.add(Ingredient.EMPTY);
			inputs.add(fragmentIngredient);

			ItemStack output = ModItems.MOB_CHARM.get().getStackFor(regName);

			ResourceLocation id = new ResourceLocation(Reference.MOD_ID, "mob_charm_" + regName.replace(':', '_'));
			recipes.add(new ShapedRecipe(id, "reliquary.mob_charm", 3, 3, inputs, output));
		}

		return recipes;
	}
}
