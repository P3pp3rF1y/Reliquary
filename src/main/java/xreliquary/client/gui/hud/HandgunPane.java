package xreliquary.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import xreliquary.client.gui.components.Box;
import xreliquary.client.gui.components.Component;
import xreliquary.client.gui.components.ItemStackPane;
import xreliquary.init.ModItems;
import xreliquary.util.potions.XRPotionHelper;

import java.util.List;

public class HandgunPane extends Component {
	Box mainPane;
	ItemStackPane[] bulletPanes = new ItemStackPane[8];
	ItemStackPane magazinePane;
	EnumHand hand;
	int time = 0;

	public HandgunPane(EnumHand hand) {
		this.hand = hand;

		magazinePane = new ItemStackPane(ModItems.magazine);

		for(int i=0; i < 8; i++) {
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

		if (hand == EnumHand.OFF_HAND) {
			mainPane = Box.createHorizontal(Box.Alignment.MIDDLE, new ItemStackPane(ModItems.handgun), magazinePane, bulletsPane);
		} else {
			mainPane = Box.createHorizontal(Box.Alignment.MIDDLE, bulletsPane, magazinePane, new ItemStackPane(ModItems.handgun));
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
		return Minecraft.getMinecraft().player.getHeldItem(hand).getItem() == ModItems.handgun;
	}

	@Override
	public void renderInternal(int x, int y) {
		ItemStack handgun = Minecraft.getMinecraft().player.getHeldItem(hand);

		if (handgun.isEmpty())
			return;

		ItemStack bullets = getBulletStackFromHandgun(handgun);

		for (int i=0; i<8;i++) {
			if (i < bullets.getCount()) {
				bulletPanes[i].setItemStack(bullets);
			} else {
				bulletPanes[i].setItemStack(ItemStack.EMPTY);
			}
		}
		if (bullets.isEmpty() && getTime() % 32 > 16) {
			magazinePane.setItem(ModItems.magazine);
		} else {
			magazinePane.setItemStack(ItemStack.EMPTY);
		}

		mainPane.render(x, y);
	}

	private int getTime() {
		time++;
		if (time > 31) {
			time = 0;
		}
		return time;
	}

	private static ItemStack getBulletStackFromHandgun(ItemStack handgun) {
		ItemStack bulletStack = new ItemStack(ModItems.bullet, ModItems.handgun.getBulletCount(handgun), ModItems.handgun.getBulletType(handgun));
		List<PotionEffect> potionEffects = ModItems.handgun.getPotionEffects(handgun);
		if(potionEffects != null && !potionEffects.isEmpty()) {
			XRPotionHelper.addPotionEffectsToStack(bulletStack, potionEffects);
		}

		return bulletStack;
	}
}
