package reliquary.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reliquary.entities.KrakenSlimeEntity;

public class SerpentStaffItem extends ItemBase {
	public SerpentStaffItem() {
		super(new Properties().durability(200).setNoRepair());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack par1ItemStack) {
		return UseAnim.BLOCK;
	}

	@Override
	public void onUsingTick(ItemStack serpentStaff, LivingEntity entity, int count) {
		if (entity.level.isClientSide || !(entity instanceof Player) || count % 3 != 0) {
			return;
		}

		shootKrakenSlime(serpentStaff, (Player) entity);
	}

	private void shootKrakenSlime(ItemStack serpentStaff, Player player) {
		player.level.playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 0.5F, 0.4F / (player.level.random.nextFloat() * 0.4F + 0.8F));

		KrakenSlimeEntity krakenSlime = new KrakenSlimeEntity(player.level, player);
		krakenSlime.shootFromRotation(player, player.getXRot(), player.getYRot(), 0F, 1.5F, 1.0F);
		player.level.addFreshEntity(krakenSlime);
		serpentStaff.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
	}

	@Override
	public void releaseUsing(ItemStack serpentStaff, Level worldIn, LivingEntity entityLiving, int timeLeft) {
		if (!entityLiving.level.isClientSide && timeLeft + 2 >= serpentStaff.getUseDuration() && entityLiving instanceof Player player) {
			shootKrakenSlime(serpentStaff, player);
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		//drain effect
		int drain = player.level.random.nextInt(4);
		if (entity.hurt(DamageSource.playerAttack(player), drain)) {
			player.heal(drain);
			stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
		}
		return false;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 11;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
	}

}
