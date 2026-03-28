package reliquary.compat.jei;

import mezz.jei.library.plugins.vanilla.crafting.JeiShapedRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import reliquary.Reliquary;
import reliquary.crafting.MobCharmRecipe;
import reliquary.init.ModItems;
import reliquary.item.MobCharmFragmentItem;
import reliquary.item.MobCharmRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MobCharmRecipeMaker {
	private MobCharmRecipeMaker() {
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes() {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		MobCharmRecipe.REGISTERED_RECIPES.forEach(baseRecipe -> addMobCharmRecipes(recipes, baseRecipe));
		return recipes;
	}

	private static void addMobCharmRecipes(List<RecipeHolder<CraftingRecipe>> recipes, MobCharmRecipe baseRecipe) {

		for (Identifier regName : MobCharmRegistry.getRegisteredNames()) {
			List<SlotDisplay> slotDisplays = new ArrayList<>();
			ItemStack output = ModItems.MOB_CHARM.get().getStackFor(regName);

			baseRecipe.getIngredients().forEach(i -> i.ifPresentOrElse(ingredient -> {
				if (ingredient.getValues().stream().anyMatch(holder -> holder.value() instanceof MobCharmFragmentItem)) {
					List<SlotDisplay> slotDisplayList = new ArrayList<>();
					ingredient.getValues().forEach(holder -> {
						if (holder.value() instanceof MobCharmFragmentItem) {
							ItemStack itemStack = ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(regName);
							slotDisplayList.add(new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(itemStack)));
						} else {
							slotDisplayList.add(new SlotDisplay.ItemSlotDisplay(holder.value()));
						}
					});

					slotDisplays.add(new SlotDisplay.Composite(slotDisplayList));
				} else {
					slotDisplays.add(ingredient.display());
				}
			}, () -> slotDisplays.add(SlotDisplay.Empty.INSTANCE)));

			ShapedRecipePattern pattern = new ShapedRecipePattern(3, 3, baseRecipe.getIngredients(), Optional.empty());
			ResourceKey<Recipe<?>> id = ResourceKey.create(Registries.RECIPE, Reliquary.getIdentifier("mob_charm_" + regName.toString().replace(':', '_')));
			recipes.add(new RecipeHolder<>(id, new JeiShapedRecipe("reliquary.mob_charm", CraftingBookCategory.MISC, pattern, slotDisplays, new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(output)))));
		}
	}
}
