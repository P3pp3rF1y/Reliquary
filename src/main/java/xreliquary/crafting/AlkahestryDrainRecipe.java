package xreliquary.crafting;

import com.google.gson.JsonObject;
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
import xreliquary.reference.Settings;

import javax.annotation.Nullable;

public class AlkahestryDrainRecipe implements CraftingRecipe {
	public static final Serializer SERIALIZER = new Serializer();
	private final int chargeToDrain;
	private final ItemStack result;
	private final ResourceLocation id;
	private final Ingredient tomeIngredient;

	private AlkahestryDrainRecipe(ResourceLocation id, int chargeToDrain, ItemStack result) {
		this.chargeToDrain = chargeToDrain;
		this.result = result;
		this.id = id;
		tomeIngredient = Ingredient.of(AlkahestryTomeItem.setCharge(new ItemStack(ModItems.ALKAHESTRY_TOME.get()), Settings.COMMON.items.alkahestryTome.chargeLimit.get()));
		AlkahestryRecipeRegistry.setDrainRecipe(this);
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		boolean hasTome = false;
		ItemStack tome = ItemStack.EMPTY;
		for (int slot = 0; slot < inv.getContainerSize(); slot++) {
			ItemStack stack = inv.getItem(slot);
			if (stack.isEmpty()) {
				continue;
			}
			if (!hasTome && stack.getItem() == ModItems.ALKAHESTRY_TOME.get()) {
				hasTome = true;
				tome = stack;
			} else {
				return false;
			}
		}

		return hasTome && AlkahestryTomeItem.getCharge(tome) > 0;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, tomeIngredient);
	}

	@Override
	public ItemStack assemble(CraftingContainer inv) {
		ItemStack tome = getTome(inv).copy();

		int charge = AlkahestryTomeItem.getCharge(tome);
		ItemStack ret = result.copy();
		ret.setCount(Math.min(ret.getMaxStackSize(), charge / chargeToDrain));

		return ret;
	}

	private ItemStack getTome(CraftingContainer inv) {
		for (int slot = 0; slot < inv.getContainerSize(); slot++) {
			ItemStack stack = inv.getItem(slot);
			if (stack.getItem() == ModItems.ALKAHESTRY_TOME.get()) {
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 1;
	}

	@Override
	public ItemStack getResultItem() {
		return result;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		NonNullList<ItemStack> ret = CraftingRecipe.super.getRemainingItems(inv);
		for (int slot = 0; slot < inv.getContainerSize(); slot++) {
			ItemStack stack = inv.getItem(slot);
			if (stack.getItem() == ModItems.ALKAHESTRY_TOME.get()) {
				ItemStack tome = stack.copy();
				int charge = AlkahestryTomeItem.getCharge(tome);
				int itemCount = Math.min(result.getMaxStackSize(), charge / chargeToDrain);
				ModItems.ALKAHESTRY_TOME.get().useCharge(tome, itemCount * chargeToDrain);
				ret.set(slot, tome);
			}
		}

		return ret;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<AlkahestryDrainRecipe> {
		@Override
		public AlkahestryDrainRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			int chargeToDrain = GsonHelper.getAsInt(json, "charge");
			ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);

			return new AlkahestryDrainRecipe(recipeId, chargeToDrain, result);
		}

		@Nullable
		@Override
		public AlkahestryDrainRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			return new AlkahestryDrainRecipe(recipeId, buffer.readInt(), buffer.readItem());
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AlkahestryDrainRecipe recipe) {
			buffer.writeInt(recipe.chargeToDrain);
			buffer.writeItem(recipe.result);
		}
	}
}
