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
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;

public class InfernalTearValueRecipe implements net.minecraft.world.item.crafting.CraftingRecipe {
	private final Ingredient ingredient;
	private final int experiencePoints;

	public InfernalTearValueRecipe(Ingredient ingredient, int experiencePoints) {
		this.ingredient = ingredient;
		this.experiencePoints = experiencePoints;
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		return false;
	}

	@Override
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY, ingredient);
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModItems.INFERNAL_TEAR_VALUE_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModItems.INFERNAL_TEAR_VALUE_TYPE.get();
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public int getExperiencePoints() {
		return experiencePoints;
	}

	public static class Serializer implements RecipeSerializer<InfernalTearValueRecipe> {
		private static final MapCodec<InfernalTearValueRecipe> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(InfernalTearValueRecipe::getIngredient),
						Codec.INT.fieldOf("xp").forGetter(InfernalTearValueRecipe::getExperiencePoints)
				).apply(instance, InfernalTearValueRecipe::new));
		private static final StreamCodec<RegistryFriendlyByteBuf, InfernalTearValueRecipe> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC,
				InfernalTearValueRecipe::getIngredient,
				ByteBufCodecs.INT,
				InfernalTearValueRecipe::getExperiencePoints,
				InfernalTearValueRecipe::new);

		@Override
		public MapCodec<InfernalTearValueRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, InfernalTearValueRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
