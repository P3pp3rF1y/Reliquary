package xreliquary.util;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

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
		if(block.getRegistryName() != null) {
			return block.getRegistryName().toString();
		}
		return null;
	}

	public static List<ItemStack> getItemStacksFromNames(List<String> torches) {
		List<ItemStack> torchStacks = Lists.newArrayList();
		for(String torch : torches) {
			Item item = Item.getByNameOrId(torch);
			if (item != null)
			torchStacks.add(new ItemStack(item));
		}
		return  torchStacks;
	}
}
