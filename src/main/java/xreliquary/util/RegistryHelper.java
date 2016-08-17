package xreliquary.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class RegistryHelper {
	public static Item getItemFromName(String registryName) {
		return Item.getByNameOrId(registryName);
	}

	public static String getItemRegistryName(Item item) {
		ResourceLocation rl = item.getRegistryName();
		//null check because some mods don't properly register items they use in recipes
		if(rl != null)
			return rl.toString();

		return "";
	}

	public static Block getBlockFromName(String registryName) {
		return Block.getBlockFromName(registryName);
	}

	public static String getBlockRegistryName(Block block) {
		if(block.getRegistryName() == null) {
			LogHelper.warn("Block " + block.toString() + " isn't registered properly");
		} else {
			return block.getRegistryName().toString();
		} return null;
	}

	public static boolean blocksEqual(Block block1, Block block2) {
		return getBlockRegistryName(block1).equals(getBlockRegistryName(block2));
	}

	public static boolean itemsEqual(Item item1, Item item2) {
		return getItemRegistryName(item1).equals(getItemRegistryName(item2));
	}
}
