package xreliquary.util;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import xreliquary.lib.Colors;

import java.util.ArrayList;
import java.util.List;

public class LanguageHelper {

    public static List<String> languages = new ArrayList<String>();
	
	public static void loadLanguages(String[] par1) {
		for(String language : par1) {
            languages.add(language);
			LanguageRegistry.instance().loadLocalization("/assets/xreliquary/lang/" + language + ".lang", language, false);
		}
	}
	
	public static String getLocalization(String key) {
        String localization;
        //??? dead code here in this if statement.
		//if(LanguageRegistry.instance().getStringLocalization(key) != null)
			//localization = LanguageRegistry.instance().getStringLocalization(key);
		localization = LanguageRegistry.instance().getStringLocalization(key, "en_US");
        if(localization.contains("{{!")) {
            while(localization.contains("{{!")) {
                int startingIndex = localization.indexOf("{{!");
                int endingIndex = localization.substring(startingIndex).indexOf("}}") + startingIndex;
                String fragment = localization.substring(startingIndex + 3, endingIndex);

                try {
                    String replacement = (String) Colors.class.getField("UNICODE_" + fragment.toUpperCase()).get(null);
                    localization = localization.substring(0, startingIndex) + replacement + localization.substring(endingIndex + 2);
                } catch(Exception e) {
                    localization = localization.substring(0, startingIndex) + localization.substring(endingIndex + 2);
                }
            }

            if(languages.contains(FMLCommonHandler.instance().getCurrentLanguage())) {
                LanguageRegistry.instance().addStringLocalization(key, FMLCommonHandler.instance().getCurrentLanguage(), localization);
            } else {
                LanguageRegistry.instance().addStringLocalization(key, "en_US", localization);
            }

            //this is a stack overflow. <_<
            //return getLocalization(key);
        //} else {
        }
        return localization;
        //}
	}

}
