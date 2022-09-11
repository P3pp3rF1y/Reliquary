package reliquary.crafting.conditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import reliquary.reference.Reference;
import reliquary.reference.Settings;

public class PassivePedestalEnabledCondition implements ICondition {
	private static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "passive_pedestal_enabled");
	public static final SimpleConditionSerializer<PassivePedestalEnabledCondition> SERIALIZER = new SimpleConditionSerializer<>(ID, PassivePedestalEnabledCondition::new);

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test(IContext context) {
		return !Settings.COMMON.disable.disablePassivePedestal.get();
	}
}
