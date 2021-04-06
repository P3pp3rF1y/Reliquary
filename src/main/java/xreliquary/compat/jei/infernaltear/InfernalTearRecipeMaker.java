package xreliquary.compat.jei.infernaltear;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.reference.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfernalTearRecipeMaker {
	private InfernalTearRecipeMaker() {}

	public static List<InfernalTearRecipe> getRecipes() {
		ArrayList<InfernalTearRecipe> recipes = new ArrayList<>();

		for (Map.Entry<String, Integer> entry : Settings.COMMON.items.infernalTear.getItemExperiences().entrySet()) {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.getKey()));

			if (item != null) {
				recipes.add(new InfernalTearRecipe(new ItemStack(item), entry.getValue()));
			}
		}

		return recipes;
	}
}
