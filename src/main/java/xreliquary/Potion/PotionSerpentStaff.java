package xreliquary.Potion;

import net.minecraft.potion.Potion;

/**
 * Created by Xeno on 10/9/2014.
 */
public class PotionSerpentStaff extends Potion {
    public static final Potion serpentStaffDebuff = (new PotionSerpentStaff(getFirstAvailableID(), false, 0)).setIconIndex(0, 0).setPotionName("potion.serpentStaffDebuff");

    protected PotionSerpentStaff(int id, boolean isNegative, int liquidColor) {
        super(id, isNegative, liquidColor);

    }

    protected static int getFirstAvailableID() {
        for (int i = 1; i < Potion.potionTypes.length; i++) {
            if (Potion.potionTypes[i] == null)
                return i;
        }
        return 0;
    }
}
