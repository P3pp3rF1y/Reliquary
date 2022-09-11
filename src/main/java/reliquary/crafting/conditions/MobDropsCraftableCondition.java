package reliquary.crafting.conditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import reliquary.reference.Reference;
import reliquary.reference.Settings;

public class MobDropsCraftableCondition implements ICondition {
	private static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "mob_drops_craftable");
	public static final SimpleConditionSerializer<MobDropsCraftableCondition> SERIALIZER = new SimpleConditionSerializer<>(ID, MobDropsCraftableCondition::new);

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	@Override
	public boolean test(IContext context) {
		return Settings.COMMON.dropCraftingRecipesEnabled.get();
	}
}
