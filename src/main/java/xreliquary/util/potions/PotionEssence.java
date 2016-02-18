package xreliquary.util.potions;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;

import java.util.*;


/**
 * PotionEssence, the helper class for well, potion essences. Abstracts away
 * juggling all that NBT data, combining effects, and applying effects.
 */
public class PotionEssence extends PotionIngredient {

    public static int MAX_DURATION = 30000;
    public static int MAX_AMPLIFIER = 4;
    public static double REDSTONE_MULTIPLIER = 2.85;
    public static double GLOWSTONE_MULTIPLIER = 0.55;

    public List<PotionIngredient> ingredients = new ArrayList<>();

    public PotionEssence(NBTTagCompound tag) {
        if (tag == null)
            return;

        for (int tagIndex = 0; tagIndex < tag.getTagList("effects", 10).tagCount(); ++tagIndex) {
            NBTTagCompound effect = tag.getTagList("effects", 10).getCompoundTagAt(tagIndex);
            effects.add(new PotionEffect(effect.getInteger("id"), effect.getInteger("duration"), effect.getInteger("potency")));
        }
    }


    //this handles the actual combining of two or more ingredients, including other essences.
    public PotionEssence(PotionIngredient... ingredients) {

        //helper list to store what we have, altogether
        Map<Integer, List<PotionEffect>> potionEffectCounterList = new HashMap<Integer, List<PotionEffect>>();

        //actual list to store what we have two or more of, these are the actual final effects
        List<Integer> potionEffectList = new ArrayList<Integer>();

        //add each effect to the counter list. if it appears twice, add it to the potionEffectList too.
        for (PotionIngredient ingredient : ingredients) {
            for (PotionEffect effect : ingredient.getEffects()) {
                if (potionEffectCounterList.keySet().contains(effect.getPotionID())){
                    if (!potionEffectList.contains(effect.getPotionID()))
                        potionEffectList.add(effect.getPotionID());
                    potionEffectCounterList.get(effect.getPotionID()).add(effect);
                } else {
                    ArrayList<PotionEffect> effects = new ArrayList<>();
                    effects.add(effect);
                    potionEffectCounterList.put(effect.getPotionID(), effects);
                }
            }
            this.ingredients.add(ingredient);
        }

        //iterate through common effects
        for(Integer potionID : potionEffectList) {
            List<PotionEffect> effects = potionEffectCounterList.get(potionID);

            int duration = getCombinedDuration(effects);
            int amplifier = getCombinedAmplifier(effects);

            if (duration == 0)
                continue;

            this.effects.add(new PotionEffect(potionID, duration, amplifier));
        }
        this.effects.sort(new EffectComparator());
    }

    private int getCombinedAmplifier(List<PotionEffect> effects) {
        int amplifier = 0;
        for(PotionEffect effect:effects) {
            amplifier += effect.getAmplifier();
        }

        return Math.min(amplifier, MAX_AMPLIFIER);
    }

    private int getCombinedDuration(List<PotionEffect> effects) {
        int count = 0;
        int duration = 0;
        for(PotionEffect effect : effects) {
            if (Potion.potionTypes[effect.getPotionID()].isInstant())
                return 1;

            count++;

            duration += effect.getDuration();
        }

        duration = (int) (duration / 1.2);
        if(count == 3)
            duration = (int) (duration / 1.1);

        return Math.min(duration, MAX_DURATION);
    }

    public void addRedstone() {
        List<PotionEffect> newEffects = new ArrayList<>();

        for(PotionEffect effect : this.effects) {
            PotionEffect newEffect = new PotionEffect(effect.getPotionID(), new Double(effect.getDuration() * REDSTONE_MULTIPLIER).intValue(), effect.getAmplifier(), effect.getIsAmbient(), effect.getIsShowParticles());
            newEffects.add(newEffect);
        }

        this.effects = newEffects;
    }

    public void addGlowstone() {
        List<PotionEffect> newEffects = new ArrayList<>();

        for(PotionEffect effect : this.effects) {
            int newAmplifier = effect.getAmplifier() == 4 ? effect.getAmplifier() : effect.getAmplifier() + 1;

            PotionEffect newEffect = new PotionEffect(effect.getPotionID(), new Double(effect.getDuration() * GLOWSTONE_MULTIPLIER).intValue(), newAmplifier, effect.getIsAmbient(), effect.getIsShowParticles());
            newEffects.add(newEffect);
        }
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        if (tag.getTagList("effects",10) == null)
            return null;
        NBTTagList effectList = tag.getTagList("effects", 10);
        for (PotionEffect object : effects) {
            NBTTagCompound effect = new NBTTagCompound();
            effect.setInteger("id", object.getPotionID());
            effect.setInteger("duration", Potion.potionTypes[object.getPotionID()].isInstant() ? 1 : object.getDuration());
            effect.setInteger("potency", object.getAmplifier());
            effectList.appendTag(effect);
        }
        tag.setTag("effects", effectList);
        return tag;
    }

    public void apply(EntityPlayer player) {
        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
        }
    }

}
