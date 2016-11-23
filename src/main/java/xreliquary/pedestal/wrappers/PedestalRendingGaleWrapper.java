package xreliquary.pedestal.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.init.ModItems;
import xreliquary.init.ModPotions;
import xreliquary.items.ItemRendingGale;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;
import java.util.List;

public class PedestalRendingGaleWrapper implements IPedestalActionItemWrapper {
	private static final int SECONDS_BETWEEN_BUFF_CHECKS = 2;
	private static final int TICKS_BETWEEN_PUSH_PULL_CHECKS = 1;

	private int buffCheckCoolDown;
	private int pushPullCheckCoolDown;

	@Override
	public void update(@Nonnull ItemStack stack, IPedestal pedestal) {
		World world = pedestal.getTheWorld();
		BlockPos pos = pedestal.getBlockPos();
		ItemRendingGale rendingGale = (ItemRendingGale) stack.getItem();
		if(rendingGale.getMode(stack).equals("flight")) {
			if(buffCheckCoolDown <= 0) {

				int flightRange = Settings.RendingGale.pedestalFlightRange;

				if(ModItems.rendingGale.getFeatherCount(stack) >= (ItemRendingGale.getChargeCost() * SECONDS_BETWEEN_BUFF_CHECKS)) {
					List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - flightRange, pos.getY() - flightRange, pos.getZ() - flightRange, pos.getX() + flightRange, pos.getY() + flightRange, pos.getZ() + flightRange));

					if(!players.isEmpty()) {
						for(EntityPlayer player : players) {
							player.addPotionEffect(new PotionEffect(ModPotions.potionFlight, 20 * 20));
						}
						ModItems.rendingGale.setFeatherCount(stack, ModItems.rendingGale.getFeatherCount(stack) - (SECONDS_BETWEEN_BUFF_CHECKS * Settings.RendingGale.pedestalCostPerSecond));
					}
				}
				buffCheckCoolDown = SECONDS_BETWEEN_BUFF_CHECKS * 20;
			} else {
				buffCheckCoolDown--;
			}
		} else if(rendingGale.getMode(stack).equals("push")) {
			if(pushPullCheckCoolDown <= 0) {
				rendingGale.doRadialPush(world, pos.getX(), pos.getY(), pos.getZ(), null, false);
				pushPullCheckCoolDown = TICKS_BETWEEN_PUSH_PULL_CHECKS;
			} else {
				pushPullCheckCoolDown--;
			}
		} else if(rendingGale.getMode(stack).equals("pull")) {
			if(pushPullCheckCoolDown <= 0) {
				rendingGale.doRadialPush(world, pos.getX(), pos.getY(), pos.getZ(), null, true);
				pushPullCheckCoolDown = TICKS_BETWEEN_PUSH_PULL_CHECKS;
			} else {
				pushPullCheckCoolDown--;
			}
		}
	}

	@Override
	public void onRemoved(@Nonnull ItemStack stack, IPedestal pedestal) {
	}

	@Override
	public void stop(@Nonnull ItemStack stack, IPedestal pedestal) {
	}
}
