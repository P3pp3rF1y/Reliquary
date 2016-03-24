package xreliquary.util.potions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PotionEssence, the helper class for well, potion essences. Abstracts away
 * juggling all that NBT data, combining effects, and applying effects.
 */
public class PotionEssence extends PotionIngredient {

	public static int MAX_DURATION = 36000;
	public static int MAX_AMPLIFIER = 4;

	public List<PotionIngredient> ingredients = new ArrayList<>();

	private int redstoneCount = 0;
	private int glowstoneCount = 0;
	private NBTTagCompound preAugmentationNBT = null;

	public PotionEssence(NBTTagCompound tag) {
		if(tag == null)
			return;

		for(int tagIndex = 0; tagIndex < tag.getTagList("effects", 10).tagCount(); ++tagIndex) {
			NBTTagCompound effect = tag.getTagList("effects", 10).getCompoundTagAt(tagIndex);
			effects.add(new PotionEffect(Potion.getPotionById(effect.getInteger("id")), effect.getInteger("duration"), effect.getInteger("potency")));
		}
	}

	//this handles the actual combining of two or more ingredients, including other essences.
	public PotionEssence(PotionIngredient... ingredients) {

		//helper list to store what we have, altogether
		Map<Integer, List<PotionEffect>> potionEffectCounterList = new HashMap<Integer, List<PotionEffect>>();

		//actual list to store what we have two or more of, these are the actual final effects
		List<Integer> potionEffectList = new ArrayList<Integer>();

		//add each effect to the counter list. if it appears twice, add it to the potionEffectList too.
		for(PotionIngredient ingredient : ingredients) {
			for(PotionEffect effect : ingredient.getEffects()) {
				if(potionEffectCounterList.keySet().contains(Potion.getIdFromPotion(effect.getPotion()))) {
					if(!potionEffectList.contains(Potion.getIdFromPotion(effect.getPotion())))
						potionEffectList.add(Potion.getIdFromPotion(effect.getPotion()));
					potionEffectCounterList.get(Potion.getIdFromPotion(effect.getPotion())).add(effect);
				} else {
					ArrayList<PotionEffect> effects = new ArrayList<>();
					effects.add(effect);
					potionEffectCounterList.put(Potion.getIdFromPotion(effect.getPotion()), effects);
				}
			}
			this.ingredients.add(ingredient);
		}

		//iterate through common effects
		for(Integer potionID : potionEffectList) {
			List<PotionEffect> effects = potionEffectCounterList.get(potionID);

			int duration = getCombinedDuration(effects);
			int amplifier = getCombinedAmplifier(effects);

			if(duration == 0)
				continue;

			this.effects.add(new PotionEffect(Potion.getPotionById(potionID), duration, amplifier));
		}
		this.effects.sort(new EffectComparator());
	}

	private int getCombinedAmplifier(List<PotionEffect> effects) {
		int amplifier = 0;
		for(PotionEffect effect : effects) {
			amplifier += effect.getAmplifier();
		}

		return Math.min(amplifier, MAX_AMPLIFIER);
	}

	private int getCombinedDuration(List<PotionEffect> effects) {
		int count = 0;
		int duration = 0;
		for(PotionEffect effect : effects) {
			if(effect.getPotion().isInstant())
				return 1;

			count++;

			duration += effect.getDuration();
		}

		duration = (int) (duration / 1.2);
		if(count == 3)
			duration = (int) (duration / 1.1);

		return Math.min(duration, MAX_DURATION);
	}

	public void addRedstone(int redstoneLevel) {
		updatePreAugmentationNBT();
		List<PotionEffect> newEffects = new ArrayList<>();

		int effectCnt = this.effects.size();
		double multiplier = (((double) (8 + effectCnt)) / ((double) (3 + effectCnt)) - (1.0 / ((double) (3 + effectCnt)) * (((double) redstoneLevel) - 1.0)));

		for(PotionEffect effect : this.effects) {
			int newDuration = new Double((double) effect.getDuration() * multiplier).intValue();
			newDuration = Math.min(newDuration, MAX_DURATION * 2);

			PotionEffect newEffect = new PotionEffect(effect.getPotion(), newDuration, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles());
			newEffects.add(newEffect);
		}

		this.effects = newEffects;
		redstoneCount++;
	}

	private void updatePreAugmentationNBT() {
		if(this.preAugmentationNBT == null)
			this.preAugmentationNBT = this.writeToNBT();
	}

	public void addGlowstone(int glowstoneLevel) {
		updatePreAugmentationNBT();
		List<PotionEffect> newEffects = new ArrayList<>();

		int effectCnt = this.effects.size();
		double multiplier = ((((double) (11 + effectCnt)) / ((double) (6 + effectCnt)) - (1.0 / ((double) (6 + effectCnt)) * ((double) glowstoneLevel)) - 1.0));

		for(PotionEffect effect : this.effects) {
			int newAmplifier = effect.getAmplifier();

			if(XRPotionHelper.isAugmentablePotionEffect(effect))
				newAmplifier = Math.min(effect.getAmplifier() + 1, MAX_AMPLIFIER + 1);

			PotionEffect newEffect = new PotionEffect(effect.getPotion(), new Double(effect.getDuration() * multiplier).intValue(), newAmplifier, effect.getIsAmbient(), effect.doesShowParticles());
			newEffects.add(newEffect);
		}

		this.effects = newEffects;
		glowstoneCount++;
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		if(tag.getTagList("effects", 10) == null)
			return null;
		NBTTagList effectList = tag.getTagList("effects", 10);
		for(PotionEffect object : effects) {
			NBTTagCompound effect = new NBTTagCompound();
			effect.setInteger("id", Potion.getIdFromPotion(object.getPotion()));
			effect.setInteger("duration", object.getPotion().isInstant() ? 1 : object.getDuration());
			effect.setInteger("potency", object.getAmplifier());
			effectList.appendTag(effect);
		}
		tag.setTag("effects", effectList);
		return tag;
	}

	public void apply(EntityPlayer player) {
		for(PotionEffect effect : effects) {
			player.addPotionEffect(effect);
		}
	}

	public int getRedstoneCount() {
		return redstoneCount;
	}

	public int getGlowstoneCount() {
		return glowstoneCount;
	}

	public NBTTagCompound getPreAugmentationNBT() {
		updatePreAugmentationNBT();
		return preAugmentationNBT;
	}
}
