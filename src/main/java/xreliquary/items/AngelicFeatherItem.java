package xreliquary.items;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.HandlerPriority;
import xreliquary.handler.IPlayerHurtHandler;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;

public class AngelicFeatherItem extends ItemBase {
	public AngelicFeatherItem() {
		super(new Properties().stacksTo(1).setNoRepair().rarity(Rarity.EPIC));
		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(Player player, LivingAttackEvent event) {
				return event.getSource() == DamageSource.FALL
						&& player.getFoodData().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.ANGELIC_FEATHER.get())
						&& player.fallDistance > 0.0F;
			}

			@Override
			public boolean apply(Player player, LivingAttackEvent event) {
				float hungerDamage = event.getAmount() * ((float) Settings.COMMON.items.angelicFeather.hungerCostPercent.get() / 100F);
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
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
		int potency = this instanceof PhoenixDownItem ? Settings.COMMON.items.phoenixDown.leapingPotency.get() : Settings.COMMON.items.angelicFeather.leapingPotency.get();
		if (potency == 0) {
			return;
		}
		if (entity instanceof Player player) {
			player.addEffect(new MobEffectInstance(MobEffects.JUMP, 2, potency, true, false));
		}
	}
}
