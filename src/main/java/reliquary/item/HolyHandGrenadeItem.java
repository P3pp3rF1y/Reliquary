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
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import reliquary.entity.HolyHandGrenade;

public class HolyHandGrenadeItem extends ItemBase implements ProjectileItem {

	public HolyHandGrenadeItem() {
		super(new Properties().rarity(Rarity.RARE));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (level.isClientSide) {
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		}

		if (!player.isCreative()) {
			stack.shrink(1);
		}

		level.playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
		HolyHandGrenade grenade = new HolyHandGrenade(level, player, stack.getHoverName().getString());
		grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.7F, 1.0F);
		level.addFreshEntity(grenade);

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	@Override
	public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
		return new HolyHandGrenade(level, position);
	}
}
