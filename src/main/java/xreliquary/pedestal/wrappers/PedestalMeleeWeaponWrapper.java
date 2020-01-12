package xreliquary.pedestal.wrappers;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.reference.Settings;

import java.util.List;

public class PedestalMeleeWeaponWrapper implements IPedestalActionItemWrapper {
	private int cooldownAfterSwing;

	public PedestalMeleeWeaponWrapper() {
		cooldownAfterSwing = Settings.COMMON.blocks.pedestal.meleeWrapperCooldown.get();
	}

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		FakePlayer fakePlayer = pedestal.getFakePlayer();

		World world = pedestal.getTheWorld();
		BlockPos pos = pedestal.getBlockPos();
		int meleeRange = Settings.COMMON.blocks.pedestal.meleeWrapperRange.get();

		List<MobEntity> entities = world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB((double) pos.getX() - meleeRange, (double) pos.getY() - meleeRange, (double) pos.getZ() - meleeRange, (double) pos.getX() + meleeRange, (double) pos.getY() + meleeRange, (double) pos.getZ() + meleeRange));

		if (entities.isEmpty()) {
			pedestal.setActionCoolDown(40);
			return;
		}

		MobEntity entityToAttack = entities.get(world.rand.nextInt(entities.size()));

		while (!entities.isEmpty() && !canAttackEntity(entityToAttack)) {
			entities.remove(entityToAttack);
			if (!entities.isEmpty()) {
				entityToAttack = entities.get(world.rand.nextInt(entities.size()));
			}
		}

		if (entities.isEmpty()) {
			pedestal.setActionCoolDown(40);
			return;
		}

		//set position so that entities get knocked back away from the altar
		fakePlayer.setPosition(pos.getX(), 0, pos.getZ());

		//set sword and update attributes
		fakePlayer.setHeldItem(Hand.MAIN_HAND, stack);
		fakePlayer.tick();

		fakePlayer.attackTargetEntityWithCurrentItem(entityToAttack);

		pedestal.setActionCoolDown((int) fakePlayer.getCooldownPeriod() + cooldownAfterSwing);

		//destroy the item when it gets used up
		if (stack.isEmpty()) {
			pedestal.destroyItem();
		}
	}

	private boolean canAttackEntity(MobEntity entityToAttack) {
		return entityToAttack.isNonBoss() && !(entityToAttack instanceof VillagerEntity)
				&& (!(entityToAttack instanceof AnimalEntity) || !entityToAttack.isChild())
				&& (!(entityToAttack instanceof HorseEntity) || !((HorseEntity) entityToAttack).isTame())
				&& (!(entityToAttack instanceof TameableEntity) || !((TameableEntity) entityToAttack).isTamed());
	}

	@Override
	public void onRemoved(ItemStack stack, IPedestal pedestal) {
		//noop
	}

	@Override
	public void stop(ItemStack stack, IPedestal pedestal) {
		//noop
	}
}
