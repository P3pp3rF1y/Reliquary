package lib.enderwizards.sandstone.mod;

import net.minecraftforge.fml.common.ModContainer;
import lib.enderwizards.sandstone.util.misc.Duo;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple registry for holding information about mods registered with Sandstone.
 *
 * @author TheMike
 */
public class ModRegistry {

    public static List<Duo<ModContainer, SandstoneMod>> mods = new ArrayList<Duo<ModContainer, SandstoneMod>>();

    /**
     * Puts the passed in Mod/SandstoneMod into the list, as a Duo. This method only abstract the Duo class, and has the same effect as directly accessing the mods field.
     *
     * @param mod  A Mod annotation, belonging to the mod being registered.
     * @param smod A SandstoneMod annotation, belonging to the mod being registered.
     */
    public static void put(ModContainer mod, SandstoneMod smod) {
        mods.add(new Duo<ModContainer, SandstoneMod>(mod, smod));
    }

    /**
     * Resolves a mod ID from a class within the mod. This is found using the basePackage value you set on @SandstoneMod.
     *
     * @param className The full name of the class in question.
     * @return The mod ID that was resolved. If no mod ID was found, it will simply return "" so you can act accordingly.
     */
    public static String getID(String className) {
        for (Duo<ModContainer, SandstoneMod> mod : mods) {
            if (className.indexOf(mod.two.basePackage()) == 0)
                return mod.one.getModId();
        }
        return "";
    }

    public static boolean hasMod(ModContainer mod) {
        for (Duo<ModContainer, SandstoneMod> currentMod : mods) {
            if (mod.getModId().equals(currentMod.one.getModId()))
                return true;
        }
        return false;
    }

}
