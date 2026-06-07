package reliquary.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import reliquary.init.ModItems;

import javax.annotation.Nullable;

public class InfernalTearValueRecipe implements Recipe<Container> {
	private final ResourceLocation id;
	private final Ingredient ingredient;
	private final int experiencePoints;

	public InfernalTearValueRecipe(ResourceLocation id, Ingredient ingredient, int experiencePoints) {
		this.id = id;
		this.ingredient = ingredient;
		this.experiencePoints = experiencePoints;
	}

	@Override
	public boolean matches(Container container, Level level) {
		return false;
	}

	@Override
	public ItemStack assemble(Container container, RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModItems.INFERNAL_TEAR_VALUE_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModItems.INFERNAL_TEAR_VALUE_TYPE.get();
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, ingredient);
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public int getExperiencePoints() {
		return experiencePoints;
	}

	public static class Serializer implements RecipeSerializer<InfernalTearValueRecipe> {
		@Override
		public InfernalTearValueRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			if (!json.has("ingredient")) {
				throw new JsonParseException("No ingredient for infernal tear value recipe");
			}

			Ingredient ingredient = CraftingHelper.getIngredient(json.get("ingredient"), false);
			int experiencePoints = GsonHelper.getAsInt(json, "xp");
			return new InfernalTearValueRecipe(recipeId, ingredient, experiencePoints);
		}

		@Nullable
		@Override
		public InfernalTearValueRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			return new InfernalTearValueRecipe(recipeId, readIngredient(buffer), buffer.readVarInt());
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, InfernalTearValueRecipe recipe) {
			writeIngredient(buffer, recipe.ingredient);
			buffer.writeVarInt(recipe.experiencePoints);
		}

		private static Ingredient readIngredient(FriendlyByteBuf buffer) {
			return Ingredient.fromJson(JsonParser.parseString(buffer.readUtf()), false);
		}

		private static void writeIngredient(FriendlyByteBuf buffer, Ingredient ingredient) {
			buffer.writeUtf(ingredient.toJson().toString());
		}
	}
}
