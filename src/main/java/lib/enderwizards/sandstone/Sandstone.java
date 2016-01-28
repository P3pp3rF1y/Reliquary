package lib.enderwizards.sandstone;

import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.Content;
import lib.enderwizards.sandstone.mod.ModIntegration;
import lib.enderwizards.sandstone.mod.ModRegistry;
import lib.enderwizards.sandstone.mod.SandstoneMod;
import lib.enderwizards.sandstone.server.CommandDebug;
import lib.enderwizards.sandstone.util.LanguageHelper;
import net.minecraft.command.CommandHandler;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sandstone {

    private static Map<String, List<ModIntegration>> modIntegrations = new HashMap<String, List<ModIntegration>>();

    /**
     * The Logger instance for Sandstone to use internally. Generally shouldn't be used outside of Sandstone itself.
     */
    public static Logger LOGGER = LogManager.getLogger("libSandstone");

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void preInit(FMLPreInitializationEvent event) {
        /* Unicode colors that you can use in the tooltips/names lang files.
         * Use by calling {{!name}}, with name being the name being colors.color. */
        LanguageHelper.globals.put("colors.black", "\u00A70");
        LanguageHelper.globals.put("colors.navy", "\u00A71");
        LanguageHelper.globals.put("colors.green", "\u00A72");
        LanguageHelper.globals.put("colors.blue", "\u00A73");
        LanguageHelper.globals.put("colors.red", "\u00A74");
        LanguageHelper.globals.put("colors.purple", "\u00A75");
        LanguageHelper.globals.put("colors.gold", "\u00A76");
        LanguageHelper.globals.put("colors.light_gray", "\u00A77");
        LanguageHelper.globals.put("colors.gray", "\u00A78");
        LanguageHelper.globals.put("colors.dark_purple", "\u00A79");
        LanguageHelper.globals.put("colors.light_green", "\u00A7a");
        LanguageHelper.globals.put("colors.light_blue", "\u00A7b");
        LanguageHelper.globals.put("colors.rose", "\u00A7c");
        LanguageHelper.globals.put("colors.light_purple", "\u00A7d");
        LanguageHelper.globals.put("colors.yellow", "\u00A7e");
        LanguageHelper.globals.put("colors.white", "\u00A7f");
        LanguageHelper.globals.put("colors.reset", EnumChatFormatting.RESET.toString());
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        CommandHandler handler = (CommandHandler) MinecraftServer.getServer().getCommandManager();

        if ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            handler.registerCommand(new CommandDebug());
        }
    }

    /**
     * Initializes SandstoneMod for the current mod. This includes registering the mod, and calling ContentHandler which will handle all your items and blocks. Look at ContentHandler for more information
     */
    public static Content preInit() {
        if (!Loader.instance().isInState(LoaderState.PREINITIALIZATION))
            return null;

        ModContainer mod = Loader.instance().activeModContainer();
        SandstoneMod smod = Loader.instance().activeModContainer().getMod().getClass().getAnnotation(SandstoneMod.class);
        if (smod.basePackage().equals("")) {
            LOGGER.error("SandstoneMod " + Loader.instance().activeModContainer().getModId() + "didn't have a basePackage! Ignoring!");
            return null;
        }

        ModRegistry.put(mod, smod);

        Content content = new Content(Loader.instance().activeModContainer().getModId());
        ClassLoader classLoader = Loader.instance().activeModContainer().getMod().getClass().getClassLoader();
        try {
            //content.init(classLoader, smod.basePackage() + "." + smod.itemsLocation());
        } catch (Exception e) {
            FMLCommonHandler.instance().raiseException(e, Loader.instance().activeModContainer().getModId() + " failed to initiate items.", true);
        }

        try {
            //content.init(classLoader, smod.basePackage() + "." + smod.blocksLocation());
        } catch (Exception e) {
            FMLCommonHandler.instance().raiseException(e, Loader.instance().activeModContainer().getModId() + " failed to initiate blocks.", true);
        }

        //sort the object list when we're done.
        List<String> sortedObjectNames = content.registeredObjectNames.subList(0, content.registeredObjectNames.size());
        java.util.Collections.sort(sortedObjectNames);
        content.registeredObjectNames = sortedObjectNames;

        return content;
    }

    public static void postInit() {
        if (!Loader.instance().isInState(LoaderState.POSTINITIALIZATION))
            return;
        String modId = Loader.instance().activeModContainer().getModId();
        if (modIntegrations.containsKey(modId)) {
            for (ModIntegration mod : modIntegrations.get(modId)) {
                mod.onLoad(Loader.isModLoaded(mod.modId));
            }
        }
    }

    public static boolean addModIntegration(ModIntegration modIntegration) {
        if (!Loader.instance().isInState(LoaderState.PREINITIALIZATION) && !Loader.instance().isInState(LoaderState.INITIALIZATION))
            return false;
        String modId = Loader.instance().activeModContainer().getModId();
        if (!modIntegrations.containsKey(modId)) {
            modIntegrations.put(modId, new ArrayList<ModIntegration>());
        }
        return modIntegrations.get(modId).add(modIntegration);
    }

}
