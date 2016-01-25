package xreliquary.util.potions;

import lib.enderwizards.sandstone.mod.config.ConfigReference;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.math.NumberUtils;
import xreliquary.Reliquary;
import xreliquary.init.XRRecipes;
import xreliquary.items.ItemPotionEssence;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import java.util.*;

/**
 * Created by Xeno on 11/8/2014.
 * Handles all the defaults for potion ingredients. As it stands there's a lot, so this is a pretty ugly class. Would be nice to get it cleaned up but
 * I just want something working right nao.
 */
public class PotionMap {
    private static List<PotionIngredient> ingredientsMap = new ArrayList<PotionIngredient>();

    public static void init() {
        //initialize the potion mapping. This is powered mainly through config file.
        //TIER ONE INGREDIENTS, these are always 0 potency and have minimal durations (3 for positive, 1 for negative or super-positive)
        addPotionConfig(Items.sugar, speed(3, 0), haste(3, 0));
        addPotionConfig(Items.apple,heal(0), hboost(3, 0));
        addPotionConfig(Items.coal, blind(1, 0), absorb(3, 0));
        addPotionConfig(Items.coal, 1, invis(1, 0), wither(0, 0));
        addPotionConfig(Items.feather, jump(3, 0), weak(1, 0));
        addPotionConfig(Items.wheat_seeds, harm(0), hboost(3, 0));
        addPotionConfig(Items.wheat,heal(0), hboost(3, 0));
        addPotionConfig(Items.flint, harm(0), dboost(3, 0));
        addPotionConfig(Items.porkchop,slow(1, 0), fatigue(1, 0));
        addPotionConfig(Items.leather,resist(3, 0), absorb(3, 0));
        addPotionConfig(Items.clay_ball,slow(1, 0), hboost(3, 0));
        addPotionConfig(Items.egg,absorb(3, 0), regen(0, 0));
        addPotionConfig(Items.dye, Reference.RED_DYE_META, heal(0), hboost(3, 0)); //rose red
        addPotionConfig(Items.dye, Reference.YELLOW_DYE_META,jump(3, 0), weak(1, 0)); //dandellion yellow
        addPotionConfig(Items.dye, Reference.GREEN_DYE_META,resist(3, 0), absorb(3, 0)); //cactus green
        addPotionConfig(Items.dye, Reference.WHITE_DYE_META, weak(1, 0), fatigue(1, 0)); //bone meal
        addPotionConfig(Items.pumpkin_seeds,invis(1, 0), fireres(1,0));
        addPotionConfig(Items.beef,slow(1,0), satur(0));
        addPotionConfig(Items.chicken,nausea(1, 0), poison(1, 0));
        addPotionConfig(Items.rotten_flesh,nausea(1, 0), hunger(1, 0), wither(0, 0));
        addPotionConfig(Items.gold_nugget, dboost(0, 0), haste(0, 0));
        addPotionConfig(Items.carrot,vision(3, 0), hboost(3, 0));
        addPotionConfig(Items.potato,hboost(3, 0), satur(0));
        addPotionConfig(Items.fish, satur(0), breath(1, 0));

        //TIER TWO INGREDIENTS, one of the effects of each will always be a one, slightly increased duration vs. TIER ONE
        addPotionConfig(Items.spider_eye, vision(4, 0), poison(2, 0));
        addPotionConfig(Items.blaze_powder, dboost(4, 0), harm(0));
        addPotionConfig(Items.iron_ingot, resist(4, 0), slow(2, 0));
        addPotionConfig(Items.string, slow(2, 0), fatigue(2, 0));
        addPotionConfig(Items.bread, hboost(4, 0), satur(0));
        addPotionConfig(Items.cooked_porkchop, fatigue(2, 0), satur(0));
        addPotionConfig(Items.slime_ball, resist(4, 0), fireres(2, 0));
        addPotionConfig(Items.cooked_fish, satur(0), breath(2, 0));
        addPotionConfig(Items.dye, Reference.BLUE_DYE_META, haste(4, 0), dboost(4, 0));  //lapis lazuli
        addPotionConfig(Items.dye, Reference.BLACK_DYE_META, blind(2, 0), invis(2, 0)); //ink
        addPotionConfig(Items.bone, weak(2, 0), fatigue(2, 0));
        addPotionConfig(Items.cookie, heal(0), satur(0));
        addPotionConfig(Items.melon, heal(0), speed(4, 0));
        addPotionConfig(Items.cooked_beef, resist(4, 0), satur(0));
        addPotionConfig(Items.cooked_chicken, jump(4, 0), satur(0));
        addPotionConfig(Items.baked_potato, satur(0), regen(1, 0));
        addPotionConfig(Items.poisonous_potato, poison(2, 0), wither(1, 0));
        addPotionConfig(Items.quartz, harm(0), dboost(4, 0));
        addPotionConfig(XRRecipes.zombieHeart(), nausea(2, 0), hunger(2, 0), wither(1, 0));
        addPotionConfig(XRRecipes.squidBeak(), hunger(2, 0), breath(2, 0));

        //TIER THREE INGREDIENTS, these are closer to vanilla durations, carry many effects or a slightly increased duration. Some/most are combos.
        addPotionConfig(Items.pumpkin_pie, invis(1, 0), fireres(1, 0), speed(3, 0), haste(3, 0), absorb(3, 0), regen(0, 0)); //combination of ingredients, strong.
        addPotionConfig(Items.magma_cream, dboost(4, 0), harm(0), resist(4, 0), fireres(2, 0)); //also a combo, strong.
        addPotionConfig(Items.speckled_melon, dboost(3, 0), haste(3, 0), heal(0), speed(4, 0)); //combo
        addPotionConfig(Items.ghast_tear, regen(3, 0), absorb(5, 0));
        addPotionConfig(Items.fermented_spider_eye, vision(4, 0), poison(2, 0), speed(3, 0), haste(3, 0)); //combo
        addPotionConfig(Items.golden_carrot, dboost(3, 0), haste(3, 0), hboost(3, 0), vision(3, 0)); //combo
        addPotionConfig(Items.gold_ingot, dboost(4, 0), haste(4, 0)); //combo
        addPotionConfig(XRRecipes.ribBone(), weak(3, 0), fatigue(3, 0));
        addPotionConfig(Items.ender_pearl, invis(5, 0), speed(5, 0));
        addPotionConfig(Items.blaze_rod, dboost(8, 0), harm(0));
        addPotionConfig(Items.fire_charge, dboost(4, 0), harm(0), blind(1, 0), absorb(3, 0)); //combo
        addPotionConfig(XRRecipes.creeperGland(), regen(3, 0), hboost(5, 0));
        addPotionConfig(XRRecipes.spiderFangs(), poison(3, 0), weak(3, 0));
        addPotionConfig(XRRecipes.slimePearl(), resist(5, 0), absorb(5, 0));
        addPotionConfig(XRRecipes.shellFragment(), absorb(5, 0), breath(5, 0));
        addPotionConfig(XRRecipes.batWing(), jump(5, 0), weak(3, 0));

        //TIER FOUR INGREDIENTS, these carry multiple one-potency effects and have the most duration for any given effect.
        addPotionConfig(Items.diamond, resist(6, 1), absorb(6, 1), fireres(6, 0));
        addPotionConfig(XRRecipes.witherRib(), wither(2, 1), weak(3, 1), slow(3, 1), fatigue(3, 1));
        addPotionConfig(Items.ender_eye, dboost(6, 1), invis(6, 0), speed(6, 1), harm(1));
        addPotionConfig(Items.emerald, haste(6, 1), speed(6, 1), hboost(6, 1));
        addPotionConfig(Items.nether_star, hboost(24, 1), regen(24, 1), absorb(24, 1)); //nether star is holy stonk
        addPotionConfig(XRRecipes.moltenCore(), dboost(6, 1), fireres(6, 0), harm(1));
        addPotionConfig(XRRecipes.stormEye(), haste(24, 1), speed(24, 1), jump(24, 1), harm(1));
        addPotionConfig(XRRecipes.fertileEssence(), hboost(8, 1), regen(3, 1), heal(1), satur(1), weak(9, 1), fatigue(9, 1));
        addPotionConfig(XRRecipes.frozenCore(), absorb(6, 1), slow(3, 1), fatigue(3, 1), harm(1), fireres(6, 0));
        addPotionConfig(XRRecipes.enderHeart(), vision(6, 0), invis(6, 0), harm(1), hboost(6, 1), dboost(6, 1), speed(6, 1), haste(6, 1));
        addPotionConfig(XRRecipes.infernalClaw(), harm(1), resist(6, 1), fireres(6, 0), dboost(6, 1), satur(1), heal(1));

        //hoping this produces an ordered list of the key/value pairs.
        Set orderedSet = configList.keySet();
        Iterator setIterator = orderedSet.iterator();
        while (setIterator.hasNext()) {
            String key = (String)setIterator.next();
            Object value = configList.get(key);
            Reliquary.CONFIG.require(Names.potion_ingredient, key, new ConfigReference(value));
        }
    }

