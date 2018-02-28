package xreliquary.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public interface IPlayerHurtHandler {
	boolean canApply(EntityPlayer player, LivingAttackEvent event);
	boolean apply(EntityPlayer player, LivingAttackEvent event);
	Priority getPriority();

	enum Priority {
		HIGHEST,
		HIGH,
		NORMAL,
		LOW,
		LOWEST
	}
}
