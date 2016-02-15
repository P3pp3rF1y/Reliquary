package xreliquary.reference;


import net.minecraft.item.ItemStack;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Settings
{
	public static List<PotionIngredient> potionMap = new ArrayList<>();
	public static List<PotionEssence> potionCombinations = new ArrayList<>();
	public static List<PotionEssence> uniquePotions = new ArrayList<>();

	public static class HudPositions {
		public static int sojournerStaff;
		public static int handgun;
		public static int alkahestryTome;
		public static int destructionCatalyst;
		public static int elsewhereFlask;
		public static int enderStaff;
		public static int iceMagusRod;
		public static int glacialStaff;
		public static int voidTear;
		public static int midasTouchstone;
		public static int harvestRod;
		public static int infernalChalice;
		public static int heroMedallion;
		public static int pyromancerStaff;
		public static int rendingGale;
	}


	public static class EasyModeRecipes
	{
		public static boolean fortuneCoin;
		public static boolean altar;
		public static boolean infernalChalice;
		public static boolean enderStaff;
		public static boolean salamanderEye;
		public static boolean rodOfLyssa;
		public static boolean serpentStaff;
		public static boolean rendingGale;
		public static boolean pyromancerStaff;
		public static boolean magicBane;
		public static boolean lanternOfParanoia;
		public static boolean alkahestryTome;
		public static boolean wraithNode;
		public static boolean glacialStaff;
		public static boolean sojournerStaff;
		public static boolean krakenShell;
		public static boolean angelicFeather;
		public static boolean emperorChalice;
		public static boolean heroMedallion;
		public static boolean iceMagusRod;
		public static boolean infernalClaws;
		public static boolean destructionCatalyst;
		public static boolean interdictionTorch;
		public static boolean voidTear;
		public static boolean infernalTear;
		public static boolean fertileEssence;
		public static boolean seekerShot;
	}

	public static class MobDrops {
		public static Map<String, Integer> mobDropProbabilities;

		public static float getBaseDrop(String s) {
			return (float) mobDropProbabilities.get(s + "_base") * 0.01F;
		}

		public static float getLootingDrop(String s) {
			return (float) mobDropProbabilities.get(s + "_looting") * 0.01F;
		}
	}

	public static class AlkahestryTome {
		public static int chargeLimit;
		public static Map<String, AlkahestCraftRecipe> craftingRecipes =  new HashMap<>();
		public static Map<String, AlkahestChargeRecipe> chargingRecipes =  new HashMap<>();
		public static ItemStack baseItem;
		public static int baseItemWorth;
	}

	public static class Altar {
		public static int redstoneCost;
		public static int timeInMinutes;
		public static int maximumTimeVarianceInMinutes;
		public static int outputLightLevelWhileActive;
	}

	public static class AngelicFeather {
		public static int hungerCostPercent;
		public static int leapingPotency;
	}

	public static class AngelHeartVial {
		public static int healPercentageOfMaxLife;
		public static boolean removeNegativeStatus;
	}

	public static class ApothecaryCauldron {
		public static int redstoneLimit;
		public static int cookTime;
		public static List<String> heatSources;
	}

	public static class DestructionCatalyst {
		public static List<String> mundaneBlocks;
		public static int gunpowderCost;
		public static int gunpowderWorth;
		public static int gunpowderLimit;
		public static int explosionRadius;
		public static boolean centeredExplosion;
		public static boolean perfectCube;
	}

	public static class EmperorChalice {
		public static int hungerSatiationMultiplier;
	}

	public static class EnderStaff {
		public static int enderPearlCastCost;
		public static int enderPearlNodeWarpCost;
		public static int enderPearlWorth;
		public static int enderPearlLimit;
		public static int nodeWarpCastTime;
	}

	public static class FortuneCoin {
		public static boolean disableAudio;
		public static int standardPullDistance;
		public static int longRangePullDistance;
	}

	public static class GlacialStaff {
		public static int snowballLimit;
		public static int snowballCost;
		public static int snowballWorth;
		public static int snowballDamage;
		public static int snowballDamageBonusFireImmune;
		public static int snowballDamageBonusBlaze;
	}

	public static class HarvestRod {
		public static int bonemealLimit;
		public static int bonemealCost;
		public static int bonemealWorth;
		public static int bonemealLuckPercentChance;
		public static int bonemealLuckRolls;
		public static int harvestBreakRadius;
	}

	public static class HeroMedallion {
		public static int experienceLevelMaximum;
		public static int experienceLevelMinimum;
		public static int experienceLimit;
	}

	public static class IceMagusRod {
		public static int snowballLimit;
		public static int snowballCost;
		public static int snowballWorth;
		public static int snowballDamage;
		public static int snowballDamageBonusFireImmune;
		public static int snowballDamageBonusBlaze;
	}

	public static class InfernalClaws {
		public static int hungerCostPercent;
	}

	public static class InfernalChalice {
		public static int hungerCostPercent;
		public static int fluidLimit;
	}

	public static class InterdictionTorch {
		public static int pushRadius;
		public static boolean canPushProjectiles;
		public static List<String> entitiesThatCanBePushed;
		public static List<String> projectilesThatCanBePushed;
	}

	public static class KrakenShell {
		public static int hungerCostPercent;
	}

	public static class LanternOfParanoia {
		public static int minLightLevel;
		public static int placementScanRadius;
	}

	public static class FertileLilypad {
		public static int secondsBetweenGrowthTicks;
		public static int tileRange;
		public static int fullPotencyRange;
	}

	public static class MidasTouchstone {
		public static List<String> goldItems;
		public static int ticksBetweenRepairTicks;
		public static int glowstoneCost;
		public static int glowstoneWorth;
		public static int glowstoneLimit;
	}

	public static class PhoenixDown {
		public static int hungerCostPercent;
		public static int leapingPotency;
		public static int healPercentageOfMaxLife;
		public static boolean removeNegativeStatus;
		public static boolean giveTemporaryDamageResistance;
		public static boolean giveTemporaryRegeneration;
		public static boolean giveTemporaryFireResistanceIfFireDamageKilledYou;
		public static boolean giveTemporaryWaterBreathingIfDrowningKilledYou;
	}

	public static class PyromancerStaff {
		public static int hungerCostPercent;
		public static int fireChargeLimit;
		public static int fireChargeCost;
		public static int fireChargeWorth;
		public static int ghastAbsorbWorth;
		public static int blazePowderLimit;
		public static int blazePowderCost;
		public static int blazePowderWorth;
		public static int blazeAbsorbWorth;
	}

	public static class RendingGale {
		public static int chargeLimit;
		public static int castChargeCost;
		public static int boltChargeCost;
		public static int chargeFeatherWorth;
		public static int blockTargetRange;
		public static int pushPullRadius;
		public static boolean canPushProjectiles;
		public static List<String> entitiesThatCanBePushed;
		public static List<String> projectilesThatCanBePushed;
	}

	public static class RodOfLyssa {
		public static boolean useLeveledFailureRate;
		public static int levelCapForLeveledFormula;
		public static int flatStealFailurePercentRate;
		public static boolean stealFromVacantSlots;
		public static boolean failStealFromVacantSlots;
		public static boolean angerOnStealFailure;
	}

	public static class SojournerStaff {
		public static List<String> torches;
		public static int maxCapacityPerItemType;
		public static int maxRange;
		public static int tilePerCostMultiplier;
	}

	public static class TwilightCloak {
		public static int maxLightLevel;
	}

	public static class VoidTear {
		public static int itemLimit;
		public static boolean absorbWhenCreated;
	}

	public static class SeekerShot {
		public static List<String> entitiesThatCanBeHunted;
	}

	public static class InfernalTear {
		public static boolean absorbWhenCreated;
	}
}
