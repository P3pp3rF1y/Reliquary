package xreliquary.client;

import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import xreliquary.client.audio.SoundHandler;
import xreliquary.common.CommonProxy;
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
import xreliquary.items.XRItems;
import xreliquary.lib.PotionData;
import xreliquary.lib.Reference;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderers() {
		// Entity Renderers
		RenderingRegistry.registerEntityRenderingHandler(EntityBlazeShot.class, new RenderBlazeShot());
		RenderingRegistry.registerEntityRenderingHandler(EntityBusterShot.class, new RenderBusterShot());
		RenderingRegistry.registerEntityRenderingHandler(EntityConcussiveShot.class, new RenderConcussiveShot());
		RenderingRegistry.registerEntityRenderingHandler(EntityEnderShot.class, new RenderEnderShot());
		RenderingRegistry.registerEntityRenderingHandler(EntityExorcismShot.class, new RenderExorcismShot());
		RenderingRegistry.registerEntityRenderingHandler(EntityNeutralShot.class, new RenderNeutralShot());
		RenderingRegistry.registerEntityRenderingHandler(EntitySeekerShot.class, new RenderSeekerShot());
		RenderingRegistry.registerEntityRenderingHandler(EntitySandShot.class, new RenderSandShot());
		RenderingRegistry.registerEntityRenderingHandler(EntityHolyHandGrenade.class, new RenderThrown(Reference.GRENADE_SPRITE));
		RenderingRegistry.registerEntityRenderingHandler(EntitySpecialSnowball.class, new RenderSnowball(Item.snowball));
		RenderingRegistry.registerEntityRenderingHandler(EntitySpecialEnderPearl.class, new RenderSnowball(Item.enderPearl));
		RenderingRegistry.registerEntityRenderingHandler(EntityGlowingWater.class, new RenderThrown(Reference.WATER_SPRITE));
		RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashAphrodite.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + PotionData.APHRODITE_META));
		RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashPoison.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + PotionData.POISON_META));
		RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashHarm.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + PotionData.ACID_META));
		RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashConfusion.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + PotionData.CONFUSION_META));
		RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashSlowness.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + PotionData.SLOWING_META));
		RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashWeakness.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + PotionData.WEAKNESS_META));
		RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashWither.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + PotionData.WITHER_META));
		RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashBlindness.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + PotionData.BLINDING_META));
		RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashRuin.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + PotionData.RUINATION_META));
		RenderingRegistry.registerEntityRenderingHandler(EntityCondensedFertility.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + PotionData.FERTILIZER_META));
		// Item Renderers
		MinecraftForgeClient.registerItemRenderer(XRItems.handgun.itemID, new ItemRendererHandgun());
		// Tile Entity Renderers [none at the moment]
	}

	@Override
	public void registerSoundHandler() {
		MinecraftForge.EVENT_BUS.register(new SoundHandler());
	}
}
