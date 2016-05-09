package xreliquary.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class StackHelper {
	public static ItemStack getItemStackFromNameMeta(String registryName) {
		return getItemStackFromNameMeta(registryName, 0);
	}

	public static ItemStack getItemStackFromNameMeta(String registryName, int meta) {
		return new ItemStack(RegistryHelper.getItemFromName(registryName), 1, meta);
	}

	@Deprecated //TODO remove
	public static ItemStack getItemStackFromNameMeta(String modId, String name, int meta) {
		ItemStack stack = null;
		Item item = GameRegistry.findItem(modId, name);

		if(item != null && item != GameData.getItemRegistry().getDefaultValue()) {
			stack = new ItemStack(item, 1, meta);
		} else {
			Block block = GameRegistry.findBlock(modId, name);
			if(block != null && block != GameData.getBlockRegistry().getDefaultValue()) {
				stack = new ItemStack(item, 1, meta);
			}
		}
		return stack;
	}

	public static boolean isItemAndNbtEqual(ItemStack ist1, ItemStack ist2) {
		return (ist1 == null && ist2 == null) || ist1.isItemEqual(ist2) && (ist1.getTagCompound() == null || ist1.getTagCompound().equals(ist2.getTagCompound()));
	}
}
