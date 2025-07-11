package reliquary.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import reliquary.item.util.IPotionItem;
import reliquary.reference.Config;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionHelper;
import reliquary.util.potions.PotionMap;

import java.util.function.Consumer;

public class PotionEssenceItem extends ItemBase implements IPotionItem {

	public PotionEssenceItem(Properties properties) {
		super(properties);
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		if (Boolean.TRUE.equals(Config.COMMON.disable.disablePotions.get())) {
			return;
		}

		for (PotionEssence essence : PotionMap.uniquePotionEssences) {
			ItemStack essenceItem = new ItemStack(this, 1);
			PotionHelper.addPotionContentsToStack(essenceItem, essence.getPotionContents());

			itemConsumer.accept(essenceItem);
		}
	}

	@Override
	public PotionContents getPotionContents(ItemStack stack) {
		return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
	}
}
