package xreliquary.compat.thaumcraft;


import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import xreliquary.init.XRRecipes;


public class TCCompat
{
	public static void register() {
		ThaumcraftApi.registerObjectTag(XRRecipes.ribBone(), new AspectList().add(Aspect.DEATH, 4).add(Aspect.LIFE, 3).add(Aspect.UNDEAD, 1));
		ThaumcraftApi.registerObjectTag(XRRecipes.witherRib(), new AspectList().add(Aspect.DEATH, 4).add(Aspect.LIFE, 3).add(Aspect.UNDEAD, 1));
		/*
		ThaumcraftApi.registerObjectTag(XRRecipes.spiderFangs(), new AspectList().add);
		ThaumcraftApi.registerObjectTag(XRRecipes.creeperGland(), new AspectList().add);
		ThaumcraftApi.registerObjectTag(XRRecipes.slimePearl(), new AspectList().add);
		ThaumcraftApi.registerObjectTag(XRRecipes.batWing(), new AspectList().add);
		ThaumcraftApi.registerObjectTag(XRRecipes.moltenCore(), new AspectList().add);
		ThaumcraftApi.registerObjectTag(XRRecipes.zombieHeart(), new AspectList().add);
		ThaumcraftApi.registerObjectTag(XRRecipes.stormEye(), new AspectList().add);
		ThaumcraftApi.registerObjectTag(XRRecipes.frozenCore(), new AspectList().add);
		ThaumcraftApi.registerObjectTag(XRRecipes.enderHeart(), new AspectList().add);
		//ThaumcraftApi.registerObjectTag(XRRecipes.fertileEssence(), new AspectList().add);
		ThaumcraftApi.registerObjectTag(XRRecipes.squidBeak(), new AspectList().add);
		*/
	}
}
