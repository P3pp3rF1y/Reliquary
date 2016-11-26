package xreliquary.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class StackHelper {
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

	public static boolean isItemAndNbtEqual(@Nonnull ItemStack ist1, @Nonnull ItemStack ist2) {
		return (ist1.isEmpty() && ist2.isEmpty()) || (ist1.isItemEqual(ist2) && (ist1.getTagCompound() == null || ist1.getTagCompound().equals(ist2.getTagCompound())));
	}
}
