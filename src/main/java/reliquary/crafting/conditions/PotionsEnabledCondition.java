package reliquary.crafting.conditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import reliquary.reference.Reference;
import reliquary.reference.Settings;

public class PotionsEnabledCondition implements ICondition {
	private static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "potions_enabled");
	public static final SimpleConditionSerializer<PotionsEnabledCondition> SERIALIZER = new SimpleConditionSerializer<>(ID, PotionsEnabledCondition::new);

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test() {
		return !Settings.COMMON.disable.disablePotions.get();
	}
}
