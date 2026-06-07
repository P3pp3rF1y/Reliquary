package reliquary.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import reliquary.init.ModItems;
import reliquary.item.AlkahestryTomeItem;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class AlkahestryChargingRecipe implements CraftingRecipe {
	private final Ingredient chargingIngredient;
	private final int chargeToAdd;
	private final ItemStack recipeOutput;
	private final ResourceLocation id;
	private final Ingredient tomeIngredient;

	private AlkahestryChargingRecipe(ResourceLocation id, Ingredient chargingIngredient, int chargeToAdd) {
		this.id = id;
		this.chargingIngredient = chargingIngredient;
		this.chargeToAdd = chargeToAdd;
		tomeIngredient = new TomeIngredient(chargeToAdd);

		recipeOutput = new ItemStack(ModItems.ALKAHESTRY_TOME.get());
		AlkahestryTomeItem.addCharge(recipeOutput, chargeToAdd);

		AlkahestryRecipeRegistry.registerChargingRecipe(this);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		ItemStack tome = ItemStack.EMPTY;
		int numberOfIngredients = 0;

		for (int x = 0; x < inv.getContainerSize(); x++) {
			ItemStack slotStack = inv.getItem(x);

			if (!slotStack.isEmpty()) {
				boolean inRecipe = false;
				if (chargingIngredient.test(slotStack)) {
					inRecipe = true;
					numberOfIngredients++;
				} else if (tome.isEmpty()) {
					inRecipe = true;
					tome = slotStack;
				}

				if (!inRecipe) {
					return false;
				}
			}
		}

		return numberOfIngredients > 0 && tome.is(ModItems.ALKAHESTRY_TOME.get()) && AlkahestryTomeItem.getCharge(tome) + chargeToAdd * numberOfIngredients <= AlkahestryTomeItem.getChargeLimit();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
		int numberOfIngredients = 0;
		ItemStack tome = ItemStack.EMPTY;
		for (int slot = 0; slot < inv.getContainerSize(); slot++) {
			ItemStack stack = inv.getItem(slot);
			if (chargingIngredient.test(stack)) {
				numberOfIngredients++;
			} else if (stack.getItem() == ModItems.ALKAHESTRY_TOME.get()) {
				tome = stack.copy();
			}
		}

		AlkahestryTomeItem.addCharge(tome, chargeToAdd * numberOfIngredients);

		return tome;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, chargingIngredient, tomeIngredient);
	}

	public ItemStack getRecipeOutput() {
		return recipeOutput;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return recipeOutput;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModItems.ALKAHESTRY_CHARGING_SERIALIZER.get();
	}

	public int getChargeToAdd() {
		return chargeToAdd;
	}

	public Ingredient getChargingIngredient() {
		return chargingIngredient;
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	public static class Serializer implements RecipeSerializer<AlkahestryChargingRecipe> {
		@Override
		public AlkahestryChargingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			if (!json.has("ingredient")) {
				throw new JsonParseException("No ingredient for alkahestry charging recipe");
			}

			Ingredient ingredient = CraftingHelper.getIngredient(json.get("ingredient"), false);

			int chargeToAdd = GsonHelper.getAsInt(json, "charge");

			return new AlkahestryChargingRecipe(recipeId, ingredient, chargeToAdd);
		}

		@Nullable
		@Override
		public AlkahestryChargingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			return new AlkahestryChargingRecipe(recipeId, readIngredient(buffer), buffer.readInt());
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AlkahestryChargingRecipe recipe) {
			writeIngredient(buffer, recipe.chargingIngredient);
			buffer.writeInt(recipe.chargeToAdd);
		}

		private static Ingredient readIngredient(FriendlyByteBuf buffer) {
			return Ingredient.fromJson(JsonParser.parseString(buffer.readUtf()), false);
		}

		private static void writeIngredient(FriendlyByteBuf buffer, Ingredient ingredient) {
			buffer.writeUtf(ingredient.toJson().toString());
		}
	}

	private static class TomeIngredient extends Ingredient {
		private final int chargeToAdd;

		private TomeIngredient(int chargeToAdd) {
			super(Stream.of(new Ingredient.ItemValue(new ItemStack(ModItems.ALKAHESTRY_TOME.get()))));
			this.chargeToAdd = chargeToAdd;
		}

		@Override
		public boolean test(ItemStack stack) {
			return stack.is(ModItems.ALKAHESTRY_TOME.get()) && AlkahestryTomeItem.getCharge(stack) + chargeToAdd <= AlkahestryTomeItem.getChargeLimit();
		}

		@Override
		public boolean isSimple() {
			return false;
		}
	}
}
