package xreliquary.util.pedestal;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.reference.Settings;

import java.util.List;

public class PedestalMeleeWeaponWrapper implements IPedestalActionItemWrapper {

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		FakePlayer fakePlayer = pedestal.getFakePlayer();

		//TODO add cooldown
		if(!fakePlayer.isUsingItem()) {
			BlockPos pos = pedestal.getPos();
			int meleeRange = Settings.Pedestal.meleeWrapperRange;

			List<EntityLiving> entities = pedestal.getWorld().getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.getX() - meleeRange, pos.getY() - meleeRange, pos.getZ() - meleeRange, pos.getX() + meleeRange, pos.getY() + meleeRange, pos.getZ() + meleeRange));

			if(entities.size() == 0) {
				pedestal.setActionCoolDown(40);
				return;
			}

			EntityLiving entityToAttack = entities.get(pedestal.getWorld().rand.nextInt(entities.size()));

			//don't want players to use this to kill bosses
			if (entityToAttack instanceof IBossDisplayData)
				return;

			fakePlayer.setCurrentItemOrArmor(0, stack);
			fakePlayer.onUpdate();

			fakePlayer.attackTargetEntityWithCurrentItem(entityToAttack);

			if (stack.stackSize == 0)
				pedestal.destroyCurrentItem();

		}
	}

}
