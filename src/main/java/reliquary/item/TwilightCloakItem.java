package reliquary.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import reliquary.item.util.ICuriosItem;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;
import reliquary.util.MobHelper;

import javax.annotation.Nullable;

public class TwilightCloakItem extends ToggleableItem implements ICuriosItem {
	public TwilightCloakItem() {
		super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
		NeoForge.EVENT_BUS.addListener(this::onEntityTargetedEvent);
		NeoForge.EVENT_BUS.addListener(this::onLivingUpdate);
	}

	@Override
	public void inventoryTick(ItemStack twilightCloak, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide() || !(entity instanceof Player player) || player.isSpectator()) {
			return;
		}

		updateInvisibility(twilightCloak, (Player) entity);
	}

	private void updateInvisibility(ItemStack twilightCloak, Player player) {
		if (!isEnabled(twilightCloak)) {
			return;
		}

		//toggled effect, makes player invisible based on light level (configurable)

		if (player.level().getMaxLocalRawBrightness(player.blockPosition()) > Config.COMMON.items.twilightCloak.maxLightLevel.get()) {
			return;
		}

		//checks if the effect would do anything. Literally all this does is make the player invisible. It doesn't interfere with mob AI.
		//for that, we're attempting to use an event handler.
		MobEffectInstance quickInvisibility = new MobEffectInstance(MobEffects.INVISIBILITY, 2, 0, false, false);
		player.addEffect(quickInvisibility);
	}

	@Override
	public ICuriosItem.Type getCuriosType() {
		return Type.BODY;
	}

	@Override
	public void onWornTick(ItemStack twilightCloak, LivingEntity player) {
		updateInvisibility(twilightCloak, (Player) player);
	}

	private void onEntityTargetedEvent(LivingChangeTargetEvent event) {
		if (shouldResetTarget(event.getNewAboutToBeSetTarget())) {
			event.setCanceled(true);
		}
	}

	private void onLivingUpdate(EntityTickEvent.Pre event) {
		doTwilightCloakCheck(event);
	}

	private void doTwilightCloakCheck(EntityTickEvent.Pre event) {
		if (event.getEntity() instanceof Mob entityLiving && shouldResetTarget(entityLiving.getTarget())) {
			MobHelper.resetTarget(entityLiving);
		}
	}

	private boolean shouldResetTarget(@Nullable Entity target) {
		if (!(target instanceof Player player)) {
			return false;
		}

		return InventoryHelper.playerHasItem(player, this, true) && player.level().getMaxLocalRawBrightness(player.blockPosition()) <= Config.COMMON.items.twilightCloak.maxLightLevel.get();
	}
}
