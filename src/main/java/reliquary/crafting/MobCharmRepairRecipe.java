package reliquary.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;
import reliquary.item.MobCharmDefinition;
import reliquary.item.MobCharmRegistry;
import reliquary.reference.Config;

import java.util.Optional;

public class MobCharmRepairRecipe extends CustomRecipe {
	private static final int PER_FRAGMENT_MULTIPLIER = 6;

	public MobCharmRepairRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput inv, Level level) {
		ItemStack ingredient = ItemStack.EMPTY;
		int numberIngredients = 0;
		ItemStack mobCharm = ItemStack.EMPTY;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack currentStack = inv.getItem(i);
			if (!currentStack.isEmpty()) {
				if (currentStack.getItem() == ModItems.MOB_CHARM.get()) {
					if (!mobCharm.isEmpty()) {
						return false;
					}
					mobCharm = currentStack;
					continue;
				}

				if (ingredient.isEmpty()) {
					ingredient = currentStack;
				} else {
					if (ingredient.getItem() != currentStack.getItem()) {
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
		if (cd.isEmpty()) {
			return false;
		}
		MobCharmDefinition charmDefinition = cd.get();

		int repairMultiplier = charmDefinition.isDynamicallyCreated() ? PER_FRAGMENT_MULTIPLIER : 1;
		int durabilityRepaired = Config.COMMON.items.mobCharm.dropDurabilityRepair.get() * repairMultiplier;
		return mobCharm.getDamageValue() >= durabilityRepaired * (numberIngredients - 1) && charmDefinition.isRepairItem(finalIngredient);
	}

	@Override
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
		ItemStack ingredient = ItemStack.EMPTY;
		int numberIngredients = 0;
		ItemStack mobCharm = ItemStack.EMPTY;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack currentStack = inv.getItem(i);
			if (!currentStack.isEmpty()) {
				if (currentStack.getItem() == ModItems.MOB_CHARM.get()) {
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

		resultingMobCharm.setDamageValue(Math.max(resultingMobCharm.getDamageValue() - (Config.COMMON.items.mobCharm.dropDurabilityRepair.get() * numberIngredients), 0));

		return resultingMobCharm;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width >= 2 && height >= 2;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
		return NonNullList.withSize(inv.size(), ItemStack.EMPTY);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModItems.MOB_CHARM_REPAIR_SERIALIZER.get();
	}
}
