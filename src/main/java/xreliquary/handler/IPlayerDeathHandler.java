package xreliquary.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public interface IPlayerDeathHandler extends IPrioritizedHandler {
	boolean canApply(EntityPlayer player, LivingDeathEvent event);
	boolean apply(EntityPlayer player, LivingDeathEvent event);

}
