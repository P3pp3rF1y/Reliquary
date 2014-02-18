package xreliquary.util;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;
import xreliquary.lib.Reference;

public class LogHelper {

	public static void log(Level logLevel, String message) {
		FMLLog.getLogger().log(logLevel, Reference.MOD_ID, message);
	}

}