package xreliquary.util.potions;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import xreliquary.util.LogHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PotionIngredient {

	public ItemStack item;
	public List<PotionEffect> effects = new ArrayList<>();

	//default constructor, used by Potion Essence, because it extends this class.
	PotionIngredient() {
	}

	public PotionIngredient(@Nonnull ItemStack ist) {
		this.item = ist;
	}

	public PotionIngredient(@Nonnull ItemStack stack, List<PotionEffect> effects) {
		this.item = stack;
		this.effects = effects;
	}

	public PotionIngredient addEffect(String potionName, int durationWeight, int ampWeight) {
		Potion potion  = Potion.getPotionFromResourceLocation(potionName);

		if (potion == null) {
			LogHelper.error("Potion name " + potionName + " is not registered. Please fix the name or remove it from potion map.");
			return this;
		}
		return this.addEffect(new PotionEffect(potion, durationWeight * 300, ampWeight, true, false));
	}

	private PotionIngredient addEffect(PotionEffect effect) {
		effects.add(effect);
		return this;
	}

	public List<PotionEffect> getEffects() {
		return this.effects;
	}
}
