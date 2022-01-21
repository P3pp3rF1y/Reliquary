package reliquary.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.function.Supplier;

public class SimpleConditionSerializer<T extends ICondition> implements IConditionSerializer<T> {
	private final ResourceLocation id;
	private final Supplier<T> instantiate;

	public SimpleConditionSerializer(ResourceLocation id, Supplier<T> instantiate) {
		this.id = id;
		this.instantiate = instantiate;
	}

	@Override
	public void write(JsonObject json, T value) {
		//noop
	}

	@Override
	public T read(JsonObject json) {
		return instantiate.get();
	}

	@Override
	public ResourceLocation getID() {
		return id;
	}
}
