package xreliquary.crafting;

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
import net.minecraftforge.registries.ForgeRegistryEntry;
import xreliquary.init.ModItems;
import xreliquary.items.AlkahestryTomeItem;

import javax.annotation.Nullable;

public class AlkahestryChargingRecipe implements CraftingRecipe {
	public static final Serializer SERIALIZER = new Serializer();
	private final Ingredient chargingIngredient;
	private final int chargeToAdd;
	private final ItemStack recipeOutput;
	private final ResourceLocation id;
	private final Ingredient tomeIngredient;

	private AlkahestryChargingRecipe(ResourceLocation id, Ingredient chargingIngredient, int chargeToAdd) {
		this.id = id;
		this.chargingIngredient = chargingIngredient;
		this.chargeToAdd = chargeToAdd;
		tomeIngredient = Ingredient.of(AlkahestryTomeItem.setCharge(new ItemStack(ModItems.ALKAHESTRY_TOME.get()), 0));

		recipeOutput = new ItemStack(ModItems.ALKAHESTRY_TOME.get());
		AlkahestryTomeItem.addCharge(recipeOutput, chargeToAdd);

		AlkahestryRecipeRegistry.registerChargingRecipe(this);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		boolean hasTome = false;
		boolean hasIngredient = false;

		for (int x = 0; x < inv.getContainerSize(); x++) {
			ItemStack slotStack = inv.getItem(x);

			if (!slotStack.isEmpty()) {
				boolean inRecipe = false;
				if (chargingIngredient.test(slotStack)) {
					inRecipe = true;
					hasIngredient = true;
				} else if (!hasTome && slotStack.getItem() == ModItems.ALKAHESTRY_TOME.get() && AlkahestryTomeItem.getCharge(slotStack) + chargeToAdd <= AlkahestryTomeItem.getChargeLimit()) {
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
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv) {
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

	@Override
	public ItemStack getResultItem() {
		return recipeOutput;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public int getChargeToAdd() {
		return chargeToAdd;
	}

	public Ingredient getChargingIngredient() {
		return chargingIngredient;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<AlkahestryChargingRecipe> {
		@Override
		public AlkahestryChargingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			if (!json.has("ingredient")) {
				throw new JsonParseException("No ingredient for alkahestry charging recipe");
			}

			Ingredient ingredient = CraftingHelper.getIngredient(json.get("ingredient"));

			int chargeToAdd = GsonHelper.getAsInt(json, "charge");

			return new AlkahestryChargingRecipe(recipeId, ingredient, chargeToAdd);
		}

		@Nullable
		@Override
		public AlkahestryChargingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			return new AlkahestryChargingRecipe(recipeId, Ingredient.fromNetwork(buffer), buffer.readInt());
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AlkahestryChargingRecipe recipe) {
			recipe.chargingIngredient.toNetwork(buffer);
			buffer.writeInt(recipe.chargeToAdd);
		}
	}
}
