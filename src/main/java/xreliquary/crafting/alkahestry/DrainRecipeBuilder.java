package xreliquary.crafting.alkahestry;

import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
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

	private DrainRecipeBuilder(IItemProvider itemResult, int charge) {
		this.itemResult = itemResult.asItem();
		this.charge = charge;
	}

	public static DrainRecipeBuilder drainRecipe(IItemProvider result, int charge) {
		return new DrainRecipeBuilder(result, charge);
	}

	public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
		ResourceLocation fullId = new ResourceLocation(Reference.MOD_ID, "alkahestry/drain/" + id.getPath());
		ConditionalRecipe.builder()
				.addCondition(new AlkahestryEnabledCondition())
				.addRecipe(conditionalConsumer -> conditionalConsumer.accept(new DrainRecipeBuilder.Result(fullId, itemResult, charge)))
				.build(consumer, fullId);
	}

	public static class Result implements IFinishedRecipe {
		private final Item itemResult;
		private final int charge;
		private final ResourceLocation id;

		public Result(ResourceLocation id, Item itemResult, int charge) {
			this.id = id;
			this.itemResult = itemResult;
			this.charge = charge;
		}

		@Override
		public void serialize(JsonObject json) {
			json.addProperty("charge", charge);
			JsonObject resultObject = new JsonObject();
			resultObject.addProperty("item", RegistryHelper.getItemRegistryName(itemResult));
			json.add("result", resultObject);
		}

		@Override
		public ResourceLocation getID() {
			return id;
		}

		@Override
		public IRecipeSerializer<?> getSerializer() {
			return AlkahestryDrainRecipe.SERIALIZER;
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
