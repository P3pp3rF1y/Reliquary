package reliquary.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import reliquary.init.ModItems;

import java.util.function.Consumer;

public class WitchHatItem extends ArmorItem implements ICreativeTabItemGenerator {
	public WitchHatItem() {
		super(ModItems.WITCH_HAT_MATERIAL, Type.HELMET, new Properties());
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		itemConsumer.accept(new ItemStack(this));
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable(getDescriptionId(stack)).withStyle(ChatFormatting.YELLOW);
	}
}
