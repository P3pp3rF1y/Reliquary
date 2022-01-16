package xreliquary.util;

import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A language file 'preprocessor', I guess you could call it. It just injects globals right now.
 *
 * @author TheMike
 * @author x3n0ph0b3
 */
public class LanguageHelper {
	private LanguageHelper() {}

	private static final Map<TranslationKey, String> preprocessed = new HashMap<>();
	private static final Map<String, String> globals = new HashMap<>();

	static {
		globals.put("colors.black", "\u00A70");
		globals.put("colors.navy", "\u00A71");
		globals.put("colors.green", "\u00A72");
		globals.put("colors.blue", "\u00A73");
		globals.put("colors.red", "\u00A74");
		globals.put("colors.purple", "\u00A75");
		globals.put("colors.gold", "\u00A76");
		globals.put("colors.light_gray", "\u00A77");
		globals.put("colors.gray", "\u00A78");
		globals.put("colors.dark_purple", "\u00A79");
		globals.put("colors.light_green", "\u00A7a");
		globals.put("colors.light_blue", "\u00A7b");
		globals.put("colors.rose", "\u00A7c");
		globals.put("colors.light_purple", "\u00A7d");
		globals.put("colors.yellow", "\u00A7e");
		globals.put("colors.white", "\u00A7f");
		globals.put("colors.reset", ChatFormatting.RESET.toString());
	}

	/**
	 * Gets the preprocessed version of the localized string. Preprocessing will only be ran once, not on every call.
	 *
	 * @param key The localization key.
	 * @return A preprocessed localized string. If your current language doesn't have a localized string, it defaults to en_US.
	 */
	public static String getLocalization(String key, Object... parameters) {
		String localization = String.format(Language.getInstance().getOrDefault(key), parameters);

		TranslationKey translationKey = new TranslationKey(key, parameters);
		if (preprocessed.containsKey(translationKey)) {
			return preprocessed.get(translationKey);
		} else if (localization.contains("{{!")) {
			while (localization.contains("{{!")) {
				int startingIndex = localization.indexOf("{{!");
				int endingIndex = localization.indexOf("}}", startingIndex);
				String fragment = localization.substring(startingIndex + 3, endingIndex);

				try {
					String replacement = globals.get(fragment.toLowerCase());
					localization = localization.substring(0, startingIndex) + replacement + localization.substring(endingIndex + 2);
				}
				catch (Exception e) {
					localization = localization.substring(0, startingIndex) + localization.substring(endingIndex + 2);
				}
			}

			preprocessed.put(translationKey, localization);
		}
		return localization;
	}

	public static void formatTooltip(String langName, List<Component> list) {
		formatTooltip(langName, Map.of(), list);
	}

	public static void formatTooltip(String langName, @Nullable Map<String, String> toFormat, List<Component> list) {
		String langTooltip = getLocalization(langName);
		if (langTooltip.equals(langName)) {
			return;
		}
		if (toFormat != null) {
			for (Map.Entry<String, String> toReplace : toFormat.entrySet()) {
				langTooltip = langTooltip.replace("{{" + toReplace.getKey() + "}}", toReplace.getValue());
			}
		}

		for (String descriptionLine : langTooltip.split(";")) {
			if (descriptionLine != null && descriptionLine.length() > 0) {
				list.add(new TextComponent(descriptionLine));
			}
		}
	}

	public static boolean localizationExists(String langName) {
		return Language.getInstance().has(langName);
	}

	private static class TranslationKey {
		private final String key;
		private final Object[] parameters;

		private TranslationKey(String key, Object[] parameters) {
			this.key = key;
			this.parameters = parameters;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			TranslationKey that = (TranslationKey) o;
			return key.equals(that.key) &&
					Arrays.equals(parameters, that.parameters);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(key);
			result = 31 * result + Arrays.hashCode(parameters);
			return result;
		}
	}
}
