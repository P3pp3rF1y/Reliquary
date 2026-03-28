package reliquary.item;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import reliquary.Reliquary;
import reliquary.data.ReliquaryEnchantmentProvider;

import java.util.function.Consumer;

public class MagicbaneItem extends ItemBase implements ICreativeTabItemGenerator {
	private static final Identifier MAGICBANE_ENCHANTMENTS_BONUS_ID = Reliquary.getIdentifier("magicbane_enchantments_bonus");

	public MagicbaneItem(Properties properties) {
		super(properties.sword(ToolMaterial.GOLD, 4, -2.4f).durability(16).setNoCombineRepair().rarity(Rarity.EPIC));
		NeoForge.EVENT_BUS.addListener(this::adjustDamageBasedOnEnchantments);
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		itemConsumer.accept(new ItemStack(this));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
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
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		int random = target.level().getRandom().nextInt(16);
		switch (random) {
			case 0, 1, 2, 3, 4 -> target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 2));
			case 5, 6, 7, 8, 9, 10, 11 -> target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2));
			case 12, 13 -> {
				target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2));
				target.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 2));
			}
			case 14 -> {
				target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 2));
				target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 2));
			}
			default -> {
				//noop
			}
		}
		super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public int getEnchantmentLevel(net.minecraft.world.item.ItemInstance stack, Holder<Enchantment> enchantment) {
		if (enchantment.is(ReliquaryEnchantmentProvider.SEVERING)) {
			return super.getEnchantmentLevel(stack, enchantment) + 2;
		}

		return super.getEnchantmentLevel(stack, enchantment);
	}

	private void adjustDamageBasedOnEnchantments(ItemAttributeModifierEvent event) {
		ItemStack stack = event.getItemStack();
		if (!(stack.getItem() instanceof MagicbaneItem)) {
			return;
		}

		ItemEnchantments enchantments = stack.getTagEnchantments();
		float attackDamage = 0;
		for (Object2IntMap.Entry<Holder<Enchantment>> holderEntry : enchantments.entrySet()) {
			attackDamage += holderEntry.getIntValue();
		}
		event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(MAGICBANE_ENCHANTMENTS_BONUS_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
	}
}
