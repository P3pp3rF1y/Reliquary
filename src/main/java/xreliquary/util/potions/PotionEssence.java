package xreliquary.util.potions;

import com.google.common.collect.Lists;
import net.minecraft.potion.EffectInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PotionEssence {

	private PotionEssence(List<PotionIngredient> ingredients, List<EffectInstance> effects, int redstoneCount, int glowstoneCount) {
		this.ingredients = ingredients;
		this.effects = effects;
		this.redstoneCount = redstoneCount;
		this.glowstoneCount = glowstoneCount;
	}

	private List<PotionIngredient> ingredients = new ArrayList<>();
	private int redstoneCount = 0;
	private int glowstoneCount = 0;
	private List<EffectInstance> effects = Lists.newArrayList();

	public int getRedstoneCount() {
		return redstoneCount;
	}
	@SuppressWarnings("SameParameterValue")
	public void setRedstoneCount(int redstoneCount) {
		this.redstoneCount = redstoneCount;
	}

	public int getGlowstoneCount() {
		return glowstoneCount;
	}
	@SuppressWarnings("SameParameterValue")
	public void setGlowstoneCount(int glowstoneCount) {
		this.glowstoneCount = glowstoneCount;
	}

	public List<EffectInstance> getEffects() {
		return effects;
	}

	public PotionEssence copy() {
		return new Builder().setIngredients(ingredients).setEffects(this.effects).build();
	}

	public List<PotionIngredient> getIngredients() {
		return ingredients;
	}

	public void setEffects(List<EffectInstance> effects) {
		this.effects = effects;
	}

	public static class Builder {
		private List<PotionIngredient> ingredients = new ArrayList<>();
		private int redstoneCount = 0;
		private int glowstoneCount = 0;
		private List<EffectInstance> effects = Lists.newArrayList();

		public Builder setIngredients(PotionIngredient... ingredients) {
			this.ingredients.addAll(Arrays.asList(ingredients));
			return this;
		}
		public Builder setIngredients(List<PotionIngredient> ingredients) {
			this.ingredients = ingredients;
			return this;
		}

		public Builder setRedstoneCount(int redstoneCount) {
			this.redstoneCount = redstoneCount;
			return this;
		}

		public Builder setGlowstoneCount(int glowstoneCount) {
			this.glowstoneCount = glowstoneCount;
			return this;
		}

		public Builder setEffects(List<EffectInstance> effects) {
			this.effects = effects;
			return this;
		}

		public PotionEssence build() {
			return new PotionEssence(ingredients, effects, redstoneCount, glowstoneCount);
		}
	}
}
