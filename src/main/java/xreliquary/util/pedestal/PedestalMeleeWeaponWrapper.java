package xreliquary.util.pedestal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItem;

public class PedestalMeleeWeaponWrapper implements IPedestalActionItem {

	private ItemStack item;
	public PedestalMeleeWeaponWrapper(ItemStack item) {
		this.item = item;
	}

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		FakePlayer fakePlayer = pedestal.getFakePlayer();
		if(!fakePlayer.isUsingItem()) {
			fakePlayer.setCurrentItemOrArmor(0, item);
			fakePlayer.attackTargetEntityWithCurrentItem(null); //TODO fix with correct entity
		}
	}
}
