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
import xreliquary.items.MobCharmDefinition;
import xreliquary.items.MobCharmRegistry;
import xreliquary.reference.Settings;

import java.util.Optional;

public class MobCharmRepairRecipe extends SpecialRecipe {
	public static final IRecipeSerializer<MobCharmRepairRecipe> SERIALIZER = new SpecialRecipeSerializer<>(MobCharmRepairRecipe::new);
	private static final int PER_FRAGMENT_MULTIPLIER = 6;

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

		if (mobCharm.isEmpty()) {
			return false;
		}

		ItemStack finalIngredient = ingredient;
		Optional<MobCharmDefinition> cd = MobCharmRegistry.getCharmDefinitionFor(mobCharm);
		if (!cd.isPresent()) {
			return false;
		}
		MobCharmDefinition charmDefinition = cd.get();

		int repairMultiplier = charmDefinition.isDynamicallyCreated() ? PER_FRAGMENT_MULTIPLIER : 1;
		int durabilityRepaired = Settings.COMMON.items.mobCharm.dropDurabilityRepair.get() * repairMultiplier;
		return mobCharm.getDamage() >= durabilityRepaired * (numberIngredients - 1) && charmDefinition.isRepairItem(finalIngredient);
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
