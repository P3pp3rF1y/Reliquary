package xreliquary.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import xreliquary.Reliquary;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nullable;
import java.util.List;

public class MercyCrossItem extends SwordItem {
	private static final String WEAPON_MODIFIER_NAME = "Weapon modifier";

	public MercyCrossItem() {
		super(ItemTier.GOLD, 3, -2.4F, new Properties().maxStackSize(1).maxDamage(64).group(Reliquary.ITEM_GROUP));
		MinecraftForge.EVENT_BUS.addListener(this::handleDamage);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack cross, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip", tooltip);
	}

	@Override
	public float getAttackDamage() {
		return 0.0F;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		if (slot == EquipmentSlotType.MAINHAND) {
			return ImmutableMultimap.<Attribute, AttributeModifier>builder()
					.putAll(super.getAttributeModifiers(slot, stack))
					.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, WEAPON_MODIFIER_NAME, 6, AttributeModifier.Operation.ADDITION))
					.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, WEAPON_MODIFIER_NAME, -2.4000000953674316D, AttributeModifier.Operation.ADDITION))
					.build();
		} else {
			return super.getAttributeModifiers(slot, stack);
		}
	}

	private void handleDamage(AttackEntityEvent event) {
		if (event.getPlayer().world.isRemote || !(event.getTarget() instanceof LivingEntity)) {
			return;
		}

		if (event.getPlayer().getHeldItemMainhand().getItem() != this) {
			return;
		}

		LivingEntity target = (LivingEntity) event.getTarget();

		updateAttackDamageModifier(target, event.getPlayer());
	}

	private void updateAttackDamageModifier(LivingEntity target, PlayerEntity player) {
		double dmg = isUndead(target) ? 12 : 6;
		ModifiableAttributeInstance attackAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);

		//noinspection ConstantConditions
		if (attackAttribute != null &&
				(attackAttribute.getModifier(ATTACK_DAMAGE_MODIFIER) == null || attackAttribute.getModifier(ATTACK_DAMAGE_MODIFIER).getAmount() != dmg)) {
			attackAttribute.removeModifier(ATTACK_DAMAGE_MODIFIER);
			attackAttribute.applyNonPersistentModifier(new AttributeModifier(ATTACK_DAMAGE_MODIFIER, WEAPON_MODIFIER_NAME, dmg, AttributeModifier.Operation.ADDITION));
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity monster) {
		if (monster instanceof MobEntity && isUndead((MobEntity) monster)) {
			monster.world.addParticle(ParticleTypes.EXPLOSION, monster.getPosX() + (random.nextFloat() - 0.5F), monster.getPosY() + (random.nextFloat() - 0.5F) + (monster.getHeight() / 2), monster.getPosZ() + (random.nextFloat() - 0.5F), 0.0F, 0.0F, 0.0F);
		}
		return super.onLeftClickEntity(stack, player, monster);
	}

	private boolean isUndead(LivingEntity e) {
		return e.getCreatureAttribute() == CreatureAttribute.UNDEAD;
	}
}
