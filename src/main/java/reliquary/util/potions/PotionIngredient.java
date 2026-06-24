package reliquary.util.potions;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import reliquary.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

public class PotionIngredient {
	private final ItemStack item;
	private List<MobEffectInstance> effects = new ArrayList<>();

	public PotionIngredient(ItemStack item) {
		this.item = item;
	}

	public PotionIngredient(ItemStack item, List<MobEffectInstance> effects) {
		this.item = item;
		this.effects = effects;
	}

	void addEffect(String potionName, int durationWeight, int ampWeight) {
		BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(potionName)).ifPresentOrElse(
				mobEffect -> effects.add(new MobEffectInstance(mobEffect, durationWeight * 300, ampWeight, true, false)),
				() -> LogHelper.error("Potion name " + potionName + " is not registered. Please fix the name or remove it from potion map."));
	}

	public List<MobEffectInstance> getEffects() {
		return effects;
	}

	public ItemStack getItem() {
		return item;
	}
}
