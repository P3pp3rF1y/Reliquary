package reliquary.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;
import reliquary.items.MobCharmDefinition;
import reliquary.items.MobCharmRegistry;
import reliquary.reference.Settings;

import java.util.Optional;

public class MobCharmRepairRecipe extends CustomRecipe {
	private static final int PER_FRAGMENT_MULTIPLIER = 6;

	public MobCharmRepairRecipe(ResourceLocation registryName) {
		super(registryName);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		ItemStack ingredient = ItemStack.EMPTY;
		int numberIngredients = 0;
		ItemStack mobCharm = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
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
					if (!ingredient.sameItem(currentStack)) {
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
		int durabilityRepaired = Settings.COMMON.items.mobCharm.dropDurabilityRepair.get() * repairMultiplier;
		return mobCharm.getDamageValue() >= durabilityRepaired * (numberIngredients - 1) && charmDefinition.isRepairItem(finalIngredient);
	}

	@Override
	public ItemStack assemble(CraftingContainer inv) {
		ItemStack ingredient = ItemStack.EMPTY;
		int numberIngredients = 0;
		ItemStack mobCharm = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
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

		resultingMobCharm.setDamageValue(Math.max(resultingMobCharm.getDamageValue() - (Settings.COMMON.items.mobCharm.dropDurabilityRepair.get() * numberIngredients), 0));

		return resultingMobCharm;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width >= 2 && height >= 2;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		return NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModItems.MOB_CHARM_REPAIR_SERIALIZER.get();
	}
}
