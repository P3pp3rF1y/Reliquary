package xreliquary.init;

import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.compat.jei.descriptions.JEIDescriptionRegistry;
import xreliquary.items.*;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.potions.PotionEssence;

import java.util.ArrayList;
import java.util.List;


public class ModItems {

    public static final ItemAlkahestryTome alkahestryTome = new ItemAlkahestryTome();
    public static final ItemMobIngredient  mobIngredient = new ItemMobIngredient();
    public static final ItemMercyCross mercyCross = new ItemMercyCross();
    public static final ItemAngelheartVial angelheartVial = new ItemAngelheartVial();
    public static final ItemAngelicFeather angelicFeather = new ItemAngelicFeather();
    public static final ItemAttractionPotion attractionPotion = new ItemAttractionPotion();
    public static final ItemPotionEssence potionEssence = new ItemPotionEssence();
    public static final ItemBullet bullet = new ItemBullet();
    public static final ItemDestructionCatalyst destructionCatalyst = new ItemDestructionCatalyst();
    public static final ItemEmperorChalice emperorChalice = new ItemEmperorChalice();
    public static final ItemEnderStaff enderStaff = new ItemEnderStaff();
    public static final ItemFertilePotion fertilePotion = new ItemFertilePotion();
    public static final ItemFortuneCoin fortuneCoin = new ItemFortuneCoin();
    public static final ItemGlacialStaff glacialStaff = new ItemGlacialStaff();
    public static final ItemGlowingBread glowingBread = new ItemGlowingBread();
    public static final ItemGlowingWater glowingWater = new ItemGlowingWater();
    public static final ItemHolyHandGrenade holyHandGrenade = new ItemHolyHandGrenade();
    public static final ItemGunPart gunPart = new ItemGunPart();
    public static final ItemHandgun handgun = new ItemHandgun();
    public static final ItemHarvestRod harvestRod = new ItemHarvestRod();
    public static final ItemHeartPearl heartPearl = new ItemHeartPearl();
    public static final ItemHeartZhu heartZhu = new ItemHeartZhu();
    public static final ItemHeroMedallion heroMedallion = new ItemHeroMedallion();
    public static final ItemIceMagusRod iceMagusRod = new ItemIceMagusRod();
    public static final ItemInfernalChalice infernalChalice = new ItemInfernalChalice();
    public static final ItemInfernalClaws infernalClaws = new ItemInfernalClaws();
    public static final ItemInfernalTear infernalTear = new ItemInfernalTear();
    public static final ItemKrakenShell krakenShell = new ItemKrakenShell();
    public static final ItemLanternOfParanoia lanternOfParanoia = new ItemLanternOfParanoia();
    public static final ItemMagazine magazine = new ItemMagazine();
    public static final ItemMagicbane magicbane = new ItemMagicbane();
    public static final ItemMidasTouchstone midasTouchstone = new ItemMidasTouchstone();
    public static final ItemPhoenixDown phoenixDown = new ItemPhoenixDown();
    public static final ItemPyromancerStaff pyromancerStaff = new ItemPyromancerStaff();
    public static final ItemRendingGale rendingGale = new ItemRendingGale();
    public static final ItemRodOfLyssa rodOfLyssa = new ItemRodOfLyssa();
    public static final ItemSalamanderEye salamanderEye = new ItemSalamanderEye();
    public static final ItemSerpentStaff serpentStaff = new ItemSerpentStaff();
    public static final ItemShearsOfWinter shearsOfWinter = new ItemShearsOfWinter();
    public static final ItemSojournerStaff sojournerStaff = new ItemSojournerStaff();
    public static final ItemTwilightCloak twilightCloak = new ItemTwilightCloak();
    public static final ItemVoidTearEmpty emptyVoidTear = new ItemVoidTearEmpty();
    public static final ItemVoidTear filledVoidTear = new ItemVoidTear();
    public static final ItemWitchHat witchHat = new ItemWitchHat();
    public static final ItemWitherlessRose witherlessRose = new ItemWitherlessRose();
    public static final ItemXRPotion potion = new ItemXRPotion();

