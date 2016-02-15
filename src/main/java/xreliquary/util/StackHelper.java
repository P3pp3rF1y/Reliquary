package xreliquary.util;


import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class StackHelper
{
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

		if (item != null && item != GameData.getItemRegistry().getDefaultValue()) {
            stack = new ItemStack(item, 1, meta);
        } else {
            Block block = GameRegistry.findBlock(modId, name);
            if (block != null && block != GameData.getBlockRegistry().getDefaultValue()) {
                stack = new ItemStack(item, 1, meta);
            }
        }
		return stack;
	}
}
