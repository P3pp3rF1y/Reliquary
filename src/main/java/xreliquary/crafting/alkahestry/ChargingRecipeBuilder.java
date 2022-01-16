package xreliquary.crafting.alkahestry;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import xreliquary.crafting.AlkahestryChargingRecipe;
import xreliquary.crafting.conditions.AlkahestryEnabledCondition;
import xreliquary.reference.Reference;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ChargingRecipeBuilder {
	private final Ingredient ingredient;
	private final int charge;

	private ChargingRecipeBuilder(ItemLike ingredient, int charge) {
		this.ingredient = Ingredient.of(ingredient);
		this.charge = charge;
	}

	public static ChargingRecipeBuilder chargingRecipe(ItemLike result, int charge) {
		return new ChargingRecipeBuilder(result, charge);
	}

	public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		ResourceLocation fullId = new ResourceLocation(Reference.MOD_ID, "alkahestry/charging/" + id.getPath());
		ConditionalRecipe.builder()
				.addCondition(new AlkahestryEnabledCondition())
				.addRecipe(conditionalConsumer -> conditionalConsumer.accept(new Result(fullId, ingredient, charge)))
				.build(consumer, fullId);
	}

	public static class Result implements FinishedRecipe {
		private final Ingredient ingredient;
		private final int charge;
		private final ResourceLocation id;

		public Result(ResourceLocation id, Ingredient ingredient, int charge) {
			this.id = id;
			this.ingredient = ingredient;
			this.charge = charge;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.addProperty("charge", charge);
			json.add("ingredient", ingredient.toJson());
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return AlkahestryChargingRecipe.SERIALIZER;
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
