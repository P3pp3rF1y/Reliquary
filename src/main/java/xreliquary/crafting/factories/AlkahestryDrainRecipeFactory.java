package xreliquary.crafting.factories;

import com.google.gson.JsonObject;
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
import xreliquary.init.ModItems;
import xreliquary.init.XRRecipes;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AlkahestryDrainRecipeFactory implements IRecipeFactory {
	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		int chargeToDrain = JsonUtils.getInt(json, "charge");
		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		return new AlkahestryDrainRecipe(new ResourceLocation(Reference.MOD_ID, "alkahestry_drain"), chargeToDrain, result);
	}

	public static class AlkahestryDrainRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
		private final int chargeToDrain;
		private final ItemStack result;
		private final NonNullList<Ingredient> input;
		private ResourceLocation group;

		public AlkahestryDrainRecipe(ResourceLocation group, int chargeToDrain, ItemStack result) {
			this.group = group;
			ItemStack tome = new ItemStack(ModItems.alkahestryTome);
			ModItems.alkahestryTome.setCharge(tome, Settings.Items.AlkahestryTome.chargeLimit);
			input = NonNullList.from(Ingredient.EMPTY, Ingredient.fromStacks(tome));

			this.chargeToDrain = chargeToDrain;
			this.result = result;

			XRRecipes.drainRecipe = this;
		}

		@Override
		public boolean matches(InventoryCrafting inv, World worldIn) {
			boolean hasTome = false;
			ItemStack tome = ItemStack.EMPTY;
			for(int slot = 0; slot < inv.getSizeInventory(); slot++) {
				ItemStack stack = inv.getStackInSlot(slot);
				if(stack.isEmpty()) {
					continue;
				}
				if(!hasTome && stack.getItem() == ModItems.alkahestryTome) {
					hasTome = true;
					tome = stack;
				} else {
					return false;
				}
			}

			return hasTome && ModItems.alkahestryTome.getCharge(tome) > 0;
		}

		@Override
		public ItemStack getCraftingResult(InventoryCrafting inv) {
			ItemStack tome = getTome(inv).copy();

			int charge = ModItems.alkahestryTome.getCharge(tome);
			ItemStack ret = result.copy();
			ret.setCount(Math.min(ret.getMaxStackSize(), charge / chargeToDrain));

			return ret;
		}

		@Override
		public boolean canFit(int width, int height) {
			return width * height >= 1;
		}

		@Override
		public ItemStack getRecipeOutput() {
			return result;
		}

		private ItemStack getTome(InventoryCrafting inv) {
			for(int slot = 0; slot < inv.getSizeInventory(); slot++) {
				ItemStack stack = inv.getStackInSlot(slot);
				if(stack.getItem() == ModItems.alkahestryTome) {
					return stack;
				}
			}

			return ItemStack.EMPTY;
		}

		@Override
		public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
			NonNullList<ItemStack> ret = IRecipe.super.getRemainingItems(inv);
			for(int slot = 0; slot < inv.getSizeInventory(); slot++) {
				ItemStack stack = inv.getStackInSlot(slot);
				if(stack.getItem() == ModItems.alkahestryTome) {
					ItemStack tome = stack.copy();
					int charge = ModItems.alkahestryTome.getCharge(tome);
					int itemCount = Math.min(result.getMaxStackSize(), charge / chargeToDrain);
					ModItems.alkahestryTome.useCharge(tome, itemCount * chargeToDrain);
					ret.set(slot, tome);
				}
			}

			return ret;
		}

		@Override
		public NonNullList<Ingredient> getIngredients() {
			return input;
		}

		@Override
		public String getGroup() {
			return group.toString();
		}
	}
}
