package xreliquary.util;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class LanguageHelper {
	
	public static void loadLanguages(String[] languages) {
		for(String language : languages) {
			LanguageRegistry.instance().loadLocalization("/assets/xreliquary/lang/" + language + ".lang", language, false);
		}
	}
	
	public static String getLocalization(String key) {
		if(LanguageRegistry.instance().getStringLocalization(key) != null)
			return LanguageRegistry.instance().getStringLocalization(key);
		return LanguageRegistry.instance().getStringLocalization(key, "en_US");
	}

}
