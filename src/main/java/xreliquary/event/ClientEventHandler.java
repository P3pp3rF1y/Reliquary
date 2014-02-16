package xreliquary.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ClientEventHandler {

    private static int time;

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if(event.phase != TickEvent.Phase.END)
            return;
        if (getTime() > 88) {
            time = 10;
        } else {
            time++;
        }
    }

    public static int getTime() {
        return time;
    }

}
