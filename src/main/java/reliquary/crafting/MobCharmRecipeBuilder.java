package reliquary.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import reliquary.Reliquary;
import reliquary.init.ModItems;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MobCharmRecipeBuilder {
	private final List<String> rows = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	private String group;

	private MobCharmRecipeBuilder() {
	}

	public static MobCharmRecipeBuilder charmRecipe() {
		return new MobCharmRecipeBuilder();
	}

	public MobCharmRecipeBuilder define(Character symbol, TagKey<Item> tag) {
		return define(symbol, Ingredient.of(tag));
	}

	public MobCharmRecipeBuilder define(Character symbol, ItemLike item) {
		return define(symbol, Ingredient.of(item));
	}

	public MobCharmRecipeBuilder define(Character symbol, Ingredient ingredient) {
		if (key.containsKey(symbol)) {
			throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
		} else if (symbol == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			key.put(symbol, ingredient);
			return this;
		}
	}

	public MobCharmRecipeBuilder pattern(String pattern) {
		if (!rows.isEmpty() && pattern.length() != rows.getFirst().length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			rows.add(pattern);
			return this;
		}
	}

	public MobCharmRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
		criteria.put(name, criterion);
		return this;
	}

	public MobCharmRecipeBuilder setGroup(String groupIn) {
		group = groupIn;
		return this;
	}

	public void save(RecipeOutput recipeOutput) {
		ResourceLocation id = Reliquary.getRL("mob_charm");
		Advancement.Builder advancementBuilder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
		criteria.forEach(advancementBuilder::addCriterion);
		recipeOutput.accept(id,
				new MobCharmRecipe(
						new ShapedRecipe(group == null ? "" : group, CraftingBookCategory.MISC, ensureValid(id), new ItemStack(ModItems.MOB_CHARM.get()))),
				null);
	}

	private ShapedRecipePattern ensureValid(ResourceLocation id) {
		if (criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id);
		} else {
			return ShapedRecipePattern.of(key, rows);
		}
	}
}
