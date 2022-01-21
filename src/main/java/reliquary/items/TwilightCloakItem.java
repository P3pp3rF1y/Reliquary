package reliquary.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import reliquary.items.util.ICuriosItem;
import reliquary.reference.Settings;
import reliquary.util.InventoryHelper;
import reliquary.util.MobHelper;

public class TwilightCloakItem extends ToggleableItem implements ICuriosItem {
	public TwilightCloakItem() {
		super(new Properties().stacksTo(1));
		MinecraftForge.EVENT_BUS.addListener(this::onEntityTargetedEvent);
		MinecraftForge.EVENT_BUS.addListener(this::onLivingUpdate);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public void inventoryTick(ItemStack twilightCloak, Level world, Entity entity, int itemSlot, boolean isSelected) {
		if (!(entity instanceof Player)) {
			return;
		}

		updateInvisibility(twilightCloak, (Player) entity);
	}

	private void updateInvisibility(ItemStack twilightCloak, Player player) {
		if (!isEnabled(twilightCloak)) {
			return;
		}

		//toggled effect, makes player invisible based on light level (configurable)

		if (player.level.getMaxLocalRawBrightness(player.blockPosition()) > Settings.COMMON.items.twilightCloak.maxLightLevel.get()) {
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

	private void onEntityTargetedEvent(LivingSetAttackTargetEvent event) {
		doTwilightCloakCheck(event);
	}

	private void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		doTwilightCloakCheck(event);
	}

	private void doTwilightCloakCheck(LivingEvent event) {
		if (event.getEntity() instanceof Mob entityLiving) {
			if (!(entityLiving.getTarget() instanceof Player player)) {
				return;
			}

			if (!InventoryHelper.playerHasItem(player, this, true, ICuriosItem.Type.BODY) || player.level.getMaxLocalRawBrightness(player.blockPosition()) > Settings.COMMON.items.twilightCloak.maxLightLevel.get()) {
				return;
			}

			MobHelper.resetTarget(entityLiving);
		}
	}
}
