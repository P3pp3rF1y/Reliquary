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
				event.manager.soundPoolSounds.addSound(Reference.MOD_ID + ":" + soundFile);
			} catch (Exception e) {
				LogHelper.log(Level.WARNING, "Failed loading sound file: " + soundFile);
			}
		}
	}
	
}