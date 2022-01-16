package xreliquary.crafting.conditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

public class PedestalEnabledCondition implements ICondition {
	private static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "pedestal_enabled");
	public static final SimpleConditionSerializer<PedestalEnabledCondition> SERIALIZER = new SimpleConditionSerializer<>(ID, PedestalEnabledCondition::new);

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test() {
		return !Settings.COMMON.disable.disablePedestal.get();
	}
}
