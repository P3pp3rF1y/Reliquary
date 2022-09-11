package reliquary.crafting.conditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import reliquary.reference.Reference;
import reliquary.reference.Settings;

public class AlkahestryEnabledCondition implements ICondition {
	private static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "alkahestry_enabled");
	public static final SimpleConditionSerializer<AlkahestryEnabledCondition> SERIALIZER = new SimpleConditionSerializer<>(ID, AlkahestryEnabledCondition::new);

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test(IContext context) {
		return !Settings.COMMON.disable.disableAlkahestry.get();
	}
}
