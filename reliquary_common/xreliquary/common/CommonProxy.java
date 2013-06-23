package xreliquary.common;

import xreliquary.Reliquary;
import xreliquary.blocks.TEAltar;
import xreliquary.entities.EntityBlazeShot;
import xreliquary.entities.EntityBusterShot;
import xreliquary.entities.EntityConcussiveShot;
import xreliquary.entities.EntityCondensedFertility;
import xreliquary.entities.EntityCondensedSplashAphrodite;
import xreliquary.entities.EntityCondensedSplashBlindness;
import xreliquary.entities.EntityCondensedSplashConfusion;
import xreliquary.entities.EntityCondensedSplashHarm;
import xreliquary.entities.EntityCondensedSplashPoison;
import xreliquary.entities.EntityCondensedSplashRuin;
import xreliquary.entities.EntityCondensedSplashSlowness;
import xreliquary.entities.EntityCondensedSplashWeakness;
import xreliquary.entities.EntityCondensedSplashWither;
import xreliquary.entities.EntityEnderShot;
import xreliquary.entities.EntityExorcismShot;
import xreliquary.entities.EntityGlowingWater;
import xreliquary.entities.EntityHolyHandGrenade;
import xreliquary.entities.EntityNeutralShot;
import xreliquary.entities.EntitySandShot;
import xreliquary.entities.EntitySeekerShot;
import xreliquary.entities.EntitySpecialEnderPearl;
import xreliquary.entities.EntitySpecialSnowball;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {
	// Client stuff
	public void registerRenderers() {
		// Nothing here as this is the server side proxy
	}

	public void registerActions() {
	}

	public void registerEvents() {
	}

	public void registerSoundHandler() {
		// Nothing here as this is a server side proxy
	}

	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TEAltar.class, "reliquaryAltar");
	}

	public void registerEntityTrackers() {
		EntityRegistry.registerModEntity(EntityHolyHandGrenade.class, "entityHGrenade", 0, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityGlowingWater.class, "enityHolyWater", 1, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntitySpecialSnowball.class, "entitySpecialSnowball", 2, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityNeutralShot.class, "entityNeutralShot", 3, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityExorcismShot.class, "entityExorcismShot", 4, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityBlazeShot.class, "entityBlazeShot", 5, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityEnderShot.class, "entityEnderShot", 6, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityConcussiveShot.class, "entityConcussiveShot", 7, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityBusterShot.class, "entityBusterShot", 8, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntitySeekerShot.class, "entitySeekerShot", 9, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntitySandShot.class, "entitySandShot", 10, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityCondensedSplashAphrodite.class, "entitySplashAphrodite", 12, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityCondensedSplashBlindness.class, "entitySplashBlindness", 13, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityCondensedSplashConfusion.class, "entitySplashConfusion", 14, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityCondensedSplashHarm.class, "entitySplashHarm", 15, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityCondensedSplashPoison.class, "entitySplashPoison", 16, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityCondensedSplashRuin.class, "entitySplashRuin", 17, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityCondensedSplashSlowness.class, "entitySplashSlowness", 18, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityCondensedSplashWeakness.class, "entitySplashWeakness", 19, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityCondensedSplashWither.class, "entitySplashWither", 20, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntityCondensedFertility.class, "entitySplashFertility", 21, Reliquary.instance, 128, 5, true);
		EntityRegistry.registerModEntity(EntitySpecialEnderPearl.class, "entitySpecialEnderPearl", 22, Reliquary.instance, 128, 5, true);
	}

	public void registerTickHandlers() {
		ITickHandler tickHandler = new TimeKeeperHandler();
		TickRegistry.registerTickHandler(tickHandler, Side.CLIENT);
	}
}
