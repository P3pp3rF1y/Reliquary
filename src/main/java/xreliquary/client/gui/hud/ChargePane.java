package xreliquary.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.client.gui.components.Component;
import xreliquary.client.gui.components.ItemStackCountPane;
import xreliquary.reference.Colors;
import xreliquary.util.InventoryHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
public class ChargePane extends Component {
	private Item mainItem;
	private ItemStackCountPane chargeablePane;
	private Function<ItemStack, Integer> getCount;

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
	public void renderInternal(int x, int y) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		ItemStack tomeStack = InventoryHelper.getCorrectItemFromEitherHand(player, mainItem);

		if(tomeStack.isEmpty())
			return;

		chargeablePane.setCount(getCount.apply(tomeStack));
		chargeablePane.render(x, y);
	}
}
