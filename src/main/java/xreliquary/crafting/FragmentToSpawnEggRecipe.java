package xreliquary.crafting;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class FragmentToSpawnEggRecipe extends ShapelessRecipe {
	public static final Serializer SERIALIZER = new Serializer();
	private final ShapelessRecipe recipeDelegate;

	public FragmentToSpawnEggRecipe(ShapelessRecipe recipeDelegate) {
		super(recipeDelegate.getId(), recipeDelegate.getGroup(), recipeDelegate.getRecipeOutput(), recipeDelegate.getIngredients());
		this.recipeDelegate = recipeDelegate;
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		return super.matches(inv, worldIn) && FragmentRecipeHelper.hasOnlyOneFragmentType(inv);
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		return FragmentRecipeHelper.getRegistryName(inv).map(FragmentRecipeHelper::getSpawnEggStack)
				.orElse(new ItemStack(FragmentRecipeHelper.FALL_BACK_SPAWN_EGG));
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FragmentToSpawnEggRecipe> {

		@Override
		public FragmentToSpawnEggRecipe read(ResourceLocation recipeId, JsonObject json) {
			return new FragmentToSpawnEggRecipe(IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, json));
		}

		@Nullable
		@Override
		public FragmentToSpawnEggRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			//noinspection ConstantConditions - shapeless crafting recipe serializer always returns an instance here so no need to check for null
			return new FragmentToSpawnEggRecipe(IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, buffer));
		}

		@Override
		public void write(PacketBuffer buffer, FragmentToSpawnEggRecipe recipe) {
			IRecipeSerializer.CRAFTING_SHAPELESS.write(buffer, recipe.recipeDelegate);
		}
	}
}
