package xreliquary.client.gui.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import xreliquary.client.gui.components.Box;
import xreliquary.client.gui.components.Component;
import xreliquary.client.gui.components.ItemStackPane;
import xreliquary.reference.Settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CharmPane extends Component {
	private static Box mainPane = Box.createVertical();
	private static final Lock lock = new ReentrantLock();

	@Override
	public int getHeightInternal() {
		return mainPane.getHeight();
	}

	@Override
	public int getWidthInternal() {
		return mainPane.getWidth();
	}

	@Override
	public int getPadding() {
		return 0;
	}

	@Override
	public boolean shouldRender() {
		removeExpiredMobCharms();
		boolean isEmpty;
		lock.lock();
		isEmpty = charmsToDraw.isEmpty();
		lock.unlock();
		return !isEmpty;
	}

	private static void updateCharmsPane() {
		lock.lock();
		Component[] components = new Component[charmsToDraw.size()];
		AtomicInteger i = new AtomicInteger(0);
		charmsToDraw.forEach((slot, charmToDraw) -> {
			int index = i.getAndIncrement();
			components[index] = new ItemStackPane(charmToDraw.getCharm(), false, true);
		});
		lock.unlock();
		mainPane = Box.createVertical(components);
	}

	@Override
	public void renderInternal(PoseStack matrixStack, int x, int y) {
		mainPane.render(matrixStack, x, y);
	}

	private static final Map<Integer, CharmToDraw> charmsToDraw = new HashMap<>();

	private static class CharmToDraw {
		CharmToDraw(ItemStack charm, long time) {
			this.charm = charm;
			this.time = time;
		}

		final ItemStack charm;
		final long time;

		ItemStack getCharm() {
			return charm;
		}
	}

	public static void addCharmToDraw(ItemStack charm, int slot) {
		int maxMobCharmsToDisplay = Settings.COMMON.items.mobCharm.maxCharmsToDisplay.get();
		lock.lock();
		if (charmsToDraw.size() == maxMobCharmsToDisplay) {
			charmsToDraw.remove(0);
		}

		if (charm.isEmpty()) {
			charmsToDraw.remove(slot);
		} else {
			charmsToDraw.put(slot, new CharmToDraw(charm, System.currentTimeMillis()));
		}
		lock.unlock();
		updateCharmsPane();
	}

	private static void removeExpiredMobCharms() {
		int secondsToExpire = 4;
		boolean changed = false;
		lock.lock();
		for (Iterator<Map.Entry<Integer, CharmToDraw>> iterator = charmsToDraw.entrySet().iterator(); iterator.hasNext(); ) {
			Map.Entry<Integer, CharmToDraw> entry = iterator.next();
			CharmToDraw charmToDraw = entry.getValue();
			if (Boolean.TRUE.equals(Settings.COMMON.items.mobCharm.keepAlmostDestroyedDisplayed.get()) && charmToDraw.getCharm().getDamageValue() >= (charmToDraw.getCharm().getMaxDamage() * 0.9)) {
				continue;
			}

			if (charmToDraw.time + secondsToExpire * 1000 < System.currentTimeMillis()) {
				iterator.remove();
				changed = true;
			}
		}
		lock.unlock();

		if (changed) {
			updateCharmsPane();
		}
	}
}
