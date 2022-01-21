package reliquary.compat.jei;

import reliquary.reference.Reference;

public enum JEICategory {
	ALKAHESTRY_CRAFTING("alkahestryCrafting"),
	ALKAHESTRY_CHARGING("alkahestryCharging"),
	MORTAR("mortar"),
	CAULDRON("cauldron");

	private final String uid;

	JEICategory(String uid) {
		this.uid = uid;
	}

	public String getUid() {
		return Reference.DOMAIN + uid;
	}
}
