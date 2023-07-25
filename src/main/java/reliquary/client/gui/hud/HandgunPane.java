package reliquary.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import reliquary.client.gui.components.Box;
import reliquary.client.gui.components.Component;
import reliquary.client.gui.components.ItemStackPane;
import reliquary.init.ModItems;

public class HandgunPane extends Component {
	private final Box mainPane;
	private final ItemStackPane[] bulletPanes = new ItemStackPane[8];
	private final ItemStackPane magazinePane;
	private final InteractionHand hand;

	public HandgunPane(InteractionHand hand) {
		this.hand = hand;

		magazinePane = new ItemStackPane(ModItems.EMPTY_MAGAZINE.get());

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

		if (hand == InteractionHand.OFF_HAND) {
			mainPane = Box.createHorizontal(Box.Alignment.MIDDLE, new ItemStackPane(ModItems.HANDGUN.get()), magazinePane, bulletsPane);
		} else {
			mainPane = Box.createHorizontal(Box.Alignment.MIDDLE, bulletsPane, magazinePane, new ItemStackPane(ModItems.HANDGUN.get()));
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
		return Minecraft.getInstance().player.getItemInHand(hand).getItem() == ModItems.HANDGUN.get();
	}

	@Override
	public void renderInternal(GuiGraphics guiGraphics, int x, int y) {
		ItemStack handgun = Minecraft.getInstance().player.getItemInHand(hand);

		if (handgun.isEmpty()) {
			return;
		}

		ItemStack bullets = ModItems.HANDGUN.get().getBulletStack(handgun);

		for (int i = 0; i < 8; i++) {
			if (i < bullets.getCount()) {
				bulletPanes[i].setItemStack(bullets);
			} else {
				bulletPanes[i].setItemStack(ItemStack.EMPTY);
			}
		}
		if (bullets.isEmpty() && (System.currentTimeMillis() / 500) % 2 == 0) {
			magazinePane.setItem(ModItems.EMPTY_MAGAZINE.get());
		} else {
			magazinePane.setItemStack(ItemStack.EMPTY);
		}

		mainPane.render(guiGraphics, x, y);
	}
}
