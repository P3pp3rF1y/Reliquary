package xreliquary.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;
import xreliquary.init.ModItems;
import xreliquary.items.AlkahestryTomeItem;

import javax.annotation.Nullable;

public class AlkahestryChargingRecipe implements ICraftingRecipe {
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
		tomeIngredient = Ingredient.fromStacks(AlkahestryTomeItem.setCharge(new ItemStack(ModItems.ALKAHESTRY_TOME.get()), 0));

		recipeOutput = new ItemStack(ModItems.ALKAHESTRY_TOME.get());
		AlkahestryTomeItem.addCharge(recipeOutput, chargeToAdd);

		AlkahestryRecipeRegistry.registerChargingRecipe(this);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		boolean hasTome = false;
		boolean hasIngredient = false;

		for (int x = 0; x < inv.getSizeInventory(); x++) {
			ItemStack slotStack = inv.getStackInSlot(x);

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
	public boolean isDynamic() {
		return true;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		int numberOfIngredients = 0;
		ItemStack tome = ItemStack.EMPTY;
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack stack = inv.getStackInSlot(slot);
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
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.from(Ingredient.EMPTY, chargingIngredient, tomeIngredient);
	}

	@Override
	public ItemStack getRecipeOutput() {
		return recipeOutput;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public int getChargeToAdd() {
		return chargeToAdd;
	}

	public Ingredient getChargingIngredient() {
		return chargingIngredient;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AlkahestryChargingRecipe> {
		@Override
		public AlkahestryChargingRecipe read(ResourceLocation recipeId, JsonObject json) {
			if (!json.has("ingredient")) {
				throw new JsonParseException("No ingredient for alkahestry charging recipe");
			}

			Ingredient ingredient = CraftingHelper.getIngredient(json.get("ingredient"));

			int chargeToAdd = JSONUtils.getInt(json, "charge");

			return new AlkahestryChargingRecipe(recipeId, ingredient, chargeToAdd);
		}

		@Nullable
		@Override
		public AlkahestryChargingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			return new AlkahestryChargingRecipe(recipeId, Ingredient.read(buffer), buffer.readInt());
		}

		@Override
		public void write(PacketBuffer buffer, AlkahestryChargingRecipe recipe) {
			recipe.chargingIngredient.write(buffer);
			buffer.writeInt(recipe.chargeToAdd);
		}
	}
}