    //include name helpers to quickly return the "proper" minecraft names for each effect, duration and potency
    //heal, saturation and harm are speshul, they don't need a duration.
    public static String harm(int potency) { return effectString(Reference.HARM, Integer.toString(0),Integer.toString(potency)); }
    public static String heal(int potency) { return effectString(Reference.HEAL, Integer.toString(0),Integer.toString(potency)); }
    public static String satur(int potency) { return effectString(Reference.SATURATION, Integer.toString(0),Integer.toString(potency)); }
    public static String invis(int duration, int potency) { return effectString(Reference.INVIS, Integer.toString(duration), Integer.toString(potency)); }
    public static String absorb(int duration, int potency) { return effectString(Reference.ABSORB, Integer.toString(duration),Integer.toString(potency)); }
    public static String hboost(int duration, int potency) { return effectString(Reference.HBOOST, Integer.toString(duration),Integer.toString(potency)); }
    public static String dboost(int duration, int potency) { return effectString(Reference.DBOOST, Integer.toString(duration),Integer.toString(potency)); }
    public static String speed(int duration, int potency) { return effectString(Reference.SPEED, Integer.toString(duration),Integer.toString(potency)); }
    public static String haste(int duration, int potency) { return effectString(Reference.HASTE, Integer.toString(duration),Integer.toString(potency)); }
    public static String slow(int duration, int potency) { return effectString(Reference.SLOW, Integer.toString(duration),Integer.toString(potency)); }
    public static String fatigue(int duration, int potency) { return effectString(Reference.FATIGUE, Integer.toString(duration),Integer.toString(potency)); }
    public static String breath(int duration, int potency) { return effectString(Reference.BREATH, Integer.toString(duration),Integer.toString(potency)); }
    public static String vision(int duration, int potency) { return effectString(Reference.VISION, Integer.toString(duration),Integer.toString(potency)); }
    public static String resist(int duration, int potency) { return effectString(Reference.RESIST, Integer.toString(duration),Integer.toString(potency)); }
    public static String fireres(int duration, int potency) { return effectString(Reference.FRESIST, Integer.toString(duration),Integer.toString(potency)); }
    public static String weak(int duration, int potency) { return effectString(Reference.WEAK, Integer.toString(duration),Integer.toString(potency)); }
    public static String jump(int duration, int potency) { return effectString(Reference.JUMP, Integer.toString(duration),Integer.toString(potency)); }
    public static String nausea(int duration, int potency) { return effectString(Reference.NAUSEA, Integer.toString(duration),Integer.toString(potency)); }
    public static String hunger(int duration, int potency) { return effectString(Reference.HUNGER, Integer.toString(duration),Integer.toString(potency)); }
    public static String regen(int duration, int potency) { return effectString(Reference.REGEN, Integer.toString(duration),Integer.toString(potency)); }
    public static String poison(int duration, int potency) { return effectString(Reference.POISON, Integer.toString(duration),Integer.toString(potency)); }
    public static String wither(int duration, int potency) { return effectString(Reference.WITHER, Integer.toString(duration),Integer.toString(potency)); }
    public static String blind(int duration, int potency) { return effectString(Reference.BLIND, Integer.toString(duration), Integer.toString(potency)); }



