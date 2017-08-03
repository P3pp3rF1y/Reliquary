package xreliquary.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import xreliquary.client.gui.components.Box;
import xreliquary.client.gui.components.Component;
import xreliquary.client.gui.components.ItemStackPane;
import xreliquary.client.gui.components.TextPane;
import xreliquary.client.gui.components.XPBarPane;
import xreliquary.init.ModItems;
import xreliquary.reference.Colors;
import xreliquary.util.InventoryHelper;
import xreliquary.util.XpHelper;

public class HeroMedallionPane extends Component {
	private XPBarPane xpBar;
	private TextPane levelPane;
	private Box mainPane;

	public HeroMedallionPane() {
		xpBar = new XPBarPane();
		levelPane = new TextPane("0", Colors.get(Colors.GREEN));
		mainPane = Box.createVertical(Box.Alignment.RIGHT, xpBar, Box.createHorizontal(Box.Alignment.MIDDLE, new ItemStackPane(ModItems.heroMedallion), levelPane));
	}

	@Override
	public int getHeightInternal() {
		return mainPane.getHeight();
	}

	@Override
	public int getWidthInternal() {
		return mainPane.getWidth();
	}

	@Override
	public boolean shouldRender() {
		return !InventoryHelper.getCorrectItemFromEitherHand(Minecraft.getMinecraft().player, ModItems.heroMedallion).isEmpty();
	}

	@Override
	public int getPadding() {
		return 1;
	}

	@Override
	public void renderInternal(int x, int y) {
		ItemStack item = InventoryHelper.getCorrectItemFromEitherHand(Minecraft.getMinecraft().player, ModItems.heroMedallion);

		if (item.isEmpty())
			return;

		int experience = ModItems.heroMedallion.getExperience(item);
		int level = XpHelper.getLevelForExperience(experience);
		levelPane.setText(String.valueOf(level));

		int remainingExperience = experience - XpHelper.getExperienceForLevel(level);
		int maxBarExperience = XpHelper.getExperienceLimitOnLevel(level);

		float xpRatio = ((float) remainingExperience) / ((float) maxBarExperience);
		xpBar.setXpRatio(xpRatio);

		mainPane.render(x, y);
	}
}
