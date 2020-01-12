package xreliquary.crafting;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.items.StandardMobCharmRegistry;
import xreliquary.reference.Settings;

public class MobCharmRepairRecipe extends SpecialRecipe {
	public static final IRecipeSerializer<MobCharmRepairRecipe> SERIALIZER = new SpecialRecipeSerializer<>(MobCharmRepairRecipe::new);
	public MobCharmRepairRecipe(ResourceLocation registryName) {
		super(registryName);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		ItemStack ingredient = ItemStack.EMPTY;
		int numberIngredients = 0;
		ItemStack mobCharm = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack currentStack = inv.getStackInSlot(i);
			if (!currentStack.isEmpty()) {
				if (currentStack.getItem() == ModItems.MOB_CHARM) {
					if (!mobCharm.isEmpty()) {
						return false;
					}
					mobCharm = currentStack;
					continue;
				}

				if (ingredient.isEmpty()) {
					ingredient = currentStack;
				} else {
					if (!ingredient.isItemEqual(currentStack)) {
						return false;
					}
				}
				numberIngredients++;
			}
		}

		ItemStack finalIngredient = ingredient;
		return !mobCharm.isEmpty() && mobCharm.getDamage() >= Settings.COMMON.items.mobCharm.dropDurabilityRepair.get() * (numberIngredients - 1)
				&& StandardMobCharmRegistry.getCharmDefinitionFor(mobCharm).map(def -> def.isRepairItem(finalIngredient)).orElse(false);
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack ingredient = ItemStack.EMPTY;
		int numberIngredients = 0;
		ItemStack mobCharm = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack currentStack = inv.getStackInSlot(i);
			if (!currentStack.isEmpty()) {
				if (currentStack.getItem() == ModItems.MOB_CHARM) {
					mobCharm = currentStack;
					continue;
				}
				if (ingredient.isEmpty()) {
					ingredient = currentStack;
				}
				numberIngredients++;
			}
		}

		ItemStack resultingMobCharm = mobCharm.copy();

		resultingMobCharm.setDamage(Math.max(resultingMobCharm.getDamage() - (Settings.COMMON.items.mobCharm.dropDurabilityRepair.get() * numberIngredients), 0));

		return resultingMobCharm;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 2 && height >= 2;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
		return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
