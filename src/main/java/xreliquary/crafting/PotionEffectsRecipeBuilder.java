package xreliquary.crafting;

import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import xreliquary.crafting.conditions.PotionsEnabledCondition;
import xreliquary.reference.Reference;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PotionEffectsRecipeBuilder {
	private final ShapedRecipeBuilder shapedRecipeBuilder;
	private final List<ICondition> additionalConditions = new ArrayList<>();
	private final float durationFactor;

	private PotionEffectsRecipeBuilder(IItemProvider item, int count, float durationFactor) {
		shapedRecipeBuilder = ShapedRecipeBuilder.shapedRecipe(item, count);
		this.durationFactor = durationFactor;
	}

	public static PotionEffectsRecipeBuilder potionEffectsRecipe(IItemProvider item, int count, float durationFactor) {
		return new PotionEffectsRecipeBuilder(item, count, durationFactor);
	}

	public PotionEffectsRecipeBuilder addCondition(ICondition condition) {
		additionalConditions.add(condition);
		return this;
	}

	public PotionEffectsRecipeBuilder key(Character symbol, ITag<Item> tagIn) {
		return key(symbol, Ingredient.fromTag(tagIn));
	}

	public PotionEffectsRecipeBuilder key(Character symbol, IItemProvider itemIn) {
		return key(symbol, Ingredient.fromItems(itemIn));
	}

	public PotionEffectsRecipeBuilder key(Character symbol, Ingredient ingredient) {
		shapedRecipeBuilder.key(symbol, ingredient);
		return this;
	}

	public PotionEffectsRecipeBuilder patternLine(String pattern) {
		shapedRecipeBuilder.patternLine(pattern);
		return this;
	}

	public PotionEffectsRecipeBuilder addCriterion(String name, ICriterionInstance criterion) {
		shapedRecipeBuilder.addCriterion(name, criterion);
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		ConditionalRecipe.Builder builder = ConditionalRecipe.builder()
				.addCondition(new PotionsEnabledCondition());
		additionalConditions.forEach(builder::addCondition);
		shapedRecipeBuilder.build(shapedResult -> builder.addRecipe(conditionalConsumer -> conditionalConsumer.accept(new Result(id, shapedResult, durationFactor))));
		builder.build(consumer, id);
	}

	public static class Result implements IFinishedRecipe {
		private final ResourceLocation id;
		private final IFinishedRecipe shapedResult;
		private final float durationFactor;

		public Result(ResourceLocation id, IFinishedRecipe shapedResult, float durationFactor) {
			this.id = id;
			this.shapedResult = shapedResult;
			this.durationFactor = durationFactor;
		}

		@Override
		public void serialize(JsonObject json) {
			shapedResult.serialize(json);
			json.addProperty("duration_factor", durationFactor);
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return PotionEffectsRecipe.SERIALIZER;
		}

		@Nullable
		@Override
		public JsonObject getAdvancementJson() {
			return null;
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementID() {
			return null;
		}
	}
}
