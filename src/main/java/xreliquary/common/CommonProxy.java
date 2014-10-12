package xreliquary.common;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.mod.config.ConfigReference;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityAltar;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.common.gui.GUIHandler;
import xreliquary.entities.*;
import xreliquary.event.CommonEventHandler;
import xreliquary.init.XRRecipes;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.items.alkahestry.AlkahestryCraftingRecipe;
import xreliquary.items.alkahestry.AlkahestryRedstoneRecipe;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.alkahestry.Alkahestry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class CommonProxy {


    public void preInit() {
        try {
            XRRecipes.init();
            Alkahestry.init();
        } catch (Exception e) {
            e.printStackTrace();
            FMLCommonHandler.instance().raiseException(e, "Reliquary failed to initiate recipies.", true);
        }

        try {
            Potion[] potionTypes = null;

            for (Field f : Potion.class.getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
                        Field modfield = Field.class.getDeclaredField("modifiers");
                        modfield.setAccessible(true);
                        modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);

                        potionTypes = (Potion[]) f.get(null);
                        final Potion[] newPotionTypes = new Potion[256];
                        System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
                        f.set(null, newPotionTypes);
                    }
                } catch (Exception e) {
                    System.err.println("XReliquary Reflection error. This is a serious bug.");
                    System.err.println(e);
                }
            }
        }
        catch (Exception e) {
            System.err.println("Reliquary failed trying to reflect custom potion effects!");
            System.err.println(e);
        }

        FluidContainerRegistry.registerFluidContainer(new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME / 8), XRRecipes.potion(Reference.WATER_META), XRRecipes.potion(Reference.EMPTY_VIAL_META));
    }


    public void init() {
        AlkahestryCraftingRecipe.returnedItem = ContentHandler.getItem(Names.alkahestry_tome);
        AlkahestryRedstoneRecipe.returnedItem = ContentHandler.getItem(Names.alkahestry_tome);

        NetworkRegistry.INSTANCE.registerGuiHandler(Reliquary.INSTANCE, new GUIHandler());
        FMLCommonHandler.instance().bus().register(new CommonEventHandler());
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());

        this.registerEntities();
        this.registerTileEntities();
    }

    public void initOptions() {
        //hero's medallion config
        //Reliquary.CONFIG.require(Names.hero_medallion, "experience_level_maximum", new ConfigReference(30).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.hero_medallion, "experience_level_minimum", new ConfigReference(0).setMinimumValue(0));

        //sojourners staff configs
        List<String> torches = ImmutableList.of();
        Reliquary.CONFIG.require(Names.sojourner_staff, "torches", new ConfigReference(torches));
        Reliquary.CONFIG.require(Names.sojourner_staff, "max_capacity_per_item_type", new ConfigReference(1500).setMinimumValue(1));
        Reliquary.CONFIG.require(Names.sojourner_staff, "hud_position", new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));

        //ender staff configs
        Reliquary.CONFIG.require(Names.ender_staff, "ender_pearl_cast_cost", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.ender_staff, "ender_pearl_node_warp_cost", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.ender_staff, "ender_pearl_worth", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.ender_staff, "node_warp_cast_time", new ConfigReference(60).setMinimumValue(10));

        //midas touchstone configs
        List<String> goldItems = ImmutableList.of();
        Reliquary.CONFIG.require(Names.midas_touchstone, "gold_items", new ConfigReference(goldItems));
        Reliquary.CONFIG.require(Names.midas_touchstone, "glowstone_cost", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.midas_touchstone, "glowstone_worth", new ConfigReference(4).setMinimumValue(0));

        //destruction catalyst configs
        Reliquary.CONFIG.require(Names.destruction_catalyst, "mundane_blocks", new ConfigReference(new ArrayList<String>(ItemDestructionCatalyst.ids)));
        Reliquary.CONFIG.require(Names.destruction_catalyst, "gunpowder_cost", new ConfigReference(3).setMinimumValue(0));

        //altar configs
        Reliquary.CONFIG.require(Names.altar, "redstone_cost", new ConfigReference(3).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.altar, "time_in_minutes", new ConfigReference(20).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.altar, "maximum_time_variance_in_minutes", new ConfigReference(5).setMinimumValue(0));

        //hunger-consuming damage-absorbing items
        Reliquary.CONFIG.require(Names.infernal_claws, "hunger_cost_percent", new ConfigReference(10).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.infernal_chalice, "hunger_cost_percent", new ConfigReference(5).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.angelic_feather, "hunger_cost_percent", new ConfigReference(50).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.phoenix_down, "hunger_cost_percent", new ConfigReference(25).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.kraken_shell, "hunger_cost_percent", new ConfigReference(25).setMinimumValue(0));

        //angelheart vial and phoenix down effect enablers
        Reliquary.CONFIG.require(Names.angelheart_vial, "heal_percentage_of_max_life", new ConfigReference(25));
        Reliquary.CONFIG.require(Names.angelheart_vial, "remove_negative_status", new ConfigReference(true));
        Reliquary.CONFIG.require(Names.phoenix_down, "heal_percentage_of_max_life", new ConfigReference(100));
        Reliquary.CONFIG.require(Names.phoenix_down, "remove_negative_status", new ConfigReference(true));
        Reliquary.CONFIG.require(Names.phoenix_down, "give_temporary_damage_resistance", new ConfigReference(true));
        Reliquary.CONFIG.require(Names.phoenix_down, "give_temporary_regeneration", new ConfigReference(true));
        Reliquary.CONFIG.require(Names.phoenix_down, "give_temporary_fire_resistance_if_fire_damage_killed_you", new ConfigReference(true));
        Reliquary.CONFIG.require(Names.phoenix_down, "give_temporary_water_breathing_if_drowning_killed_you", new ConfigReference(true));

        //lilypad of fertility configs
        Reliquary.CONFIG.require(Names.lilypad, "time_between_ticks_percent", new ConfigReference(100).setMinimumValue(2));
        Reliquary.CONFIG.require(Names.lilypad, "tile_range", new ConfigReference(4).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.lilypad, "full_potency_range", new ConfigReference(0).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.lilypad, "full_potency_threshold", new ConfigReference(0).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.lilypad, "full_potency_offset", new ConfigReference(7).setMinimumValue(0));

        //misc configs
        Reliquary.CONFIG.require(Names.fortune_coin, "disable_audio", new ConfigReference(false));
        Reliquary.CONFIG.require(Names.emperor_chalice, "hunger_satiation_multiplier", new ConfigReference(4).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.alkahestry_tome, "redstone_limit", new ConfigReference(256).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.twilight_cloak, "max_light_level", new ConfigReference(4).setMinimumValue(0).setMaximumValue(15));
        Reliquary.CONFIG.require(Names.lantern_of_paranoia, "min_light_level", new ConfigReference(8).setMinimumValue(0).setMaximumValue(15));
        Reliquary.CONFIG.require(Names.handgun, "hud_position", new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));

        Reliquary.CONFIG.save();
    }

    public void initRecipeDisablers() {
        //this is the substring(5) version of the unlocalized name!!!
        List<String> sortedObjectNames = ContentHandler.registeredObjectNames.subList(0, ContentHandler.registeredObjectNames.size());
        java.util.Collections.sort(sortedObjectNames);
        for (int i = 0; i < sortedObjectNames.size(); i++) {
            Reliquary.CONFIG.require(Names.recipe_enabled, sortedObjectNames.get(i), new ConfigReference(true));
        }
        Reliquary.CONFIG.save();
    }

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityAltar.class, "reliquaryAltar");
        GameRegistry.registerTileEntity(TileEntityMortar.class, "apothecaryMortar");
        GameRegistry.registerTileEntity(TileEntityCauldron.class, "reliquaryCauldron");
    }

    public void registerEntities() {
        EntityRegistry.registerModEntity(EntityHolyHandGrenade.class, "entityHGrenade", 0, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityGlowingWater.class, "entityHolyWater", 1, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntitySpecialSnowball.class, "entitySpecialSnowball", 2, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityNeutralShot.class, "entityNeutralShot", 3, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityExorcismShot.class, "entityExorcismShot", 4, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityBlazeShot.class, "entityBlazeShot", 5, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityEnderShot.class, "entityEnderShot", 6, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityConcussiveShot.class, "entityConcussiveShot", 7, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityBusterShot.class, "entityBusterShot", 8, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntitySeekerShot.class, "entitySeekerShot", 9, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntitySandShot.class, "entitySandShot", 10, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityStormShot.class, "entityStormShot", 11, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityCondensedSplashAphrodite.class, "entitySplashAphrodite", 12, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityCondensedSplashBlindness.class, "entitySplashBlindness", 13, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityCondensedSplashConfusion.class, "entitySplashConfusion", 14, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityCondensedSplashHarm.class, "entitySplashHarm", 15, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityCondensedSplashPoison.class, "entitySplashPoison", 16, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityCondensedSplashRuin.class, "entitySplashRuin", 17, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityCondensedSplashSlowness.class, "entitySplashSlowness", 18, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityCondensedSplashWeakness.class, "entitySplashWeakness", 19, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityCondensedSplashWither.class, "entitySplashWither", 20, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityCondensedFertility.class, "entitySplashFertility", 21, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityKrakenSlime.class, "entityKSlime", 22, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityEnderStaffProjectile.class, "entityEnderStaffProjectile", 23, Reliquary.INSTANCE, 128, 5, true);
    }

}
