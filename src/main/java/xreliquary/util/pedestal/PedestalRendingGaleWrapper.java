package xreliquary.util.pedestal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.init.ModPotions;
import xreliquary.items.ItemRendingGale;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;

import java.util.List;

public class PedestalRendingGaleWrapper implements IPedestalActionItemWrapper {
	private static final int SECONDS_BETWEEN_CHECKS = 2;

	private int buffCheckCoolDown;

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		if (buffCheckCoolDown <= 0) {
			World world = pedestal.getTheWorld();
			BlockPos pos = pedestal.getBlockPos();
			ItemRendingGale rendingGale = (ItemRendingGale) stack.getItem();

			int flightRange = Settings.RendingGale.pedestalFlightRange;

			if (NBTHelper.getInteger("feathers", stack) >= (rendingGale.getChargeCost() * SECONDS_BETWEEN_CHECKS)) {
				if(rendingGale.getMode(stack).equals("flight")) {
					List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.getX() - flightRange, pos.getY() - flightRange, pos.getZ() - flightRange, pos.getX() + flightRange, pos.getY() + flightRange, pos.getZ() + flightRange));

					if (!players.isEmpty()) {
						for (EntityPlayer player : players) {
							player.addPotionEffect(new PotionEffect(ModPotions.potionFlight.getId(), 20 * 20));
						}
						NBTHelper.setInteger("feathers", stack, NBTHelper.getInteger("feathers", stack) - (SECONDS_BETWEEN_CHECKS * Settings.RendingGale.pedestalCostPerSecond));
					}
				}
			}
			buffCheckCoolDown = SECONDS_BETWEEN_CHECKS * 20;
		} else {
			buffCheckCoolDown--;
		}
	}
}
