package reliquary.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;
import reliquary.Reliquary;
import reliquary.init.ModItems;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MobCharmRecipeBuilder {
	private final List<String> rows = Lists.newArrayList();
	private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	@Nullable
	private String group;
	private final HolderGetter<Item> items;

	private MobCharmRecipeBuilder(HolderGetter<Item> items) {
		this.items = items;
	}

	public static MobCharmRecipeBuilder charmRecipe(HolderGetter<Item> items) {
		return new MobCharmRecipeBuilder(items);
	}

	public MobCharmRecipeBuilder define(Character symbol, TagKey<Item> tag) {
		return define(symbol, Ingredient.of(items.getOrThrow(tag)));
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
		ResourceKey<Recipe<?>> id = ResourceKey.create(Registries.RECIPE, Reliquary.getIdentifier("mob_charm"));
		Advancement.Builder advancementBuilder = recipeOutput.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(AdvancementRequirements.Strategy.OR);
		criteria.forEach(advancementBuilder::addCriterion);
		recipeOutput.accept(id, new MobCharmRecipe(new ShapedRecipe(new Recipe.CommonInfo(false), new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, group == null ? "" : group), ensureValid(id), new ItemStackTemplate(ModItems.MOB_CHARM.get()))), null);
	}

	private ShapedRecipePattern ensureValid(ResourceKey<Recipe<?>> id) {
		if (criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id);
		} else {
			return ShapedRecipePattern.of(key, rows);
		}
	}
}
