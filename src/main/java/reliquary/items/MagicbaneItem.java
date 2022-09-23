package reliquary.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reliquary.Reliquary;
import reliquary.util.LanguageHelper;

import javax.annotation.Nullable;
import java.util.List;

public class MagicbaneItem extends SwordItem {
	private static final AttributeModifier SPEED_ATTRIBUTE = new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4, AttributeModifier.Operation.ADDITION);
	public MagicbaneItem() {
		super(Tiers.GOLD, 3, -2.4f, new Properties().durability(16).setNoRepair().tab(Reliquary.ITEM_GROUP).rarity(Rarity.EPIC));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack magicBane, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip", tooltip);
	}

	/**
	 * Returns the strength of the stack against a given block. 1.0F base,
	 * (Quality+1)*2 if correct blocktype, 1.5F if sword
	 */
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState blockState) {
		return blockState.getBlock() == Blocks.COBWEB ? 15.0F : 1.5F;
	}

	/**
	 * Current implementations of this method in child classes do not use the
	 * entry argument beside ev. They just raise the damage on the stack.
	 */
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		int random = target.level.random.nextInt(16);
		switch (random) {
			case 0, 1, 2, 3, 4 -> target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 2));
			case 5, 6, 7, 8, 9, 10, 11 -> target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
			case 12, 13 -> {
				target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2));
				target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 2));
			}
			case 14 -> {
				target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 2));
				target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 2));
			}
			default -> {
				//noop
			}
		}
		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		if (slot != EquipmentSlot.MAINHAND) {
			return ImmutableMultimap.of();
		}

		ListTag enchants = stack.getEnchantmentTags();
		int attackDamage = 4;
		for (int enchant = 0; enchant < enchants.size(); enchant++) {
			attackDamage += enchants.getCompound(enchant).getShort("lvl");
		}
		return ImmutableMultimap.of(
				Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION),
				Attributes.ATTACK_SPEED, SPEED_ATTRIBUTE
		);
	}
}
