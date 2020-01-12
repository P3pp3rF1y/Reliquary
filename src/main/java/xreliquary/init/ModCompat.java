package xreliquary.init;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import xreliquary.compat.ICompat;
import xreliquary.compat.waila.WailaCompat;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Reference;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCompat {
	private ModCompat() {}

	private static ArrayList<ICompat> compats = new ArrayList<>();

	public static void registerModCompat() {
		compats.add(new WailaCompat());
		compats.add(new WailaCompat() {
			@Override
			public String getModId() {
				return Compatibility.MOD_ID.HWYLA;
			}
		});
	}

	public static void loadCompats() {
		compats.stream().filter(compatibility -> ModList.get().isLoaded(compatibility.getModId())).forEach(ICompat::loadCompatibility);
	}
}
