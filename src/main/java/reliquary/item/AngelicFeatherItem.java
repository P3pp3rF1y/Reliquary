package reliquary.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import reliquary.handler.CommonEventHandler;
import reliquary.handler.HandlerPriority;
import reliquary.handler.IPlayerHurtHandler;
import reliquary.init.ModItems;
import reliquary.item.util.ICuriosItem;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;

import javax.annotation.Nullable;

public class AngelicFeatherItem extends ItemBase implements ICuriosItem {
	public AngelicFeatherItem(Properties properties) {
		super(properties.stacksTo(1).setNoCombineRepair().rarity(Rarity.EPIC));
		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(Player player, LivingIncomingDamageEvent event) {
				return event.getSource() == player.damageSources().fall() && player.getFoodData().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.ANGELIC_FEATHER.get()) && player.fallDistance > 0.0F;
			}

			@Override
			public boolean apply(Player player, LivingIncomingDamageEvent event) {
				float hungerDamage = event.getAmount() * ((float) Config.COMMON.items.angelicFeather.hungerCostPercent.get() / 100F);
				player.causeFoodExhaustion(hungerDamage);
				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.HIGH;
			}
		});
	}

	// minor jump buff
	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
		int potency = this instanceof PhoenixDownItem
				? Config.COMMON.items.phoenixDown.leapingPotency.get()
				: Config.COMMON.items.angelicFeather.leapingPotency.get();
		if (potency == 0) {
			return;
		}
		if (entity instanceof Player player) {
			player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 2, potency, true, false));
		}
	}

	@Override
	public Type getCuriosType() {
		return Type.CHARM;
	}

	@Override
	public void onWornServerTick(ItemStack stack, ServerLevel serverLevel, LivingEntity player) {
		inventoryTick(stack, serverLevel, player, null);
	}
}
