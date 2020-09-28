package xreliquary.util.potions;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

public class PotionIngredient {
	private final ItemStack item;
	private List<EffectInstance> effects = new ArrayList<>();

	public PotionIngredient(ItemStack item) {
		this.item = item;
	}

	public PotionIngredient(ItemStack item, List<EffectInstance> effects) {
		this.item = item;
		this.effects = effects;
	}

	void addEffect(String potionName, int durationWeight, int ampWeight) {
		Effect potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionName));

		if (potion == null) {
			LogHelper.error("Potion name " + potionName + " is not registered. Please fix the name or remove it from potion map.");
			return;
		}
		effects.add(new EffectInstance(potion, durationWeight * 300, ampWeight, true, false));
	}

	public List<EffectInstance> getEffects() {
		return effects;
	}

	public ItemStack getItem() {
		return item;
	}
}
