package reliquary.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public class RegistryHelper {
	private RegistryHelper() {
	}

	public static String getItemRegistryName(Item item) {
		return BuiltInRegistries.ITEM.getKey(item).toString();
	}

	public static Identifier getRegistryName(Item item) {
		return BuiltInRegistries.ITEM.getKey(item);
	}

	public static Identifier getRegistryName(Entity entity) {
		return getRegistryName(entity.getType());
	}

	public static Identifier getRegistryName(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block);
	}

	public static Identifier getRegistryName(EntityType<?> entityType) {
		return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
	}

	public static boolean registryNamesEqual(Item itemA, Item itemB) {
		return getRegistryName(itemA).equals(getRegistryName(itemB));
	}

	public static Identifier getRegistryName(MobEffect effect) {
		return Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.getKey(effect));
	}
}
