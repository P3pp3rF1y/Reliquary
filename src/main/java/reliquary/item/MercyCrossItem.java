package reliquary.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import java.util.List;
import java.util.function.Consumer;

public class MercyCrossItem extends SwordItem implements ICreativeTabItemGenerator {
	public MercyCrossItem() {
		super(Tiers.GOLD, new Properties().stacksTo(1).durability(64).rarity(Rarity.EPIC)
				.attributes(SwordItem.createAttributes(Tiers.GOLD, 6, -2.4f)));
		NeoForge.EVENT_BUS.addListener(this::handleDamage);
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		itemConsumer.accept(new ItemStack(this));
	}

	@Override
	public void appendHoverText(ItemStack cross, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
	}

	private void handleDamage(AttackEntityEvent event) {
		if (event.getEntity().level().isClientSide || !(event.getTarget() instanceof LivingEntity target)) {
			return;
		}

		if (event.getEntity().getMainHandItem().getItem() != this) {
			return;
		}

		updateAttackDamageModifier(target, event.getEntity());
	}

	private void updateAttackDamageModifier(LivingEntity target, Player player) {
		double dmg = isUndead(target) ? 12 : 6;
		AttributeInstance attackAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);

		//noinspection ConstantConditions
		if (attackAttribute != null &&
				(attackAttribute.getModifier(BASE_ATTACK_DAMAGE_ID) == null || attackAttribute.getModifier(BASE_ATTACK_DAMAGE_ID).amount() != dmg)) {
			attackAttribute.removeModifier(BASE_ATTACK_DAMAGE_ID);
			attackAttribute.addTransientModifier(new AttributeModifier(BASE_ATTACK_DAMAGE_ID, dmg, AttributeModifier.Operation.ADD_VALUE));
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity monster) {
		if (monster instanceof Mob mob && isUndead(mob)) {
			monster.level().addParticle(ParticleTypes.EXPLOSION, monster.getX() + (player.level().random.nextFloat() - 0.5F), monster.getY() + (player.level().random.nextFloat() - 0.5F) + (monster.getBbHeight() / 2), monster.getZ() + (player.level().random.nextFloat() - 0.5F), 0.0F, 0.0F, 0.0F);
		}
		return super.onLeftClickEntity(stack, player, monster);
	}

	private boolean isUndead(LivingEntity e) {
		return e.getType().is(EntityTypeTags.UNDEAD);
	}
}
