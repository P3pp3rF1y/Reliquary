package reliquary.client.gui.hud;

public enum HUDPosition {
	BOTTOM_LEFT,
	LEFT,
	TOP_LEFT,
	TOP,
	TOP_RIGHT,
	RIGHT,
	BOTTOM_RIGHT;

	public boolean isLeftSide() {
		return this == BOTTOM_LEFT || this == LEFT || this == TOP_LEFT;
	}
	public boolean isRightSide() {
		return this == BOTTOM_RIGHT || this == RIGHT || this == TOP_RIGHT;
	}
}
