package xreliquary.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public interface IPlayerDeathHandler extends IPrioritizedHandler {
	boolean canApply(PlayerEntity player, LivingDeathEvent event);
	boolean apply(PlayerEntity player, LivingDeathEvent event);

}
