package xreliquary.client.gui.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.client.gui.components.Component;
import xreliquary.client.gui.components.ItemStackCountPane;
import xreliquary.reference.Colors;
import xreliquary.util.InventoryHelper;

import java.util.function.Function;

public class ChargePane extends Component {
	private final Item mainItem;
	private final ItemStackCountPane chargeablePane;
	private final Function<ItemStack, Integer> getCount;

	public ChargePane(Item mainItem, ItemStack chargeItem, Function<ItemStack, Integer> getCount) {
		this(mainItem, chargeItem, getCount, Colors.get(Colors.PURE));
	}

	public ChargePane(Item mainItem, ItemStack chargeItem, Function<ItemStack, Integer> getCount, int textColor) {
		this.mainItem = mainItem;
		this.getCount = getCount;

		chargeablePane = new ItemStackCountPane(chargeItem, 0, textColor);
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

		chargeablePane.setCount(getCount.apply(itemStack));
		chargeablePane.render(matrixStack, x, y);
	}
}
