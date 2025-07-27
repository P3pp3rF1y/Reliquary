package reliquary.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import reliquary.item.util.IPotionItem;
import reliquary.reference.Config;
import reliquary.util.TooltipBuilder;
import reliquary.util.potions.PotionEssence;
import reliquary.util.potions.PotionHelper;
import reliquary.util.potions.PotionMap;

import java.util.List;
import java.util.function.Consumer;

public class BulletItem extends ItemBase implements IPotionItem {
	private final boolean hasTooltip;
	private final boolean addPotionBulletsInItemGroup;
	private final int color;

	public BulletItem(boolean hasTooltip, boolean addPotionBulletsInItemGroup, int color) {
		super(new Properties().setNoRepair());
		this.hasTooltip = hasTooltip;
		this.addPotionBulletsInItemGroup = addPotionBulletsInItemGroup;
		this.color = color;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		TooltipBuilder tooltipBuilder = TooltipBuilder.of(tooltip, context);
		if (hasTooltip) {
			tooltipBuilder.itemTooltip(this);
		}
		tooltipBuilder.potionEffects(stack);
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		if (Boolean.TRUE.equals(Config.COMMON.disable.disableHandgun.get())) {
			return;
		}

		itemConsumer.accept(new ItemStack(this));

		if (!addPotionBulletsInItemGroup || Boolean.TRUE.equals(Config.COMMON.disable.disablePotions.get())) {
			return;
		}

		for (PotionEssence essence : PotionMap.uniquePotionEssences) {
			ItemStack bullet = new ItemStack(this);
			PotionHelper.addPotionContentsToStack(bullet, PotionHelper.changePotionEffectsDuration(essence.getPotionContents(), 0.2F));

			itemConsumer.accept(bullet);
		}
	}

	@Override
	public PotionContents getPotionContents(ItemStack stack) {
		return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
	}

	public int getColor() {
		return color;
	}
}
