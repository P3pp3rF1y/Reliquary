package reliquary.crafting.conditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import reliquary.reference.Reference;
import reliquary.reference.Settings;

public class HandgunEnabledCondition implements ICondition {
	private static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "handgun_enabled");
	public static final SimpleConditionSerializer<HandgunEnabledCondition> SERIALIZER = new SimpleConditionSerializer<>(ID, HandgunEnabledCondition::new);

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test(IContext context) {
		return !Settings.COMMON.disable.disableHandgun.get();
	}
}
