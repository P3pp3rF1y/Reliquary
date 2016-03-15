package xreliquary.util.potions;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

public class PotionFlight extends Potion {
	public PotionFlight() {
		super(new ResourceLocation(Reference.DOMAIN + "potions/flight"), false, 0);
	}
}
