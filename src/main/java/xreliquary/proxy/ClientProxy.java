package xreliquary.proxy;

import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import xreliquary.client.*;
import xreliquary.client.audio.SoundHandler;
import xreliquary.entities.*;
import xreliquary.items.XRItems;
import xreliquary.lib.Reference;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit() {
		super.preInit();
        MinecraftForge.EVENT_BUS.register(new SoundHandler());
	}
	
	@Override
	public void init() {
		super.init();
		this.registerRenderers();
	}

    public void registerRenderers() {
    	
        RenderingRegistry.registerEntityRenderingHandler(EntityBlazeShot.class, new RenderBlazeShot());
        RenderingRegistry.registerEntityRenderingHandler(EntityBusterShot.class, new RenderBusterShot());
        RenderingRegistry.registerEntityRenderingHandler(EntityConcussiveShot.class, new RenderConcussiveShot());
        RenderingRegistry.registerEntityRenderingHandler(EntityEnderShot.class, new RenderEnderShot());
        RenderingRegistry.registerEntityRenderingHandler(EntityExorcismShot.class, new RenderExorcismShot());
        RenderingRegistry.registerEntityRenderingHandler(EntityNeutralShot.class, new RenderNeutralShot());
        RenderingRegistry.registerEntityRenderingHandler(EntitySeekerShot.class, new RenderSeekerShot());
        RenderingRegistry.registerEntityRenderingHandler(EntitySandShot.class, new RenderSandShot());
        RenderingRegistry.registerEntityRenderingHandler(EntityStormShot.class, new RenderStormShot());
        RenderingRegistry.registerEntityRenderingHandler(EntityHolyHandGrenade.class, new RenderThrown(12));
        RenderingRegistry.registerEntityRenderingHandler(EntitySpecialSnowball.class, new RenderSnowball(Item.snowball));
        RenderingRegistry.registerEntityRenderingHandler(EntityGlowingWater.class, new RenderThrown(Reference.WATER_SPRITE));
        RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashAphrodite.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + Reference.APHRODITE_META));
        RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashPoison.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + Reference.POISON_META));
        RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashHarm.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + Reference.ACID_META));
        RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashConfusion.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + Reference.CONFUSION_META));
        RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashSlowness.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + Reference.SLOWING_META));
        RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashWeakness.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + Reference.WEAKNESS_META));
        RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashWither.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + Reference.WITHER_META));
        RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashBlindness.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + Reference.BLINDING_META));
        RenderingRegistry.registerEntityRenderingHandler(EntityCondensedSplashRuin.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + Reference.RUINATION_META));
        RenderingRegistry.registerEntityRenderingHandler(EntityCondensedFertility.class, new RenderThrown(Reference.SPLASH_POTION_SPRITE + Reference.FERTILIZER_META));

        MinecraftForgeClient.registerItemRenderer(XRItems.handgun.itemID, new ItemRendererHandgun());
    }

}
