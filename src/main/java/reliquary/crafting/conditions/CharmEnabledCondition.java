package reliquary.crafting.conditions;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import reliquary.reference.Config;

public class CharmEnabledCondition implements ICondition {
	private static final SpawnEggEnabledCondition INSTANCE = new SpawnEggEnabledCondition();
	public static final MapCodec<SpawnEggEnabledCondition> CODEC = MapCodec.unit(INSTANCE).stable();

	@Override
	public boolean test(ICondition.IContext context) {
		return !Config.COMMON.disable.disableCharms.get();
	}

	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}
}
