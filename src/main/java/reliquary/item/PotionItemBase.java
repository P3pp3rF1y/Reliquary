package reliquary.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import reliquary.init.ModItems;
import reliquary.item.util.IPotionItem;
import reliquary.reference.Config;
import reliquary.util.TooltipBuilder;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionHelper;
import reliquary.util.potions.PotionMap;

import java.util.List;
import java.util.function.Consumer;

public class PotionItemBase extends ItemBase implements IPotionItem {
	public PotionItemBase(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack getCraftingRemainder(ItemStack itemStack) {
		if (PotionHelper.hasPotionContents(itemStack)) {
			new ItemStack(ModItems.EMPTY_POTION_VIAL.get());
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void appendHoverText(ItemStack potion, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		TooltipBuilder.of(tooltip, context).potionEffects(potion);
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		if (Boolean.TRUE.equals(Config.COMMON.disable.disablePotions.get())) {
			return;
		}

		for (PotionEssence essence : PotionMap.uniquePotions) {
			ItemStack potion = new ItemStack(this, 1);
			PotionHelper.addPotionContentsToStack(potion, essence.getPotionContents());

			itemConsumer.accept(potion);
		}
	}

	@Override
	public PotionContents getPotionContents(ItemStack stack) {
		return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
	}
}
