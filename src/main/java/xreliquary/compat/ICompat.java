package xreliquary.compat;

import net.minecraft.world.World;

public interface ICompat {
	void loadCompatibility(InitializationPhase phase, World world);

	String getModId();

	enum InitializationPhase {
		PRE_INIT,
		INIT,
		POST_INIT,
		WORLD_LOAD
	}
}
