package xreliquary.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import xreliquary.init.ModItems;
import xreliquary.util.potions.XRPotionHelper;

public class PotionItem extends PotionItemBase {
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack par1ItemStack) {
		return 16;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!XRPotionHelper.getPotionEffectsFromStack(stack).isEmpty()) {
			player.startUsingItem(hand);
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		} else {
			return new InteractionResultHolder<>(InteractionResult.PASS, stack);
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
		if (!(entity instanceof Player player) || world.isClientSide) {
			return stack;
		}

		XRPotionHelper.applyEffectsToEntity(XRPotionHelper.getPotionEffectsFromStack(stack), player, null, player);

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
