package reliquary.crafting;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import reliquary.init.ModItems;
import reliquary.reference.Reference;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class InfernalTearValueRecipeBuilder {
	private final Ingredient ingredient;
	private final int experiencePoints;

	private InfernalTearValueRecipeBuilder(Ingredient ingredient, int experiencePoints) {
		this.ingredient = ingredient;
		this.experiencePoints = experiencePoints;
	}

	public static InfernalTearValueRecipeBuilder valueRecipe(ItemLike item, int experiencePoints) {
		return new InfernalTearValueRecipeBuilder(Ingredient.of(item), experiencePoints);
	}

	public static InfernalTearValueRecipeBuilder valueRecipe(TagKey<Item> tag, int experiencePoints) {
		return new InfernalTearValueRecipeBuilder(Ingredient.of(tag), experiencePoints);
	}

	public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		consumer.accept(new Result(new ResourceLocation(Reference.MOD_ID, "infernal_tear/" + id.getPath()), ingredient, experiencePoints));
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final Ingredient ingredient;
		private final int experiencePoints;

		public Result(ResourceLocation id, Ingredient ingredient, int experiencePoints) {
			this.id = id;
			this.ingredient = ingredient;
			this.experiencePoints = experiencePoints;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("ingredient", ingredient.toJson());
			json.addProperty("xp", experiencePoints);
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return ModItems.INFERNAL_TEAR_VALUE_SERIALIZER.get();
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
