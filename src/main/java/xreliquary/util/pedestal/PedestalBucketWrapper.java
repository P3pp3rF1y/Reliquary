package xreliquary.util.pedestal;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.init.ModFluids;
import xreliquary.reference.Settings;

import java.util.List;

public class PedestalBucketWrapper implements IPedestalActionItemWrapper {

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		BlockPos pos = pedestal.getPos();
		int meleeRange = Settings.Pedestal.bucketWrapperRange;

		List<EntityCow> entities = pedestal.getWorld().getEntitiesWithinAABB(EntityCow.class, new AxisAlignedBB(pos.getX() - meleeRange, pos.getY() - meleeRange, pos.getZ() - meleeRange, pos.getX() + meleeRange, pos.getY() + meleeRange, pos.getZ() + meleeRange));

		if(entities.size() == 0) {
			pedestal.setActionCoolDown(80);
			return;
		}

		EntityCow cow = entities.get(pedestal.getWorld().rand.nextInt(entities.size()));

		FakePlayer fakePlayer = pedestal.getFakePlayer();
		fakePlayer.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));

		cow.interact(fakePlayer);

		if (fakePlayer.getCurrentEquippedItem().getItem() == Items.milk_bucket) {
			int fluidAdded = pedestal.addToConnectedTank(new FluidStack(ModFluids.milk, FluidContainerRegistry.BUCKET_VOLUME));
			if (fluidAdded == 0) {
				pedestal.replaceCurrentItem(new ItemStack(Items.milk_bucket));
				return;
			}
		}

		pedestal.setActionCoolDown(Settings.Pedestal.bucketWrapperCooldown);
	}
}
