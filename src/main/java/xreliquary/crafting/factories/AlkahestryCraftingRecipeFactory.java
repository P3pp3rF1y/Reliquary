package xreliquary.crafting.factories;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
public class AlkahestryCraftingRecipeFactory implements IRecipeFactory {
	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		String group = JsonUtils.getString(json, "group", "");

		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients"))
			ingredients.add(CraftingHelper.getIngredient(ele, context));

		if (ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for alkahestry crafting recipe");
		}
		if (ingredients.size() > 1) {
			throw new JsonParseException("Too many ingredients for alkahestry crafting recipe");
		}


		int resultCount = JsonUtils.getInt(json, "result_count");
		int chargeNeeded = JsonUtils.getInt(json, "charge");
		ItemStack result = ingredients.get(0).getMatchingStacks()[0].copy();
		result.setCount(resultCount);

		ItemStack tome = new ItemStack(ModItems.alkahestryTome);
		ModItems.alkahestryTome.setCharge(tome, Settings.AlkahestryTome.chargeLimit);

		ingredients.add(new AlkahestryTomeIngredient(tome, chargeNeeded));

		return new AlkahestryCraftingRecipe(new ResourceLocation(Reference.MOD_ID, "alkahestry_crafting"), ingredients, result, resultCount, chargeNeeded);
	}

	public static class AlkahestryCraftingRecipe extends ShapelessOreRecipe {
		private final int chargeNeeded;
		private final int resultCount;

		public int getResultCount() {
			return resultCount;
		}

		public int getChargeNeeded() {
			return chargeNeeded;
		}

		public AlkahestryCraftingRecipe(ResourceLocation group, NonNullList<Ingredient> input, ItemStack result, int resultCount, int chargeNeeded) {
			super(group, input, result);
			this.resultCount = resultCount;
			this.chargeNeeded = chargeNeeded;

			Reliquary.PROXY.registerJEI(this);
		}

		@Override
		public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
			NonNullList<ItemStack> remainingItems = super.getRemainingItems(inv);

			addTomeWithUsedCharge(remainingItems, inv);

			return remainingItems;
		}

		private void addTomeWithUsedCharge(NonNullList<ItemStack> remainingItems, InventoryCrafting inv) {
			for(int slot = 0; slot < remainingItems.size(); slot++) {
				ItemStack stack = inv.getStackInSlot(slot);

				if(stack.getItem() == ModItems.alkahestryTome) {
					ItemStack tome = stack.copy();
					ModItems.alkahestryTome.useCharge(tome, chargeNeeded);
					remainingItems.set(slot, tome);

					break;
				}
			}
		}

		@Nonnull
		@Override
		public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
			for(int slot=0; slot < inv.getSizeInventory(); slot++) {
				ItemStack stack = inv.getStackInSlot(slot);

				if (!stack.isEmpty() && stack.getItem() != ModItems.alkahestryTome) {
					ItemStack result = stack.copy();
					result.setCount(resultCount);
					return result;
				}
			}

			return ItemStack.EMPTY;
		}
	}
}
