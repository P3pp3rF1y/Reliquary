package reliquary.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import reliquary.init.ModItems;
import reliquary.item.AlkahestryTomeItem;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class AlkahestryCraftingRecipe implements CraftingRecipe {
	private final Ingredient craftingIngredient;
	private final int chargeNeeded;
	private final int resultCount;
	private ItemStack result = ItemStack.EMPTY;
	private final Ingredient tomeIngredient;

	public AlkahestryCraftingRecipe(Ingredient craftingIngredient, int chargeNeeded, int resultCount) {
		this.craftingIngredient = craftingIngredient;
		this.chargeNeeded = chargeNeeded;
		tomeIngredient = new TomeIngredient(chargeNeeded).toVanilla();
		this.resultCount = resultCount;

		AlkahestryRecipeRegistry.registerCraftingRecipe(this);
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		boolean hasIngredient = false;
		boolean hasTome = false;
		for (int x = 0; x < inv.size(); x++) {
			ItemStack slotStack = inv.getItem(x);

			if (!slotStack.isEmpty()) {
				boolean inRecipe = false;
				if (craftingIngredient.test(slotStack)) {
					inRecipe = true;
					hasIngredient = true;
				} else if (!hasTome && tomeIngredient.test(slotStack)) {
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
		return NonNullList.of(Ingredient.EMPTY, craftingIngredient, tomeIngredient);
	}

	@Override
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
		for (int slot = 0; slot < inv.size(); slot++) {
			ItemStack stack = inv.getItem(slot);

			if (!stack.isEmpty() && stack.getItem() != ModItems.ALKAHESTRY_TOME.get()) {
				ItemStack craftingResult = stack.copy();
				craftingResult.setCount(resultCount);
				return craftingResult;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	public ItemStack getResult() {
		if (result.isEmpty()) {
			ItemStack[] ingredientItems = craftingIngredient.getItems();
			if (ingredientItems.length > 0) {
				result = ingredientItems[0].copy();
				result.setCount(resultCount);
			}
		}

		return result;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return getResult();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModItems.ALKAHESTRY_CRAFTING_SERIALIZER.get();
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
		NonNullList<ItemStack> remainingItems = CraftingRecipe.super.getRemainingItems(inv);

		addTomeWithUsedCharge(remainingItems, inv);

		return remainingItems;
	}

	private void addTomeWithUsedCharge(NonNullList<ItemStack> remainingItems, CraftingInput inv) {
		for (int slot = 0; slot < remainingItems.size(); slot++) {
			ItemStack stack = inv.getItem(slot);

			if (stack.getItem() == ModItems.ALKAHESTRY_TOME.get()) {
				ItemStack tome = stack.copy();
				ModItems.ALKAHESTRY_TOME.get().useCharge(tome, chargeNeeded);
				remainingItems.set(slot, tome);

				break;
			}
		}
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	public int getChargeNeeded() {
		return chargeNeeded;
	}

	public Ingredient getCraftingIngredient() {
		return craftingIngredient;
	}

	public int getResultCount() {
		return resultCount;
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	public static class Serializer implements RecipeSerializer<AlkahestryCraftingRecipe> {

		private static final MapCodec<AlkahestryCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
								Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.craftingIngredient),
								Codec.INT.fieldOf("charge").forGetter(recipe -> recipe.chargeNeeded),
								Codec.INT.fieldOf("result_count").forGetter(recipe -> recipe.resultCount)
						)
						.apply(instance, AlkahestryCraftingRecipe::new));
		private static final StreamCodec<RegistryFriendlyByteBuf, AlkahestryCraftingRecipe> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC,
				AlkahestryCraftingRecipe::getCraftingIngredient,
				ByteBufCodecs.INT,
				AlkahestryCraftingRecipe::getChargeNeeded,
				ByteBufCodecs.INT,
				AlkahestryCraftingRecipe::getResultCount,
				AlkahestryCraftingRecipe::new
		);

		@Override
		public MapCodec<AlkahestryCraftingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, AlkahestryCraftingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}

	private static class TomeIngredient implements ICustomIngredient {
		private final int chargeNeeded;
		private final ItemStack tomeStack;

		private TomeIngredient(int chargeNeeded) {
			this.tomeStack = AlkahestryTomeItem.setCharge(new ItemStack(ModItems.ALKAHESTRY_TOME.get()), chargeNeeded);
			this.chargeNeeded = chargeNeeded;
		}

		@Override
		public boolean test(@Nullable ItemStack stack) {
			return stack != null && stack.is(ModItems.ALKAHESTRY_TOME.get()) && AlkahestryTomeItem.getCharge(stack) >= chargeNeeded;
		}

		@Override
		public Stream<ItemStack> getItems() {
			return Stream.of(tomeStack);
		}

		@Override
		public boolean isSimple() {
			return false;
		}

		@Override
		public IngredientType<?> getType() {
			//noinspection DataFlowIssue - the ingredient only exists to be returned in the list of ingredients, it is never serialized / deserialized
			return null;
		}
	}
}
