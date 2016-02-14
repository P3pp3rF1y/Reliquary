package xreliquary.compat.jer;

import jeresources.api.IJERAPI;
import jeresources.api.JERPlugin;
import jeresources.api.conditionals.Conditional;
import jeresources.api.conditionals.LightLevel;
import jeresources.api.conditionals.WatchableData;
import jeresources.api.drop.DropItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.init.XRRecipes;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;


public class JERCompat
{
	@JERPlugin
	public static IJERAPI api;

	public static void register(World world)
	{
		//Squid
		registerMobDrop(EntitySquid.class, XRRecipes.ingredient(Reference.SQUID_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.squid_beak), Conditional.playerKill);

		//Witch
		registerMobDrop(EntityWitch.class, new ItemStack(ModItems.witchHat), Settings.MobDrops.getBaseDrop(Names.witch_hat), Conditional.playerKill);

		//Spider
		registerMobDrop(EntitySpider.class, XRRecipes.ingredient(Reference.SPIDER_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.spider_fangs), Conditional.playerKill);

		//Cave Spider
		//registerMobDrop(EntityCaveSpider.class, XRRecipes.ingredient(Reference.SPIDER_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.cave_spider_fangs), Conditional.playerKill);

		//Skeleton
		registerMobDrop(EntitySkeleton.class, WatchableData.REGULAR_SKELETON, XRRecipes.ingredient(Reference.SKELETON_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.rib_bone), Conditional.playerKill);

		//Wither Skeleton
		registerMobDrop(EntitySkeleton.class, WatchableData.WITHER_SKELETON, XRRecipes.ingredient(Reference.WITHER_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.withered_rib), Conditional.playerKill);

		//Zombie
		registerMobDrop(EntityZombie.class, XRRecipes.ingredient(Reference.ZOMBIE_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.zombie_heart), Conditional.playerKill);

		//Zombie Pigman
		//registerMobDrop(EntityPigZombie.class, XRRecipes.ingredient(Reference.ZOMBIE_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.pigman_heart), Conditional.playerKill);

		//Slime
		registerMobDrop(EntitySlime.class, XRRecipes.ingredient(Reference.SLIME_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.slime_pearl), Conditional.playerKill);

		//Blaze
		registerMobDrop(EntityBlaze.class, XRRecipes.ingredient(Reference.MOLTEN_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.blaze_molten_core), Conditional.playerKill);

		//Magma Cube
		registerMobDrop(EntityMagmaCube.class, XRRecipes.ingredient(Reference.MOLTEN_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.magma_cube_molten_core), Conditional.playerKill);

		//Ghast
		registerMobDrop(EntityGhast.class, XRRecipes.ingredient(Reference.CREEPER_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.ghast_gland), Conditional.playerKill);

		//Creeper
		registerMobDrop(EntityCreeper.class, XRRecipes.ingredient(Reference.CREEPER_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.creeper_gland), Conditional.playerKill);

		//Charged Creeper
		DropItem eyeOfTheStorm = new DropItem(XRRecipes.ingredient(Reference.STORM_INGREDIENT_META), 1, 1, Settings.MobDrops.getBaseDrop(Names.eye_of_the_storm), Conditional.playerKill);
		EntityCreeper chargedCreeper = new EntityCreeper(world);
		chargedCreeper.onStruckByLightning(null);
		chargedCreeper.extinguish();
		api.getMobRegistry().register(chargedCreeper, LightLevel.hostile, 5, eyeOfTheStorm);

		//Enderman
		registerMobDrop(EntityEnderman.class, XRRecipes.ingredient(Reference.ENDER_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.ender_heart), Conditional.playerKill);

		//Bat
		registerMobDrop(EntityBat.class, XRRecipes.ingredient(Reference.BAT_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.bat_wing), Conditional.playerKill);

		//Snow Golem
		registerMobDrop(EntitySnowman.class, XRRecipes.ingredient(Reference.FROZEN_INGREDIENT_META), Settings.MobDrops.getBaseDrop(Names.frozen_core), Conditional.playerKill);
	}

	private static void registerMobDrop(Class<? extends EntityLivingBase> entity, WatchableData watchableData, ItemStack drop, float chance, Conditional... conditionals){
		DropItem dropItem = new DropItem(drop, 1, 1, chance, conditionals);
		api.getMobRegistry().registerDrops(entity, watchableData, dropItem);
	}
	private static void registerMobDrop(Class<? extends EntityLivingBase> entity, ItemStack drop, float chance, Conditional... conditionals){
		registerMobDrop(entity, WatchableData.EMPTY, drop, chance, conditionals);
	}



}
