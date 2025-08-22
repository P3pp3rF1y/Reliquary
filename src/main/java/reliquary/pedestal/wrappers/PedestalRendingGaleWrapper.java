package reliquary.pedestal.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import reliquary.api.IPedestal;
import reliquary.api.IPedestalActionItemWrapper;
import reliquary.init.ModEffects;
import reliquary.init.ModItems;
import reliquary.item.RendingGaleItem;
import reliquary.reference.Config;

import java.util.List;

public class PedestalRendingGaleWrapper implements IPedestalActionItemWrapper {
	private static final int SECONDS_BETWEEN_BUFF_CHECKS = 2;
	private static final int TICKS_BETWEEN_PUSH_PULL_CHECKS = 1;

	private int buffCheckCoolDown;
	private int pushPullCheckCoolDown;

	@Override
	public void update(ItemStack stack, Level level, IPedestal pedestal) {
		BlockPos pos = pedestal.getBlockPosition();
		RendingGaleItem rendingGale = (RendingGaleItem) stack.getItem();
		if (rendingGale.getMode(stack) == RendingGaleItem.Mode.FLIGHT) {
			if (buffCheckCoolDown <= 0) {
				buffPlayersWithFlight(stack, level, pos);
				buffCheckCoolDown = SECONDS_BETWEEN_BUFF_CHECKS * 20;
			} else {
				buffCheckCoolDown--;
			}
		} else if (rendingGale.getMode(stack) == RendingGaleItem.Mode.PUSH) {
			if (pushPullCheckCoolDown <= 0) {
				pushEntities(stack, level, pos, rendingGale, false);
				pushPullCheckCoolDown = TICKS_BETWEEN_PUSH_PULL_CHECKS;
			} else {
				pushPullCheckCoolDown--;
			}
		} else if (rendingGale.getMode(stack) == RendingGaleItem.Mode.PULL) {
			if (pushPullCheckCoolDown <= 0) {
				pushEntities(stack, level, pos, rendingGale, true);
				pushPullCheckCoolDown = TICKS_BETWEEN_PUSH_PULL_CHECKS;
			} else {
				pushPullCheckCoolDown--;
			}
		}
	}

	private void pushEntities(ItemStack stack, Level level, BlockPos pos, RendingGaleItem rendingGale, boolean b) {
		rendingGale.doRadialPush(level, pos.getX(), pos.getY(), pos.getZ(), null, b);
		ModItems.RENDING_GALE.get().useCharge(stack, (int) (TICKS_BETWEEN_PUSH_PULL_CHECKS / 20F * Config.COMMON.items.rendingGale.pedestalCostPerSecond.get()));
	}

	private void buffPlayersWithFlight(ItemStack stack, Level level, BlockPos pos) {
		int flightRange = Config.COMMON.items.rendingGale.pedestalFlightRange.get();

		if (ModItems.RENDING_GALE.get().getFeatherCount(stack) >= (RendingGaleItem.getChargeCost() * SECONDS_BETWEEN_BUFF_CHECKS)) {
			List<Player> players = level.getEntitiesOfClass(Player.class, new AABB((double) pos.getX() - flightRange, (double) pos.getY() - flightRange, (double) pos.getZ() - flightRange, (double) pos.getX() + flightRange, (double) pos.getY() + flightRange, (double) pos.getZ() + flightRange));

			if (!players.isEmpty()) {
				for (Player player : players) {
					player.addEffect(new MobEffectInstance(ModEffects.FLIGHT, 20 * 20, 0, false, false, true));
				}
				ModItems.RENDING_GALE.get().useCharge(stack, SECONDS_BETWEEN_BUFF_CHECKS * Config.COMMON.items.rendingGale.pedestalCostPerSecond.get());
			}
		}
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
