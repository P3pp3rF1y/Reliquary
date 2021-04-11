package xreliquary.crafting;

import net.minecraft.entity.EntityType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.init.ModItems;
import xreliquary.items.MobCharmFragmentItem;

import java.util.Map;
import java.util.Optional;

public class FragmentRecipeHelper {
	private FragmentRecipeHelper() {}

	public static final Item FALL_BACK_SPAWN_EGG = Items.CHICKEN_SPAWN_EGG;

	public static boolean hasOnlyOneFragmentType(CraftingInventory inv) {
		String regName = null;
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack slotStack = inv.getStackInSlot(slot);
			if (slotStack.getItem() != ModItems.MOB_CHARM_FRAGMENT.get()) {
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

	public static Optional<String> getRegistryName(CraftingInventory inv) {
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack slotStack = inv.getStackInSlot(slot);
			if (slotStack.getItem() != ModItems.MOB_CHARM_FRAGMENT.get()) {
				continue;
			}
			return Optional.of(MobCharmFragmentItem.getEntityRegistryName(slotStack));
		}
		return Optional.empty();
	}

	public static ItemStack getSpawnEggStack(String regName) {
		Map<EntityType<?>, SpawnEggItem> spawnEggs = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "field_195987_b");

		if (spawnEggs == null) {
			return new ItemStack(FALL_BACK_SPAWN_EGG);
		}

		SpawnEggItem spawnEggItem = spawnEggs.get(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(regName)));
		return new ItemStack(spawnEggItem == null ? FALL_BACK_SPAWN_EGG : spawnEggItem);
	}
}
