package xreliquary.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.reference.Reference;

public class ModSounds {
	public static SoundEvent book = new SoundEvent(new ResourceLocation(Reference.MOD_ID, "book"));
	public static SoundEvent xload = new SoundEvent(new ResourceLocation(Reference.MOD_ID, "xload"));
	public static SoundEvent xshot = new SoundEvent(new ResourceLocation(Reference.MOD_ID, "xshot"));

	public static void init() {
		registerSoundEvent(book, new ResourceLocation(Reference.MOD_ID, "book"));
		registerSoundEvent(xload, new ResourceLocation(Reference.MOD_ID, "xload"));
		registerSoundEvent(xshot, new ResourceLocation(Reference.MOD_ID, "xshot"));
	}

	private static void registerSoundEvent(SoundEvent soundEvent, ResourceLocation resourceLocation) {
		GameRegistry.register(soundEvent.setRegistryName(resourceLocation));
	}
}
