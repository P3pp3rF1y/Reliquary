package reliquary.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;

public class LootItemRandomChanceWithSeveringBonusCondition {
	public static LootItemCondition.Builder randomChanceAndSeveringBoost(HolderGetter<Enchantment> enchantments, float baseChance, float perLevelBoost) {
		return () -> new LootItemRandomChanceWithEnchantedBonusCondition(baseChance, new LevelBasedValue.Linear(baseChance + perLevelBoost, perLevelBoost),
				enchantments.getOrThrow(ReliquaryEnchantmentProvider.SEVERING));
	}
}
