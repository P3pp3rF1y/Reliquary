package xreliquary.crafting;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import xreliquary.crafting.conditions.PotionsEnabledCondition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PotionEffectsRecipeBuilder {
	private final ShapedRecipeBuilder shapedRecipeBuilder;
	private final List<ICondition> additionalConditions = new ArrayList<>();
	private final float durationFactor;

	private PotionEffectsRecipeBuilder(ItemLike item, int count, float durationFactor) {
		shapedRecipeBuilder = ShapedRecipeBuilder.shaped(item, count);
		this.durationFactor = durationFactor;
	}

	public static PotionEffectsRecipeBuilder potionEffectsRecipe(ItemLike item, int count, float durationFactor) {
		return new PotionEffectsRecipeBuilder(item, count, durationFactor);
	}

	public PotionEffectsRecipeBuilder addCondition(ICondition condition) {
		additionalConditions.add(condition);
		return this;
	}

	public PotionEffectsRecipeBuilder key(Character symbol, Tag<Item> tagIn) {
		return key(symbol, Ingredient.of(tagIn));
	}

	public PotionEffectsRecipeBuilder key(Character symbol, ItemLike itemIn) {
		return key(symbol, Ingredient.of(itemIn));
	}

	public PotionEffectsRecipeBuilder key(Character symbol, Ingredient ingredient) {
		shapedRecipeBuilder.define(symbol, ingredient);
		return this;
	}

	public PotionEffectsRecipeBuilder patternLine(String pattern) {
		shapedRecipeBuilder.pattern(pattern);
		return this;
	}

	public PotionEffectsRecipeBuilder addCriterion(String name, CriterionTriggerInstance criterion) {
		shapedRecipeBuilder.unlockedBy(name, criterion);
		return this;
	}

	public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		ConditionalRecipe.Builder builder = ConditionalRecipe.builder()
				.addCondition(new PotionsEnabledCondition());
		additionalConditions.forEach(builder::addCondition);
		shapedRecipeBuilder.save(shapedResult -> builder.addRecipe(conditionalConsumer -> conditionalConsumer.accept(new Result(id, shapedResult, durationFactor))));
		builder.build(consumer, id);
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final FinishedRecipe shapedResult;
		private final float durationFactor;

		public Result(ResourceLocation id, FinishedRecipe shapedResult, float durationFactor) {
			this.id = id;
			this.shapedResult = shapedResult;
			this.durationFactor = durationFactor;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			shapedResult.serializeRecipeData(json);
			json.addProperty("duration_factor", durationFactor);
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return PotionEffectsRecipe.SERIALIZER;
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}
