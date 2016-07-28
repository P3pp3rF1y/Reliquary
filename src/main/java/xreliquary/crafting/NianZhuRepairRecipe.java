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

public class NianZhuRepairRecipe implements IRecipe {
	private static final HashMap<Byte, ItemStack> REPAIR_INGREDIENTS;

	static {
		REPAIR_INGREDIENTS = new HashMap<>();
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.BLAZE_META, XRRecipes.MOLTEN_CORE);
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.CAVE_SPIDER_META, XRRecipes.CHELICERAE);
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.CREEPER_META, XRRecipes.CREEPER_GLAND);
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.ENDERMAN_META, XRRecipes.NEBULOUS_HEART);
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.GHAST_META, new ItemStack(Items.GHAST_TEAR));
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.MAGMA_CUBE_META, XRRecipes.MOLTEN_CORE);
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.SKELETON_META, XRRecipes.RIB_BONE);
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.SLIME_META, XRRecipes.SLIME_PEARL);
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.SPIDER_META, XRRecipes.CHELICERAE);
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.WITCH_META, new ItemStack(ModItems.witchHat));
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.WITHER_SKELETON_META, XRRecipes.WITHER_RIB);
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.ZOMBIE_META, XRRecipes.ZOMBIE_HEART);
		REPAIR_INGREDIENTS.put(Reference.NIAN_ZHU.ZOMBIE_PIGMAN_META, XRRecipes.ZOMBIE_HEART);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		ItemStack ingredient = null;
		int numberIngredients = 0;
		ItemStack nianZhu = null;

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack currentStack = inv.getStackInSlot(i);
			if(currentStack != null) {
				if(currentStack.getItem() == ModItems.nianZhu) {
					if(nianZhu != null || !REPAIR_INGREDIENTS.keySet().contains(ModItems.nianZhu.getType(currentStack)))
						return false;
					nianZhu = currentStack;
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

		if (nianZhu == null || ingredient == null || !REPAIR_INGREDIENTS.get(ModItems.nianZhu.getType(nianZhu)).isItemEqual(ingredient))
			return false;

		if (nianZhu.getItemDamage() < (Settings.NianZhu.dropDurabilityRepair * (numberIngredients - 1)))
			return false;

		return true;
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
		ItemStack nianZhu = null;

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack currentStack = inv.getStackInSlot(i);
			if(currentStack != null) {
				if(currentStack.getItem() == ModItems.nianZhu) {
					nianZhu = currentStack;
					continue;
				}
				if(ingredient == null) {
					ingredient = currentStack;
				}
				numberIngredients++;
			}
		}

		ItemStack resultingNianZhu = nianZhu.copy();

		resultingNianZhu.setItemDamage(Math.max(resultingNianZhu.getItemDamage() - (Settings.NianZhu.dropDurabilityRepair * numberIngredients), 0));

		return resultingNianZhu;
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
