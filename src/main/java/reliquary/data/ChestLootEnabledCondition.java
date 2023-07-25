package reliquary.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import reliquary.init.ModItems;
import reliquary.reference.Settings;

public class ChestLootEnabledCondition implements LootItemCondition {

	private ChestLootEnabledCondition() {
	}

	@Override
	public LootItemConditionType getType() {
		return ModItems.CHEST_LOOT_ENABLED_CONDITION.get();
	}

	@Override
	public boolean test(LootContext lootContext) {
		return Boolean.TRUE.equals(Settings.COMMON.chestLootEnabled.get());
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder implements LootItemCondition.Builder {
		@Override
		public LootItemCondition build() {
			return new ChestLootEnabledCondition();
		}
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ChestLootEnabledCondition> {
		@Override
		public void serialize(JsonObject object, ChestLootEnabledCondition instance, JsonSerializationContext ctx) {
			//nothing to serialize
		}

		@Override
		public ChestLootEnabledCondition deserialize(JsonObject object, JsonDeserializationContext ctx) {
			return new ChestLootEnabledCondition();
		}
	}
}
