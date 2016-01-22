package xreliquary.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.items.*;
import xreliquary.items.block.ItemFertileLilyPad;
import xreliquary.lib.Reference;

public class ModItems {
    public static final ItemFertileLilyPad fertileLilypad = new ItemFertileLilyPad(ModBlocks.fertileLilypad);
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
    public static final ItemIceRod iceRod = new ItemIceRod();
    public static final ItemGlowingBread glowingBread = new ItemGlowingBread();
    public static final ItemGlowingWater glowingWater = new ItemGlowingWater();
    public static final ItemHolyHandGrenade holyHandGrenade = new ItemHolyHandGrenade();
    public static final ItemKrakenShell krakenShell = new ItemKrakenShell();
    public static final ItemSerpentStaff serpentStaff = new ItemSerpentStaff();
    public static final ItemGunPart gunPart = new ItemGunPart();
    public static final ItemHandgun handgun = new ItemHandgun();
    public static final ItemHarvestRod harvestRod = new ItemHarvestRod();
    public static final ItemMagazine magazine = new ItemMagazine();
    public static final ItemHeartPearl heartPearl = new ItemHeartPearl();

    public static void init() {
        //TODO: cleanup magic strings
        GameRegistry.registerItem(alkahestryTome, Reference.DOMAIN + "alkahestry_tome");
        GameRegistry.registerItem(mobIngredient, Reference.DOMAIN + "mob_ingredient");
        GameRegistry.registerItem(mercyCross, Reference.DOMAIN + "mercy_cross");
        GameRegistry.registerItem(angelheartVial, Reference.DOMAIN + "angelheart_vial");
        GameRegistry.registerItem(angelicFeather, Reference.DOMAIN + "angelic_feather");
        GameRegistry.registerItem(attractionPotion, Reference.DOMAIN + "attraction_potion");
        GameRegistry.registerItem(bullet, Reference.DOMAIN + "bullet");
        GameRegistry.registerItem(destructionCatalyst, Reference.DOMAIN + "destruction_catalyst");
        GameRegistry.registerItem(emperorChalice, Reference.DOMAIN + "emperor_chalice");
        GameRegistry.registerItem(enderStaff, Reference.DOMAIN + "ender_staff");
        GameRegistry.registerItem(fertilePotion, Reference.DOMAIN + "fertile_potion");
        GameRegistry.registerItem(fortuneCoin, Reference.DOMAIN + "fortune_coin");
        GameRegistry.registerItem(glacialStaff, Reference.DOMAIN + "glacial_staff");
        GameRegistry.registerItem(iceRod, Reference.DOMAIN + "ice_rod");
        GameRegistry.registerItem(glowingBread, Reference.DOMAIN + "glowing_bread");
        GameRegistry.registerItem(glowingWater, Reference.DOMAIN + "glowing_water");
        GameRegistry.registerItem(gunPart, Reference.DOMAIN + "gun_part");
        GameRegistry.registerItem(handgun, Reference.DOMAIN + "handgun");
        GameRegistry.registerItem(harvestRod, Reference.DOMAIN + "harvest_rod");
        GameRegistry.registerItem(heartPearl, Reference.DOMAIN + "heart_pearl");

        GameRegistry.registerItem(holyHandGrenade, Reference.DOMAIN + "holy_hand_grenade");

        GameRegistry.registerItem(krakenShell, Reference.DOMAIN + "kraken_shell");
        GameRegistry.registerItem(magazine, Reference.DOMAIN + "magazine");

        GameRegistry.registerItem(serpentStaff, Reference.DOMAIN + "serpent_staff");
    }

    public static void initModels() {
        //TODO: get rid of these magic strings
        registerItemModel(alkahestryTome, "alkahestry_tome");
        registerItemModel(mercyCross, "mercy_cross");
        registerItemModel(angelheartVial, "angelheart_vial");
        registerItemModel(angelicFeather, "angelic_feather");
        registerItemModel(attractionPotion, "attraction_potion");
        registerItemModel(destructionCatalyst, "destruction_catalyst");
        registerItemModel(emperorChalice, "emperor_chalice");
        registerItemModel(enderStaff, "ender_staff");
        registerItemModel(fertilePotion, "fertile_potion");
        registerItemModel(fortuneCoin, "fortune_coin");
        registerItemModel(glacialStaff, "glacial_staff");
        registerItemModel(iceRod, "ice_rod");
        registerItemModel(glowingBread, "glowing_bread");
        registerItemModel(glowingWater, "glowing_water");
        registerItemModel(handgun, "handgun");
        registerItemModel(harvestRod, "harvest_rod");
        registerItemModel(holyHandGrenade, "holy_hand_grenade");
        registerItemModel(krakenShell, "kraken_shell");
        registerItemModel(serpentStaff, "serpent_staff");

        for (int i=0; i< 16; i++) {
            registerItemModel(mobIngredient, "mob_ingredient", i, true);
        }

        for (int i=0; i< 3; i++) {
            registerItemModel(gunPart, "gun_part", i, true);
        }

        registerItemModelForAllVariants(bullet, "bullet", 10, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation("xreliquary:bullet", "inventory");
            }
        });

        registerItemModelForAllVariants(heartPearl, "heart_pearl", 4, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation("xreliquary:heart_pearl", "inventory");
            }
        });

        registerItemModelForAllVariants(magazine, "magazine", 10, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation("xreliquary:magazine", "inventory");
            }
        });

    }
    private static void registerItemModelForAllVariants(Item item, String resourceName, int numberOfVariants, ItemMeshDefinition itemMeshDefinition) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, itemMeshDefinition);

    }
    private static void registerItemModel(Item item, String resourceName) {
        registerItemModel(item, resourceName, 0, false);
    }
    private static void registerItemModel(Item item, String resourceName, int meta, boolean hasSubTypes){
        //TODO: figure out what is the best way to register item models

        if (hasSubTypes) {
            resourceName = resourceName + "_" + meta;
        }

        resourceName = Reference.DOMAIN + resourceName;

        ModelBakery.registerItemVariants(item, new ResourceLocation(resourceName));

        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(item, meta, new ModelResourceLocation(resourceName, "inventory"));
    }


}