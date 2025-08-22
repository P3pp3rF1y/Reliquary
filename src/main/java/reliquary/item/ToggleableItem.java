package reliquary.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import reliquary.init.ModDataComponents;
import reliquary.util.RandHelper;

import java.util.function.Supplier;

public abstract class ToggleableItem extends ItemBase {
	protected ToggleableItem(Properties properties, Supplier<Boolean> isDisabled) {
		super(properties, isDisabled);
	}

	protected ToggleableItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return isEnabled(stack);
	}

	protected void setCooldown(ItemStack stack, Level level, int cooldown) {
		stack.set(ModDataComponents.COOLDOWN_TIME, level.getGameTime() + cooldown);
	}

	protected boolean isInCooldown(ItemStack stack, Level level) {
		return stack.getOrDefault(ModDataComponents.COOLDOWN_TIME, 0L) > level.getGameTime();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide && player.isShiftKeyDown()) {
			toggleEnabled(stack);
			player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.level().random) * 0.7F + 1.2F));

			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		}
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem() || oldStack.hasFoil() != newStack.hasFoil();
	}

	public boolean isEnabled(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.ENABLED, false);
	}

	void toggleEnabled(ItemStack stack) {
		stack.set(ModDataComponents.ENABLED, !isEnabled(stack));
	}
}
