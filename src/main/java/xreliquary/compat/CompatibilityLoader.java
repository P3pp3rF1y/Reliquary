package xreliquary.compat;


import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.compat.jer.JERCompat;
import xreliquary.reference.Compatibility;


public class CompatibilityLoader
{
	private static boolean JERDataLoaded = false;

	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event) {
		if (Loader.isModLoaded(Compatibility.MOD_ID.JER))
			registerJER(event.world);
	}

	@SideOnly(Side.CLIENT)
	private void registerJER(World world) {
		if (!JERDataLoaded)
			JERCompat.register(world);
		JERDataLoaded = true;
	}


 
}
