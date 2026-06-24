package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;

public class LootItemRandomChanceWithSeveringBonusCondition {
	public static LootItemCondition.Builder randomChanceAndSeveringBoost(HolderLookup.Provider registries, float baseChance, float perLevelBoost) {
		return () -> new LootItemRandomChanceWithEnchantedBonusCondition(baseChance, new LevelBasedValue.Linear(baseChance + perLevelBoost, perLevelBoost),
				registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ReliquaryEnchantmentProvider.SEVERING));
	}
}
