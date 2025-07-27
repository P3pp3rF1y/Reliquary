package reliquary.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;
import reliquary.util.potions.PotionHelper;

public class PotionItem extends PotionItemBase {
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
		return 16;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (PotionHelper.hasPotionContents(stack)) {
			return ItemUtils.startUsingInstantly(level, player, hand);
		} else {
			return new InteractionResultHolder<>(InteractionResult.PASS, stack);
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!(entity instanceof Player player) || level.isClientSide) {
			return stack;
		}

		PotionHelper.applyEffectsToEntity(stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY), player, null, player);

		if (!player.isCreative()) {
			stack.shrink(1);
			ItemStack emptyVial = new ItemStack(ModItems.EMPTY_POTION_VIAL.get());
			if (stack.getCount() <= 0) {
				return emptyVial;
			} else {
				player.getInventory().add(emptyVial);
			}
		}

		return stack;
	}

}
