package reliquary.util.potions;

import com.google.common.collect.Lists;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PotionEssence {

	private PotionEssence(List<PotionIngredient> ingredients, List<MobEffectInstance> effects, int redstoneCount, int glowstoneCount) {
		this.ingredients = ingredients;
		this.effects = effects;
		this.redstoneCount = redstoneCount;
		this.glowstoneCount = glowstoneCount;
	}

	private final List<PotionIngredient> ingredients;
	private int redstoneCount;
	private int glowstoneCount;
	private List<MobEffectInstance> effects;

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

	public List<MobEffectInstance> getEffects() {
		return effects;
	}

	public PotionEssence copy() {
		return new Builder().setIngredients(ingredients).setEffects(effects).build();
	}

	public List<PotionIngredient> getIngredients() {
		return ingredients;
	}

	public void setEffects(List<MobEffectInstance> effects) {
		this.effects = effects;
	}

	public static class Builder {
		private List<PotionIngredient> ingredients = new ArrayList<>();
		private int redstoneCount = 0;
		private int glowstoneCount = 0;
		private List<MobEffectInstance> effects = Lists.newArrayList();

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

		public Builder setEffects(List<MobEffectInstance> effects) {
			this.effects = effects;
			return this;
		}

		public PotionEssence build() {
			return new PotionEssence(ingredients, effects, redstoneCount, glowstoneCount);
		}
	}
}
