package reliquary.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import reliquary.init.ModDataComponents;
import reliquary.init.ModItems;
import reliquary.item.AlkahestryTomeItem;

import java.util.List;
import java.util.stream.Stream;

public class AlkahestryChargingRecipe implements CraftingRecipe {
	private final Ingredient chargingIngredient;
	private final int chargeToAdd;
	private final Ingredient tomeIngredient;
	private final ItemStackTemplate recipeOutputTemplate;

	public AlkahestryChargingRecipe(Ingredient chargingIngredient, int chargeToAdd) {
		this.chargingIngredient = chargingIngredient;
		this.chargeToAdd = chargeToAdd;
		tomeIngredient = new TomeIngredient(0).toVanilla();
		recipeOutputTemplate = new ItemStackTemplate(ModItems.ALKAHESTRY_TOME, DataComponentPatch.builder().set(ModDataComponents.CHARGE.get(), chargeToAdd).build());

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
	public boolean showNotification() {
		return false;
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
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

	public ItemStack getRecipeOutput() {
		return recipeOutputTemplate.create();
	}

	@Override
	public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
		return ModItems.ALKAHESTRY_CHARGING_SERIALIZER.get();
	}

	@Override
	public PlacementInfo placementInfo() {
		return PlacementInfo.create(List.of(chargingIngredient, tomeIngredient));
	}

	public int getChargeToAdd() {
		return chargeToAdd;
	}

	public Ingredient getChargingIngredient() {
		return chargingIngredient;
	}

	public Ingredient getTomeIngredient() {
		return tomeIngredient;
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	@Override
	public String group() {
		return "";
	}

	public static final MapCodec<AlkahestryChargingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
							Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.chargingIngredient),
							Codec.INT.fieldOf("charge").forGetter(recipe -> recipe.chargeToAdd)
					)
					.apply(instance, AlkahestryChargingRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AlkahestryChargingRecipe> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC,
				AlkahestryChargingRecipe::getChargingIngredient,
				ByteBufCodecs.INT,
				AlkahestryChargingRecipe::getChargeToAdd,
				AlkahestryChargingRecipe::new);

	private static class TomeIngredient implements ICustomIngredient {
		private final int chargeToAdd;

		private TomeIngredient(int chargeToAdd) {
			super();
			this.chargeToAdd = chargeToAdd;
		}

		@Override
		public boolean test(ItemStack stack) {
			return stack.is(ModItems.ALKAHESTRY_TOME.get()) && AlkahestryTomeItem.getCharge(stack) + chargeToAdd <= AlkahestryTomeItem.getChargeLimit();
		}

		@Override
		public Stream<Holder<Item>> items() {
			return Stream.of(ModItems.ALKAHESTRY_TOME);
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
