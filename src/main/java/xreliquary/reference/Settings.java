package xreliquary.reference;

import net.minecraft.item.ItemStack;
import xreliquary.client.gui.hud.HUDPosition;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {
	public static boolean chestLootEnabled;
	public static boolean wailaShiftForInfo;
	public static boolean dropCraftingRecipesEnabled;
	public static boolean mobDropsEnabled;

	public static class Potions {
		public static int maxEffectCount;
		public static boolean threeIngredients;
		public static boolean differentDurations;
		public static boolean redstoneAndGlowstone;
		public static List<PotionIngredient> potionMap = new ArrayList<>();
		public static List<PotionEssence> potionCombinations = new ArrayList<>();
		public static List<PotionEssence> uniquePotionEssences = new ArrayList<>();
		public static List<PotionEssence> uniquePotions = new ArrayList<>();
	}

	public static class HudPositions {
		public static HUDPosition sojournerStaff;
		public static HUDPosition handgun;
		public static HUDPosition alkahestryTome;
		public static HUDPosition destructionCatalyst;
		public static HUDPosition enderStaff;
		public static HUDPosition iceMagusRod;
		public static HUDPosition glacialStaff;
		public static HUDPosition voidTear;
		public static HUDPosition midasTouchstone;
		public static HUDPosition harvestRod;
		public static HUDPosition infernalChalice;
		public static HUDPosition heroMedallion;
		public static HUDPosition pyromancerStaff;
		public static HUDPosition rendingGale;
		public static HUDPosition mobCharm;
	}

	public static class AlkahestryTome {
		public static int chargeLimit;
		public static Map<String, AlkahestCraftRecipe> craftingRecipes = new HashMap<>();
		public static Map<String, AlkahestChargeRecipe> chargingRecipes = new HashMap<>();
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
		public static int glowstoneLimit;
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
		public static int boneMealLimit;
		public static int boneMealCost;
		public static int boneMealWorth;
		public static int boneMealLuckPercentChance;
		public static int boneMealLuckRolls;
		public static int AOERadius;
		public static int AOECooldown;
		public static int maxCapacityPerPlantable;
		public static int pedestalRange;
		public static byte pedestalCooldown;
	}

	public static class HeroMedallion {
		public static int experienceLevelMaximum;
		public static int experienceLevelMinimum;
		public static int experienceLimit;
		public static int experienceDrop;
		public static int pedestalRange;
		public static int pedestalRepairStepXP;
		public static int pedestalCoolDown;
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
		public static List<String> pushableEntitiesBlacklist;
		public static List<String> pushableProjectilesBlacklist;
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
		public static List<String> pushableEntitiesBlacklist;
		public static List<String> pushableProjectilesBlacklist;
		public static int pedestalFlightRange;
		public static int pedestalCostPerSecond;
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
		public static List<ItemStack> torches;
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
		public static List<String> huntableEntitiesBlacklist;
	}

	public static class InfernalTear {
		public static boolean absorbWhenCreated;
	}

	public static class Pedestal {
		public static int meleeWrapperRange;
		public static byte meleeWrapperCooldown;
		public static int bucketWrapperRange;
		public static byte bucketWrapperCooldown;
		public static int shearsWrapperRange;
		public static byte shearsWrapperCooldown;
		public static int redstoneWrapperRange;
		public static int fishingWrapperSuccessRate;
		public static int fishingWrapperRetractDelay;
	}

	public static class MobCharm {
		public static int durability;
		public static int damagePerKill;
		public static int dropDurabilityRepair;
		public static int maxCharmsToDisplay;
		public static int pedestalRange;
		public static boolean keepAlmostDestroyedDisplayed;
	}
}
