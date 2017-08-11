package xreliquary.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.init.XRRecipes;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class MobCharmRepairRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	private static final HashMap<Byte, ItemStack> REPAIR_INGREDIENTS;

	public MobCharmRepairRecipe() {
		setRegistryName(Reference.MOD_ID, "mob_charm_repair");
	}

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
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
		ItemStack ingredient = ItemStack.EMPTY;
		int numberIngredients = 0;
		ItemStack mobCharm = ItemStack.EMPTY;

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack currentStack = inv.getStackInSlot(i);
			if(!currentStack.isEmpty()) {
				if(currentStack.getItem() == ModItems.mobCharm) {
					if(!mobCharm.isEmpty() || !REPAIR_INGREDIENTS.keySet().contains(ModItems.mobCharm.getType(currentStack)))
						return false;
					mobCharm = currentStack;
					continue;
				}

				if(!isRepairIngredient(currentStack))
					return false;

				if(ingredient.isEmpty()) {
					ingredient = currentStack;
				} else {
					if(!ingredient.isItemEqual(currentStack))
						return false;
				}
				numberIngredients++;
			}
		}

		return !(mobCharm.isEmpty() || ingredient.isEmpty() || !REPAIR_INGREDIENTS.get(ModItems.mobCharm.getType(mobCharm)).isItemEqual(ingredient)) && mobCharm.getItemDamage() >= (Settings.Items.MobCharm.dropDurabilityRepair * (numberIngredients - 1));

	}

	private boolean isRepairIngredient(ItemStack stack) {
		for(ItemStack repairIngredient : REPAIR_INGREDIENTS.values()) {
			if(repairIngredient.isItemEqual(stack))
				return true;
		}
		return false;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack ingredient = ItemStack.EMPTY;
		int numberIngredients = 0;
		ItemStack mobCharm = ItemStack.EMPTY;

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack currentStack = inv.getStackInSlot(i);
			if(!currentStack.isEmpty()) {
				if(currentStack.getItem() == ModItems.mobCharm) {
					mobCharm = currentStack;
					continue;
				}
				if(ingredient.isEmpty()) {
					ingredient = currentStack;
				}
				numberIngredients++;
			}
		}

		ItemStack resultingMobCharm = mobCharm.copy();

		resultingMobCharm.setItemDamage(Math.max(resultingMobCharm.getItemDamage() - (Settings.Items.MobCharm.dropDurabilityRepair * numberIngredients), 0));

		return resultingMobCharm;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 2 && height >= 2;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
		return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	}

	@Override
	public boolean isHidden() {
		return true;
	}
}
