package xreliquary.crafting.alkahestry;

import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import xreliquary.crafting.AlkahestryChargingRecipe;
import xreliquary.crafting.conditions.AlkahestryEnabledCondition;
import xreliquary.reference.Reference;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ChargingRecipeBuilder {
	private final Ingredient ingredient;
	private final int charge;

	private ChargingRecipeBuilder(IItemProvider ingredient, int charge) {
		this.ingredient = Ingredient.fromItems(ingredient);
		this.charge = charge;
	}

	public static ChargingRecipeBuilder chargingRecipe(IItemProvider result, int charge) {
		return new ChargingRecipeBuilder(result, charge);
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		ResourceLocation fullId = new ResourceLocation(Reference.MOD_ID, "alkahestry/charging/" + id.getPath());
		ConditionalRecipe.builder()
				.addCondition(new AlkahestryEnabledCondition())
				.addRecipe(conditionalConsumer -> conditionalConsumer.accept(new Result(fullId, ingredient, charge)))
				.build(consumer, fullId);
	}

	public static class Result implements IFinishedRecipe {
		private final Ingredient ingredient;
		private final int charge;
		private final ResourceLocation id;

		public Result(ResourceLocation id, Ingredient ingredient, int charge) {
			this.id = id;
			this.ingredient = ingredient;
			this.charge = charge;
		}

		@Override
		public void serialize(JsonObject json) {
			json.addProperty("charge", charge);
			json.add("ingredient", ingredient.serialize());
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return AlkahestryChargingRecipe.SERIALIZER;
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
