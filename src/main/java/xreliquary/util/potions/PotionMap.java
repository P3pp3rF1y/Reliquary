package xreliquary.util.potions;

import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.mod.config.ConfigReference;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.math.NumberUtils;
import xreliquary.Reliquary;
import xreliquary.init.XRRecipes;
import xreliquary.items.ItemPotionEssence;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

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
        addPotionConfig(Items.magma_cream, "fireResistance_1_1","damageBoost_1_1");
        addPotionConfig(Items.sugar, "moveSpeed_3_0","absorption_1_0");
        addPotionConfig(Items.speckled_melon,"heal_1_3","digSpeed_2_1");
        addPotionConfig(Items.spider_eye,"poison_3_0","digSlowDown_2_0");
        addPotionConfig(Items.ghast_tear,"regeneration_1_1","moveSlowdown_5_4");
        addPotionConfig(Items.blaze_powder,"damageBoost_3_0","digSpeed_2_1");
        addPotionConfig(Items.fermented_spider_eye,"weakness_3_1","poison_2_1");
        addPotionConfig(Items.golden_carrot,"nightVision_2_1","digSpeed_2_1");
        //addPotionConfig(Items.pufferfish,"waterBreathing_2_1","damageBoost_2_0");
        //addPotionConfig(Items.rabbit_foot,"jump_4_1","moveSpeed_4_1");
        addPotionConfig(Items.apple,"healthBoost_4_0","heal_1_1");
        addPotionConfig(Items.coal,"nightVision_2_0","resistance_1_0");
        addPotionConfig(Items.coal, 1,"invisibility_2_0","wither_1_0");
        addPotionConfig(Items.diamond,"absorption_4_2","regeneration_3_2");
        addPotionConfig(Items.iron_ingot,"resistance_2_1","damageBoost_1_1");
        addPotionConfig(Items.gold_ingot,"digSpeed_4_2","absorption_1_0");
        addPotionConfig(Items.string,"moveSlowdown_3_0","digSlowDown_3_0");
        addPotionConfig(Items.feather,"jump_3_0","moveSpeed_2_0");
        addPotionConfig(Items.wheat_seeds,"absorption_2_0","hunger_1_0");
        addPotionConfig(Items.wheat,"heal_1_1","saturation_1_0");
        addPotionConfig(Items.bread,"heal_1_2","saturation_1_1");
        addPotionConfig(Items.flint,"fireResistance_3_1","damageBoost_1_0");
        addPotionConfig(Items.porkchop,"saturation_2_1","digSlowDown_3_0");
        addPotionConfig(Items.cooked_porkchop,"saturation_3_1","digSlowDown_2_1");
        addPotionConfig(Items.golden_apple,"healthBoost_4_3","digSpeed_4_1");
        //addPotionConfig(Items.notch_apple,"healthBoost_40_4","digSpeed_40_2");
        addPotionConfig(Items.leather,"resistance_3_1","absorption_2_0");
        addPotionConfig(Items.clay_ball,"hunger_1_1","absorption_2_0");
        addPotionConfig(Items.slime_ball,"fireResistance_3_1","resistance_2_1");
        addPotionConfig(Items.egg,"harm_1_1","absorption_1_1");
        addPotionConfig(Items.fish,"saturation_1_0","waterBreathing_1_1");
        //addPotionConfig(Items.salmon,"saturation_1_1","waterBreathing_1_1");
        //addPotionConfig(Items.clownfish,"confusion_1_1","waterBreathing_1_1");
        addPotionConfig(Items.cooked_fished,"saturation_2_1","waterBreathing_2_1");
        //addPotionConfig(Items.cooked_salmon,"saturation_2_2","waterBreathing_2_2");
        //addPotionConfig(Items.cooked_clownfish,"jump_2_1","waterBreathing_2_1");
        //addPotionConfig(Items.cooked_pufferfish,"poison_2_1","waterBreathing_2_1");
        addPotionConfig(Items.dye, Reference.BLACK_DYE_META,"waterBreathing_3_0","Blindness_3_1"); //ink
        addPotionConfig(Items.dye, Reference.RED_DYE_META,"heal_1_1","regeneration_2_0"); //rose red
        addPotionConfig(Items.dye, Reference.GREEN_DYE_META,"saturation_2_1","absorption_1_0"); //cactus green
        addPotionConfig(Items.dye, Reference.BLUE_DYE_META,"regeneration_1_1","invisibility_2_0");  //lapis lazuli
        addPotionConfig(Items.dye, Reference.YELLOW_DYE_META,"absorption_1_0","jump_2_0"); //dandellion yellow
        addPotionConfig(Items.dye, Reference.WHITE_DYE_META,"digSpeed_1_1","moveSpeed_1_1"); //bone meal
        addPotionConfig(Items.bone,"digSpeed_3_1","moveSpeed_3_1");
        addPotionConfig(Items.cookie,"heal_1_2","saturation_2_1");
        addPotionConfig(Items.melon,"heal_1_2","healthBoost_1_1");
        addPotionConfig(Items.pumpkin_seeds,"moveSlowdown_1_1","saturation_1_1");
        addPotionConfig(Items.pumpkin_pie,"heal_1_2","saturation_2_1");
        addPotionConfig(Items.beef,"saturation_1_0","confusion_1_1");
        addPotionConfig(Items.cooked_beef,"saturation_2_1","damageBoost_2_0");
        addPotionConfig(Items.chicken,"confusion_2_1","poison_1_1");
        addPotionConfig(Items.cooked_chicken,"saturation_2_1","heal_1_1");
        addPotionConfig(Items.rotten_flesh,"wither_2_1","hunger_2_2");
        addPotionConfig(Items.ender_pearl,"harm_1_2","invisibility_4_1");
        addPotionConfig(Items.blaze_rod,"damageBoost_3_1","digSpeed_3_1");
        addPotionConfig(Items.gold_nugget,"digSpeed_2_0","harm_1_1");
        addPotionConfig(Items.ender_eye,"harm_1_4","damageBoost_2_1");
        addPotionConfig(Items.fire_charge,"damageBoost_2_1","harm_1_2");
        addPotionConfig(Items.emerald,"absorption_2_2","resistance_4_2");
        addPotionConfig(Items.carrot,"nightVision_2_0","heal_1_1");
        addPotionConfig(Items.potato,"healthBoost_2_0","saturation_1_0");
        addPotionConfig(Items.baked_potato,"healthBoost_2_1","saturation_1_1");
        addPotionConfig(Items.poisonous_potato,"poison_4_1","wither_1_0");
        addPotionConfig(Items.nether_star,"wither_8_3","absorption_40_3");
        addPotionConfig(Items.quartz,"damageBoost_3_1","harm_1_1");
        //mod items using mob items, using the XRRecipe shortcuts for ease-of-read
        addPotionConfig(XRRecipes.zombieHeart(),"confusion_8_1","wither_6_1");
        addPotionConfig(XRRecipes.ribBone(),"digSpeed_3_2","absorption_1_1");
        addPotionConfig(XRRecipes.creeperGland(),"harm_1_3","regeneration_2_2");
        addPotionConfig(XRRecipes.squidBeak(),"waterBreathing_1_1","hunger_2_1");
        addPotionConfig(XRRecipes.witherRib(),"wither_8_1","poison_3_2");
        addPotionConfig(XRRecipes.batWing(),"jump_3_2","moveSpeed_2_1");
        addPotionConfig(XRRecipes.spiderFangs(),"poison_4_1","harm_1_2");
        addPotionConfig(XRRecipes.slimePearl(),"resistance_5_2","fireResistance_3_2");
        addPotionConfig(XRRecipes.moltenCore(),"harm_1_3","regeneration_3_1");
        addPotionConfig(XRRecipes.fertileEssence(),"healthBoost_5_2","heal_1_3");
        addPotionConfig(XRRecipes.stormEye(),"harm_1_3","moveSpeed_8_2");
        addPotionConfig(XRRecipes.enderHeart(),"invisibility_40_1","absorption_3_2");

        for (Map.Entry<String, List<String>> entry : configList.entrySet())
            Reliquary.CONFIG.require(Names.potion_ingredient, entry.getKey(), new ConfigReference(entry.getValue()));
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
            key = splitKey[0] + ":" + splitKey[1];

            Item item = ContentHandler.getItem(key);
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
