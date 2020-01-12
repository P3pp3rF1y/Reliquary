package xreliquary.crafting;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

public class MobDropsCraftableCondition implements ICondition {
	private static final ResourceLocation NAME = new ResourceLocation(Reference.MOD_ID, "mob_drops_craftable");

	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		return Settings.COMMON.dropCraftingRecipesEnabled.get();
	}

	public static class Serializer implements IConditionSerializer<MobDropsCraftableCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, MobDropsCraftableCondition value) {
			//noop
		}

		@Override
		public MobDropsCraftableCondition read(JsonObject json) {
			return new MobDropsCraftableCondition();
		}

		@Override
		public ResourceLocation getID() {
			return NAME;
		}
	}
}
