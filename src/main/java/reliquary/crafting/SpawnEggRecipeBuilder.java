package reliquary.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class SpawnEggRecipeBuilder {
	private final List<Ingredient> ingredients = Lists.newArrayList();
	private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

	private SpawnEggRecipeBuilder() {}

	public static SpawnEggRecipeBuilder spawnEggRecipe() {
		return new SpawnEggRecipeBuilder();
	}

	public SpawnEggRecipeBuilder addIngredient(ItemLike itemProvider) {
		ingredients.add(Ingredient.of(itemProvider));
		return this;
	}

	public SpawnEggRecipeBuilder addCriterion(String name, CriterionTriggerInstance criterionIn) {
		advancementBuilder.addCriterion(name, criterionIn);
		return this;
	}

	public void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
		if (advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id);
		}
		advancementBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
		consumerIn.accept(new Result(id, ingredients, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath())));

	}

	public static class Result implements FinishedRecipe {
		private final List<Ingredient> ingredients;
		private final Advancement.Builder advancementBuilder;
		private final ResourceLocation advancementId;
		private final ResourceLocation id;

		public Result(ResourceLocation id, List<Ingredient> ingredients, Advancement.Builder advancementBuilder, ResourceLocation advancementId) {
			this.id = id;
			this.ingredients = ingredients;
			this.advancementBuilder = advancementBuilder;
			this.advancementId = advancementId;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			JsonArray jsonarray = new JsonArray();

			for (Ingredient ingredient : ingredients) {
				jsonarray.add(ingredient.toJson());
			}

			json.add("ingredients", jsonarray);
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("item", "minecraft:chicken_spawn_egg");
			json.add("result", jsonobject);
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return FragmentToSpawnEggRecipe.SERIALIZER;
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return advancementBuilder.serializeToJson();
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementId() {
			return advancementId;
		}
	}
}
