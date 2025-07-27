package reliquary.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;
import reliquary.item.MobCharmItem;

import java.util.HashSet;
import java.util.Set;

public class MobCharmRecipe extends ShapedRecipe {
	public static final Set<MobCharmRecipe> REGISTERED_RECIPES = new HashSet<>();

	private final ShapedRecipe compose;

	public MobCharmRecipe(ShapedRecipe compose) {
		super(compose.getGroup(), CraftingBookCategory.MISC, compose.pattern, compose.result);
		this.compose = compose;
		REGISTERED_RECIPES.add(this);
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		return super.matches(inv, level) && FragmentRecipeHelper.hasOnlyOneFragmentType(inv);
	}

	@Override
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
		ItemStack ret = super.assemble(inv, registries);
		FragmentRecipeHelper.getRegistryName(inv).ifPresent(regName -> MobCharmItem.setEntityRegistryName(ret, regName));
		return ret;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModItems.MOB_CHARM_RECIPE_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<MobCharmRecipe> {
		private static final MapCodec<MobCharmRecipe> CODEC = RecipeSerializer.SHAPED_RECIPE.codec()
				.xmap(MobCharmRecipe::new, recipe -> recipe.compose);
		private static final StreamCodec<RegistryFriendlyByteBuf, MobCharmRecipe> STREAM_CODEC = RecipeSerializer.SHAPED_RECIPE.streamCodec()
				.map(MobCharmRecipe::new, recipe -> recipe.compose);

		@Override
		public MapCodec<MobCharmRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, MobCharmRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
