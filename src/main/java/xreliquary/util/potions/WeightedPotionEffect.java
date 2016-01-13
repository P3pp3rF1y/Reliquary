package xreliquary.util.potions;

import net.minecraft.potion.PotionEffect;

/**
 * Created by Xeno on 11/8/2014.
 */
public class WeightedPotionEffect extends PotionEffect {
    public int durationWeight;
    public int ampWeight;

    //the only constructor I care about, honestly. This takes the mapping and turns it into something usable.
    public WeightedPotionEffect(int id, int duration, int amp) {
        //calls with amp of 0 (level 1)
        //potion effects are always ambient, it helps with stupid looking particles.
        //1.8.9 set the particles to not display
        super(id, duration * 300, amp, true,false);
        this.durationWeight = duration;
        this.ampWeight = amp;
    }
}
