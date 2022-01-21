package reliquary.client.gui.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import reliquary.client.gui.components.Component;
import reliquary.client.gui.components.ItemStackCountPane;
import reliquary.reference.Colors;
import reliquary.util.InventoryHelper;

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
	public void renderInternal(PoseStack matrixStack, int x, int y) {
		Player player = Minecraft.getInstance().player;
		ItemStack itemStack = InventoryHelper.getCorrectItemFromEitherHand(player, mainItem);

		if (itemStack.isEmpty()) {
			return;
		}

		chargeablePane.setCount(getCount.apply(itemStack));
		chargeablePane.render(matrixStack, x, y);
	}
}
