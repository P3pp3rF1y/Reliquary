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
import xreliquary.init.ModItems;
import xreliquary.items.MobCharmFragmentItem;
import xreliquary.items.MobCharmItem;

import javax.annotation.Nullable;
import java.util.Optional;

public class MobCharmRecipe extends ShapedRecipe {
	public static final Serializer SERIALIZER = new Serializer();
	private final ShapedRecipe compose;

	public MobCharmRecipe(ShapedRecipe compose) {
		super(compose.getId(), compose.getGroup(), compose.getRecipeWidth(), compose.getRecipeHeight(), compose.getIngredients(), compose.getRecipeOutput());
		this.compose = compose;
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		return super.matches(inv, worldIn) && hasOnlyOneFragmentType(inv);
	}

	private boolean hasOnlyOneFragmentType(CraftingInventory inv) {
		String regName = null;
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack slotStack = inv.getStackInSlot(slot);
			if (slotStack.getItem() != ModItems.MOB_CHARM_FRAGMENT) {
				continue;
			}
			if (regName == null) {
				regName = MobCharmFragmentItem.getEntityRegistryName(slotStack);
			} else {
				if (!regName.equals(MobCharmFragmentItem.getEntityRegistryName(slotStack))) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack ret = super.getCraftingResult(inv);
		getRegistryName(inv).ifPresent(regName -> MobCharmItem.setEntityRegistryName(ret, regName));
		return ret;
	}

	private Optional<String> getRegistryName(CraftingInventory inv) {
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack slotStack = inv.getStackInSlot(slot);
			if (slotStack.getItem() != ModItems.MOB_CHARM_FRAGMENT) {
				continue;
			}
			return Optional.of(MobCharmFragmentItem.getEntityRegistryName(slotStack));
		}
		return Optional.empty();
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
