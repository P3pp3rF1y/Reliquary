package reliquary.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class InfernalTearValueRecipeSerializer implements RecipeSerializer<InfernalTearValueRecipe> {
	@Override
	public MapCodec<InfernalTearValueRecipe> codec() {
		return InfernalTearValueRecipe.MAP_CODEC;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, InfernalTearValueRecipe> streamCodec() {
		return InfernalTearValueRecipe.STREAM_CODEC;
	}
}
