package xreliquary.entities.potion;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xreliquary.init.ModEntities;
import xreliquary.init.ModItems;
import xreliquary.reference.Colors;

public class FertilePotionEntity extends ThrownPotionEntity {
	public FertilePotionEntity(EntityType<FertilePotionEntity> entityType, World world) {
		super(entityType, world, new ItemStack(ModItems.FERTILE_POTION));
	}

	public FertilePotionEntity(World world, PlayerEntity player) {
		super(ModEntities.FERTILE_POTION, world, player, new ItemStack(ModItems.FERTILE_POTION));
	}

	public FertilePotionEntity(World world, double x, double y, double z) {
		super(ModEntities.FERTILE_POTION, world, x, y, z, new ItemStack(ModItems.FERTILE_POTION));
	}

	@Override
	boolean hasLivingEntityEffect() {
		return false;
	}

	// fertility is one of the only potion that has this effect, the rest of
	// them will be mostly empty
	@Override
	@SuppressWarnings({"squid:CallToDeprecatedMethod", "deprecation"}) // there's no point in calling player/hand applyBonemeal version as that would mean repeating code in this "deprecated" version
	void doGroundSplashEffect() {
		BlockPos.getAllInBox(getPosition().add(-1, -2, -1), getPosition().add(1, 1, 1))
				.forEach(pos -> BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, pos));
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
		return new ItemStack(ModItems.FERTILE_POTION);
	}
}
