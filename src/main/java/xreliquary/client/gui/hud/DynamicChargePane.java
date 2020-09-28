package xreliquary.client.gui.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.client.gui.components.Component;
import xreliquary.client.gui.components.ItemStackCountPane;
import xreliquary.util.InventoryHelper;

import java.util.function.Function;

public class DynamicChargePane extends Component {
	private final Item mainItem;
	private final ItemStackCountPane chargeablePane;
	private final Function<ItemStack, ItemStack> getChargeItem;
	private final Function<ItemStack, Integer> getCount;

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
	public void renderInternal(MatrixStack matrixStack, int x, int y) {
		PlayerEntity player = Minecraft.getInstance().player;
		ItemStack itemStack = InventoryHelper.getCorrectItemFromEitherHand(player, mainItem);

		if (itemStack.isEmpty()) {
			return;
		}

		chargeablePane.setItemStack(getChargeItem.apply(itemStack));
		chargeablePane.setCount(getCount.apply(itemStack));
		chargeablePane.render(matrixStack, x, y);
	}
}
