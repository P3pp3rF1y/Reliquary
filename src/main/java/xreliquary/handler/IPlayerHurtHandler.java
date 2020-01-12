package xreliquary.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public interface IPlayerHurtHandler extends IPrioritizedHandler {
	boolean canApply(PlayerEntity player, LivingAttackEvent event);
	boolean apply(PlayerEntity player, LivingAttackEvent event);
}
