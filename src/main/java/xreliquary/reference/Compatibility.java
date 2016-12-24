package xreliquary.reference;

import net.minecraftforge.fml.common.Loader;

import java.util.HashMap;
import java.util.Map;

public class Compatibility {
	
	private static Map<String, Boolean> loadedMods = new HashMap<>();
	
	public static class MOD_ID {
		public static final String JEI = "jei";
		public static final String JER = "jeresources";
		public static final String BAUBLES = "baubles";
		//public static final String BOTANIA = "Botania";
		//public static final String THAUMCRAFT = "Thaumcraft";
		public static final String WAILA = "Waila";
		public static final String TINKERS_CONSTRUCT = "tconstruct";
		public static final String OPEN_BLOCKS = "OpenBlocks";
		public static final String ENDERIO = "EnderIO";
		//public static final String BLOOD_MAGIC = "BloodMagic";
	}
	
	public static boolean isLoaded(String modName) {
		if (!loadedMods.containsKey(modName)) {
			loadedMods.put(modName, Loader.isModLoaded(modName));
		}
		return loadedMods.get(modName);
	}
}
