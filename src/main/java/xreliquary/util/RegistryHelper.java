package xreliquary.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

public class RegistryHelper {
	public static Item getItemFromName(String registryName) {
		return GameData.getItemRegistry().getObject(new ResourceLocation(registryName));
	}

	public static String getItemRegistryName(Item item) {
		ResourceLocation rl = item.getRegistryName();
		//null check because some mods don't properly register items they use in recipes
		if (rl != null)
			return rl.toString();

		return "";
	}

	public static Block getBlockFromName(String registryName) {
		return GameData.getBlockRegistry().getObject(new ResourceLocation(registryName));
	}

	public static String getBlockRegistryName(Block block) {
		return GameData.getBlockRegistry().getNameForObject(block).toString();
	}

	public static boolean blocksEqual(Block block1, Block block2) {
		return getBlockRegistryName(block1).equals(getBlockRegistryName(block2));
	}

	public static boolean itemsEqual(Item item1, Item item2) {
		return getItemRegistryName(item1).equals(getItemRegistryName(item2));
	}
}
