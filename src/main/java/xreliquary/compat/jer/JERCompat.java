package xreliquary.compat.jer;

import jeresources.api.IJERAPI;
import jeresources.api.JERPlugin;
import jeresources.api.conditionals.Conditional;
import jeresources.api.conditionals.WatchableData;
import jeresources.api.drop.LootDrop;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.compat.ICompat;
import xreliquary.init.ModItems;
import xreliquary.init.XRRecipes;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

public class JERCompat implements ICompat {
	private static boolean JERDataLoaded = false;

	@Override
	public void loadCompatibility(InitializationPhase phase, World world) {
		if(Settings.mobDropsEnabled && phase == InitializationPhase.WORLD_LOAD && !JERDataLoaded) {
			register(world);
			JERDataLoaded = true;
		}
	}

	@Override
	public String getModId() {
		return Compatibility.MOD_ID.JER;
	}

	@JERPlugin
	public static IJERAPI api;

	public static void register(World world) {
/*
 TODO fix when there are loot tables or JER logic
		//Charged Creeper
		LootDrop eyeOfTheStorm = new LootDrop(XRRecipes.ingredient(1, Reference.STORM_INGREDIENT_META), 1, 1, Settings.MobDrops.getBaseDrop(Names.eye_of_the_storm), Conditional.playerKill);
		EntityCreeper chargedCreeper = new EntityCreeper(world);
		chargedCreeper.getDataManager().updateObject(17, Byte.valueOf((byte)1));
		api.getMobRegistry().register(chargedCreeper, LightLevel.hostile, 5, eyeOfTheStorm);
*/
	}

	private static void registerMobDrop(Class<? extends EntityLivingBase> entity, WatchableData watchableData, ItemStack drop, float chance, Conditional... conditionals) {
		LootDrop dropItem = new LootDrop(drop, 1, 1, chance, conditionals);
		api.getMobRegistry().registerDrops(entity, watchableData, dropItem);
	}

	private static void registerMobDrop(Class<? extends EntityLivingBase> entity, ItemStack drop, float chance, Conditional... conditionals) {
		registerMobDrop(entity, WatchableData.EMPTY, drop, chance, conditionals);
	}
}
