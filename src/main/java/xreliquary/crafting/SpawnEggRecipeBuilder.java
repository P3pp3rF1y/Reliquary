package xreliquary.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class SpawnEggRecipeBuilder {
	private final List<Ingredient> ingredients = Lists.newArrayList();
	private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();

	private SpawnEggRecipeBuilder() {}

	public static SpawnEggRecipeBuilder spawnEggRecipe() {
		return new SpawnEggRecipeBuilder();
	}

	public SpawnEggRecipeBuilder addIngredient(IItemProvider itemProvider) {
		ingredients.add(Ingredient.fromItems(itemProvider));
		return this;
	}

	public SpawnEggRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
		advancementBuilder.withCriterion(name, criterionIn);
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
		if (advancementBuilder.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id);
		}
		advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", RecipeUnlockedTrigger.create(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
		consumerIn.accept(new Result(id, ingredients, advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath())));

	}

	public static class Result implements IFinishedRecipe {
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
		public void serialize(JsonObject json) {
			JsonArray jsonarray = new JsonArray();

			for (Ingredient ingredient : ingredients) {
				jsonarray.add(ingredient.serialize());
			}

			json.add("ingredients", jsonarray);
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("item", "minecraft:chicken_spawn_egg");
			json.add("result", jsonobject);
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return FragmentToSpawnEggRecipe.SERIALIZER;
		}

		@Nullable
		@Override
		public JsonObject getAdvancementJson() {
			return advancementBuilder.serialize();
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementID() {
			return advancementId;
		}
	}
}
