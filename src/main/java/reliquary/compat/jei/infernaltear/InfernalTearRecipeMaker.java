package reliquary.compat.jei.infernaltear;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import reliquary.reference.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfernalTearRecipeMaker {
	private InfernalTearRecipeMaker() {
	}

	public static List<InfernalTearRecipe> getRecipes() {
		ArrayList<InfernalTearRecipe> recipes = new ArrayList<>();

		for (Map.Entry<String, Integer> entry : Config.COMMON.items.infernalTear.getItemExperiences().entrySet()) {
			Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(entry.getKey()));

			if (item != Items.AIR) {
				recipes.add(new InfernalTearRecipe(new ItemStack(item), entry.getValue()));
			}
		}

		return recipes;
	}
}
