package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import xreliquary.handler.CommonEventHandler;
import xreliquary.handler.HandlerPriority;
import xreliquary.handler.IPlayerHurtHandler;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;

public class AngelicFeatherItem extends ItemBase {
	public AngelicFeatherItem() {
		this("angelic_feather");

		CommonEventHandler.registerPlayerHurtHandler(new IPlayerHurtHandler() {
			@Override
			public boolean canApply(PlayerEntity player, LivingAttackEvent event) {
				return event.getSource() == DamageSource.FALL
						&& player.getFoodStats().getFoodLevel() > 0
						&& InventoryHelper.playerHasItem(player, ModItems.ANGELIC_FEATHER)
						&& player.fallDistance > 0.0F;
			}

			@Override
			public boolean apply(PlayerEntity player, LivingAttackEvent event) {
				float hungerDamage = event.getAmount() * ((float) Settings.COMMON.items.angelicFeather.hungerCostPercent.get() / 100F);
				player.addExhaustion(hungerDamage);
				return true;
			}

			@Override
			public HandlerPriority getPriority() {
				return HandlerPriority.HIGH;
			}
		});
	}

	// so it can be extended by phoenix down
	AngelicFeatherItem(String name) {
		super(name, new Properties().maxStackSize(1).setNoRepair().rarity(Rarity.EPIC));
	}

	// minor jump buff
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		int potency = this instanceof PhoenixDownItem ? Settings.COMMON.items.phoenixDown.leapingPotency.get() : Settings.COMMON.items.angelicFeather.leapingPotency.get();
		if(potency == 0) {
			return;
		}
		potency -= 1;
		if(entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 2, potency, true, false));
		}
	}
}
