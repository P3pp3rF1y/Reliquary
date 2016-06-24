package xreliquary.init;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.compat.ICompat;
import xreliquary.compat.jer.JERCompat;
import xreliquary.compat.waila.WailaCompat;

import java.util.ArrayList;

public class ModCompat {
	private static ArrayList<ICompat> compats = new ArrayList<ICompat>();

	public static void registerModCompat() {
		compats.add(new JERCompat());
		//compats.add(new TCCompat()); // TODO add back when TC is updated
		compats.add(new WailaCompat());
	}

	public static void loadCompat(ICompat.InitializationPhase phase, World world) {
		for(ICompat compatibility : compats)
			if(Loader.isModLoaded(compatibility.getModId()))
				compatibility.loadCompatibility(phase, world);
	}

	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event) {
		loadCompat(ICompat.InitializationPhase.WORLD_LOAD, event.getWorld());
	}

}
