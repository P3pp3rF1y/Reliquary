package xreliquary.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.reference.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModSounds {
	public static SoundEvent book = new SoundEvent(new ResourceLocation(Reference.MOD_ID, "book"));
	public static SoundEvent xload = new SoundEvent(new ResourceLocation(Reference.MOD_ID, "xload"));
	public static SoundEvent xshot = new SoundEvent(new ResourceLocation(Reference.MOD_ID, "xshot"));

	@SubscribeEvent
	public static void register(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().register(book.setRegistryName(Reference.MOD_ID, "book"));
		event.getRegistry().register(xload.setRegistryName(Reference.MOD_ID, "xload"));
		event.getRegistry().register(xshot.setRegistryName(Reference.MOD_ID, "xshot"));
	}
}
