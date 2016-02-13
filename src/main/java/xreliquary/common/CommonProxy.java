package xreliquary.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import xreliquary.Reliquary;
import xreliquary.common.gui.GUIHandler;
import xreliquary.compat.CompatibilityLoader;
import xreliquary.entities.*;
import xreliquary.entities.potion.EntityAttractionPotion;
import xreliquary.entities.potion.EntityFertilePotion;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.entities.shot.*;
import xreliquary.event.CommonEventHandler;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.init.ModBlocks;
import xreliquary.init.XRRecipes;


public class CommonProxy {

//TODO: rewrite proxy to the EE style so that it has area specific method names rather than generic preInit/init/postInit
    public void preInit() {
        try {
            XRRecipes.init();

            ModBlocks.initTileEntities();
        } catch (Exception e) {
            e.printStackTrace();
            FMLCommonHandler.instance().raiseException(e, "Reliquary failed to initiate recipes.", true);
        }

        //TODO: figure out what this commented out section is for / is it just old code to be removed?
//        try {
//            Potion[] potionTypes = null;
//
//            for (Field f : Potion.class.getDeclaredFields()) {
//                f.setAccessible(true);
//                try {
//                    if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
//                        Field modfield = Field.class.getDeclaredField("modifiers");
//                        modfield.setAccessible(true);
//                        modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);
//
//                        potionTypes = (Potion[]) f.get(null);
//                        final Potion[] newPotionTypes = new Potion[256];
//                        System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
//                        f.set(null, newPotionTypes);
//                    }
//                } catch (Exception e) {
//                    System.err.println("XReliquary Reflection error. This is a serious bug due to our custom potion effects.");
//                    System.err.println(e);
//                }
//            }
//        }
//        catch (Exception e) {
//            System.err.println("Reliquary failed trying to reflect custom potion effects!");
//            System.err.println(e);
//        }

        //FluidContainerRegistry.registerFluidContainer(new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME / 8), XRRecipes.potion(Reference.WATER_META), XRRecipes.potion(Reference.EMPTY_VIAL_META));
    }


    public void init() {
        MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());

        NetworkRegistry.INSTANCE.registerGuiHandler(Reliquary.INSTANCE, new GUIHandler());
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
        MinecraftForge.EVENT_BUS.register(new CompatibilityLoader());

        this.registerEntities();
    }


    public void postInit() {

    }

    //TODO: figure out if we need recipe disablers (guess looking at the 1.7 modpacks should give us some idea)
/*
    public void initRecipeDisablers() {
        //recipe disablers
        for (int i = 0; i < Reliquary.CONTENT.registeredObjectNames.size(); i++) {
            Reliquary.CONFIG.require(Names.recipe_enabled, Reliquary.CONTENT.registeredObjectNames.get(i).replace(':', '_'), new ConfigReference(true));
        }
    }
*/

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
        EntityRegistry.registerModEntity(EntityAttractionPotion.class, "entitySplashAphrodite", 12, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityThrownXRPotion.class, "entityThrownXRPotion", 13, Reliquary.INSTANCE, 128, 5, true);
        //EntityRegistry.instance().lookupModSpawn(EntityThrownXRPotion.class,false).setCustomSpawning(null, false);
        EntityRegistry.registerModEntity(EntityFertilePotion.class, "entitySplashFertility", 21, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityKrakenSlime.class, "entityKSlime", 22, Reliquary.INSTANCE, 128, 5, true);
        EntityRegistry.registerModEntity(EntityEnderStaffProjectile.class, "entityEnderStaffProjectile", 23, Reliquary.INSTANCE, 128, 5, true);
    }

    public void initColors() {
    }

    public void registerJEI(Item item, String name) {
    }

    public void registerJEI(Block block, String name) {
    }

    public void initPotionsJEI() {
    }
}
