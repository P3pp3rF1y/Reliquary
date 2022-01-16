package xreliquary.crafting.alkahestry;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import xreliquary.crafting.AlkahestryDrainRecipe;
import xreliquary.crafting.conditions.AlkahestryEnabledCondition;
import xreliquary.reference.Reference;
import xreliquary.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class DrainRecipeBuilder {
	private final Item itemResult;
	private final int charge;

	private DrainRecipeBuilder(ItemLike itemResult, int charge) {
		this.itemResult = itemResult.asItem();
		this.charge = charge;
	}

	public static DrainRecipeBuilder drainRecipe(ItemLike result, int charge) {
		return new DrainRecipeBuilder(result, charge);
	}

	public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		ResourceLocation fullId = new ResourceLocation(Reference.MOD_ID, "alkahestry/drain/" + id.getPath());
		ConditionalRecipe.builder()
				.addCondition(new AlkahestryEnabledCondition())
				.addRecipe(conditionalConsumer -> conditionalConsumer.accept(new DrainRecipeBuilder.Result(fullId, itemResult, charge)))
				.build(consumer, fullId);
	}

	public static class Result implements FinishedRecipe {
		private final Item itemResult;
		private final int charge;
		private final ResourceLocation id;

		public Result(ResourceLocation id, Item itemResult, int charge) {
			this.id = id;
			this.itemResult = itemResult;
			this.charge = charge;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.addProperty("charge", charge);
			JsonObject resultObject = new JsonObject();
			resultObject.addProperty("item", RegistryHelper.getItemRegistryName(itemResult));
			json.add("result", resultObject);
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return AlkahestryDrainRecipe.SERIALIZER;
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
