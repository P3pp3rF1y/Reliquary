package reliquary.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import reliquary.init.ModItems;
import reliquary.items.AlkahestryTomeItem;
import reliquary.reference.Settings;

import javax.annotation.Nullable;

public class AlkahestryCraftingRecipe implements CraftingRecipe {
	public static final Serializer SERIALIZER = new Serializer();
	private final Ingredient craftingIngredient;
	private final int chargeNeeded;
	private final int resultCount;
	private ItemStack result = ItemStack.EMPTY;
	private final ResourceLocation id;
	private final Ingredient tomeIngredient;

	private AlkahestryCraftingRecipe(ResourceLocation id, Ingredient craftingIngredient, int chargeNeeded, int resultCount) {
		this.id = id;
		this.craftingIngredient = craftingIngredient;
		this.chargeNeeded = chargeNeeded;
		tomeIngredient = Ingredient.of(AlkahestryTomeItem.setCharge(new ItemStack(ModItems.ALKAHESTRY_TOME.get()), Settings.COMMON.items.alkahestryTome.chargeLimit.get()));
		this.resultCount = resultCount;

		AlkahestryRecipeRegistry.registerCraftingRecipe(this);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		boolean hasIngredient = false;
		boolean hasTome = false;
		for (int x = 0; x < inv.getContainerSize(); x++) {
			ItemStack slotStack = inv.getItem(x);

			if (!slotStack.isEmpty()) {
				boolean inRecipe = false;
				if (craftingIngredient.test(slotStack)) {
					inRecipe = true;
					hasIngredient = true;
				} else if (!hasTome && slotStack.getItem() == ModItems.ALKAHESTRY_TOME.get() && AlkahestryTomeItem.getCharge(slotStack) >= chargeNeeded) {
					inRecipe = true;
					hasTome = true;
				}

				if (!inRecipe) {
					return false;
				}

			}
		}

		return hasIngredient && hasTome;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, craftingIngredient, tomeIngredient);
	}

	@Override
	public ItemStack assemble(CraftingContainer inv) {
		for (int slot = 0; slot < inv.getContainerSize(); slot++) {
			ItemStack stack = inv.getItem(slot);

			if (!stack.isEmpty() && stack.getItem() != ModItems.ALKAHESTRY_TOME.get()) {
				ItemStack craftingResult = stack.copy();
				craftingResult.setCount(resultCount);
				return craftingResult;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getResultItem() {
		if (result == ItemStack.EMPTY) {
			ItemStack[] ingredientItems = craftingIngredient.getItems();
			if (ingredientItems.length > 0) {
				result = ingredientItems[0].copy();
				result.setCount(resultCount);
			}
		}

		return result;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		NonNullList<ItemStack> remainingItems = CraftingRecipe.super.getRemainingItems(inv);

		addTomeWithUsedCharge(remainingItems, inv);

		return remainingItems;
	}

	private void addTomeWithUsedCharge(NonNullList<ItemStack> remainingItems, CraftingContainer inv) {
		for (int slot = 0; slot < remainingItems.size(); slot++) {
			ItemStack stack = inv.getItem(slot);

			if (stack.getItem() == ModItems.ALKAHESTRY_TOME.get()) {
				ItemStack tome = stack.copy();
				AlkahestryTomeItem.useCharge(tome, chargeNeeded);
				remainingItems.set(slot, tome);

				break;
			}
		}
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	public int getChargeNeeded() {
		return chargeNeeded;
	}

	public static class Serializer implements RecipeSerializer<AlkahestryCraftingRecipe> {
		@Override
		public AlkahestryCraftingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			if (!json.has("ingredient")) {
				throw new JsonParseException("No ingredient for alkahestry crafting recipe");
			}

			Ingredient ingredient = CraftingHelper.getIngredient(json.get("ingredient"));
			int resultCount = GsonHelper.getAsInt(json, "result_count");
			int chargeNeeded = GsonHelper.getAsInt(json, "charge");

			return new AlkahestryCraftingRecipe(recipeId, ingredient, chargeNeeded, resultCount);
		}

		@Nullable
		@Override
		public AlkahestryCraftingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			return new AlkahestryCraftingRecipe(recipeId, Ingredient.fromNetwork(buffer), buffer.readInt(), buffer.readInt());
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AlkahestryCraftingRecipe recipe) {
			recipe.craftingIngredient.toNetwork(buffer);
			buffer.writeInt(recipe.chargeNeeded);
			buffer.writeInt(recipe.resultCount);
		}
	}
}
