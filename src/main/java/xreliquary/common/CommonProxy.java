package xreliquary.common;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.mod.config.ConfigReference;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
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
import xreliquary.entities.potion.*;
import xreliquary.entities.shot.*;
import xreliquary.event.CommonEventHandler;
import xreliquary.init.XRRecipes;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.items.alkahestry.AlkahestryCraftingRecipe;
import xreliquary.items.alkahestry.AlkahestryDrainRecipe;
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
                    System.err.println("XReliquary Reflection error. This is a serious bug due to our custom potion effects.");
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

        AlkahestryCraftingRecipe alkahestryCraftingRecipeHandler = new AlkahestryCraftingRecipe();
        AlkahestryDrainRecipe alkahestryDrainRecipeHandler = new AlkahestryDrainRecipe();

        MinecraftForge.EVENT_BUS.register(alkahestryCraftingRecipeHandler);
        MinecraftForge.EVENT_BUS.register(alkahestryDrainRecipeHandler);

        FMLCommonHandler.instance().bus().register(alkahestryCraftingRecipeHandler);
        FMLCommonHandler.instance().bus().register(alkahestryDrainRecipeHandler);

        NetworkRegistry.INSTANCE.registerGuiHandler(Reliquary.INSTANCE, new GUIHandler());
        FMLCommonHandler.instance().bus().register(new CommonEventHandler());
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());

        this.registerEntities();
        this.registerTileEntities();
    }

    public void initOptions() {
        int itemCap = 9999;
        int cleanShortMax = 30000;
        int cleanIntMax = 2000000000;

        //global HUD positions
        Reliquary.CONFIG.require(Names.hud_positions, Names.sojourner_staff, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.handgun, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.alkahestry_tome, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.destruction_catalyst, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.elsewhere_flask, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.ender_staff, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.ice_magus_rod, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.glacial_staff, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.void_tear, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.midas_touchstone, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.harvest_rod, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.infernal_chalice, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.hero_medallion, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.pyromancer_staff, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));
        Reliquary.CONFIG.require(Names.hud_positions, Names.rending_gale, new ConfigReference(3).setMinimumValue(1).setMaximumValue(4));

        //easy mode recipes
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.fortune_coin, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.altar, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.infernal_chalice, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.ender_staff, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.salamander_eye, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.rod_of_lyssa, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.serpent_staff, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.rending_gale, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.pyromancer_staff, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.magicbane, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.lantern_of_paranoia, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.alkahestry_tome, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.wraith_node, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.glacial_staff, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.sojourner_staff, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.kraken_shell, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.angelic_feather, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.emperor_chalice, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.hero_medallion, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.ice_magus_rod, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.infernal_claws, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.destruction_catalyst, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.interdiction_torch, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.void_tear, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.infernal_tear, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.fertile_essence, new ConfigReference(false));
        Reliquary.CONFIG.require(Names.easy_mode_recipes, Names.seeker_shot, new ConfigReference(false));

        //mob drop addition probabilities
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.zombie_heart + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.zombie_heart + "_looting", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.pigman_heart + "_base", new ConfigReference(15));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.pigman_heart + "_looting", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.rib_bone + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.rib_bone + "_looting", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.withered_rib + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.withered_rib + "_looting", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.spider_fangs + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.spider_fangs + "_looting", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.cave_spider_fangs + "_base", new ConfigReference(15));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.cave_spider_fangs + "_looting", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.blaze_molten_core + "_base", new ConfigReference(15));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.blaze_molten_core + "_looting", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.magma_cube_molten_core + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.magma_cube_molten_core + "_looting", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.frozen_core + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.frozen_core + "_looting", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.eye_of_the_storm + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.eye_of_the_storm + "_looting", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.bat_wing + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.bat_wing + "_looting", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.creeper_gland + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.creeper_gland + "_looting", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.ghast_gland + "_base", new ConfigReference(15));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.ghast_gland + "_looting", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.witch_hat + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.witch_hat + "_looting", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.squid_beak + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.squid_beak + "_looting", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.slime_pearl + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.slime_pearl+ "_looting", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.ender_heart + "_base", new ConfigReference(10));
        Reliquary.CONFIG.require(Names.mob_drop_probability, Names.ender_heart + "_looting", new ConfigReference(5));

        //alkahestry tome configs
        Reliquary.CONFIG.require(Names.alkahestry_tome, "redstone_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));

        //altar configs
        Reliquary.CONFIG.require(Names.altar, "redstone_cost", new ConfigReference(3).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.altar, "time_in_minutes", new ConfigReference(20).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.altar, "maximum_time_variance_in_minutes", new ConfigReference(5).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.altar, "output_light_level_while_active", new ConfigReference(16).setMaximumValue(16).setMinimumValue(0));

        //angelic feather configs
        Reliquary.CONFIG.require(Names.angelic_feather, "hunger_cost_percent", new ConfigReference(50).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.angelic_feather, "leaping_potency", new ConfigReference(1).setMinimumValue(0).setMaximumValue(5));

        //angelheart vial configs
        Reliquary.CONFIG.require(Names.angelheart_vial, "heal_percentage_of_max_life", new ConfigReference(25));
        Reliquary.CONFIG.require(Names.angelheart_vial, "remove_negative_status", new ConfigReference(true));

        //destruction catalyst configs
        Reliquary.CONFIG.require(Names.destruction_catalyst, "mundane_blocks", new ConfigReference(new ArrayList<String>(ItemDestructionCatalyst.ids)));
        Reliquary.CONFIG.require(Names.destruction_catalyst, "gunpowder_cost", new ConfigReference(3).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.destruction_catalyst, "gunpowder_worth", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.destruction_catalyst, "gunpowder_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
        Reliquary.CONFIG.require(Names.destruction_catalyst, "explosion_radius", new ConfigReference(1).setMinimumValue(1).setMaximumValue(5));
        Reliquary.CONFIG.require(Names.destruction_catalyst, "centered_explosion", new ConfigReference(false));
        Reliquary.CONFIG.require(Names.destruction_catalyst, "perfect_cube", new ConfigReference(true));

        //emperor's chalice configs
        Reliquary.CONFIG.require(Names.emperor_chalice, "hunger_satiation_multiplier", new ConfigReference(4).setMinimumValue(0));

        //ender staff configs
        Reliquary.CONFIG.require(Names.ender_staff, "ender_pearl_cast_cost", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.ender_staff, "ender_pearl_node_warp_cost", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.ender_staff, "ender_pearl_worth", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.ender_staff, "ender_pearl_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
        Reliquary.CONFIG.require(Names.ender_staff, "node_warp_cast_time", new ConfigReference(60).setMinimumValue(10));

        //fortune coin configs
        Reliquary.CONFIG.require(Names.fortune_coin, "disable_audio", new ConfigReference(false));
        Reliquary.CONFIG.require(Names.fortune_coin, "standard_pull_distance", new ConfigReference(5));
        Reliquary.CONFIG.require(Names.fortune_coin, "long_range_pull_distance", new ConfigReference(15));

        //glacial staff configs
        Reliquary.CONFIG.require(Names.glacial_staff, "snowball_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
        Reliquary.CONFIG.require(Names.glacial_staff, "snowball_cost", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.glacial_staff, "snowball_worth", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.glacial_staff, "snowball_damage", new ConfigReference(3).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.glacial_staff, "snowball_damage_bonus_fire_immune", new ConfigReference(3).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.glacial_staff, "snowball_damage_bonus_blaze", new ConfigReference(6).setMinimumValue(0));

        //harvest rod configs
        Reliquary.CONFIG.require(Names.harvest_rod, "bonemeal_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
        Reliquary.CONFIG.require(Names.harvest_rod, "bonemeal_cost", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.harvest_rod, "bonemeal_worth", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.harvest_rod, "bonemeal_luck_percent_chance", new ConfigReference(33).setMinimumValue(1).setMaximumValue(100));
        Reliquary.CONFIG.require(Names.harvest_rod, "bonemeal_luck_rolls", new ConfigReference(2).setMinimumValue(0).setMaximumValue(7));
        Reliquary.CONFIG.require(Names.harvest_rod, "harvest_break_radius", new ConfigReference(2).setMinimumValue(0).setMaximumValue(5));

        //hero's medallion config
        Reliquary.CONFIG.require(Names.hero_medallion, "experience_level_maximum", new ConfigReference(30).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.hero_medallion, "experience_level_minimum", new ConfigReference(0).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.hero_medallion, "experience_limit", new ConfigReference(cleanIntMax).setMinimumValue(0).setMaximumValue(cleanIntMax));

        //ice rod configs
        Reliquary.CONFIG.require(Names.ice_magus_rod, "snowball_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
        Reliquary.CONFIG.require(Names.ice_magus_rod, "snowball_cost", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.ice_magus_rod, "snowball_worth", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.ice_magus_rod, "snowball_damage", new ConfigReference(2).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.ice_magus_rod, "snowball_damage_bonus_fire_immune", new ConfigReference(2).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.ice_magus_rod, "snowball_damage_bonus_blaze", new ConfigReference(4).setMinimumValue(0));

        //infernal claws configs
        Reliquary.CONFIG.require(Names.infernal_claws, "hunger_cost_percent", new ConfigReference(10).setMinimumValue(0));

        //infernal chalice configs
        Reliquary.CONFIG.require(Names.infernal_chalice, "hunger_cost_percent", new ConfigReference(5).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.infernal_chalice, "fluid_limit", new ConfigReference(500000).setMinimumValue(0).setMaximumValue(cleanIntMax));

        //interdiction torch configs
        //see post init for entity configs
        Reliquary.CONFIG.require(Names.interdiction_torch, "push_radius", new ConfigReference(5).setMinimumValue(1).setMaximumValue(15));
        Reliquary.CONFIG.require(Names.interdiction_torch, "can_push_projectiles", new ConfigReference(false));

        //kraken shell configs
        Reliquary.CONFIG.require(Names.kraken_shell, "hunger_cost_percent", new ConfigReference(25).setMinimumValue(0));

        //lantern of paranoia configs
        Reliquary.CONFIG.require(Names.lantern_of_paranoia, "min_light_level", new ConfigReference(8).setMinimumValue(0).setMaximumValue(15));
        Reliquary.CONFIG.require(Names.lantern_of_paranoia, "placement_scan_radius", new ConfigReference(6).setMinimumValue(1).setMaximumValue(15));
        //Reliquary.CONFIG.require(Names.lantern_of_paranoia, "only_place_on_visible_blocks", new ConfigReference(false));

        //lilypad of fertility configs
        Reliquary.CONFIG.require(Names.lilypad, "seconds_between_growth_ticks", new ConfigReference(47).setMinimumValue(1));
        Reliquary.CONFIG.require(Names.lilypad, "tile_range", new ConfigReference(4).setMinimumValue(1).setMaximumValue(15));
        Reliquary.CONFIG.require(Names.lilypad, "full_potency_range", new ConfigReference(1).setMinimumValue(1).setMaximumValue(15));

        //midas touchstone configs
        List<String> goldItems = ImmutableList.of();
        Reliquary.CONFIG.require(Names.midas_touchstone, "gold_items", new ConfigReference(goldItems));
        Reliquary.CONFIG.require(Names.midas_touchstone, "ticks_between_repair_ticks", new ConfigReference(4).setMinimumValue(1).setMaximumValue(cleanShortMax));
        Reliquary.CONFIG.require(Names.midas_touchstone, "glowstone_cost", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.midas_touchstone, "glowstone_worth", new ConfigReference(4).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.midas_touchstone, "glowstone_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));

        //phoenix down configs
        Reliquary.CONFIG.require(Names.phoenix_down, "hunger_cost_percent", new ConfigReference(25).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.phoenix_down, "leaping_potency", new ConfigReference(1).setMinimumValue(0).setMaximumValue(5));
        Reliquary.CONFIG.require(Names.phoenix_down, "heal_percentage_of_max_life", new ConfigReference(100));
        Reliquary.CONFIG.require(Names.phoenix_down, "remove_negative_status", new ConfigReference(true));
        Reliquary.CONFIG.require(Names.phoenix_down, "give_temporary_damage_resistance", new ConfigReference(true));
        Reliquary.CONFIG.require(Names.phoenix_down, "give_temporary_regeneration", new ConfigReference(true));
        Reliquary.CONFIG.require(Names.phoenix_down, "give_temporary_fire_resistance_if_fire_damage_killed_you", new ConfigReference(true));
        Reliquary.CONFIG.require(Names.phoenix_down, "give_temporary_water_breathing_if_drowning_killed_you", new ConfigReference(true));

        //pyromancer staff configs
        Reliquary.CONFIG.require(Names.pyromancer_staff, "hunger_cost_percent", new ConfigReference(5).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.pyromancer_staff, "fire_charge_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
        Reliquary.CONFIG.require(Names.pyromancer_staff, "fire_charge_cost", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.pyromancer_staff, "fire_charge_worth", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.pyromancer_staff, "ghast_absorb_worth", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.pyromancer_staff, "blaze_powder_limit", new ConfigReference(250).setMinimumValue(0).setMaximumValue(itemCap));
        Reliquary.CONFIG.require(Names.pyromancer_staff, "blaze_powder_cost", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.pyromancer_staff, "blaze_powder_worth", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.pyromancer_staff, "blaze_absorb_worth", new ConfigReference(1).setMinimumValue(0));

        //rending gale configs
        Reliquary.CONFIG.require(Names.rending_gale, "charge_limit", new ConfigReference(cleanShortMax).setMinimumValue(0).setMaximumValue(cleanIntMax));
        Reliquary.CONFIG.require(Names.rending_gale, "cast_charge_cost", new ConfigReference(1).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.rending_gale, "bolt_charge_cost", new ConfigReference(100).setMinimumValue(0));
        Reliquary.CONFIG.require(Names.rending_gale, "charge_feather_worth", new ConfigReference(100).setMinimumValue(1));
        Reliquary.CONFIG.require(Names.rending_gale, "block_target_range", new ConfigReference(12).setMaximumValue(15));
        Reliquary.CONFIG.require(Names.rending_gale, "push_pull_radius", new ConfigReference(10).setMinimumValue(1));
        Reliquary.CONFIG.require(Names.rending_gale, "can_push_projectiles", new ConfigReference(false));

        //rod of lyssa configs
        Reliquary.CONFIG.require(Names.rod_of_lyssa, "use_leveled_failure_rate", new ConfigReference(true));
        Reliquary.CONFIG.require(Names.rod_of_lyssa, "level_cap_for_leveled_formula", new ConfigReference(100).setMinimumValue(1).setMaximumValue(900));
        Reliquary.CONFIG.require(Names.rod_of_lyssa, "flat_steal_failure_percent_rate", new ConfigReference(10).setMinimumValue(0).setMaximumValue(100));
        Reliquary.CONFIG.require(Names.rod_of_lyssa, "steal_from_vacant_slots", new ConfigReference(true));
        Reliquary.CONFIG.require(Names.rod_of_lyssa, "fail_steal_from_vacant_slots", new ConfigReference(false));
        Reliquary.CONFIG.require(Names.rod_of_lyssa, "anger_on_steal_failure", new ConfigReference(true));

        //sojourners staff configs
        List<String> torches = ImmutableList.of();
        Reliquary.CONFIG.require(Names.sojourner_staff, "torches", new ConfigReference(torches));
        Reliquary.CONFIG.require(Names.sojourner_staff, "max_capacity_per_item_type", new ConfigReference(1500).setMinimumValue(1).setMaximumValue(itemCap));
        Reliquary.CONFIG.require(Names.sojourner_staff, "max_range", new ConfigReference(30).setMinimumValue(1).setMaximumValue(30));
        Reliquary.CONFIG.require(Names.sojourner_staff, "tile_per_cost_multiplier", new ConfigReference(6).setMinimumValue(6).setMaximumValue(30));

        //twilight cloak configs
        Reliquary.CONFIG.require(Names.twilight_cloak, "max_light_level", new ConfigReference(4).setMinimumValue(0).setMaximumValue(15));
        //Reliquary.CONFIG.require(Names.twilight_cloak, "only_works_at_night", new ConfigReference(false));

        //void tear configs
        Reliquary.CONFIG.require(Names.void_tear, "item_limit", new ConfigReference(2000000000).setMinimumValue(0).setMaximumValue(cleanIntMax));
        Reliquary.CONFIG.require(Names.void_tear, "absorb_when_created", new ConfigReference(true));
    }

    public void postInit() {
        List<String> entityNames = new ArrayList<String>();
        for (Object o : EntityList.stringToClassMapping.values()) {
            Class c = (Class)o;
            if (EntityLiving.class.isAssignableFrom(c)) {
                entityNames.add((String)EntityList.classToStringMapping.get(o));
            }
        }
        List<String> projectileNames = new ArrayList<String>();
        for (Object o : EntityList.stringToClassMapping.values()) {
            Class c = (Class)o;
            if (IProjectile.class.isAssignableFrom(c)) {
                projectileNames.add((String)EntityList.classToStringMapping.get(o));
            }
        }

        Reliquary.CONFIG.require(Names.interdiction_torch, "entities_that_can_be_pushed", new ConfigReference(entityNames));
        Reliquary.CONFIG.require(Names.interdiction_torch, "projectiles_that_can_be_pushed", new ConfigReference(projectileNames));

        Reliquary.CONFIG.require(Names.rending_gale, "entities_that_can_be_pushed", new ConfigReference(entityNames));
        Reliquary.CONFIG.require(Names.rending_gale, "projectiles_that_can_be_pushed", new ConfigReference(projectileNames));

        Reliquary.CONFIG.require(Names.seeker_shot, "entities_that_can_be_hunted", new ConfigReference(entityNames));

    }

    public void initRecipeDisablers() {
        //recipe disablers
        for (int i = 0; i < ContentHandler.registeredObjectNames.size(); i++) {
            Reliquary.CONFIG.require(Names.recipe_enabled, ContentHandler.registeredObjectNames.get(i).replace(':', '_'), new ConfigReference(true));
        }
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