    public static void init() {
        registerItem(alkahestryTome, Names.alkahestry_tome);
        registerItem(mobIngredient, Names.mob_ingredient);
        registerItem(mercyCross, Names.mercy_cross);
        registerItem(angelheartVial, Names.angelheart_vial);
        registerItem(angelicFeather, Names.angelic_feather);
        registerItem(attractionPotion, Names.attraction_potion);
        registerItem(bullet, Names.bullet);
        registerItem(destructionCatalyst, Names.destruction_catalyst);
        registerItem(emperorChalice, Names.emperor_chalice);
        registerItem(enderStaff, Names.ender_staff);
        registerItem(fertilePotion, Names.fertile_potion);
        registerItem(fortuneCoin, Names.fortune_coin);
        registerItem(glacialStaff, Names.glacial_staff);
        registerItem(glowingBread, Names.glowing_bread);
        registerItem(glowingWater, Names.glowing_water);
        registerItem(gunPart, Names.gun_part);
        registerItem(handgun, Names.handgun);
        registerItem(harvestRod, Names.harvest_rod);
        registerItem(heartPearl, Names.heart_pearl);
        registerItem(heartZhu, Names.heart_zhu);
        registerItem(heroMedallion, Names.hero_medallion);
        registerItem(holyHandGrenade, Names.holy_hand_grenade);
        registerItem(iceMagusRod, Names.ice_magus_rod);
        registerItem(infernalChalice, Names.infernal_chalice);
        registerItem(infernalClaws, Names.infernal_claws);
        registerItem(infernalTear, Names.infernal_tear);
        registerItem(krakenShell, Names.kraken_shell);
        registerItem(lanternOfParanoia, Names.lantern_of_paranoia);
        registerItem(magazine, Names.magazine);
        registerItem(magicbane, Names.magicbane);
        registerItem(midasTouchstone, Names.midas_touchstone);
        registerItem(potionEssence, Names.potion_essence, false);
        registerItem(phoenixDown, Names.phoenix_down);
        registerItem(pyromancerStaff, Names.pyromancer_staff);
        registerItem(rendingGale, Names.rending_gale);
        registerItem(rodOfLyssa, Names.rod_of_lyssa);
        registerItem(salamanderEye, Names.salamander_eye);
        registerItem(serpentStaff, Names.serpent_staff);
        registerItem(shearsOfWinter, Names.shears_of_winter);
        registerItem(sojournerStaff, Names.sojourner_staff);
        registerItem(twilightCloak, Names.twilight_cloak);
        registerItem(emptyVoidTear, Names.void_tear_empty);
        registerItem(filledVoidTear, Names.void_tear);
        registerItem(witchHat, Names.witch_hat);
        registerItem(witherlessRose, Names.witherless_rose);
        registerItem(potion, Names.potion);
    }

    @SideOnly(Side.CLIENT)
    public static void initPotionsJEI() {
        if (!Loader.isModLoaded(Compatibility.MOD_ID.JEI))
            return;

        List<ItemStack> subItems = new ArrayList<>();
        potionEssence.getSubItems(potionEssence, potionEssence.getCreativeTab(), subItems);
        JEIDescriptionRegistry.register(subItems, Names.potion_essence);

        List<ItemStack> potions = new ArrayList<>();
        List<ItemStack> splashPotions = new ArrayList<>();

        for (PotionEssence essence : Settings.uniquePotions) {
            ItemStack potion = new ItemStack(ModItems.potion, 1);
            potion.setTagCompound(essence.writeToNBT());
            NBTHelper.setBoolean("hasPotion", potion, true);
            potions.add(potion);

            ItemStack splashPotion = potion.copy();
            NBTHelper.setBoolean("splash", splashPotion, true);
            splashPotions.add(splashPotion);
        }
        JEIDescriptionRegistry.register(potions, Names.potion);
        JEIDescriptionRegistry.register(splashPotions, Names.potion_splash);
    }

    private static void registerItem(Item item, String name) {
        registerItem(item, name, true);
    }
    private static void registerItem(Item item, String name, boolean registerInJEI) {
        GameRegistry.registerItem(item, Reference.DOMAIN + name);
        if (registerInJEI)
            registerJEI(item, name);
    }

    @SideOnly(Side.CLIENT)
    private static void registerJEI(Item item, String name) {
        if (Loader.isModLoaded(Compatibility.MOD_ID.JEI))
            JEIDescriptionRegistry.register(item, name);
    }

}