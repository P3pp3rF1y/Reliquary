package reliquary.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;

public class FragmentToSpawnEggRecipe extends CustomShapelessRecipe {
	private final ShapelessRecipe recipeDelegate;

	public FragmentToSpawnEggRecipe(ShapelessRecipe recipeDelegate) {
		super(recipeDelegate.group(), CraftingBookCategory.MISC, recipeDelegate.result, recipeDelegate.placementInfo().ingredients());
		this.recipeDelegate = recipeDelegate;
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		return super.matches(inv, level) && FragmentRecipeHelper.hasOnlyOneFragmentType(inv);
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		return FragmentRecipeHelper.getRegistryName(inv).map(FragmentRecipeHelper::getSpawnEggStack)
				.orElse(new ItemStack(FragmentRecipeHelper.FALL_BACK_SPAWN_EGG));
	}

	@Override
	public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
		return ModItems.FRAGMENT_TO_SPAWN_EGG_SERIALIZER.get();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	public static final MapCodec<FragmentToSpawnEggRecipe> MAP_CODEC = ShapelessRecipe.SERIALIZER.codec()
			.xmap(FragmentToSpawnEggRecipe::new, recipe -> recipe.recipeDelegate);
	public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, FragmentToSpawnEggRecipe> STREAM_CODEC = ShapelessRecipe.SERIALIZER.streamCodec()
			.map(FragmentToSpawnEggRecipe::new, recipe -> recipe.recipeDelegate);
}
