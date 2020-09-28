package xreliquary.client.gui.hud;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.client.gui.components.Box;
import xreliquary.client.gui.components.Component;
import xreliquary.client.gui.components.ItemStackPane;
import xreliquary.util.InventoryHelper;

import java.util.Map;
import java.util.function.Function;

public class ChargeableItemInfoPane extends Component {
	public static final String DYNAMIC_PANE = "dynamic";
	private final ItemStack mainItem;
	private Box mainPanel;
	private Map<String, Component> modePanes = Maps.newHashMap();
	private final HUDPosition hudPosition;
	private String lastMode;
	private final Function<ItemStack, String> getMode;

	public ChargeableItemInfoPane(ItemStack mainItem, HUDPosition hudPosition, Function<ItemStack, String> getMode, Map<String, Component> modePanes) {
		this(mainItem, getMode, hudPosition);
		this.modePanes = modePanes;

		String mode = modePanes.keySet().iterator().next();
		updateCurrentPane(modePanes.get(mode), mode);
	}

	public ChargeableItemInfoPane(Item mainItem, HUDPosition hudPosition, Function<ItemStack, String> getMode, Map<String, Component> modePanes) {
		this(new ItemStack(mainItem), hudPosition, getMode, modePanes);
	}

	public ChargeableItemInfoPane(Item mainItem, HUDPosition hudPosition, ItemStack chargeItem, Function<ItemStack, Integer> getCount) {
		this(new ItemStack(mainItem), is -> "single", hudPosition);
		updateCurrentPane(new ChargePane(mainItem, chargeItem, getCount), "single");
	}

	public ChargeableItemInfoPane(Item mainItem, HUDPosition hudPosition, ItemStack chargeItem, Function<ItemStack, Integer> getCount, int textColor) {
		this(new ItemStack(mainItem), is -> "single", hudPosition);
		updateCurrentPane(new ChargePane(mainItem, chargeItem, getCount, textColor), "single");
	}

	private ChargeableItemInfoPane(ItemStack mainItem, Function<ItemStack, String> getMode, HUDPosition hudPosition) {
		this.mainItem = mainItem;
		this.getMode = getMode;
		this.hudPosition = hudPosition;
	}

	private void updateCurrentPane(Component modePane, String currentMode) {
		lastMode = currentMode;
		Box.Alignment alignment = getMainStackAlignment(hudPosition);
		mainPanel = Box.createVertical(alignment, new ItemStackPane(mainItem), modePane);
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
	public int getPadding() {
		return 1;
	}

	@Override
	public boolean shouldRender() {
		return !InventoryHelper.getCorrectItemFromEitherHand(Minecraft.getInstance().player, mainItem.getItem()).isEmpty();
	}

	@Override
	public void renderInternal(MatrixStack matrixStack, int x, int y) {
		ItemStack mainStack = InventoryHelper.getCorrectItemFromEitherHand(Minecraft.getInstance().player, mainItem.getItem());

		String mode = getMode.apply(mainStack);
		if (!lastMode.equals(mode)) {
			if (modePanes.containsKey(mode)) {
				updateCurrentPane(modePanes.get(mode), mode);
			} else if (modePanes.containsKey(DYNAMIC_PANE)) {
				updateCurrentPane(modePanes.get(DYNAMIC_PANE), DYNAMIC_PANE);
			}
		}

		mainPanel.render(matrixStack, x, y);
	}

	private static Box.Alignment getMainStackAlignment(HUDPosition position) {
		Box.Alignment alignment = position == HUDPosition.TOP ? Box.Alignment.MIDDLE : Box.Alignment.LEFT;
		if (position.isRightSide()) {
			alignment = Box.Alignment.RIGHT;
		}
		return alignment;
	}
}
