package xreliquary.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.client.render.*;
import xreliquary.common.CommonProxy;
import xreliquary.entities.*;
import xreliquary.entities.potion.*;
import xreliquary.entities.shot.*;
import xreliquary.event.ClientEventHandler;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();

    }

    @Override
    public void init() {
        super.init();
        FMLCommonHandler.instance().bus().register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

        this.registerRenderers();
        ModBlocks.initModels();
        ModItems.initModels();
    }


    public void registerRenderers() {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        //TODO:replace deprecated call
        RenderingRegistry.registerEntityRenderingHandler( EntityBlazeShot.class, new RenderShot<EntityBlazeShot>(renderManager) );
        RenderingRegistry.registerEntityRenderingHandler(EntityBusterShot.class, new RenderShot<EntityBusterShot>(renderManager));
        RenderingRegistry.registerEntityRenderingHandler(EntityConcussiveShot.class, new RenderShot<EntityConcussiveShot>(renderManager));
        RenderingRegistry.registerEntityRenderingHandler(EntityEnderShot.class, new RenderShot<EntityEnderShot>(renderManager));
        RenderingRegistry.registerEntityRenderingHandler(EntityExorcismShot.class, new RenderShot<EntityExorcismShot>(renderManager));
        RenderingRegistry.registerEntityRenderingHandler(EntityNeutralShot.class, new RenderShot<EntityNeutralShot>(renderManager));
        RenderingRegistry.registerEntityRenderingHandler(EntitySeekerShot.class, new RenderShot<EntitySeekerShot>(renderManager));
        RenderingRegistry.registerEntityRenderingHandler(EntitySandShot.class, new RenderShot<EntitySandShot>(renderManager));
        RenderingRegistry.registerEntityRenderingHandler(EntityStormShot.class, new RenderShot<EntityStormShot>(renderManager));
        RenderingRegistry.registerEntityRenderingHandler(EntityGlowingWater.class, new RenderThrown<EntityGlowingWater>(renderManager,ModItems.glowingWater, renderItem));
        RenderingRegistry.registerEntityRenderingHandler(EntityAttractionPotion.class, new RenderThrown<EntityAttractionPotion>(renderManager, ModItems.attractionPotion, renderItem));
        RenderingRegistry.registerEntityRenderingHandler(EntityFertilePotion.class, new RenderThrown<EntityFertilePotion>(renderManager, ModItems.fertilePotion, renderItem));
        RenderingRegistry.registerEntityRenderingHandler(EntityHolyHandGrenade.class, new RenderThrown<EntityHolyHandGrenade>(renderManager, ModItems.holyHandGrenade, renderItem));
        RenderingRegistry.registerEntityRenderingHandler(EntityKrakenSlime.class, new RenderThrownKrakenSlime(renderManager));
        RenderingRegistry.registerEntityRenderingHandler(EntitySpecialSnowball.class, new RenderSnowball<EntitySpecialSnowball>(renderManager, Items.snowball, renderItem));
        RenderingRegistry.registerEntityRenderingHandler(EntityEnderStaffProjectile.class, new RenderSnowball<EntityEnderStaffProjectile>(renderManager, Items.ender_pearl, renderItem));
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownXRPotion.class, new RenderThrownXRPotion(renderManager, renderItem));

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMortar.class, new RenderApothecaryMortar());

        //TODO: add rendering for these custom items
        // MinecraftForgeClient.registerItemRenderer(ItemBlock.getItemFromBlock(Reliquary.CONTENT.getBlock(Names.apothecary_mortar)), new ItemRendererApothecaryMortar());
        // MinecraftForgeClient.registerItemRenderer(Reliquary.CONTENT.getItem(Names.handgun), new ItemRendererHandgun());
    }

}
