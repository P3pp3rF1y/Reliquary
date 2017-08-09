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
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.registries.IForgeRegistryEntry;
import xreliquary.Reliquary;
import xreliquary.compat.jei.JEICategory;
import xreliquary.crafting.AlkahestryTomeIngredient;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AlkahestryCraftingRecipeFactory implements IRecipeFactory {
	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
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

	public static class AlkahestryCraftingRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
		private final int chargeNeeded;
		private final ResourceLocation group;
		private final NonNullList<Ingredient> input;
		private final ItemStack result;
		private final int resultCount;

		public int getResultCount() {
			return resultCount;
		}

		public int getChargeNeeded() {
			return chargeNeeded;
		}

		public AlkahestryCraftingRecipe(ResourceLocation group, NonNullList<Ingredient> input, ItemStack result, int resultCount, int chargeNeeded) {
			this.group = group;
			this.input = input;
			this.result = result;
			this.resultCount = resultCount;
			this.chargeNeeded = chargeNeeded;

			Reliquary.PROXY.registerJEI(JEICategory.ALKAHESTRY_CRAFTING, this);
		}

		@Override
		public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
			NonNullList<ItemStack> remainingItems = IRecipe.super.getRemainingItems(inv);

			addTomeWithUsedCharge(remainingItems, inv);

			return remainingItems;
		}

		@Override
		public NonNullList<Ingredient> getIngredients() {
			return input;
		}

		@Override
		public String getGroup() {
			return group.toString();
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

		@Override
		public boolean matches(InventoryCrafting inv, World worldIn) {
			NonNullList<Ingredient> required = NonNullList.create();
			required.addAll(input);

			for (int x = 0; x < inv.getSizeInventory(); x++)
			{
				ItemStack slot = inv.getStackInSlot(x);

				if (!slot.isEmpty())
				{
					boolean inRecipe = false;
					Iterator<Ingredient> req = required.iterator();

					while (req.hasNext())
					{
						if (req.next().apply(slot))
						{
							inRecipe = true;
							req.remove();
							break;
						}
					}

					if (!inRecipe)
					{
						return false;
					}
				}
			}

			return required.isEmpty();
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

		@Override
		public boolean canFit(int width, int height) {
			return width * height >= 2;
		}

		@Override
		public ItemStack getRecipeOutput() {
			return result;
		}
	}
}
