package reliquary.entity.potion;

import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import reliquary.init.ModEntities;
import reliquary.init.ModItems;
import reliquary.reference.Colors;

public class AphroditePotion extends ThrownPotionBase {
	public AphroditePotion(EntityType<? extends AphroditePotion> entityType, Level level) {
		super(entityType, level, new ItemStack(ModItems.APHRODITE_POTION.get()));
	}

	public AphroditePotion(Level level, Player player) {
		super(ModEntities.APHRODITE_POTION.get(), level, player, new ItemStack(ModItems.APHRODITE_POTION.get()));
	}

	public AphroditePotion(Level level, Position position) {
		super(ModEntities.APHRODITE_POTION.get(), level, position.x(), position.y(), position.z(), new ItemStack(ModItems.APHRODITE_POTION.get()));
	}

	@Override
	boolean hasLivingEntityEffect() {
		return true;
	}

	@Override
	void doGroundSplashEffect() {
		// none
	}

	@Override
	void doLivingSplashEffect(LivingEntity el) {
		if (!(el instanceof Animal animal)) {
			return;
		}
		if (animal.getAge() != 0) {
			return;
		}
		Entity thrower = getOwner();
		if (thrower instanceof Player player) {
			animal.setInLove(player);
		} else {
			animal.setInLove(null);
		}

		for (int var3 = 0; var3 < 7; ++var3) {
			double var4 = random.nextGaussian() * 0.02D;
			double var6 = random.nextGaussian() * 0.02D;
			double var8 = random.nextGaussian() * 0.02D;
			level().addParticle(ParticleTypes.HEART, animal.getX() + random.nextFloat() * animal.getBbWidth() * 2.0F - animal.getBbWidth(), animal.getY() + 0.5D + random.nextFloat() * animal.getBbHeight(), animal.getZ() + random.nextFloat() * animal.getBbWidth() * 2.0F - animal.getBbWidth(), var4, var6, var8);
		}
	}

	@Override
	int getColor() {
		return Colors.get(Colors.RED);
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ModItems.APHRODITE_POTION.get());
	}
}
