package reliquary.crafting.alkahestry;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import reliquary.crafting.AlkahestryCraftingRecipe;
import reliquary.crafting.conditions.AlkahestryEnabledCondition;
import reliquary.reference.Reference;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CraftingRecipeBuilder {
	private final Ingredient item;
	private final int charge;
	private final int resultCount;
	private final List<ICondition> additionalConditions = new ArrayList<>();

	private CraftingRecipeBuilder(Ingredient item, int charge, int resultCount) {
		this.item = item;
		this.charge = charge;
		this.resultCount = resultCount;
	}

	public static CraftingRecipeBuilder craftingRecipe(ItemLike item, int charge, int resultCount) {
		return new CraftingRecipeBuilder(Ingredient.of(item), charge, resultCount);
	}

	public static CraftingRecipeBuilder craftingRecipe(Tag<Item> tag, int charge, int resultCount) {
		return new CraftingRecipeBuilder(Ingredient.of(tag), charge, resultCount);
	}

	public CraftingRecipeBuilder addCondition(ICondition condition) {
		additionalConditions.add(condition);
		return this;
	}

	public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		ResourceLocation fullId = new ResourceLocation(Reference.MOD_ID, "alkahestry/crafting/" + id.getPath());
		ConditionalRecipe.Builder builder = ConditionalRecipe.builder()
				.addCondition(new AlkahestryEnabledCondition());
		additionalConditions.forEach(builder::addCondition);
		builder.addRecipe(conditionalConsumer -> conditionalConsumer.accept(new Result(fullId, item, charge, resultCount)));
		builder.build(consumer, fullId);
	}

	public static class Result implements FinishedRecipe {
		private final Ingredient item;
		private final int charge;
		private final int resultCount;
		private final ResourceLocation id;

		public Result(ResourceLocation id, Ingredient item, int charge, int resultCount) {
			this.id = id;
			this.item = item;
			this.charge = charge;
			this.resultCount = resultCount;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.addProperty("charge", charge);
			json.add("ingredient", item.toJson());
			json.addProperty("result_count", resultCount);
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return AlkahestryCraftingRecipe.SERIALIZER;
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
