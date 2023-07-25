package reliquary.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import reliquary.init.ModItems;
import reliquary.reference.Settings;

public class EntityLootEnabledCondition implements LootItemCondition {

	private EntityLootEnabledCondition() {
	}

	@Override
	public LootItemConditionType getType() {
		return ModItems.ENTITY_LOOT_ENABLED_CONDITION.get();
	}

	@Override
	public boolean test(LootContext lootContext) {
		return Boolean.TRUE.equals(Settings.COMMON.mobDropsEnabled.get());
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder implements LootItemCondition.Builder {
		@Override
		public LootItemCondition build() {
			return new EntityLootEnabledCondition();
		}
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<EntityLootEnabledCondition> {
		@Override
		public void serialize(JsonObject object, EntityLootEnabledCondition instance, JsonSerializationContext ctx) {
			//nothing to serialize
		}

		@Override
		public EntityLootEnabledCondition deserialize(JsonObject object, JsonDeserializationContext ctx) {
			return new EntityLootEnabledCondition();
		}
	}
}
