package reliquary.client.gui.hud;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;
import reliquary.Reliquary;

import java.util.Locale;

public enum HUDPosition implements TranslatableEnum {
	BOTTOM_LEFT, LEFT, TOP_LEFT, TOP, TOP_RIGHT, RIGHT, BOTTOM_RIGHT;

	@Override
	public Component getTranslatedName() {
		return Component.translatable(Reliquary.MOD_ID + ".configuration.hudPosition." + name().toLowerCase(Locale.ROOT));
	}

	public boolean isLeftSide() {
		return this == BOTTOM_LEFT || this == LEFT || this == TOP_LEFT;
	}
	public boolean isRightSide() {
		return this == BOTTOM_RIGHT || this == RIGHT || this == TOP_RIGHT;
	}
}
