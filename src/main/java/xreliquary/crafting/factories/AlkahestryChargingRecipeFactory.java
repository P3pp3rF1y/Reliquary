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
import xreliquary.crafting.AlkahestryTomeIngredient;
import xreliquary.init.ModItems;
import xreliquary.init.XRRecipes;
import xreliquary.items.ItemAlkahestryTome;
import xreliquary.reference.Reference;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AlkahestryChargingRecipeFactory implements IRecipeFactory {
	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for(JsonElement ele : JsonUtils.getJsonArray(json, "ingredients"))
			ingredients.add(CraftingHelper.getIngredient(ele, context));

		if(ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for alkahestry crafting recipe");
		}
		if(ingredients.size() > 1) {
			throw new JsonParseException("Too many ingredients for alkahestry crafting recipe");
		}

		int chargeToAdd = JsonUtils.getInt(json, "charge");

		ItemStack tome = new ItemStack(ModItems.alkahestryTome);
		ItemAlkahestryTome.setCharge(tome, 0);

		return new AlkahestryChargingRecipe(new ResourceLocation(Reference.MOD_ID, "alkahestry_charging"), ingredients.get(0), new AlkahestryTomeIngredient(tome, 0), chargeToAdd);
	}

	public class AlkahestryChargingRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
		private final ResourceLocation group;
		private final Ingredient chargingIngredient;
		private final Ingredient tomeIngredient;
		private final Predicate<ItemStack> tomeIngredientMatch;

		public int getChargeToAdd() {
			return chargeToAdd;
		}

		private final int chargeToAdd;
		private final ItemStack result;

		public AlkahestryChargingRecipe(ResourceLocation group, Ingredient chargingIngredient, Ingredient tomeIngredient, int chargeToAdd) {
			this.group = group;
			this.chargingIngredient = chargingIngredient;
			this.tomeIngredientMatch = s -> tomeIngredient.apply(s) && ItemAlkahestryTome.getCharge(s) + chargeToAdd <= ItemAlkahestryTome.getChargeLimit();
			this.tomeIngredient = tomeIngredient;
			this.chargeToAdd = chargeToAdd;
			result = new ItemStack(ModItems.alkahestryTome);
			ItemAlkahestryTome.addCharge(result, chargeToAdd);

			XRRecipes.chargingRecipes.add(this);
		}

		@Override
		public boolean matches(InventoryCrafting inv, World world) {
			boolean hasTome = false;
			boolean hasIngredient = false;

			for(int x = 0; x < inv.getSizeInventory(); x++) {
				ItemStack slot = inv.getStackInSlot(x);

				if(!slot.isEmpty()) {
					boolean inRecipe = false;
					if(chargingIngredient.apply(slot)) {
						inRecipe = true;
						hasIngredient = true;
					} else if(!hasTome && tomeIngredientMatch.test(slot)) {
						inRecipe = true;
						hasTome = true;
					}

					if(!inRecipe) {
						return false;
					}
				}
			}

			return hasIngredient && hasTome;
		}

		@Override
		public ItemStack getCraftingResult(InventoryCrafting inv) {
			int numberOfIngredients = 0;
			ItemStack tome = ItemStack.EMPTY;
			for(int slot = 0; slot < inv.getSizeInventory(); slot++) {
				ItemStack stack = inv.getStackInSlot(slot);
				if (chargingIngredient.apply(stack)) {
					numberOfIngredients++;
				} else if (tomeIngredientMatch.test(stack)) {
					tome = stack.copy();
				}
			}

			ItemAlkahestryTome.addCharge(tome, chargeToAdd * numberOfIngredients);

			return tome;
		}

		@Override
		public boolean canFit(int width, int height) {
			return width * height >= 2;
		}

		@Override
		public ItemStack getRecipeOutput() {
			return result;
		}

		@Override
		public String getGroup() {
			return group.toString();
		}

		@Override
		public NonNullList<Ingredient> getIngredients() {
			return NonNullList.from(Ingredient.EMPTY, chargingIngredient, tomeIngredient);
		}

		public Ingredient getChargingIngredient() {
			return chargingIngredient;
		}
	}
}
