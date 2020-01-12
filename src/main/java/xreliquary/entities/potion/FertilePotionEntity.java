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
	void doGroundSplashEffect() {
		int x = (int) (posX + 0.5);
		int y = (int) (posY + 0.5);
		int z = (int) (posZ + 0.5);
		// applies bonemeal to every block it finds in a 3x3 area.
		for (int xD = -1; xD <= 1; xD++) {
			for (int yD = -2; yD <= 1; yD++) {
				for (int zD = -1; zD <= 1; zD++) {
					BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, new BlockPos(x + xD, y + yD, z + zD));
				}
			}
		}
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
