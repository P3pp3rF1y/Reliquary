package reliquary.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.level.Level;
import reliquary.util.potions.PotionHelper;

public class PotionItem extends PotionItemBase {
	public PotionItem(Properties properties) {
		super(properties.component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK));
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack stack) {
		return ItemUseAnimation.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
		return 16;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		ItemStack result = super.finishUsingItem(stack, level, entity);

		if ((entity instanceof Player player) && !level.isClientSide()) {
			PotionHelper.applyEffectsToEntity(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY), player, null, player);
		}

		return stack;
	}

}
