package xreliquary.init;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.compat.ICompat;
import xreliquary.reference.Reference;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModCompat {
	private static ArrayList<ICompat> compats = new ArrayList<>();

	public static void registerModCompat() {
		//compats.add(new TCCompat()); // TODO add back when TC is updated
		//compats.add(new WailaCompat()); TODO waila
	}

	public static void loadCompat(ICompat.InitializationPhase phase, World world) {
		compats.stream().filter(compatibility -> Loader.isModLoaded(compatibility.getModId())).forEach(compatibility -> compatibility.loadCompatibility(phase, world));
	}

	@SubscribeEvent
	public static void worldLoad(WorldEvent.Load event) {
		loadCompat(ICompat.InitializationPhase.WORLD_LOAD, event.getWorld());
	}

}
