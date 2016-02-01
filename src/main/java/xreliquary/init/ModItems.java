package xreliquary.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.items.*;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

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
    public static final ItemIceMagusRod iceRod = new ItemIceMagusRod();
    public static final ItemInfernalChalice infernalChalice = new ItemInfernalChalice();
    public static final ItemInfernalClaws infernalClaws = new ItemInfernalClaws();
    public static final ItemInfernalTear infernalTear = new ItemInfernalTear();
    public static final ItemKrakenShell krakenShell = new ItemKrakenShell();
    public static final ItemLanternOfParanoia lanterOfParanoia = new ItemLanternOfParanoia();
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

    public static void init() {
        GameRegistry.registerItem(alkahestryTome, Reference.DOMAIN + Names.alkahestry_tome);
        GameRegistry.registerItem(mobIngredient, Reference.DOMAIN + Names.mob_ingredient);
        GameRegistry.registerItem(mercyCross, Reference.DOMAIN + Names.mercy_cross);
        GameRegistry.registerItem(angelheartVial, Reference.DOMAIN + Names.angelheart_vial);
        GameRegistry.registerItem(angelicFeather, Reference.DOMAIN + Names.angelic_feather);
        GameRegistry.registerItem(attractionPotion, Reference.DOMAIN + Names.attraction_potion);
        GameRegistry.registerItem(bullet, Reference.DOMAIN + Names.bullet);
        GameRegistry.registerItem(destructionCatalyst, Reference.DOMAIN + Names.destruction_catalyst);
        GameRegistry.registerItem(emperorChalice, Reference.DOMAIN + Names.emperor_chalice);
        GameRegistry.registerItem(enderStaff, Reference.DOMAIN + Names.ender_staff);
        GameRegistry.registerItem(fertilePotion, Reference.DOMAIN + Names.fertile_potion);
        GameRegistry.registerItem(fortuneCoin, Reference.DOMAIN + Names.fortune_coin);
        GameRegistry.registerItem(glacialStaff, Reference.DOMAIN + Names.glacial_staff);
        GameRegistry.registerItem(glowingBread, Reference.DOMAIN + Names.glowing_bread);
        GameRegistry.registerItem(glowingWater, Reference.DOMAIN + Names.glowing_water);
        GameRegistry.registerItem(gunPart, Reference.DOMAIN + Names.gun_part);
        GameRegistry.registerItem(handgun, Reference.DOMAIN + Names.handgun);
        GameRegistry.registerItem(harvestRod, Reference.DOMAIN + Names.harvest_rod);
        GameRegistry.registerItem(heartPearl, Reference.DOMAIN + Names.heart_pearl);
        GameRegistry.registerItem(heartZhu, Reference.DOMAIN + Names.heart_zhu);
        GameRegistry.registerItem(heroMedallion, Reference.DOMAIN + Names.hero_medallion);
        GameRegistry.registerItem(holyHandGrenade, Reference.DOMAIN + Names.holy_hand_grenade);
        GameRegistry.registerItem(iceRod, Reference.DOMAIN + Names.ice_magus_rod);
        GameRegistry.registerItem(infernalChalice, Reference.DOMAIN + Names.infernal_chalice);
        GameRegistry.registerItem(infernalClaws, Reference.DOMAIN + Names.infernal_claws);
        GameRegistry.registerItem(infernalTear, Reference.DOMAIN + Names.infernal_tear);
        GameRegistry.registerItem(krakenShell, Reference.DOMAIN + Names.kraken_shell);
        GameRegistry.registerItem(lanterOfParanoia, Reference.DOMAIN + Names.lantern_of_paranoia);
        GameRegistry.registerItem(magazine, Reference.DOMAIN + Names.magazine);
        GameRegistry.registerItem(magicbane, Reference.DOMAIN + Names.magicbane);
        GameRegistry.registerItem(midasTouchstone, Reference.DOMAIN + Names.midas_touchstone);
        GameRegistry.registerItem(phoenixDown, Reference.DOMAIN + Names.phoenix_down);
        GameRegistry.registerItem(pyromancerStaff, Reference.DOMAIN + Names.pyromancer_staff);
        GameRegistry.registerItem(rendingGale, Reference.DOMAIN + Names.rending_gale);
        GameRegistry.registerItem(rodOfLyssa, Reference.DOMAIN + Names.rod_of_lyssa);
        GameRegistry.registerItem(salamanderEye, Reference.DOMAIN + Names.salamander_eye);
        GameRegistry.registerItem(serpentStaff, Reference.DOMAIN + Names.serpent_staff);
        GameRegistry.registerItem(shearsOfWinter, Reference.DOMAIN + Names.shears_of_winter);
        GameRegistry.registerItem(sojournerStaff, Reference.DOMAIN + Names.sojourner_staff);
        GameRegistry.registerItem(twilightCloak, Reference.DOMAIN + Names.twilight_cloak);
        GameRegistry.registerItem(emptyVoidTear, Reference.DOMAIN + Names.void_tear_empty);
        GameRegistry.registerItem(filledVoidTear, Reference.DOMAIN + Names.void_tear);
        GameRegistry.registerItem(witchHat, Reference.DOMAIN + Names.witch_hat);
        GameRegistry.registerItem(witherlessRose, Reference.DOMAIN + Names.witherless_rose);
    }

    public static void registerItemModels() {
        registerItemModel(alkahestryTome, Names.alkahestry_tome);
        registerItemModel(mercyCross, Names.mercy_cross);
        registerItemModel(angelheartVial, Names.angelheart_vial);
        registerItemModel(angelicFeather, Names.angelic_feather);
        registerItemModel(attractionPotion, Names.attraction_potion);
        registerItemModel(destructionCatalyst, Names.destruction_catalyst);
        registerItemModel(emperorChalice, Names.emperor_chalice);
        registerItemModel(enderStaff, Names.ender_staff);
        registerItemModel(fertilePotion, Names.fertile_potion);
        registerItemModel(fortuneCoin, Names.fortune_coin);
        registerItemModel(glacialStaff, Names.glacial_staff);
        registerItemModel(glowingBread, Names.glowing_bread);
        registerItemModel(glowingWater, Names.glowing_water);
        registerItemModel(handgun, Names.handgun);
        registerItemModel(harvestRod, Names.harvest_rod);
        registerItemModel(heroMedallion, Names.hero_medallion);
        registerItemModel(holyHandGrenade, Names.holy_hand_grenade);
        registerItemModel(iceRod, Names.ice_magus_rod);
        registerItemModel(infernalChalice, Names.infernal_chalice);
        registerItemModel(infernalClaws, Names.infernal_claws);
        registerItemModel(krakenShell, Names.kraken_shell);
        registerItemModel(lanterOfParanoia, Names.lantern_of_paranoia);
        registerItemModel(magicbane, Names.magicbane);
        registerItemModel(midasTouchstone, Names.midas_touchstone);
        registerItemModel(phoenixDown, Names.phoenix_down);
        registerItemModel(pyromancerStaff, Names.pyromancer_staff);
        registerItemModel(rendingGale, Names.rending_gale);
        registerItemModel(salamanderEye, Names.salamander_eye);
        registerItemModel(serpentStaff, Names.serpent_staff);
        registerItemModel(shearsOfWinter, Names.shears_of_winter);
        registerItemModel(sojournerStaff, Names.sojourner_staff);
        registerItemModel(twilightCloak, Names.twilight_cloak);

        registerItemModel(emptyVoidTear, Names.void_tear_empty);
        registerItemModel(filledVoidTear, Names.void_tear);

        registerItemModel(witchHat, Names.witch_hat);
        registerItemModel(witherlessRose, Names.witherless_rose);

        registerItemModel(infernalTear, Names.infernal_tear_empty);
        ModelBakery.registerItemVariants(infernalTear, ItemModels.getInstance().getModel(ItemModels.INFERNAL_TEAR));

        registerItemModel(rodOfLyssa, Names.rod_of_lyssa);
        ModelBakery.registerItemVariants(rodOfLyssa, ItemModels.getInstance().getModel(ItemModels.ROD_OF_LYSSA_CAST));

        for (int i=0; i< 16; i++) {
            registerItemModel(mobIngredient, Names.mob_ingredient, i, true);
        }

        for (int i=0; i< 3; i++) {
            registerItemModel(gunPart, Names.gun_part, i, true);
        }

        registerItemModelForAllVariants(bullet, Names.bullet, 10, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation(Reference.DOMAIN + Names.bullet, "inventory");
            }
        });

        registerItemModelForAllVariants(heartPearl, "heart_pearl", 4, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation(Reference.DOMAIN + Names.heart_pearl, "inventory");
            }
        });

        registerItemModelForAllVariants(heartZhu, "heart_zhu", 4, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation(Reference.DOMAIN + Names.heart_zhu, "inventory");
            }
        });

        registerItemModelForAllVariants(magazine, "magazine", 10, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation(Reference.DOMAIN + Names.magazine, "inventory");
            }
        });

    }
    private static void registerItemModelForAllVariants(Item item, String resourceName, int numberOfVariants, ItemMeshDefinition itemMeshDefinition) {

        resourceName = Reference.DOMAIN + resourceName;

        ModelBakery.registerItemVariants(item, new ResourceLocation(resourceName));

        ModelLoader.setCustomMeshDefinition( item, itemMeshDefinition);
    }

    private static void registerItemModel(Item item, String resourceName) {
        registerItemModel(item, resourceName, 0, false);
    }

    private static void registerItemModel(Item item, String resourceName, int meta, boolean hasSubTypes){
        if (hasSubTypes) {
            resourceName = resourceName + "_" + meta;
        }

        resourceName = Reference.DOMAIN + resourceName;

        ModelLoader.setCustomModelResourceLocation(item, meta,  new ModelResourceLocation(resourceName, "inventory"));
    }
}