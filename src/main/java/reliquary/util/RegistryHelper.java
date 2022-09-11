package reliquary.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class RegistryHelper {
	private RegistryHelper() {}

	public static String getItemRegistryName(Item item) {
		ResourceLocation rl = ForgeRegistries.ITEMS.getKey(item);
		//null check because some mods don't properly register items they use in recipes
		if (rl != null) {
			return rl.toString();
		}

		return "";
	}

	public static ResourceLocation getRegistryName(Item item) {
		return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
	}

	public static ResourceLocation getRegistryName(Entity entity) {
		return getRegistryName(entity.getType());
	}

	public static ResourceLocation getRegistryName(Block block) {
		return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block));
	}

	public static ResourceLocation getRegistryName(EntityType<?> entityType) {
		return Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(entityType));
	}

	public static boolean registryNamesEqual(Item itemA, Item itemB) {
		return getRegistryName(itemA).equals(getRegistryName(itemB));
	}

	public static ResourceLocation getRegistryName(MobEffect effect) {
		return Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getKey(effect));
	}
}
