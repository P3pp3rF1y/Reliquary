package reliquary.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import reliquary.entity.KrakenSlime;

public class SerpentStaffItem extends ItemBase {
	public SerpentStaffItem() {
		super(new Properties().durability(200).setNoRepair());
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		return super.getName(stack).withStyle(ChatFormatting.DARK_GREEN);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BLOCK;
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack serpentStaff, int remainingUseDuration) {
		if (livingEntity.level().isClientSide || !(livingEntity instanceof Player player) || remainingUseDuration % 3 != 0) {
			return;
		}

		shootKrakenSlime(serpentStaff, player);
	}

	private void shootKrakenSlime(ItemStack serpentStaff, Player player) {
		player.level().playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 0.5F, 0.4F / (player.level().random.nextFloat() * 0.4F + 0.8F));

		KrakenSlime krakenSlime = new KrakenSlime(player.level(), player);
		krakenSlime.shootFromRotation(player, player.getXRot(), player.getYRot(), 0F, 1.5F, 1.0F);
		player.level().addFreshEntity(krakenSlime);
		serpentStaff.hurtAndBreak(1, player, player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
	}

	@Override
	public void releaseUsing(ItemStack serpentStaff, Level level, LivingEntity livingEntity, int timeLeft) {
		if (!livingEntity.level().isClientSide && timeLeft + 2 >= serpentStaff.getUseDuration(livingEntity) && livingEntity instanceof Player player) {
			shootKrakenSlime(serpentStaff, player);
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		//drain effect
		int drain = player.level().random.nextInt(4);
		if (entity.hurt(player.damageSources().playerAttack(player), drain)) {
			player.heal(drain);
			stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
		}
		return false;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
		return 11;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
	}

}
