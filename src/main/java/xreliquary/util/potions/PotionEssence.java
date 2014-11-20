package xreliquary.util.potions;

import lib.enderwizards.sandstone.util.misc.Duo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.*;

/**
 * PotionEssence, the helper class for well, potion essences. Abstracts away
 * juggling all that NBT data, combining effects, and applying effects.
 */
public class PotionEssence extends PotionIngredient {

    public static int MAX_DURATION = 30000;

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
        List<Integer> potionEffectCounterList = new ArrayList<Integer>();

        //actual list to store what we have two or more of, these are the actual final effects
        List<Integer> potionEffectList = new ArrayList<Integer>();

        //finally, the merged effects list, this is the result of all the combining we do at the end.
        HashMap<Integer, Duo<Integer, Integer>> mergedPotionEffects = new HashMap<Integer, Duo<Integer, Integer>>();

        //add each effect to the counter list. if it appears twice, add it to the potionEffectList too.
        for (PotionIngredient ingredient : ingredients) {
            for (PotionEffect effect : ingredient.getEffects()) {
                if (potionEffectCounterList.contains(effect.getPotionID()))
                    potionEffectList.add(effect.getPotionID());
                else
                    potionEffectCounterList.add(effect.getPotionID());
            }
        }

        //iterate again, this time checking for potions we're sure we need, add them to the merged effects list.
        for (PotionIngredient ingredient : ingredients) {
            for (PotionEffect effect : ingredient.getEffects()) {
                if (potionEffectList.contains(effect.getPotionID())) {
                    //if the effect list contains it already, merge them together
                    if (mergedPotionEffects.containsKey(effect.getPotionID())) {
                        int duration =  Math.min(MAX_DURATION,Potion.potionTypes[effect.getPotionID()].isInstant() ? 1 : mergedPotionEffects.get(effect.getPotionID()).one);
                        //0 duration potion means we have two useless ingredients. This is planned for things like wither combinations of T1 ingredients sucking hard.
                        if (duration == 0)
                            continue;
                        int amp = mergedPotionEffects.get(effect.getPotionID()).two;
                        Duo<Integer, Integer> newWeight = new Duo<Integer, Integer>((int)(((float)duration + (float)effect.getDuration()) / 1.2F), Math.min(4, amp + effect.getAmplifier()));
                        //remove existing, replace with awesome (or less awesome) one.
                        mergedPotionEffects.remove(effect.getPotionID());
                        mergedPotionEffects.put(effect.getPotionID(), newWeight);
                    } else {
                        //just add it to the list for the first time
                        mergedPotionEffects.put(effect.getPotionID(), new Duo<Integer, Integer>(effect.getDuration(), effect.getAmplifier()));
                    }
                }
            }
        }

        //iterate over the effects in the map and actually add them to this essence, in a cleaned up/merged list.
        Iterator i = mergedPotionEffects.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<Integer, Duo<Integer, Integer>> pair = (Map.Entry<Integer, Duo<Integer, Integer>>)i.next();
            //the effect is added. If the end result of this contains nothing, it means the items are invalid for mixing.
            //this is important in cases where the grinder is full of incompatible ingredients. Rather than mix them, we simply return them to the player.
            effects.add(new PotionEffect(pair.getKey(), Potion.potionTypes[pair.getKey()].isInstant() ? 1 : pair.getValue().one, pair.getValue().two));
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
