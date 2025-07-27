package reliquary.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import reliquary.entity.potion.AphroditePotion;
import reliquary.init.ModItems;
import reliquary.reference.Config;

public class AphroditePotionItem extends ItemBase implements ProjectileItem {

	public AphroditePotionItem() {
		super(new Properties(), Config.COMMON.disable.disablePotions);
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		return new ItemStack(ModItems.EMPTY_POTION_VIAL.get());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (level.isClientSide) {
			return new InteractionResultHolder<>(InteractionResult.PASS, stack);
		}
		if (!player.isCreative()) {
			stack.shrink(1);
		}
		level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
		AphroditePotion aphroditePotion = new AphroditePotion(level, player);
		aphroditePotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.7F, 1.0F);
		level.addFreshEntity(aphroditePotion);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	@Override
	public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
		return new AphroditePotion(level, position);
	}
}
