package reliquary.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import reliquary.items.MobCharmItem;

import javax.annotation.Nullable;

public class MobCharmRecipe extends ShapedRecipe {
	public static final Serializer SERIALIZER = new Serializer();
	private final ShapedRecipe compose;

	public MobCharmRecipe(ShapedRecipe compose) {
		super(compose.getId(), compose.getGroup(), compose.getRecipeWidth(), compose.getRecipeHeight(), compose.getIngredients(), compose.getResultItem());
		this.compose = compose;
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		return super.matches(inv, worldIn) && FragmentRecipeHelper.hasOnlyOneFragmentType(inv);
	}

	@Override
	public ItemStack assemble(CraftingContainer inv) {
		ItemStack ret = super.assemble(inv);
		FragmentRecipeHelper.getRegistryName(inv).ifPresent(regName -> MobCharmItem.setEntityRegistryName(ret, regName));
		return ret;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MobCharmRecipe> {
		@Override
		public MobCharmRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			return new MobCharmRecipe(RecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json));
		}

		@Nullable
		@Override
		public MobCharmRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			//noinspection ConstantConditions - shaped recipe serializer always returns an instance of recipe despite RecipeSerializer's null allowing contract
			return new MobCharmRecipe(RecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer));
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, MobCharmRecipe recipe) {
			RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe.compose);
		}
	}
}
