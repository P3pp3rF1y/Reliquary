package xreliquary.compat;


import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.compat.jer.JERCompat;
import xreliquary.reference.Compatibility;


public class CompatibilityLoader
{
	private static boolean JERLoaded = false;

	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event) {
		registerJER(event.world);
	}



	@SideOnly(Side.CLIENT)
	@Optional.Method(modid = Compatibility.MOD_ID.JER)
	private void registerJER(World world) {
		if (!JERLoaded)
			JERCompat.register(world);
		JERLoaded = true;
	}



}
