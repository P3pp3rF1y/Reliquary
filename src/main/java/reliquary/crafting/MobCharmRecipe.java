package reliquary.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;
import reliquary.item.MobCharmItem;

import java.util.HashSet;
import java.util.Set;

public class MobCharmRecipe extends ShapedRecipe {
	public static final Set<MobCharmRecipe> REGISTERED_RECIPES = new HashSet<>();

	private final ShapedRecipe compose;

	public MobCharmRecipe(ShapedRecipe compose) {
		super(new Recipe.CommonInfo(false), new CraftingRecipe.CraftingBookInfo(compose.category(), compose.group()), compose.pattern, compose.result);
		this.compose = compose;
		REGISTERED_RECIPES.add(this);
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		return super.matches(inv, level) && FragmentRecipeHelper.hasOnlyOneFragmentType(inv);
	}

	@Override
	public ItemStack assemble(CraftingInput inv) {
		ItemStack ret = super.assemble(inv);
		FragmentRecipeHelper.getRegistryName(inv).ifPresent(regName -> MobCharmItem.setEntityRegistryName(ret, regName));
		return ret;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RecipeSerializer<ShapedRecipe> getSerializer() {
		return (RecipeSerializer<ShapedRecipe>) (RecipeSerializer<?>) ModItems.MOB_CHARM_RECIPE_SERIALIZER.get();
	}

	public static final MapCodec<MobCharmRecipe> MAP_CODEC = ShapedRecipe.SERIALIZER.codec()
			.xmap(MobCharmRecipe::new, recipe -> recipe.compose);
	public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, MobCharmRecipe> STREAM_CODEC = ShapedRecipe.SERIALIZER.streamCodec()
			.map(MobCharmRecipe::new, recipe -> recipe.compose);
}
