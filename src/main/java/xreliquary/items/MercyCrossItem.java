package xreliquary.items;

import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nullable;
import java.util.List;

public class MercyCrossItem extends SwordItem {
	private static final String WEAPON_MODIFIER_NAME = "Weapon modifier";

	public MercyCrossItem() {
		super(ItemTier.GOLD, 3, -2.4F, new Properties().maxStackSize(1).maxDamage(64).group(Reliquary.ITEM_GROUP));
		setRegistryName(Reference.MOD_ID, "mercy_cross");
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public void addInformation(ItemStack cross, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		LanguageHelper.formatTooltip(getRegistryName() + ".tooltip", tooltip);
	}

	@Override
	public float getAttackDamage() {
		return 0.0F;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EquipmentSlotType.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, WEAPON_MODIFIER_NAME, 6, AttributeModifier.Operation.ADDITION));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, WEAPON_MODIFIER_NAME, -2.4000000953674316D, AttributeModifier.Operation.ADDITION));
		}

		return multimap;
	}

	public void updateAttackDamageModifier(LivingEntity target, PlayerEntity player) {
		double dmg = isUndead(target) ? 12 : 6;
		IAttributeInstance attackAttribute = player.getAttributes().getAttributeInstanceByName(SharedMonsterAttributes.ATTACK_DAMAGE.getName());

		//noinspection ConstantConditions
		if (attackAttribute != null &&
				(attackAttribute.getModifier(ATTACK_DAMAGE_MODIFIER) == null || attackAttribute.getModifier(ATTACK_DAMAGE_MODIFIER).getAmount() != dmg)) {
			attackAttribute.removeModifier(ATTACK_DAMAGE_MODIFIER);
			attackAttribute.applyModifier(new AttributeModifier(ATTACK_DAMAGE_MODIFIER, WEAPON_MODIFIER_NAME, dmg, AttributeModifier.Operation.ADDITION));
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity monster) {
		if (monster instanceof MobEntity && isUndead((MobEntity) monster)) {
			monster.world.addParticle(ParticleTypes.EXPLOSION, monster.posX + (random.nextFloat() - 0.5F), monster.posY + (random.nextFloat() - 0.5F) + (monster.getHeight() / 2), monster.posZ + (random.nextFloat() - 0.5F), 0.0F, 0.0F, 0.0F);
		}
		return super.onLeftClickEntity(stack, player, monster);
	}

	private boolean isUndead(LivingEntity e) {
		return e.getCreatureAttribute() == CreatureAttribute.UNDEAD;
	}
}
