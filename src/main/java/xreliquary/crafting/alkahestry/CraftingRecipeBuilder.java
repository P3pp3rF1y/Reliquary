package xreliquary.crafting.alkahestry;

import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import xreliquary.crafting.AlkahestryCraftingRecipe;
import xreliquary.crafting.conditions.AlkahestryEnabledCondition;
import xreliquary.reference.Reference;

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

	public static CraftingRecipeBuilder craftingRecipe(IItemProvider item, int charge, int resultCount) {
		return new CraftingRecipeBuilder(Ingredient.fromItems(item), charge, resultCount);
	}

	public static CraftingRecipeBuilder craftingRecipe(ITag<Item> tag, int charge, int resultCount) {
		return new CraftingRecipeBuilder(Ingredient.fromTag(tag), charge, resultCount);
	}

	public CraftingRecipeBuilder addCondition(ICondition condition) {
		additionalConditions.add(condition);
		return this;
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		ResourceLocation fullId = new ResourceLocation(Reference.MOD_ID, "alkahestry/crafting/" + id.getPath());
		ConditionalRecipe.Builder builder = ConditionalRecipe.builder()
				.addCondition(new AlkahestryEnabledCondition());
		additionalConditions.forEach(builder::addCondition);
		builder.addRecipe(conditionalConsumer -> conditionalConsumer.accept(new Result(fullId, item, charge, resultCount)));
		builder.build(consumer, fullId);
	}

	public static class Result implements IFinishedRecipe {
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
		public void serialize(JsonObject json) {
			json.addProperty("charge", charge);
			json.add("ingredient", item.serialize());
			json.addProperty("result_count", resultCount);
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return AlkahestryCraftingRecipe.SERIALIZER;
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
