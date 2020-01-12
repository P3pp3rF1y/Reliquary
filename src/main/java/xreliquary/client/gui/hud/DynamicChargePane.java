package xreliquary.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.client.gui.components.Component;
import xreliquary.client.gui.components.ItemStackCountPane;
import xreliquary.util.InventoryHelper;

import java.util.function.Function;

public class DynamicChargePane extends Component {
	private Item mainItem;
	private ItemStackCountPane chargeablePane;
	private Function<ItemStack, ItemStack> getChargeItem;
	private Function<ItemStack, Integer> getCount;

	public DynamicChargePane(Item mainItem, Function<ItemStack, ItemStack> getChargeItem, Function<ItemStack, Integer> getCount) {
		this.mainItem = mainItem;
		this.getChargeItem = getChargeItem;
		this.getCount = getCount;

		chargeablePane = new ItemStackCountPane(ItemStack.EMPTY, 0);
	}

	@Override
	public int getHeightInternal() {
		return chargeablePane.getHeight();
	}

	@Override
	public int getWidthInternal() {
		return chargeablePane.getWidth();
	}

	@Override
	public int getPadding() {
		return chargeablePane.getPadding();
	}

	@Override
	public void renderInternal(int x, int y) {
		PlayerEntity player = Minecraft.getInstance().player;
		ItemStack itemStack = InventoryHelper.getCorrectItemFromEitherHand(player, mainItem);

		if (itemStack.isEmpty())
			return;

		chargeablePane.setItemStack(getChargeItem.apply(itemStack));
		chargeablePane.setCount(getCount.apply(itemStack));
		chargeablePane.render(x, y);
	}
}
