package xreliquary.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import xreliquary.Reliquary;
import xreliquary.common.gui.GUIHandler;
import xreliquary.entities.*;
import xreliquary.entities.potion.EntityAttractionPotion;
import xreliquary.entities.potion.EntityFertilePotion;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.entities.shot.*;
import xreliquary.reference.Reference;

public class CommonProxy {

	//TODO: rewrite proxy to the EE style so that it has area specific method names rather than generic preInit/init/postInit
	public void preInit() {
	}

	public void init() {
		NetworkRegistry.INSTANCE.registerGuiHandler(Reliquary.INSTANCE, new GUIHandler());

		this.registerEntities();
	}

	public void postInit() {

	}

	private void registerEntities() {
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "hand_grenade"), EntityHolyHandGrenade.class, "entityHGrenade", 0, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "holy_water"), EntityGlowingWater.class, "entityHolyWater", 1, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "special_snowball"), EntitySpecialSnowball.class, "entitySpecialSnowball", 2, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "neutral_shot"), EntityNeutralShot.class, "entityNeutralShot", 3, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "exorcism_shot"), EntityExorcismShot.class, "entityExorcismShot", 4, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "blaze_shot"), EntityBlazeShot.class, "entityBlazeShot", 5, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "ender_shot"), EntityEnderShot.class, "entityEnderShot", 6, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "concussive_shot"), EntityConcussiveShot.class, "entityConcussiveShot", 7, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "buster_shot"), EntityBusterShot.class, "entityBusterShot", 8, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "seeker_shot"), EntitySeekerShot.class, "entitySeekerShot", 9, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "sand_shot"), EntitySandShot.class, "entitySandShot", 10, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "storm_shot"), EntityStormShot.class, "entityStormShot", 11, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "thrown_aphrodite"), EntityAttractionPotion.class, "entitySplashAphrodite", 12, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "thrown_potion"), EntityThrownXRPotion.class, "entityThrownXRPotion", 13, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "lyssa_hook"), EntityLyssaHook.class, "entityLyssaHook", 14, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "thrown_fertility"), EntityFertilePotion.class, "entitySplashFertility", 21, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "kraken_slime"), EntityKrakenSlime.class, "entityKSlime", 22, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "ender_staff_projectile"), EntityEnderStaffProjectile.class, "entityEnderStaffProjectile", 23, Reliquary.INSTANCE, 128, 5, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID, "tipped arrow"), EntityXRTippedArrow.class, "entityTippedArrow", 24, Reliquary.INSTANCE, 128, 5, true);
	}

	public void initColors() {
	}

	public void registerJEI(Item item, String name) {
	}

	public void registerJEI(Block block, String name, boolean oneDescription) {
	}

	public void initSpecialJEIDescriptions() {
	}
}
