package reliquary.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import reliquary.handler.CommonEventHandler;
import reliquary.handler.HandlerPriority;
import reliquary.handler.IPlayerHurtHandler;
import reliquary.init.ModItems;
import reliquary.reference.Settings;
import reliquary.util.InventoryHelper;

public class KrakenShellItem extends ItemBase {
	public KrakenShellItem() {
		super(new Properties().stacksTo(1));

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(Player player, LivingAttackEvent event) {
				return event.getSource() == player.damageSources().drown()
						&& player.getFoodData().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.KRAKEN_SHELL.get());
			}

			@Override
			public boolean apply(Player player, LivingAttackEvent event) {
				float hungerDamage = event.getAmount() * ((float) Settings.COMMON.items.krakenShell.hungerCostPercent.get() / 100F);
				player.causeFoodExhaustion(hungerDamage);
				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.HIGH;
			}
		});
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	// checks to see if the player is in water. If so, give them some minor
	// buffs.
	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.getGameTime() % 3 != 0) {
			return;
		}

		if (entity instanceof Player player && player.isInWater()) {
			player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 5, 0, true, false));
			player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5, 0, true, false));
			player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false));
		}
	}
}
