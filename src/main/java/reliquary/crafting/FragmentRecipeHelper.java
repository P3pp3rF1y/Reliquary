package reliquary.crafting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.CraftingInput;
import reliquary.init.ModItems;
import reliquary.item.MobCharmFragmentItem;

import java.util.Optional;

public class FragmentRecipeHelper {
	private FragmentRecipeHelper() {
	}

	public static final Item FALL_BACK_SPAWN_EGG = Items.CHICKEN_SPAWN_EGG;

	public static boolean hasOnlyOneFragmentType(CraftingInput inv) {
		ResourceLocation regName = null;
		for (int slot = 0; slot < inv.size(); slot++) {
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

	public static Optional<ResourceLocation> getRegistryName(CraftingInput inv) {
		for (int slot = 0; slot < inv.size(); slot++) {
			ItemStack slotStack = inv.getItem(slot);
			if (slotStack.getItem() != ModItems.MOB_CHARM_FRAGMENT.get()) {
				continue;
			}
			return Optional.of(MobCharmFragmentItem.getEntityRegistryName(slotStack));
		}
		return Optional.empty();
	}

	public static ItemStack getSpawnEggStack(ResourceLocation regName) {
		SpawnEggItem spawnEggItem = SpawnEggItem.byId(BuiltInRegistries.ENTITY_TYPE.get(regName));
		return new ItemStack(spawnEggItem == null ? FALL_BACK_SPAWN_EGG : spawnEggItem);
	}
}