    public static String effectString(String name, String duration, String potency) {
        return name + "_" + duration + "_" + potency;
    }

    private static void addPotionConfig(Item item, String... params) {
        addPotionConfig(item, 0, params);
    }

    private static void addPotionConfig(Item item, int meta, String... params) {
        addPotionConfig(new ItemStack(item, 1, meta), params);
    }

    private static TreeMap<String, List<String>> configList = new TreeMap<String, List<String>>();
    private static void addPotionConfig(ItemStack ist, String... params) {
        List<String> ingredientEffects = new ArrayList<String>();
        for (String effect : params) {
            ingredientEffects.add(effect);
        }
        configList.put(ContentHelper.getIdent(ist.getItem()).replace(':', '_') + (ist.getItemDamage() == 0 ? "" : "_" + ist.getItemDamage()), ingredientEffects);
    }


    public static void initializePotionMappings() {
        //_ replaces : in identity names. the last _ should always denote a meta value that separates this from another item, if possible.

        Set<Map.Entry<String, Object>> configIngredients = Reliquary.CONFIG.getGroup(Names.potion_ingredient).entrySet();
        for (Map.Entry<String, Object> configIngredient : configIngredients) {
            String key = configIngredient.getKey();
            int meta = 0;
            String[] splitKey = key.split("_");
            //the final should always be a meta, or someone has a weirdly named item
            if (NumberUtils.isNumber(splitKey[splitKey.length - 1])) {
                meta = Integer.parseInt(splitKey[splitKey.length - 1]);
                key = "";
                for (int i = 0; i < splitKey.length - 1; ++i) {
                    //resets the splitkey and drops the meta, I hope.
                    key += splitKey[i];
                    if (i < splitKey.length - 2) key += "_";
                }
            }
            //we split the string a second time, but this time we're only doing this to match the first underscore so we can replace it with a colon.
            //the string is limited to being split "in two", one for the mod_id and the other the actual item name, and whatever meta it has having been dropped.
            splitKey = key.split("_", 2);

            Item item = Reliquary.CONTENT.getItem(splitKey[0] + ":" + splitKey[1]);
            List<String> effectList = (List<String>)configIngredient.getValue();
            PotionIngredient ingredient = new PotionIngredient(item, meta);
            for (String effect : effectList) {
                String potionName = effect.split("_")[0];
                int durationWeight = Integer.parseInt(effect.split("_")[1]);
                int ampWeight = Integer.parseInt(effect.split("_")[2]);
                ingredient.addEffect(XRPotionHelper.getPotionIdByName(potionName), durationWeight, ampWeight);
            }
            ingredientsMap.add(ingredient);
        }
    }

    public static List<PotionIngredient> getIngredients() {
        return ingredientsMap;
    }

    public static PotionIngredient getIngredient(ItemStack ist) {
        if (ist.getItem() instanceof ItemPotionEssence) {
            return new PotionEssence(ist.getTagCompound());
        }
        for (PotionIngredient ingredient : ingredientsMap) {
            if (ingredient.itemName.equals(ist.getItem().getUnlocalizedNameInefficiently(ist)))
                return ingredient;
        }
        return null;
    }


}
