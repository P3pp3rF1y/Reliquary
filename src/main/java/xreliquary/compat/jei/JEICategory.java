package xreliquary.compat.jei;

import xreliquary.reference.Reference;

public enum JEICategory {
	ALKAHESTRY_CRAFTING("alkahestryCrafting"),
	ALKAHESTRY_CHARGING("alkahestryCharging"),
	MORTAR("mortar"),
	CAULDRON("cauldron");

	private String uid;

	JEICategory(String uid) {
		this.uid = uid;
	}

	public String getUid() {
		return Reference.DOMAIN + uid;
	}
}
