package xreliquary.crafting;

import com.google.gson.JsonObject;
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

public class AlkahestryDrainRecipe implements ICraftingRecipe {
	public static final Serializer SERIALIZER = new Serializer();
	private final int chargeToDrain;
	private final ItemStack result;
	private final ResourceLocation id;
	private final Ingredient tomeIngredient;

	private AlkahestryDrainRecipe(ResourceLocation id, int chargeToDrain, ItemStack result) {
		this.chargeToDrain = chargeToDrain;
		this.result = result;
		this.id = id;
		tomeIngredient = Ingredient.fromStacks(AlkahestryTomeItem.setCharge(new ItemStack(ModItems.ALKAHESTRY_TOME), Settings.COMMON.items.alkahestryTome.chargeLimit.get()));
		AlkahestryRecipeRegistry.setDrainRecipe(this);
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		boolean hasTome = false;
		ItemStack tome = ItemStack.EMPTY;
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack stack = inv.getStackInSlot(slot);
			if (stack.isEmpty()) {
				continue;
			}
			if (!hasTome && stack.getItem() == ModItems.ALKAHESTRY_TOME) {
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
		return NonNullList.from(Ingredient.EMPTY, tomeIngredient);
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack tome = getTome(inv).copy();

		int charge = AlkahestryTomeItem.getCharge(tome);
		ItemStack ret = result.copy();
		ret.setCount(Math.min(ret.getMaxStackSize(), charge / chargeToDrain));

		return ret;
	}

	private ItemStack getTome(CraftingInventory inv) {
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack stack = inv.getStackInSlot(slot);
			if (stack.getItem() == ModItems.ALKAHESTRY_TOME) {
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 1;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return result;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
		NonNullList<ItemStack> ret = ICraftingRecipe.super.getRemainingItems(inv);
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack stack = inv.getStackInSlot(slot);
			if (stack.getItem() == ModItems.ALKAHESTRY_TOME) {
				ItemStack tome = stack.copy();
				int charge = AlkahestryTomeItem.getCharge(tome);
				int itemCount = Math.min(result.getMaxStackSize(), charge / chargeToDrain);
				ModItems.ALKAHESTRY_TOME.useCharge(tome, itemCount * chargeToDrain);
				ret.set(slot, tome);
			}
		}

		return ret;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AlkahestryDrainRecipe> {
		@Override
		public AlkahestryDrainRecipe read(ResourceLocation recipeId, JsonObject json) {
			if (Boolean.TRUE.equals(Settings.COMMON.disable.disableAlkahestry.get())) {
				//noinspection ConstantConditions - this is the easiest way to disable recipes without having to code special condition and adding to every recipe
				return null;
			}

			int chargeToDrain = JSONUtils.getInt(json, "charge");
			ItemStack result = CraftingHelper.getItemStack(JSONUtils.getJsonObject(json, "result"), true);

			return new AlkahestryDrainRecipe(recipeId, chargeToDrain, result);
		}

		@Nullable
		@Override
		public AlkahestryDrainRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			return new AlkahestryDrainRecipe(recipeId, buffer.readInt(), buffer.readItemStack());
		}

		@Override
		public void write(PacketBuffer buffer, AlkahestryDrainRecipe recipe) {
			buffer.writeInt(recipe.chargeToDrain);
			buffer.writeItemStack(recipe.result);
		}
	}
}
