package xreliquary.client.gui.hud;

import net.minecraft.item.ItemStack;
import xreliquary.client.gui.components.Box;
import xreliquary.client.gui.components.Component;
import xreliquary.client.gui.components.ItemStackPane;
import xreliquary.init.ModItems;
import xreliquary.items.ItemMobCharm;
import xreliquary.reference.Settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CharmPane extends Component {
	private static Box mainPane = Box.createVertical();

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

		return getCharmsToDraw().size() > 0;
	}

	private static void updateCharmsPane() {
		HashMap<Integer, CharmToDraw> charms = getCharmsToDraw();
		Component[] components = new Component[charms.size()];
		int i = 0;
		for (CharmToDraw charmToDraw : charms.values()) {
			ItemStack stackToRender = ItemMobCharm.getCharmStack(charmToDraw.type);
			stackToRender.setItemDamage(charmToDraw.damage);
			components[i] = new ItemStackPane(stackToRender, false, true);
			i++;
		}
		mainPane = Box.createVertical(components);
	}

	@Override
	public void renderInternal(int x, int y) {
		mainPane.render(x, y);
	}

	private static final HashMap<Integer, CharmToDraw> charmsToDraw = new HashMap<>();

	private static synchronized HashMap<Integer, CharmToDraw> getCharmsToDraw() {
		return charmsToDraw;
	}

	private static class CharmToDraw {
		CharmToDraw(byte type, int damage, long time) {
			this.type = type;
			this.damage = damage;
			this.time = time;
		}

		byte type;
		int damage;
		long time;
	}

	public static void addCharmToDraw(byte type, int damage, int slot) {
		int maxMobCharmsToDisplay = Settings.Items.MobCharm.maxCharmsToDisplay;
		synchronized(charmsToDraw) {
			if(charmsToDraw.size() == maxMobCharmsToDisplay) {
				charmsToDraw.remove(0);
			}

			if(charmsToDraw.keySet().contains(slot)) {
				charmsToDraw.remove(slot);
			}

			if(damage > ModItems.mobCharm.getMaxDamage(ItemStack.EMPTY))
				charmsToDraw.remove(slot);

			if(damage <= ModItems.mobCharm.getMaxDamage(ItemStack.EMPTY))
				charmsToDraw.put(slot, new CharmToDraw(type, damage, System.currentTimeMillis()));
		}
		updateCharmsPane();
	}

	private static void removeExpiredMobCharms() {
		int secondsToExpire = 4;
		synchronized(charmsToDraw) {
			for(Iterator<Map.Entry<Integer, CharmToDraw>> iterator = charmsToDraw.entrySet().iterator(); iterator.hasNext(); ) {
				Map.Entry<Integer, CharmToDraw> entry = iterator.next();
				if(Settings.Items.MobCharm.keepAlmostDestroyedDisplayed && entry.getValue().damage >= (ModItems.mobCharm.getMaxDamage(ItemStack.EMPTY) * 0.9))
					continue;

				if(entry.getValue().time + secondsToExpire * 1000 < System.currentTimeMillis()) {
					iterator.remove();
					updateCharmsPane();
				}
			}
		}
	}
}
