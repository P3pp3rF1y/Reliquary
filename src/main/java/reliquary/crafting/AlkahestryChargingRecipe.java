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

import java.util.stream.Stream;

public class AlkahestryChargingRecipe implements CraftingRecipe {
	private final Ingredient chargingIngredient;
	private final int chargeToAdd;
	private final ItemStack recipeOutput;
	private final Ingredient tomeIngredient;

	public AlkahestryChargingRecipe(Ingredient chargingIngredient, int chargeToAdd) {
		this.chargingIngredient = chargingIngredient;
		this.chargeToAdd = chargeToAdd;
		tomeIngredient = new TomeIngredient(chargeToAdd).toVanilla();

		recipeOutput = new ItemStack(ModItems.ALKAHESTRY_TOME.get());
		AlkahestryTomeItem.addCharge(recipeOutput, chargeToAdd);

		AlkahestryRecipeRegistry.registerChargingRecipe(this);
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		ItemStack tome = ItemStack.EMPTY;
		int numberOfIngredients = 0;

		for (int x = 0; x < inv.size(); x++) {
			ItemStack slotStack = inv.getItem(x);

			if (!slotStack.isEmpty()) {
				boolean inRecipe = false;
				if (chargingIngredient.test(slotStack)) {
					inRecipe = true;
					numberOfIngredients++;
				} else if (tome.isEmpty()) {
					inRecipe = true;
					tome = slotStack;
				}

				if (!inRecipe) {
					return false;
				}
			}
		}

		return numberOfIngredients > 0 && tome.is(ModItems.ALKAHESTRY_TOME.get()) && AlkahestryTomeItem.getCharge(tome) + chargeToAdd * numberOfIngredients <= AlkahestryTomeItem.getChargeLimit();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
		int numberOfIngredients = 0;
		ItemStack tome = ItemStack.EMPTY;
		for (int slot = 0; slot < inv.size(); slot++) {
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

	public ItemStack getRecipeOutput() {
		return recipeOutput;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return recipeOutput;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModItems.ALKAHESTRY_CHARGING_SERIALIZER.get();
	}

	public int getChargeToAdd() {
		return chargeToAdd;
	}

	public Ingredient getChargingIngredient() {
		return chargingIngredient;
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	public static class Serializer implements RecipeSerializer<AlkahestryChargingRecipe> {
		private static final MapCodec<AlkahestryChargingRecipe> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
								Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.chargingIngredient),
								Codec.INT.fieldOf("charge").forGetter(recipe -> recipe.chargeToAdd)
						)
						.apply(instance, AlkahestryChargingRecipe::new));
		private static final StreamCodec<RegistryFriendlyByteBuf, AlkahestryChargingRecipe> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC,
				AlkahestryChargingRecipe::getChargingIngredient,
				ByteBufCodecs.INT,
				AlkahestryChargingRecipe::getChargeToAdd,
				AlkahestryChargingRecipe::new);

		@Override
		public MapCodec<AlkahestryChargingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, AlkahestryChargingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}

	private static class TomeIngredient implements ICustomIngredient {
		private final int chargeToAdd;

		private final ItemStack tome;

		private TomeIngredient(int chargeToAdd) {
			super();
			this.chargeToAdd = chargeToAdd;
			this.tome = new ItemStack(ModItems.ALKAHESTRY_TOME.get());
		}

		@Override
		public boolean test(ItemStack stack) {
			return stack.is(ModItems.ALKAHESTRY_TOME.get()) && AlkahestryTomeItem.getCharge(stack) + chargeToAdd <= AlkahestryTomeItem.getChargeLimit();
		}

		@Override
		public Stream<ItemStack> getItems() {
			return Stream.of(tome);
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
