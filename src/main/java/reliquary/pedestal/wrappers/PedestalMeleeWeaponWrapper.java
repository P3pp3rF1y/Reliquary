package reliquary.pedestal.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;
import reliquary.api.IPedestal;
import reliquary.api.IPedestalActionItemWrapper;
import reliquary.reference.Settings;

import java.util.List;

public class PedestalMeleeWeaponWrapper implements IPedestalActionItemWrapper {
	private final int cooldownAfterSwing;

	public PedestalMeleeWeaponWrapper() {
		cooldownAfterSwing = Settings.COMMON.blocks.pedestal.meleeWrapperCooldown.get();
	}

	@Override
	public void update(ItemStack stack, Level level, IPedestal pedestal) {
		BlockPos pos = pedestal.getBlockPos();
		int meleeRange = Settings.COMMON.blocks.pedestal.meleeWrapperRange.get();

		List<Mob> entities = level.getEntitiesOfClass(Mob.class, new AABB((double) pos.getX() - meleeRange, (double) pos.getY() - meleeRange, (double) pos.getZ() - meleeRange, (double) pos.getX() + meleeRange, (double) pos.getY() + meleeRange, (double) pos.getZ() + meleeRange));

		if (entities.isEmpty()) {
			pedestal.setActionCoolDown(40);
			return;
		}

		Mob entityToAttack = entities.get(level.random.nextInt(entities.size()));

		while (!entities.isEmpty() && !canAttackEntity(entityToAttack)) {
			entities.remove(entityToAttack);
			if (!entities.isEmpty()) {
				entityToAttack = entities.get(level.random.nextInt(entities.size()));
			}
		}

		if (entities.isEmpty()) {
			pedestal.setActionCoolDown(40);
			return;
		}

		Mob finalEntityToAttack = entityToAttack;
		pedestal.getFakePlayer().ifPresent(fakePlayer -> attackEntity(stack, pedestal, pos, finalEntityToAttack, fakePlayer));

		//destroy the item when it gets used up
		if (stack.isEmpty()) {
			pedestal.destroyItem();
		}
	}

	private void attackEntity(ItemStack stack, IPedestal pedestal, BlockPos pos, Mob entityToAttack, FakePlayer fakePlayer) {
		//set position so that entities get knocked back away from the altar
		fakePlayer.setPos(pos.getX(), 0, pos.getZ());

		//set sword and update attributes
		fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
		fakePlayer.tick();

		fakePlayer.attack(entityToAttack);

		pedestal.setActionCoolDown((int) fakePlayer.getCurrentItemAttackStrengthDelay() + cooldownAfterSwing);
	}

	private boolean canAttackEntity(Mob entityToAttack) {
		return !(entityToAttack instanceof Villager)
				&& (!(entityToAttack instanceof Animal) || !entityToAttack.isBaby())
				&& (!(entityToAttack instanceof Horse horse) || !horse.isTamed())
				&& (!(entityToAttack instanceof TamableAnimal tamableAnimal) || !tamableAnimal.isTame());
	}

	@Override
	public void onRemoved(ItemStack stack, Level level, IPedestal pedestal) {
		//noop
	}

	@Override
	public void stop(ItemStack stack, Level level, IPedestal pedestal) {
		//noop
	}
}
