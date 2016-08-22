package xreliquary.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.blocks.tile.TileEntityPedestalPassive;
import xreliquary.client.init.ItemBlockModels;
import xreliquary.client.init.ItemModels;
import xreliquary.client.init.ModBlockColors;
import xreliquary.client.init.ModItemColors;
import xreliquary.client.registry.PedestalClientRegistry;
import xreliquary.client.render.*;
import xreliquary.common.CommonProxy;
import xreliquary.compat.jei.descriptions.JEIDescriptionRegistry;
import xreliquary.entities.*;
import xreliquary.entities.potion.EntityAttractionPotion;
import xreliquary.entities.potion.EntityFertilePotion;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.entities.shot.*;
import xreliquary.handler.ClientEventHandler;
import xreliquary.init.ModFluids;
import xreliquary.init.ModItems;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO refactor proxy so that it has functionally named methods that get called from main mod class
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void registerJEI(Block block, String name) {
		registerJEI(block, name, false);
	}

	@Override
	public void registerJEI(Block block, String name, boolean oneDescription) {
		if(Loader.isModLoaded(Compatibility.MOD_ID.JEI)) {
			if(oneDescription) {
				List<ItemStack> subBlocks = new ArrayList<>();

				block.getSubBlocks(Item.getItemFromBlock(block), null, subBlocks);

				JEIDescriptionRegistry.register(subBlocks, name);
			} else {
				JEIDescriptionRegistry.register(Item.getItemFromBlock(block), name);
			}
		}
	}

	@Override
	public void initPotionsJEI() {
		if(!Loader.isModLoaded(Compatibility.MOD_ID.JEI))
			return;

		List<ItemStack> subItems = new ArrayList<>();
		ModItems.potionEssence.getSubItems(ModItems.potionEssence, ModItems.potionEssence.getCreativeTab(), subItems);
		JEIDescriptionRegistry.register(subItems, Names.potion_essence);

		List<ItemStack> potions = new ArrayList<>();
		List<ItemStack> splashPotions = new ArrayList<>();

		for(PotionEssence essence : Settings.Potions.uniquePotions) {
			ItemStack potion = new ItemStack(ModItems.potion, 1);
			potion.setTagCompound(essence.writeToNBT());
			NBTHelper.setBoolean("hasPotion", potion, true);
			potions.add(potion);

			ItemStack splashPotion = potion.copy();
			NBTHelper.setBoolean("splash", splashPotion, true);
			splashPotions.add(splashPotion);
		}
		JEIDescriptionRegistry.register(potions, Names.potion);
		JEIDescriptionRegistry.register(splashPotions, Names.potion_splash);
	}

	@Override
	public void registerJEI(Item item, String name) {
		if(Loader.isModLoaded(Compatibility.MOD_ID.JEI))
			JEIDescriptionRegistry.register(item, name);
	}

	@Override
	public void preInit() {
		super.preInit();

		ItemBlockModels.registerItemBlockModels();
		ItemModels.registerItemModels();
		registerEntityRenderers();
	}

	private void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityBlazeShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBusterShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityConcussiveShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityEnderShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityExorcismShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityNeutralShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySeekerShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySandShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityStormShot.class, RenderShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGlowingWater.class, renderManager -> new RenderThrown(renderManager, ModItems.glowingWater, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityAttractionPotion.class, renderManager -> new RenderThrown(renderManager, ModItems.attractionPotion, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFertilePotion.class, renderManager -> new RenderThrown(renderManager, ModItems.fertilePotion, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityHolyHandGrenade.class, renderManager -> new RenderThrown(renderManager, ModItems.holyHandGrenade, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityKrakenSlime.class, RenderThrownKrakenSlime::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySpecialSnowball.class, renderManager -> new RenderSnowball(renderManager, Items.SNOWBALL, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityEnderStaffProjectile.class, renderManager -> new RenderSnowball(renderManager, Items.ENDER_PEARL, Minecraft.getMinecraft().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityThrownXRPotion.class, renderManager -> new RenderThrownXRPotion(renderManager, Minecraft.getMinecraft().getRenderItem()));
	}

	@Override
	public void init() {
		super.init();
		ModItemColors.init();
		ModBlockColors.init();
		RegisterBeltRender();
		FMLCommonHandler.instance().bus().register(new ClientEventHandler());
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		MinecraftForge.EVENT_BUS.register(new ModFluids());

		this.registerRenderers();
	}

	@Override
	public void postInit() {
		super.postInit();
		PedestalClientRegistry.registerItemRenderer(ItemFishingRod.class, RenderPedestalFishHook.class);
	}

	private void RegisterBeltRender() {
		Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
		RenderPlayer render;
		render = skinMap.get("default");
		render.addLayer(new MobCharmBeltLayerRenderer());
	}

	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMortar.class, new RenderApothecaryMortar());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPedestal.class, new TileEntityPedestalRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPedestalPassive.class, new TileEntityPedestalPassiveRenderer());
	}

	@Override
	public void initColors() {
		 /* Unicode colors that you can use in the tooltips/names lang files.
		 Use by calling {{!name}}, with name being the name being colors.color. */
		LanguageHelper.globals.put("colors.black", "\u00A70");
		LanguageHelper.globals.put("colors.navy", "\u00A71");
		LanguageHelper.globals.put("colors.green", "\u00A72");
		LanguageHelper.globals.put("colors.blue", "\u00A73");
		LanguageHelper.globals.put("colors.red", "\u00A74");
		LanguageHelper.globals.put("colors.purple", "\u00A75");
		LanguageHelper.globals.put("colors.gold", "\u00A76");
		LanguageHelper.globals.put("colors.light_gray", "\u00A77");
		LanguageHelper.globals.put("colors.gray", "\u00A78");
		LanguageHelper.globals.put("colors.dark_purple", "\u00A79");
		LanguageHelper.globals.put("colors.light_green", "\u00A7a");
		LanguageHelper.globals.put("colors.light_blue", "\u00A7b");
		LanguageHelper.globals.put("colors.rose", "\u00A7c");
		LanguageHelper.globals.put("colors.light_purple", "\u00A7d");
		LanguageHelper.globals.put("colors.yellow", "\u00A7e");
		LanguageHelper.globals.put("colors.white", "\u00A7f");
		LanguageHelper.globals.put("colors.reset", TextFormatting.RESET.toString());

	}
}
