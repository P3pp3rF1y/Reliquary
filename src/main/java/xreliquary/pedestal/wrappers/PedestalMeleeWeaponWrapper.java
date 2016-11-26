package xreliquary.pedestal.wrappers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;
import java.util.List;

public class PedestalMeleeWeaponWrapper implements IPedestalActionItemWrapper {

	private byte cooldownAfterSwing;

	@SuppressWarnings("unused")
	public PedestalMeleeWeaponWrapper() {
		this(Settings.Pedestal.meleeWrapperCooldown);
	}

	private PedestalMeleeWeaponWrapper(byte cooldownAfterSwing) {
		this.cooldownAfterSwing = cooldownAfterSwing;
	}

	@Override
	public void update(@Nonnull ItemStack stack, IPedestal pedestal) {
		FakePlayer fakePlayer = pedestal.getFakePlayer();

		World world = pedestal.getTheWorld();
		BlockPos pos = pedestal.getBlockPos();
		int meleeRange = Settings.Pedestal.meleeWrapperRange;

		List<EntityLiving> entities = world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.getX() - meleeRange, pos.getY() - meleeRange, pos.getZ() - meleeRange, pos.getX() + meleeRange, pos.getY() + meleeRange, pos.getZ() + meleeRange));

		if(entities.size() == 0) {
			pedestal.setActionCoolDown(40);
			return;
		}

		EntityLiving entityToAttack = entities.get(world.rand.nextInt(entities.size()));

		//don't want players to use this to kill bosses
		if(!entityToAttack.isNonBoss())
			return;

		//set position so that entities get knocked back away from the altar
		fakePlayer.setPosition(pos.getX(), 0, pos.getZ());

		//set sword and update attributes
		fakePlayer.setHeldItem(EnumHand.MAIN_HAND, stack);
		fakePlayer.onUpdate();

		fakePlayer.attackTargetEntityWithCurrentItem(entityToAttack);

		pedestal.setActionCoolDown((int) fakePlayer.getCooldownPeriod() + cooldownAfterSwing);

		//destroy the item when it gets used up
		if(stack.getCount() == 0)
			pedestal.destroyCurrentItem();
	}

	@Override
	public void onRemoved(@Nonnull ItemStack stack, IPedestal pedestal) {
	}

	@Override
	public void stop(@Nonnull ItemStack stack, IPedestal pedestal) {
	}
}
