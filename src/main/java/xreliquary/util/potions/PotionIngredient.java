package xreliquary.util.potions;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

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
		return this.addEffect(new WeightedPotionEffect(potionName, durationWeight, ampWeight));
	}

	private PotionIngredient addEffect(WeightedPotionEffect effect) {
		effects.add(effect);
		return this;
	}

	public List<PotionEffect> getEffects() {
		return this.effects;
	}
}
