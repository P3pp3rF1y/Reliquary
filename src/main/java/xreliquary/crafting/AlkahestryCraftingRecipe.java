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
import xreliquary.reference.Settings;

import javax.annotation.Nullable;

public class AlkahestryCraftingRecipe implements ICraftingRecipe {
	public static final Serializer SERIALIZER = new Serializer();
	private final Ingredient craftingIngredient;
	private final int chargeNeeded;
	private final int resultCount;
	private final ItemStack result;
	private final ResourceLocation id;
	private final Ingredient tomeIngredient;

	private AlkahestryCraftingRecipe(ResourceLocation id, Ingredient craftingIngredient, int chargeNeeded, int resultCount) {
		this.id = id;
		this.craftingIngredient = craftingIngredient;
		this.chargeNeeded = chargeNeeded;
		tomeIngredient = Ingredient.fromStacks(AlkahestryTomeItem.setCharge(new ItemStack(ModItems.ALKAHESTRY_TOME.get()), Settings.COMMON.items.alkahestryTome.chargeLimit.get()));
		this.resultCount = resultCount;
		result = craftingIngredient.getMatchingStacks()[0].copy();
		result.setCount(resultCount);

		AlkahestryRecipeRegistry.registerCraftingRecipe(this);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		boolean hasIngredient = false;
		boolean hasTome = false;
		for (int x = 0; x < inv.getSizeInventory(); x++) {
			ItemStack slotStack = inv.getStackInSlot(x);

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
		return NonNullList.from(Ingredient.EMPTY, craftingIngredient, tomeIngredient);
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack stack = inv.getStackInSlot(slot);

			if (!stack.isEmpty() && stack.getItem() != ModItems.ALKAHESTRY_TOME.get()) {
				ItemStack craftingResult = stack.copy();
				craftingResult.setCount(resultCount);
				return craftingResult;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return result;
	}

	public Ingredient getCraftingIngredient() {
		return craftingIngredient;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
		NonNullList<ItemStack> remainingItems = ICraftingRecipe.super.getRemainingItems(inv);

		addTomeWithUsedCharge(remainingItems, inv);

		return remainingItems;
	}

	private void addTomeWithUsedCharge(NonNullList<ItemStack> remainingItems, CraftingInventory inv) {
		for (int slot = 0; slot < remainingItems.size(); slot++) {
			ItemStack stack = inv.getStackInSlot(slot);

			if (stack.getItem() == ModItems.ALKAHESTRY_TOME.get()) {
				ItemStack tome = stack.copy();
				ModItems.ALKAHESTRY_TOME.get().useCharge(tome, chargeNeeded);
				remainingItems.set(slot, tome);

				break;
			}
		}
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	public int getChargeNeeded() {
		return chargeNeeded;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AlkahestryCraftingRecipe> {
		@Override
		public AlkahestryCraftingRecipe read(ResourceLocation recipeId, JsonObject json) {
			if (!json.has("ingredient")) {
				throw new JsonParseException("No ingredient for alkahestry crafting recipe");
			}

			Ingredient ingredient = CraftingHelper.getIngredient(json.get("ingredient"));
			int resultCount = JSONUtils.getInt(json, "result_count");
			int chargeNeeded = JSONUtils.getInt(json, "charge");

			return new AlkahestryCraftingRecipe(recipeId, ingredient, chargeNeeded, resultCount);
		}

		@Nullable
		@Override
		public AlkahestryCraftingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			return new AlkahestryCraftingRecipe(recipeId, Ingredient.read(buffer), buffer.readInt(), buffer.readInt());
		}

		@Override
		public void write(PacketBuffer buffer, AlkahestryCraftingRecipe recipe) {
			recipe.craftingIngredient.write(buffer);
			buffer.writeInt(recipe.chargeNeeded);
			buffer.writeInt(recipe.resultCount);
		}
	}
}
