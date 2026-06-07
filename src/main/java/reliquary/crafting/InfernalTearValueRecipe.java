package reliquary.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;

import java.util.List;

public class InfernalTearValueRecipe implements Recipe<RecipeInput> {
	private final Ingredient ingredient;
	private final int experiencePoints;

	public InfernalTearValueRecipe(Ingredient ingredient, int experiencePoints) {
		this.ingredient = ingredient;
		this.experiencePoints = experiencePoints;
	}

	@Override
	public boolean matches(RecipeInput input, Level level) {
		return false;
	}

	@Override
	public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public String group() {
		return "";
	}

	@Override
	public RecipeSerializer<InfernalTearValueRecipe> getSerializer() {
		return ModItems.INFERNAL_TEAR_VALUE_SERIALIZER.get();
	}

	@Override
	public RecipeType<InfernalTearValueRecipe> getType() {
		return ModItems.INFERNAL_TEAR_VALUE_TYPE.get();
	}

	@Override
	public PlacementInfo placementInfo() {
		return PlacementInfo.create(List.of(ingredient));
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return net.minecraft.world.item.crafting.RecipeBookCategories.CRAFTING_MISC;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public int getExperiencePoints() {
		return experiencePoints;
	}

	public static class Serializer implements RecipeSerializer<InfernalTearValueRecipe> {
		private static final MapCodec<InfernalTearValueRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
						Ingredient.CODEC.fieldOf("ingredient").forGetter(InfernalTearValueRecipe::getIngredient),
						Codec.INT.fieldOf("xp").forGetter(InfernalTearValueRecipe::getExperiencePoints)
				)
				.apply(instance, InfernalTearValueRecipe::new));
		private static final StreamCodec<RegistryFriendlyByteBuf, InfernalTearValueRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.fromCodecWithRegistries(Ingredient.CODEC),
				InfernalTearValueRecipe::getIngredient,
				ByteBufCodecs.INT,
				InfernalTearValueRecipe::getExperiencePoints,
				InfernalTearValueRecipe::new
		);

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
