package xreliquary.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import xreliquary.client.gui.components.Box;
import xreliquary.client.gui.components.Component;
import xreliquary.client.gui.components.ItemStackCountPane;
import xreliquary.client.gui.components.ItemStackPane;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChargeableItemPane extends Component {
	private Item mainItem;
	private ItemStack chargeItem;
	private Box mainPanel;
	private ItemStackCountPane chargeablePane;
	private Function<ItemStack, Integer> getCount;

	public ChargeableItemPane(Item mainItem, @Nonnull ItemStack chargeItem, HUDPosition hudPosition, @Nonnull Function<ItemStack, Integer> getCount) {
		this.mainItem = mainItem;
		this.chargeItem = chargeItem;
		this.getCount = getCount;

		chargeablePane = new ItemStackCountPane(chargeItem, 0);
		Box.Alignment alignment = getMainStackAlignment(hudPosition);

		mainPanel = Box.createVertical(alignment, new ItemStackPane(new ItemStack(mainItem)), chargeablePane);
	}

	@Override
	public int getHeightInternal() {
		return mainPanel.getHeight();
	}

	@Override
	public int getWidthInternal() {
		return mainPanel.getWidth();
	}

	@Override
	public boolean shouldRender() {
		return !getCorrectItemFromEitherHand(Minecraft.getMinecraft().player, mainItem).isEmpty();
	}

	@Override
	public void renderInternal(int x, int y) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		ItemStack tomeStack = getCorrectItemFromEitherHand(player, mainItem);

		if(tomeStack.isEmpty())
			return;

		chargeablePane.setCount(getCount.apply(tomeStack));
		mainPanel.render(x, y);
	}

	@Nonnull
	private static ItemStack getCorrectItemFromEitherHand(EntityPlayer player, Item item) {
		if(player == null)
			return ItemStack.EMPTY;

		EnumHand itemInHand = getHandHoldingCorrectItem(player, item);

		if(itemInHand == null)
			return ItemStack.EMPTY;

		return player.getHeldItem(itemInHand);
	}

	private static EnumHand getHandHoldingCorrectItem(EntityPlayer player, Item item) {
		if(player.getHeldItemMainhand().getItem() == item) {
			return EnumHand.MAIN_HAND;
		}

		if(player.getHeldItemOffhand().getItem() == item) {
			return EnumHand.OFF_HAND;
		}
		return null;
	}

	private static Box.Alignment getMainStackAlignment(HUDPosition position) {
		Box.Alignment alignment = position == HUDPosition.TOP ? Box.Alignment.MIDDLE : Box.Alignment.LEFT;
		if (position.isRightSide()) {
			alignment = Box.Alignment.RIGHT;
		}
		return alignment;
	}
}
