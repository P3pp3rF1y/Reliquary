package xreliquary.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class StackHelper {
	private StackHelper() {}

	public static Optional<ItemStack> getItemStackFromName(String name) {
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
		if (item == null) {
			return Optional.empty();
		}
		return Optional.of(new ItemStack(item));
	}

	public static boolean isItemAndNbtEqual(ItemStack ist1, ItemStack ist2) {
		return (ist1.isEmpty() && ist2.isEmpty()) || (ist1.isItemEqual(ist2) && (ist1.getTag() == null || ist1.getTag().equals(ist2.getTag())));
	}
}
