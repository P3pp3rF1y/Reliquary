package xreliquary.client.audio;

import java.util.logging.Level;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import xreliquary.lib.Reference;
import xreliquary.util.LogHelper;

public class SoundHandler {

    @ForgeSubscribe
    public void onSound(SoundLoadEvent event) {
        for (String soundFile : Reference.soundFiles) {

            try {
                event.manager.soundPoolSounds.addSound(soundFile, this
                        .getClass().getResource("/" + soundFile));
                System.out.println(this.getClass().getResource("/" + soundFile)
                        .toString());
            }
            // If we cannot add the custom sound file to the pool, log the
            // exception
            catch (Exception e) {
                LogHelper.log(Level.WARNING, "Failed loading sound file: "
                        + soundFile);
            }
        }
    }
}