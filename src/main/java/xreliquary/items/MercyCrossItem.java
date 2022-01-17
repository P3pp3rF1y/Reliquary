package xreliquary.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import xreliquary.Reliquary;

import javax.annotation.Nullable;
import java.util.List;

public class MercyCrossItem extends SwordItem {
	private static final String WEAPON_MODIFIER_NAME = "Weapon modifier";

	public MercyCrossItem() {
		super(Tiers.GOLD, 3, -2.4F, new Properties().stacksTo(1).durability(64).tab(Reliquary.ITEM_GROUP));
		MinecraftForge.EVENT_BUS.addListener(this::handleDamage);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack cross, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(new TranslatableComponent(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public float getDamage() {
		return 0.0F;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		if (slot == EquipmentSlot.MAINHAND) {
			return ImmutableMultimap.<Attribute, AttributeModifier>builder()
					.putAll(super.getAttributeModifiers(slot, stack))
					.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, WEAPON_MODIFIER_NAME, 6, AttributeModifier.Operation.ADDITION))
					.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, WEAPON_MODIFIER_NAME, -2.4000000953674316D, AttributeModifier.Operation.ADDITION))
					.build();
		} else {
			return super.getAttributeModifiers(slot, stack);
		}
	}

	private void handleDamage(AttackEntityEvent event) {
		if (event.getPlayer().level.isClientSide || !(event.getTarget() instanceof LivingEntity target)) {
			return;
		}

		if (event.getPlayer().getMainHandItem().getItem() != this) {
			return;
		}

		updateAttackDamageModifier(target, event.getPlayer());
	}

	private void updateAttackDamageModifier(LivingEntity target, Player player) {
		double dmg = isUndead(target) ? 12 : 6;
		AttributeInstance attackAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);

		//noinspection ConstantConditions
		if (attackAttribute != null &&
				(attackAttribute.getModifier(BASE_ATTACK_DAMAGE_UUID) == null || attackAttribute.getModifier(BASE_ATTACK_DAMAGE_UUID).getAmount() != dmg)) {
			attackAttribute.removeModifier(BASE_ATTACK_DAMAGE_UUID);
			attackAttribute.addTransientModifier(new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, WEAPON_MODIFIER_NAME, dmg, AttributeModifier.Operation.ADDITION));
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity monster) {
		if (monster instanceof Mob mob && isUndead(mob)) {
			monster.level.addParticle(ParticleTypes.EXPLOSION, monster.getX() + (player.level.random.nextFloat() - 0.5F), monster.getY() + (player.level.random.nextFloat() - 0.5F) + (monster.getBbHeight() / 2), monster.getZ() + (player.level.random.nextFloat() - 0.5F), 0.0F, 0.0F, 0.0F);
		}
		return super.onLeftClickEntity(stack, player, monster);
	}

	private boolean isUndead(LivingEntity e) {
		return e.getMobType() == MobType.UNDEAD;
	}
}
