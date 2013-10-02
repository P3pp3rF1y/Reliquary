package xreliquary.common;

import java.util.EnumSet;

import xreliquary.lib.Reference;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TimeKeeperHandler implements ITickHandler {
    // this handler is specifically used to keep time for the Salamander's Eye
    // blinking effect
    private static int time;

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        for (TickType tickType : type) {
            if (tickType != TickType.CLIENT) {
                continue;
            }
            if (getTime() > 88) {
                resetTime();
            } else {
                incrementTime();
            }
        }
    }

    private static void incrementTime() {
        time++;
    }

    private static void resetTime() {
        time = 10;
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT);
    }

    @Override
    public String getLabel() {
        return Reference.MOD_NAME + ": " + this.getClass().getSimpleName();
    }

    public static int getTime() {
        return time;
    }
}
