package xreliquary.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StackHelper {
	public static ItemStack getItemStackFromNameMeta(String registryName) {
		return getItemStackFromNameMeta(registryName, 0);
	}

	private static ItemStack getItemStackFromNameMeta(String registryName, int meta) {
		return new ItemStack(RegistryHelper.getItemFromName(registryName), 1, meta);
	}

	public static ItemStack getItemStackFromNameMeta(String modId, String name, int meta) {
		ItemStack stack;
		Item item = Item.REGISTRY.getObject(new ResourceLocation(modId, name));

		if(item != null) {
			stack = new ItemStack(item, 1, meta);
		} else {
			Block block = Block.REGISTRY.getObject(new ResourceLocation(modId, name));
			stack = new ItemStack(block, 1, meta);
		}
		return stack;
	}

	public static boolean isItemAndNbtEqual(ItemStack ist1, ItemStack ist2) {
		return (ist1 == null && ist2 == null) || (ist1 != null && ist1.isItemEqual(ist2) && (ist1.getTagCompound() == null || ist1.getTagCompound().equals(ist2.getTagCompound())));
	}
}
