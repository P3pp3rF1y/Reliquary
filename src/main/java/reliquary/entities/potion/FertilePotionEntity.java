package reliquary.entities.potion;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import reliquary.init.ModEntities;
import reliquary.init.ModItems;
import reliquary.reference.Colors;

public class FertilePotionEntity extends ThrownPotionEntity {
	public FertilePotionEntity(EntityType<FertilePotionEntity> entityType, Level world) {
		super(entityType, world, new ItemStack(ModItems.FERTILE_POTION.get()));
	}

	public FertilePotionEntity(Level world, Player player) {
		super(ModEntities.FERTILE_POTION.get(), world, player, new ItemStack(ModItems.FERTILE_POTION.get()));
	}

	public FertilePotionEntity(Level world, double x, double y, double z) {
		super(ModEntities.FERTILE_POTION.get(), world, x, y, z, new ItemStack(ModItems.FERTILE_POTION.get()));
	}

	@Override
	boolean hasLivingEntityEffect() {
		return false;
	}

	// fertility is one of the only potion that has this effect, the rest of
	// them will be mostly empty
	@Override
	@SuppressWarnings({"squid:CallToDeprecatedMethod", "deprecation"})
	// there's no point in calling player/hand applyBonemeal version as that would mean repeating code in this "deprecated" version
	void doGroundSplashEffect() {
		BlockPos.betweenClosedStream(blockPosition().offset(-1, -2, -1), blockPosition().offset(1, 1, 1))
				.forEach(pos -> BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), level, pos));
	}

	@Override
	void doLivingSplashEffect(LivingEntity e) {
		// overridden because fertility potion has no effect on living
		// entities.
	}

	@Override
	int getColor() {
		return Colors.get(Colors.LIGHT_GRAY);
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(ModItems.FERTILE_POTION.get());
	}
}
