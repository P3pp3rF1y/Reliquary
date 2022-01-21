package reliquary.init;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public class RandomChanceLootingSeveringCondition implements LootItemCondition {
	public static final LootItemConditionType LOOT_CONDITION_TYPE = new LootItemConditionType(new RandomChanceLootingSeveringCondition.Serializer());

	final float percent;
	final float lootingMultiplier;
	private final float severingMultiplier;

	RandomChanceLootingSeveringCondition(float percent, float lootingMultiplier, float severingMultiplier) {
		this.percent = percent;
		this.lootingMultiplier = lootingMultiplier;
		this.severingMultiplier = severingMultiplier;
	}

	public LootItemConditionType getType() {
		return LOOT_CONDITION_TYPE;
	}

	@Override
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return Set.of(LootContextParams.KILLER_ENTITY);
	}

	public boolean test(LootContext lootContext) {
		int i = lootContext.getLootingModifier();
		return lootContext.getRandom().nextFloat() < percent + i * lootingMultiplier + getSeveringModifier(lootContext) * severingMultiplier;
	}

	private int getSeveringModifier(LootContext lootContext) {
		Entity killer = lootContext.getParamOrNull(LootContextParams.KILLER_ENTITY);
		if (!(killer instanceof LivingEntity livingEntity)) {
			return 0;
		}
		Enchantment severingEnchantment = ModEnchantments.SEVERING.get();
		Iterable<ItemStack> iterable = severingEnchantment.getSlotItems(livingEntity).values();
		int severingLevel = 0;

		for (ItemStack itemstack : iterable) {
			int j = EnchantmentHelper.getItemEnchantmentLevel(severingEnchantment, itemstack);
			if (itemstack.getItem() == ModItems.MAGICBANE.get()) {
				j += 2;
			}

			if (j > severingLevel) {
				severingLevel = j;
			}
		}

		return severingLevel;
	}

	public static Builder randomChanceLootingSevering(float percent, float lootingMultiplier, float severingMultiplier) {
		return () -> new RandomChanceLootingSeveringCondition(percent, lootingMultiplier, severingMultiplier);
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<RandomChanceLootingSeveringCondition> {
		public void serialize(JsonObject jsonObject, RandomChanceLootingSeveringCondition condition, JsonSerializationContext context) {
			jsonObject.addProperty("chance", condition.percent);
			jsonObject.addProperty("looting_multiplier", condition.lootingMultiplier);
			jsonObject.addProperty("severing_multiplier", condition.severingMultiplier);
		}

		public RandomChanceLootingSeveringCondition deserialize(JsonObject jsonObject, JsonDeserializationContext context) {
			return new RandomChanceLootingSeveringCondition(GsonHelper.getAsFloat(jsonObject, "chance"), GsonHelper.getAsFloat(jsonObject, "looting_multiplier")
					, GsonHelper.getAsFloat(jsonObject, "severing_multiplier"));
		}
	}
}
