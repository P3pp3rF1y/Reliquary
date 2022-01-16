package xreliquary.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.init.ModItems;
import xreliquary.items.MobCharmFragmentItem;

import java.util.Map;
import java.util.Optional;

public class FragmentRecipeHelper {
	private FragmentRecipeHelper() {}

	public static final Item FALL_BACK_SPAWN_EGG = Items.CHICKEN_SPAWN_EGG;

	public static boolean hasOnlyOneFragmentType(CraftingContainer inv) {
		String regName = null;
		for (int slot = 0; slot < inv.getContainerSize(); slot++) {
			ItemStack slotStack = inv.getItem(slot);
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

	public static Optional<String> getRegistryName(CraftingContainer inv) {
		for (int slot = 0; slot < inv.getContainerSize(); slot++) {
			ItemStack slotStack = inv.getItem(slot);
			if (slotStack.getItem() != ModItems.MOB_CHARM_FRAGMENT.get()) {
				continue;
			}
			return Optional.of(MobCharmFragmentItem.getEntityRegistryName(slotStack));
		}
		return Optional.empty();
	}

	public static ItemStack getSpawnEggStack(String regName) {
		Map<EntityType<?>, SpawnEggItem> spawnEggs = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "BY_ID");

		if (spawnEggs == null) {
			return new ItemStack(FALL_BACK_SPAWN_EGG);
		}

		SpawnEggItem spawnEggItem = spawnEggs.get(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(regName)));
		return new ItemStack(spawnEggItem == null ? FALL_BACK_SPAWN_EGG : spawnEggItem);
	}
}
