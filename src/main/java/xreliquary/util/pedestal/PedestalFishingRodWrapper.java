package xreliquary.util.pedestal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.entities.EntityXRFakePlayer;
import xreliquary.network.PacketHandler;
import xreliquary.network.PacketPedestalFishHook;

import java.util.Random;

public class PedestalFishingRodWrapper implements IPedestalActionItemWrapper {
	private static final int PACKET_RANGE = 50;

	private EntityXRFakePlayer fakePlayer;
	private static Random rand = new Random();

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		setupFakePlayer(pedestal.getTheWorld(), pedestal.getBlockPos(), stack);

		if (fakePlayer.fishEntity != null) {
			syncHookData(pedestal);
			if (getTicksCatchable(fakePlayer.fishEntity) > 0) {
				int i = fakePlayer.fishEntity.handleHookRetraction();
				stack.damageItem(i, fakePlayer);
			}
		} else {
			World world = pedestal.getTheWorld();
			world.playSound(null, pedestal.getBlockPos(), SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));

			world.spawnEntityInWorld(new EntityFishHook(world, fakePlayer));
		}
	}

	private void syncHookData(IPedestal pedestal) {
		EntityFishHook hook = fakePlayer.fishEntity;
		BlockPos pedestalPos = pedestal.getBlockPos();

		PacketHandler.networkWrapper.sendToAllAround(new PacketPedestalFishHook(pedestal.getBlockPos(), pedestal.getCurrentItemIndex(), hook.posX, hook.posY, hook.posZ),
				new NetworkRegistry.TargetPoint(pedestal.getTheWorld().provider.getDimension(), pedestalPos.getX(), pedestalPos.getY(), pedestalPos.getZ(), PACKET_RANGE));
	}

	@Override
	public void onRemoved(ItemStack stack, IPedestal pedestal) {
		if (fakePlayer.fishEntity != null) {
			fakePlayer.fishEntity.setDead();
		}
	}

	private void setupFakePlayer(World world, BlockPos pos, ItemStack fishingRod) {
		if (fakePlayer == null) {
			fakePlayer = new EntityXRFakePlayer((WorldServer) world);
			fakePlayer.setPosition(pos.getX(), pos.getY() + 1, pos.getZ());
		}
		if (fakePlayer.getHeldItemMainhand() == null)
			fakePlayer.setHeldItem(EnumHand.MAIN_HAND, fishingRod);

	}

	private int getTicksCatchable(EntityFishHook hook) {
		return ReflectionHelper.getPrivateValue(EntityFishHook.class, hook, "ticksCatchable", "field_146045_ax");
	}

}
