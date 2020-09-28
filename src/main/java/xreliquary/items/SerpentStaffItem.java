package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.entities.KrakenSlimeEntity;

public class SerpentStaffItem extends ItemBase {
	public SerpentStaffItem() {
		super("serpent_staff", new Properties().maxDamage(200).setNoRepair());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public UseAction getUseAction(ItemStack par1ItemStack) {
		return UseAction.BLOCK;
	}

	@Override
	public void onUsingTick(ItemStack serpentStaff, LivingEntity entity, int count) {
		if (entity.world.isRemote || !(entity instanceof PlayerEntity) || count % 3 != 0) {
			return;
		}

		shootKrakenSlime(serpentStaff, (PlayerEntity) entity);
	}

	private void shootKrakenSlime(ItemStack serpentStaff, PlayerEntity player) {
		player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

		KrakenSlimeEntity krakenSlime = new KrakenSlimeEntity(player.world, player);
		krakenSlime.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0F, 1.5F, 1.0F);
		player.world.addEntity(krakenSlime);
		serpentStaff.damageItem(1, player, p -> p.sendBreakAnimation(p.getActiveHand()));
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack serpentStaff, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if (!entityLiving.world.isRemote && timeLeft + 2 >= serpentStaff.getUseDuration() && entityLiving instanceof PlayerEntity) {
			shootKrakenSlime(serpentStaff, (PlayerEntity) entityLiving);
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		//drain effect
		int drain = player.world.rand.nextInt(4);
		if (entity.attackEntityFrom(DamageSource.causePlayerDamage(player), drain)) {
			player.heal(drain);
			stack.damageItem(1, player, p -> p.sendBreakAnimation(p.getActiveHand()));
		}
		return false;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 11;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		player.setActiveHand(hand);
		return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}

}
