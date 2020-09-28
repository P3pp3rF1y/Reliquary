package xreliquary.client.gui.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import xreliquary.client.gui.components.Box;
import xreliquary.client.gui.components.Component;
import xreliquary.client.gui.components.ItemStackPane;
import xreliquary.init.ModItems;

public class HandgunPane extends Component {
	private final Box mainPane;
	private final ItemStackPane[] bulletPanes = new ItemStackPane[8];
	private final ItemStackPane magazinePane;
	private final Hand hand;
	private int time = 0;

	public HandgunPane(Hand hand) {
		this.hand = hand;

		magazinePane = new ItemStackPane(ModItems.EMPTY_MAGAZINE);

		for (int i = 0; i < 8; i++) {
			bulletPanes[i] = new ItemStackPane(ItemStack.EMPTY) {
				@Override
				public int getPadding() {
					return -3; //hack to let bullets overlap a bit
				}
			};
		}
		Box bulletsPane = new Box(Box.Layout.HORIZONTAL, Box.Alignment.MIDDLE, bulletPanes) {
			@Override
			public int getPadding() {
				return 3; //hack to counter the minus padding of bullets
			}
		};

		if (hand == Hand.OFF_HAND) {
			mainPane = Box.createHorizontal(Box.Alignment.MIDDLE, new ItemStackPane(ModItems.HANDGUN), magazinePane, bulletsPane);
		} else {
			mainPane = Box.createHorizontal(Box.Alignment.MIDDLE, bulletsPane, magazinePane, new ItemStackPane(ModItems.HANDGUN));
		}
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
	public int getHeight() {
		return holdsHandgun() ? super.getHeight() : 0;
	}

	@Override
	public boolean shouldRender() {
		return holdsHandgun();
	}

	private boolean holdsHandgun() {
		return Minecraft.getInstance().player.getHeldItem(hand).getItem() == ModItems.HANDGUN;
	}

	@Override
	public void renderInternal(MatrixStack matrixStack, int x, int y) {
		ItemStack handgun = Minecraft.getInstance().player.getHeldItem(hand);

		if (handgun.isEmpty()) {
			return;
		}

		ItemStack bullets = ModItems.HANDGUN.getBulletStack(handgun);

		for (int i = 0; i < 8; i++) {
			if (i < bullets.getCount()) {
				bulletPanes[i].setItemStack(bullets);
			} else {
				bulletPanes[i].setItemStack(ItemStack.EMPTY);
			}
		}
		if (bullets.isEmpty() && getTime() % 32 > 16) {
			magazinePane.setItem(ModItems.EMPTY_MAGAZINE);
		} else {
			magazinePane.setItemStack(ItemStack.EMPTY);
		}

		mainPane.render(matrixStack, x, y);
	}

	private int getTime() {
		time++;
		if (time > 31) {
			time = 0;
		}
		return time;
	}
}
