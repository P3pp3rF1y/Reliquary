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
        HashMap<Integer, Duo<Integer, Integer>> mergedPotionEffects = new HashMap<Integer, Duo<Integer, Integer>>();
        //initialize and combine all the different effects, we're not done yet
        for (PotionIngredient ingredient : ingredients) {
            for (PotionEffect effect : ingredient.getEffects()) {
                if (mergedPotionEffects.containsKey(effect.getPotionID())) {
                    int duration =  Math.min(MAX_DURATION,Potion.potionTypes[effect.getPotionID()].isInstant() ? 1 : mergedPotionEffects.get(effect.getPotionID()).one);
                    int amp = mergedPotionEffects.get(effect.getPotionID()).two;
                    Duo<Integer, Integer> newWeight = new Duo<Integer, Integer>((int)(((float)duration + (float)effect.getDuration()) / 1.5F), Math.max(amp, effect.getAmplifier()));
                    mergedPotionEffects.remove(effect.getPotionID());
                    mergedPotionEffects.put(effect.getPotionID(), newWeight);
                } else {
                    mergedPotionEffects.put(effect.getPotionID(), new Duo<Integer, Integer>(effect.getDuration(), effect.getAmplifier()));
                }
            }
        }
        //iterate over the effects in the map and actually add them to this essence, in a cleaned up/merged list.
        Iterator i = mergedPotionEffects.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<Integer, Duo<Integer, Integer>> pair = (Map.Entry<Integer, Duo<Integer, Integer>>)i.next();
            effects.add(new PotionEffect(pair.getKey(), pair.getValue().one, pair.getValue().two));
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
