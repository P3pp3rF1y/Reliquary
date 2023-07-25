package reliquary.items;

import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface ICreativeTabItemGenerator {
	void addCreativeTabItems(Consumer<ItemStack> itemConsumer);
}
