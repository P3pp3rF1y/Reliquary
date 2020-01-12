package xreliquary.pedestal.wrappers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.init.ModItems;
import xreliquary.init.ModPotions;
import xreliquary.items.RendingGaleItem;
import xreliquary.reference.Settings;

import java.util.List;

public class PedestalRendingGaleWrapper implements IPedestalActionItemWrapper {
	private static final int SECONDS_BETWEEN_BUFF_CHECKS = 2;
	private static final int TICKS_BETWEEN_PUSH_PULL_CHECKS = 1;

	private int buffCheckCoolDown;
	private int pushPullCheckCoolDown;

	@Override
	public void update( ItemStack stack, IPedestal pedestal) {
		World world = pedestal.getTheWorld();
		BlockPos pos = pedestal.getBlockPos();
		RendingGaleItem rendingGale = (RendingGaleItem) stack.getItem();
		if(rendingGale.getMode(stack).equals("flight")) {
			if(buffCheckCoolDown <= 0) {
				buffPlayersWithFlight(stack, world, pos);
				buffCheckCoolDown = SECONDS_BETWEEN_BUFF_CHECKS * 20;
			} else {
				buffCheckCoolDown--;
			}
		} else if(rendingGale.getMode(stack).equals("push")) {
			if(pushPullCheckCoolDown <= 0) {
				pushEntities(stack, world, pos, rendingGale, false);
				pushPullCheckCoolDown = TICKS_BETWEEN_PUSH_PULL_CHECKS;
			} else {
				pushPullCheckCoolDown--;
			}
		} else if(rendingGale.getMode(stack).equals("pull")) {
			if(pushPullCheckCoolDown <= 0) {
				pushEntities(stack, world, pos, rendingGale, true);
				pushPullCheckCoolDown = TICKS_BETWEEN_PUSH_PULL_CHECKS;
			} else {
				pushPullCheckCoolDown--;
			}
		}
	}

	private void pushEntities(ItemStack stack, World world, BlockPos pos, RendingGaleItem rendingGale, boolean b) {
		rendingGale.doRadialPush(world, pos.getX(), pos.getY(), pos.getZ(), null, b);
		ModItems.RENDING_GALE.setFeatherCount(stack, ModItems.RENDING_GALE.getFeatherCount(stack) - (int) (TICKS_BETWEEN_PUSH_PULL_CHECKS / 20F * Settings.COMMON.items.rendingGale.pedestalCostPerSecond.get()), true);
	}

	private void buffPlayersWithFlight(ItemStack stack, World world, BlockPos pos) {
		int flightRange = Settings.COMMON.items.rendingGale.pedestalFlightRange.get();

		if(ModItems.RENDING_GALE.getFeatherCount(stack) >= (RendingGaleItem.getChargeCost() * SECONDS_BETWEEN_BUFF_CHECKS)) {
			List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB((double) pos.getX() - flightRange, (double) pos.getY() - flightRange, (double) pos.getZ() - flightRange, (double) pos.getX() + flightRange, (double) pos.getY() + flightRange, (double) pos.getZ() + flightRange));

			if(!players.isEmpty()) {
				for(PlayerEntity player : players) {
					player.addPotionEffect(new EffectInstance(ModPotions.potionFlight, 20 * 20));
				}
				ModItems.RENDING_GALE.setFeatherCount(stack, ModItems.RENDING_GALE.getFeatherCount(stack) - (SECONDS_BETWEEN_BUFF_CHECKS * Settings.COMMON.items.rendingGale.pedestalCostPerSecond.get()), true);
			}
		}
	}

	@Override
	public void onRemoved( ItemStack stack, IPedestal pedestal) {
		//noop
	}

	@Override
	public void stop( ItemStack stack, IPedestal pedestal) {
		//noop
	}
}
