package xreliquary.compat.thaumcraft;


import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import xreliquary.compat.ICompat;
import xreliquary.init.XRRecipes;
import xreliquary.reference.Compatibility;


public class TCCompat implements ICompat
{
	@Override
	public void loadCompatibility(InitializationPhase phase, World world) {
		if(phase == InitializationPhase.POST_INIT)
			register();
	}

	@Override
	public String getModId() {
		return Compatibility.MOD_ID.THAUMCRAFT;
	}

	public static void register() {
		ThaumcraftApi.registerObjectTag(XRRecipes.RIB_BONE, new AspectList().add(Aspect.DEATH, 4).add(Aspect.LIFE, 3).add(Aspect.UNDEAD, 1));
		ThaumcraftApi.registerObjectTag(XRRecipes.WITHER_RIB, new AspectList().add(Aspect.DEATH, 6).add(Aspect.LIFE, 4).add(Aspect.UNDEAD, 1));
		ThaumcraftApi.registerObjectTag(XRRecipes.CHELICERAE, new AspectList().add(Aspect.BEAST, 8).add(Aspect.AVERSION, 4));
		ThaumcraftApi.registerObjectTag(XRRecipes.CREEPER_GLAND, new AspectList().add(Aspect.ENTROPY, 9).add(Aspect.FIRE, 9).add(Aspect.ENERGY, 4));
		ThaumcraftApi.registerObjectTag(XRRecipes.SLIME_PEARL, new AspectList().add(Aspect.WATER, 7).add(Aspect.LIFE, 6));
		ThaumcraftApi.registerObjectTag(XRRecipes.BAT_WING, new AspectList().add(Aspect.BEAST, 9));
		ThaumcraftApi.registerObjectTag(XRRecipes.MOLTEN_CORE, new AspectList().add(Aspect.FIRE, 8).add(Aspect.ENERGY, 4).add(Aspect.LIFE, 4));
		ThaumcraftApi.registerObjectTag(XRRecipes.ZOMBIE_HEART, new AspectList().add(Aspect.MAN, 5).add(Aspect.LIFE, 11));
		ThaumcraftApi.registerObjectTag(XRRecipes.STORM_EYE, new AspectList().add(Aspect.ENERGY, 15).add(Aspect.FIRE, 9).add(Aspect.ENTROPY, 9));
		ThaumcraftApi.registerObjectTag(XRRecipes.FROZEN_CORE, new AspectList().add(Aspect.COLD, 9).add(Aspect.LIFE, 4));
		ThaumcraftApi.registerObjectTag(XRRecipes.NEBULOUS_HEART, new AspectList().add(Aspect.ELDRITCH, 8).add(Aspect.LIFE, 3));
		ThaumcraftApi.registerObjectTag(XRRecipes.SQUID_BEAK, new AspectList().add(Aspect.BEAST, 9));
	}
}
