package xreliquary.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.init.XRRecipes;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import javax.annotation.Nullable;
import java.util.HashMap;

public class MobCharmRepairRecipe implements IRecipe {
	private static final HashMap<Byte, ItemStack> REPAIR_INGREDIENTS;

	static {
		REPAIR_INGREDIENTS = new HashMap<>();
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.BLAZE_META, XRRecipes.MOLTEN_CORE);
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.CAVE_SPIDER_META, XRRecipes.CHELICERAE);
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.CREEPER_META, XRRecipes.CREEPER_GLAND);
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.ENDERMAN_META, XRRecipes.NEBULOUS_HEART);
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.GHAST_META, new ItemStack(Items.GHAST_TEAR));
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.MAGMA_CUBE_META, XRRecipes.MOLTEN_CORE);
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.SKELETON_META, XRRecipes.RIB_BONE);
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.SLIME_META, XRRecipes.SLIME_PEARL);
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.SPIDER_META, XRRecipes.CHELICERAE);
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.WITCH_META, new ItemStack(ModItems.witchHat));
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.WITHER_SKELETON_META, XRRecipes.WITHER_RIB);
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.ZOMBIE_META, XRRecipes.ZOMBIE_HEART);
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.ZOMBIE_PIGMAN_META, XRRecipes.ZOMBIE_HEART);
		REPAIR_INGREDIENTS.put(Reference.MOB_CHARM.GUARDIAN_META, XRRecipes.GUARDIAN_SPIKE);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		ItemStack ingredient = null;
		int numberIngredients = 0;
		ItemStack mobCharm = null;

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack currentStack = inv.getStackInSlot(i);
			if(currentStack != null) {
				if(currentStack.getItem() == ModItems.mobCharm) {
					if(mobCharm != null || !REPAIR_INGREDIENTS.keySet().contains(ModItems.mobCharm.getType(currentStack)))
						return false;
					mobCharm = currentStack;
					continue;
				}

				if(!isRepairIngredient(currentStack))
					return false;

				if(ingredient == null) {
					ingredient = currentStack;
				} else {
					if(!ingredient.isItemEqual(currentStack))
						return false;
				}
				numberIngredients++;
			}
		}

		if (mobCharm == null || ingredient == null || !REPAIR_INGREDIENTS.get(ModItems.mobCharm.getType(mobCharm)).isItemEqual(ingredient))
			return false;

		return mobCharm.getItemDamage() >= (Settings.MobCharm.dropDurabilityRepair * (numberIngredients - 1));

	}

	private boolean isRepairIngredient(ItemStack stack) {
		for(ItemStack repairIngredient : REPAIR_INGREDIENTS.values()) {
			if(repairIngredient.isItemEqual(stack))
				return true;
		}
		return false;
	}

	@Nullable
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack ingredient = null;
		int numberIngredients = 0;
		ItemStack mobCharm = null;

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack currentStack = inv.getStackInSlot(i);
			if(currentStack != null) {
				if(currentStack.getItem() == ModItems.mobCharm) {
					mobCharm = currentStack;
					continue;
				}
				if(ingredient == null) {
					ingredient = currentStack;
				}
				numberIngredients++;
			}
		}

		ItemStack resultingMobCharm = mobCharm.copy();

		resultingMobCharm.setItemDamage(Math.max(resultingMobCharm.getItemDamage() - (Settings.MobCharm.dropDurabilityRepair * numberIngredients), 0));

		return resultingMobCharm;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Nullable
	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return new ItemStack[inv.getSizeInventory()];
	}
}
