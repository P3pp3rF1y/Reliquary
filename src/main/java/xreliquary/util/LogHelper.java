package xreliquary.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import xreliquary.lib.Reference;
import cpw.mods.fml.common.FMLLog;

public class LogHelper {

    private static Logger xrLogger = Logger.getLogger(Reference.MOD_NAME);

    public static void init() {

        xrLogger.setParent(FMLLog.getLogger());
    }

    public static void log(Level logLevel, String message) {

        xrLogger.log(logLevel, message);
    }

}