package xreliquary.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import xreliquary.items.MobCharmItem;

import javax.annotation.Nullable;

public class MobCharmRecipe extends ShapedRecipe {
	public static final Serializer SERIALIZER = new Serializer();
	private final ShapedRecipe compose;

	public MobCharmRecipe(ShapedRecipe compose) {
		super(compose.getId(), compose.getGroup(), compose.getRecipeWidth(), compose.getRecipeHeight(), compose.getIngredients(), compose.getRecipeOutput());
		this.compose = compose;
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		return super.matches(inv, worldIn) && FragmentRecipeHelper.hasOnlyOneFragmentType(inv);
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack ret = super.getCraftingResult(inv);
		FragmentRecipeHelper.getRegistryName(inv).ifPresent(regName -> MobCharmItem.setEntityRegistryName(ret, regName));
		return ret;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MobCharmRecipe> {
		@Override
		public MobCharmRecipe read(ResourceLocation recipeId, JsonObject json) {
			return new MobCharmRecipe(IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, json));
		}

		@Nullable
		@Override
		public MobCharmRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			//noinspection ConstantConditions - shaped recipe serializer always returns an instance of recipe despite IRecipeSerializer's null allowing contract
			return new MobCharmRecipe(IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, buffer));
		}

		@Override
		public void write(PacketBuffer buffer, MobCharmRecipe recipe) {
			IRecipeSerializer.CRAFTING_SHAPED.write(buffer, recipe.compose);
		}
	}
}
