package xreliquary.client.init;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import xreliquary.client.ItemModelLocations;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

public class ItemModels {
    public static void registerItemModels() {
        registerItemModel(ModItems.alkahestryTome, Names.alkahestry_tome);
        registerItemModel(ModItems.mercyCross, Names.mercy_cross);
        registerItemModel(ModItems.angelheartVial, Names.angelheart_vial);
        registerItemModel(ModItems.angelicFeather, Names.angelic_feather);
        registerItemModel(ModItems.attractionPotion, Names.attraction_potion);
        registerItemModel(ModItems.destructionCatalyst, Names.destruction_catalyst);
        registerItemModel(ModItems.emperorChalice, Names.emperor_chalice);
        registerItemModel(ModItems.enderStaff, Names.ender_staff);
        registerItemModel(ModItems.fertilePotion, Names.fertile_potion);
        registerItemModel(ModItems.fortuneCoin, Names.fortune_coin);
        registerItemModel(ModItems.glacialStaff, Names.glacial_staff);
        registerItemModel(ModItems.glowingBread, Names.glowing_bread);
        registerItemModel(ModItems.glowingWater, Names.glowing_water);
        registerItemModel(ModItems.handgun, Names.handgun);
        registerItemModel(ModItems.harvestRod, Names.harvest_rod);
        registerItemModel(ModItems.heroMedallion, Names.hero_medallion);
        registerItemModel(ModItems.holyHandGrenade, Names.holy_hand_grenade);
        registerItemModel(ModItems.iceMagusRod, Names.ice_magus_rod);
        registerItemModel(ModItems.infernalChalice, Names.infernal_chalice);
        registerItemModel(ModItems.infernalClaws, Names.infernal_claws);
        registerItemModel(ModItems.krakenShell, Names.kraken_shell);
        registerItemModel(ModItems.lanternOfParanoia, Names.lantern_of_paranoia);
        registerItemModel(ModItems.magicbane, Names.magicbane);
        registerItemModel(ModItems.midasTouchstone, Names.midas_touchstone);
        registerItemModel(ModItems.phoenixDown, Names.phoenix_down);
        registerItemModel(ModItems.pyromancerStaff, Names.pyromancer_staff);
        registerItemModel(ModItems.rendingGale, Names.rending_gale);
        registerItemModel(ModItems.salamanderEye, Names.salamander_eye);
        registerItemModel(ModItems.serpentStaff, Names.serpent_staff);
        registerItemModel(ModItems.shearsOfWinter, Names.shears_of_winter);
        registerItemModel(ModItems.sojournerStaff, Names.sojourner_staff);
        registerItemModel(ModItems.twilightCloak, Names.twilight_cloak);

        registerItemModel(ModItems.emptyVoidTear, Names.void_tear_empty);
        registerItemModel(ModItems.filledVoidTear, Names.void_tear);

        registerItemModel(ModItems.witchHat, Names.witch_hat);
        registerItemModel(ModItems.witherlessRose, Names.witherless_rose);


        registerItemModel(ModItems.rodOfLyssa, Names.rod_of_lyssa);
        ModelBakery.registerItemVariants(ModItems.rodOfLyssa, ItemModelLocations.getInstance().getModel(ItemModelLocations.ROD_OF_LYSSA_CAST));

        for (int i=0; i< 16; i++) {
            registerItemModel(ModItems.mobIngredient, Names.mob_ingredient, i, true);
        }

        for (int i=0; i< 3; i++) {
            registerItemModel(ModItems.gunPart, Names.gun_part, i, true);
        }

        registerItemModelForAllVariants(ModItems.bullet, Names.bullet,  new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation(Reference.DOMAIN + Names.bullet, "inventory");
            }
        });

        registerItemModelForAllVariants(ModItems.heartPearl, Names.heart_pearl, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation(Reference.DOMAIN + Names.heart_pearl, "inventory");
            }
        });

        registerItemModelForAllVariants(ModItems.heartZhu, Names.heart_zhu, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation(Reference.DOMAIN + Names.heart_zhu, "inventory");
            }
        });

        registerItemModelForAllVariants(ModItems.potionEssence, Names.potion_essence, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation(Reference.DOMAIN + Names.potion_essence, "inventory");
            }
        });

        registerItemModelForAllVariants(ModItems.infernalTear, Names.infernal_tear_empty, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                if (ModItems.infernalTear.getStackFromTear(stack) != null) {
                    return ItemModelLocations.getInstance().getModel(ItemModelLocations.INFERNAL_TEAR);
                }
                return ItemModelLocations.getInstance().getModel(ItemModelLocations.INFERNAL_TEAR_EMPTY);
            }
        });
        ModelBakery.registerItemVariants(ModItems.infernalTear, ItemModelLocations.getInstance().getModel(ItemModelLocations.INFERNAL_TEAR));

        registerItemModelForAllVariants(ModItems.potion, Names.potion, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                if (ModItems.potion.getSplash(stack)) {
                    return ItemModelLocations.getInstance().getModel(ItemModelLocations.POTION_SPLASH);
                }
                return ItemModelLocations.getInstance().getModel(ItemModelLocations.POTION);
            }
        });
        ModelBakery.registerItemVariants(ModItems.potion, ItemModelLocations.getInstance().getModel(ItemModelLocations.POTION_SPLASH));

        registerItemModelForAllVariants(ModItems.magazine, Names.magazine, new ItemMeshDefinition() {
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation(Reference.DOMAIN + Names.magazine, "inventory");
            }
        });
    }
    private static void registerItemModelForAllVariants(Item item, String resourceName, ItemMeshDefinition itemMeshDefinition) {

        resourceName = Reference.DOMAIN + resourceName;

        ModelBakery.registerItemVariants(item, new ResourceLocation(resourceName));

        ModelLoader.setCustomMeshDefinition(item, itemMeshDefinition);
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
