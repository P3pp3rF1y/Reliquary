package xreliquary.common;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public class CommonProxy {
	public void registerHandlers() {
		//no default implementation, overriden in client proxy
	}

	public void registerJEI(Supplier<List<ItemStack>> items, String... name) {
		//no default implementation, overriden in client proxy
	}

	public void registerJEI(Block block, String name) {
		//no default implementation, overriden in client proxy
	}
}
