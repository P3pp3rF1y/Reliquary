package xreliquary.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.reference.Reference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A language file 'preprocessor', I guess you could call it. It just injects globals right now.
 *
 * @author TheMike
 * @author x3n0ph0b3
 */
@SideOnly(Side.CLIENT)
public class LanguageHelper {

	private static Map<String, String> preprocesssed = new HashMap<>();
	public static Map<String, String> globals = new HashMap<>();

	/**
	 * Gets the preprocessed version of the localized string. Preprocessing will only be ran once, not on every call.
	 *
	 * @param key The localization key.
	 * @return A preprocessed localized string. If your current language dosen't have a localized string, it defaults to en_US.
	 */
	public static String getLocalization(String key, Object... parameters) {
		String localization = getLocalization(key, true, parameters);

		if(preprocesssed.containsKey(key)) {
			return preprocesssed.get(key);
		} else if(localization.contains("{{!")) {
			while(localization.contains("{{!")) {
				int startingIndex = localization.indexOf("{{!");
				int endingIndex = localization.substring(startingIndex).indexOf("}}") + startingIndex;
				String fragment = localization.substring(startingIndex + 3, endingIndex);

				try {
					String replacement = globals.get(fragment.toLowerCase());
					localization = localization.substring(0, startingIndex) + replacement + localization.substring(endingIndex + 2);
				}
				catch(Exception e) {
					localization = localization.substring(0, startingIndex) + localization.substring(endingIndex + 2);
				}
			}

			preprocesssed.put(key, localization);
		}
		return localization;
	}

	private static String getLocalization(String key, boolean fallback, Object... parameters) {
		//noinspection deprecation
		String localization = I18n.format(Reference.MOD_ID + "." + key, parameters);
		if(localization.equals(key) && fallback) {
			//noinspection deprecation
			localization = I18n.format(Reference.MOD_ID + "." + key, parameters);
		}
		return localization;
	}

	public static void formatTooltip(String langName, List<String> list) {
		formatTooltip(langName, ImmutableMap.of(), list);
	}
	public static void formatTooltip(String langName, ImmutableMap<String, String> toFormat, List<String> list) {
		String langTooltip = LanguageHelper.getLocalization(langName);
		if(langTooltip == null || langTooltip.equals(langName))
			return;
		if(toFormat != null) {
			for(Map.Entry<String, String> toReplace : toFormat.entrySet()) {
				langTooltip = langTooltip.replace("{{" + toReplace.getKey() + "}}", toReplace.getValue());
			}
		}

		for(String descriptionLine : langTooltip.split(";")) {
			if(descriptionLine != null && descriptionLine.length() > 0)
				list.add(descriptionLine);
		}
	}

	public static boolean localizationExists(String langName) {
		return I18n.hasKey(Reference.MOD_ID + "." + langName);
	}

}
