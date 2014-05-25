package xreliquary.util;

import net.minecraft.util.StatCollector;
import xreliquary.lib.Colors;

import java.util.HashMap;
import java.util.Map;

public class LanguageHelper {

    public static Map<String, String> preprocesssed = new HashMap<String, String>();

	public static String getLocalization(String key) {
        String localization = getLocalization(key, true);

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

            preprocesssed.put(key, localization);
        } else if(preprocesssed.containsKey(key)) {
            return preprocesssed.get(key);
        }
        return localization;
	}

    private static String getLocalization(String key, boolean fallback) {
        String localization = StatCollector.translateToLocal(key);
        if(localization.equals(key) && fallback) {
            localization = StatCollector.translateToFallback(key);
        }
        return localization;
    }

}
