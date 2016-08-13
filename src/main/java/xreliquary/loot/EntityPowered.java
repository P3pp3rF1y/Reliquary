package xreliquary.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import jeresources.api.conditionals.Conditional;
import jeresources.api.conditionals.ICustomEntityProperty;
import jeresources.api.drop.LootDrop;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraftforge.fml.common.Optional;
import xreliquary.reference.Compatibility;
import java.util.Random;

@Optional.Interface(iface = "jeresources.api.conditionals.ICustomEntityProperty", modid = Compatibility.MOD_ID.JER, striprefs = true)
public class EntityPowered implements EntityProperty, ICustomEntityProperty {

	private final boolean powered;

	public EntityPowered(boolean powered) {
		this.powered = powered;
	}

	@Override
	public boolean testProperty(Random random, Entity entityIn) {
		if (!(entityIn instanceof EntityCreeper))
			return false;

		EntityCreeper creeper = (EntityCreeper) entityIn;

		return creeper.getPowered();
	}

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.JER)
	public void applyProperty(LootDrop lootDrop) {
		lootDrop.addConditional(Conditional.powered);
	}

	public static class Serializer extends EntityProperty.Serializer<EntityPowered> {

		public Serializer() {
			super(new ResourceLocation("powered"), EntityPowered.class);
		}

		@Override
		public JsonElement serialize(EntityPowered property, JsonSerializationContext serializationContext) {
			return new JsonPrimitive(property.powered);
		}

		@Override
		public EntityPowered deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			return new EntityPowered(JsonUtils.getBoolean(element, "powered"));
		}
	}
}
