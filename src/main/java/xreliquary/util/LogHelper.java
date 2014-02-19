package xreliquary.util;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xreliquary.lib.Reference;

public class LogHelper {

    private static Logger logger = LogManager.getLogger(Reference.MOD_ID);

	public static void log(Level logLevel, String message) {
		logger.log(logLevel, message);
	}

}