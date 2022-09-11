package reliquary.reference;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import reliquary.client.gui.hud.HUDPosition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static reliquary.util.RegistryHelper.getItemRegistryName;

@SuppressWarnings("squid:S1192") //no issue repeating the same string literal as they are independent
public class Settings {
	private Settings() {}

	private static final int ITEM_CAP = 9999;

	@SuppressWarnings("unused") // parameter needs to stay for addListener logic to recognize what this method is listening to
	public static void onFileChange(ModConfigEvent.Reloading configEvent) {
		COMMON.items.infernalTear.resetCache();
	}

	public static class Client {
		public final HudPos hudPositions;
		public final BooleanValue wailaShiftForInfo;

		public static class HudPos {
			public final EnumValue<HUDPosition> sojournerStaff;
			public final EnumValue<HUDPosition> handgun;
			public final EnumValue<HUDPosition> alkahestryTome;
			public final EnumValue<HUDPosition> destructionCatalyst;
			public final EnumValue<HUDPosition> enderStaff;
			public final EnumValue<HUDPosition> iceMagusRod;
			public final EnumValue<HUDPosition> glacialStaff;
			public final EnumValue<HUDPosition> voidTear;
			public final EnumValue<HUDPosition> midasTouchstone;
			public final EnumValue<HUDPosition> harvestRod;
			public final EnumValue<HUDPosition> infernalChalice;
			public final EnumValue<HUDPosition> heroMedallion;
			public final EnumValue<HUDPosition> pyromancerStaff;
			public final EnumValue<HUDPosition> rendingGale;
			public final EnumValue<HUDPosition> mobCharm;

			HudPos(ForgeConfigSpec.Builder builder) {
				builder.comment("Position of mode and/or item display on the screen - used by some of the tools and weapons.")
						.push("hudPositions");

				sojournerStaff = builder
						.comment("Position of Sojouner Staff HUD")
						.defineEnum("sojournerStaff", HUDPosition.BOTTOM_RIGHT);
				handgun = builder
						.comment("Position of Handgun HUD")
						.defineEnum("handgun", HUDPosition.BOTTOM_RIGHT);
				alkahestryTome = builder
						.comment("Position of Alkahestry Tome HUD")
						.defineEnum("alkahestryTome", HUDPosition.BOTTOM_RIGHT);
				destructionCatalyst = builder
						.comment("Position of Destruction Catalyst HUD")
						.defineEnum("destructionCatalyst", HUDPosition.BOTTOM_RIGHT);
				enderStaff = builder
						.comment("Position of Ender Staff HUD")
						.defineEnum("enderStaff", HUDPosition.BOTTOM_RIGHT);
				iceMagusRod = builder
						.comment("Position of Ice Magus Rod HUD")
						.defineEnum("iceMagusRod", HUDPosition.BOTTOM_RIGHT);
				glacialStaff = builder
						.comment("Position of Glacial Staff HUD")
						.defineEnum("glacialStaff", HUDPosition.BOTTOM_RIGHT);
				voidTear = builder
						.comment("Position of Void Tear HUD")
						.defineEnum("voidTear", HUDPosition.BOTTOM_RIGHT);
				midasTouchstone = builder
						.comment("Position of Midas Touchstone HUD")
						.defineEnum("midasTouchstone", HUDPosition.BOTTOM_RIGHT);
				harvestRod = builder
						.comment("Position of Infernal Chalice HUD")
						.defineEnum("harvestRod", HUDPosition.BOTTOM_RIGHT);
				infernalChalice = builder
						.comment("Position of Ender Staff HUD")
						.defineEnum("infernalChalice", HUDPosition.BOTTOM_RIGHT);
				heroMedallion = builder
						.comment("Position of Hero Medallion HUD")
						.defineEnum("heroMedallion", HUDPosition.BOTTOM_RIGHT);
				pyromancerStaff = builder
						.comment("Position of Pyromancer Staff HUD")
						.defineEnum("pyromancerStaff", HUDPosition.BOTTOM_RIGHT);
				rendingGale = builder
						.comment("Position of Rending Gale HUD")
						.defineEnum("rendingGale", HUDPosition.BOTTOM_RIGHT);
				mobCharm = builder
						.comment("Position of Mob Charm HUD")
						.defineEnum("mobCharm", HUDPosition.RIGHT);

				builder.pop();
			}
		}

		Client(ForgeConfigSpec.Builder builder) {
			builder.comment("Client Settings").push("client");
			hudPositions = new HudPos(builder);
			wailaShiftForInfo = builder
					.comment("Whether player has to sneak to see additional info in waila")
					.define("waila_shift_for_info", false);
			builder.pop();
		}
	}

	public static final Client CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;

	static {
		final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	public static class Common {
		public final DisableSettings disable;
		public final PotionSettings potions;
		public final ItemSettings items;
		public final BlockSettings blocks;
		public final BooleanValue chestLootEnabled;
		public final BooleanValue dropCraftingRecipesEnabled;
		public final BooleanValue mobDropsEnabled;

		public static class DisableSettings {
			public final BooleanValue disableAlkahestry;
			public final BooleanValue disableHandgun;
			public final BooleanValue disablePotions;
			public final BooleanValue disablePedestal;
			public final BooleanValue disablePassivePedestal;
			public final BooleanValue disableSpawnEggRecipes;

			DisableSettings(ForgeConfigSpec.Builder builder) {
				builder.comment("Disable sections of the mod")
						.push("disable");

				disableAlkahestry = builder
						.comment("Disable Alkahestry tome and its recipes")
						.worldRestart()
						.define("alkahestryTome", false);

				disableHandgun = builder
						.comment("Disable the HANDGUN, bullets, magazines, and gun parts")
						.worldRestart()
						.define("handgun", false);

				disablePotions = builder
						.comment("Disable the POTION system including mortar, altar, potions, tipped arrows, and powder")
						.worldRestart()
						.define("potion", false);

				disablePedestal = builder
						.comment("Disable all pedestals")
						.worldRestart()
						.define("pedestal", false);

				disablePassivePedestal = builder
						.comment("Disable all display-only pedestals")
						.worldRestart()
						.define("passivePedestal", false);

				disableSpawnEggRecipes = builder
						.comment("Disable recipes to craft spawn eggs from fragments")
						.worldRestart()
						.define("disableSpawnEggRecipes", false);

				builder.pop();
			}
		}

		Common(ForgeConfigSpec.Builder builder) {
			chestLootEnabled = builder
					.comment("Determines whether Reliquary items will be generated in chest loot (mostly mob drops, very rarely some lower level items)")
					.worldRestart()
					.define("chestLootEnabled", true);

			dropCraftingRecipesEnabled = builder
					.comment("Determines wheter Reliquary mob drops have crafting recipes")
					.define("dropCraftingRecipesEnabled", false);

			mobDropsEnabled = builder
					.comment("Whether mobs drop the Reliquary mob drops. This won't remove mob drop items from registry and replace them with something else, but allows to turn off the additional drops when mobs are killed by player. If this is turned off the mob drop crafting recipes turned on by the other setting can be used.")
					.worldRestart()
					.define("mobDropsEnabled", true);

			disable = new DisableSettings(builder);
			potions = new PotionSettings(builder);
			items = new ItemSettings(builder);
			blocks = new BlockSettings(builder);
		}

		public static class PotionSettings {
			public final ConfigValue<List<String>> potionMap;
			public final IntValue maxEffectCount;
			public final BooleanValue threeIngredients;
			public final BooleanValue differentDurations;
			public final BooleanValue redstoneAndGlowstone;

			PotionSettings(ForgeConfigSpec.Builder builder) {
				builder.comment("Potions related settings").push("potions");

				potionMap = builder
						.comment("Map of POTION ingredients and their effects")
						.define("potionMap", new ArrayList<>());

				maxEffectCount = builder
						.comment("Maximum number of effects a POTION can have to appear in creative tabs / JEI")
						.defineInRange("maxEffectCount", 1, 1, 6);

				threeIngredients = builder
						.comment("Whether potions that are made out of three base ingredients appear in creative tabs / JEI")
						.define("threeIngredients", false);

				differentDurations = builder
						.comment("Whether potions with the same effect combination, but different duration appear in creative tabs / JEI")
						.define("differentDurations", false);

				redstoneAndGlowstone = builder
						.comment("Whether potions augmented with Redstone and Glowstone appear in creative tabs / JEI")
						.define("redstoneAndGlowstone", false);

				builder.pop();
			}
		}

		public static class ItemSettings {
			ItemSettings(ForgeConfigSpec.Builder builder) {
				builder.push("items");

				alkahestryTome = new AlkahestryTomeSettings(builder);
				angelicFeather = new AngelicFeatherSettings(builder);
				angelHeartVial = new AngelHeartVialSettings(builder);
				destructionCatalyst = new DestructionCatalystSettings(builder);
				emperorChalice = new EmperorChaliceSettings(builder);
				enderStaff = new EnderStaffSettings(builder);
				fortuneCoin = new FortuneCoinSettings(builder);
				glacialStaff = new GlacialStaffSettings(builder);
				handgun = new HandgunSettings(builder);
				harvestRod = new HarvestRodSettings(builder);
				heroMedallion = new HeroMedallionSettings(builder);
				iceMagusRod = new IceMagusRodSettings(builder);
				infernalChalice = new InfernalChaliceSettings(builder);
				infernalClaws = new InfernalClawsSettings(builder);
				infernalTear = new InfernalTearSettings(builder);
				krakenShell = new KrakenShellSettings(builder);
				lanternOfParanoia = new LanternOfParanoiaSettings(builder);
				midasTouchstone = new MidasTouchstoneSettings(builder);
				mobCharm = new MobCharmSettings(builder);
				mobCharmFragment = new MobCharmFragmentSettings(builder);
				phoenixDown = new PhoenixDownSettings(builder);
				pyromancerStaff = new PyromancerStaffSettings(builder);
				rendingGale = new RendingGaleSettings(builder);
				rodOfLyssa = new RodOfLyssaSettings(builder);
				seekerShot = new SeekerShotSettings(builder);
				sojournerStaff = new SojournerStaffSettings(builder);
				twilightCloak = new TwilightCloakSettings(builder);
				voidTear = new VoidTearSettings(builder);

				builder.pop();
			}

			public final AlkahestryTomeSettings alkahestryTome;

			public static class AlkahestryTomeSettings {
				public final IntValue chargeLimit;

				AlkahestryTomeSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Alkahestry Tome settings")
							.push("alkahestryTome");

					chargeLimit = builder.comment("Charge limit of the tome").defineInRange("chargeLimit", 1000, 0, ITEM_CAP);

					builder.pop();
				}
			}

			public final MobCharmFragmentSettings mobCharmFragment;

			public static class MobCharmFragmentSettings {
				public final DoubleValue dropChance;
				public final DoubleValue lootingMultiplier;

				MobCharmFragmentSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Mob Charm Fragment Settings")
							.push("mobCharmFragment");

					dropChance = builder.comment("Chance of fragment droping from mobs that don't have fragment that can be crafted").defineInRange("dropChance", 0.1f / 6, 0, 1);
					lootingMultiplier = builder.comment("Additional chance per level of looting").defineInRange("lootingMultiplier", 0.05f / 6, 0, 1);

					builder.pop();
				}
			}

			public final AngelicFeatherSettings angelicFeather;

			public static class AngelicFeatherSettings {
				public final IntValue hungerCostPercent;
				public final IntValue leapingPotency;

				AngelicFeatherSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Angelic Feather settings").push("angelicFeather");

					hungerCostPercent = builder
							.comment("Percent hunger used to heal player per 1 damage that would be taken otherwise.")
							.defineInRange("hungerCostPercent", 50, 0, 100);
					leapingPotency = builder
							.comment("Potency of the leaping effect")
							.defineInRange("leapingPotency", 1, 0, 5);

					builder.pop();
				}
			}

			public final AngelHeartVialSettings angelHeartVial;

			public static class AngelHeartVialSettings {
				public final IntValue healPercentageOfMaxLife;
				public final BooleanValue removeNegativeStatus;

				AngelHeartVialSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Angelheart Vial settings").push("angelheartVial");

					healPercentageOfMaxLife = builder
							.comment("Percent of life that gets healed when the player would die")
							.defineInRange("healPercentageOfMaxLife", 25, 0, 100);

					removeNegativeStatus = builder
							.comment("Whether the player gets negative statuses removed")
							.define("removeNegativeStatus", true);

					builder.pop();
				}
			}

			public final DestructionCatalystSettings destructionCatalyst;

			public static class DestructionCatalystSettings {
				public final ConfigValue<List<String>> mundaneBlocks;
				public final IntValue gunpowderCost;
				public final IntValue gunpowderWorth;
				public final IntValue gunpowderLimit;
				public final IntValue explosionRadius;
				public final BooleanValue centeredExplosion;
				public final BooleanValue perfectCube;

				DestructionCatalystSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Destruction Catalyst settings").push("destructionCatalyst");

					mundaneBlocks = builder
							.comment("List of mundane blocks the catalyst will break")
							.define("mundaneBlocks", Lists.newArrayList(
									"minecraft:dirt",
									"minecraft:coarse_dirt",
									"minecraft:podzol",
									"minecraft:mycelium",
									"minecraft:grass_block",
									"minecraft:gravel",
									"minecraft:cobblestone",
									"minecraft:stone",
									"minecraft:granite",
									"minecraft:diorite",
									"minecraft:andesite",
									"minecraft:sand",
									"minecraft:sandstone",
									"minecraft:snow",
									"minecraft:soul_sand",
									"minecraft:netherrack",
									"minecraft:end_stone"));

					gunpowderCost = builder
							.comment("Number of gunpowder it costs per catalyst use")
							.defineInRange("gunpowderCost", 3, 0, 10);

					gunpowderWorth = builder
							.comment("Number of gunpowder that gets added to catalyst per one that's consumed from players inventory")
							.defineInRange("gunpowderWorth", 1, 1, 3);

					gunpowderLimit = builder
							.comment("Number of gunpowder that can be stored in destruction catalyst")
							.defineInRange("gunpowderLimit", 250, 0, ITEM_CAP);

					explosionRadius = builder
							.comment("Radius of the explosion")
							.defineInRange("explosionRadius", 1, 1, 5);

					centeredExplosion = builder
							.comment("Whether the explosion is centered on the block that gets clicked")
							.define("centeredExplosion", false);

					perfectCube = builder
							.comment("Whether the explosion makes a perfect cube hole")
							.define("perfectCube", true);

					builder.pop();
				}
			}

			public final EmperorChaliceSettings emperorChalice;

			public static class EmperorChaliceSettings {
				public final IntValue hungerSatiationMultiplier;

				EmperorChaliceSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Emperor Chalice settings").push("emperorChalice");

					hungerSatiationMultiplier = builder
							.comment("How much saturation is added in addition to filling the hunger")
							.defineInRange("hungerSatiationMultiplier", 4, 0, 10);

					builder.pop();
				}
			}

			public final EnderStaffSettings enderStaff;

			public static class EnderStaffSettings {
				public final IntValue enderPearlCastCost;
				public final IntValue enderPearlNodeWarpCost;
				public final IntValue enderPearlWorth;
				public final IntValue enderPearlLimit;
				public final IntValue nodeWarpCastTime;

				EnderStaffSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Ender Staff settings").push("enderStaff");

					enderPearlCastCost = builder
							.comment("Number of ender pearls per use")
							.defineInRange("enderPearlCastCost", 1, 0, 3);

					enderPearlNodeWarpCost = builder
							.comment("Number of ender pearls per teleportation to the wraith node")
							.defineInRange("enderPearlNodeWarpCost", 1, 0, 3);

					enderPearlWorth = builder
							.comment("Number of ender pearls that get added to the staff per one that's consumed from players inventory")
							.defineInRange("enderPearlWorth", 1, 1, 10);

					enderPearlLimit = builder
							.comment("Number of ender pearls that the ender staff can store")
							.defineInRange("enderPearlLimit", 250, 0, ITEM_CAP);

					nodeWarpCastTime = builder
							.comment("Time it takes to teleport to the wraith node")
							.defineInRange("nodeWarpCastTime", 60, 10, 120);

					builder.pop();
				}
			}

			public final FortuneCoinSettings fortuneCoin;

			public static class FortuneCoinSettings {
				public final IntValue standardPullDistance;
				public final IntValue longRangePullDistance;

				FortuneCoinSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Fortune Coin settings").push("fortuneCoin");

					standardPullDistance = builder
							.comment("The distance that it pulls from when activated")
							.defineInRange("standardPullDistance", 5, 3, 10);

					longRangePullDistance = builder
							.comment("The distance that it pulls from when right click is held")
							.defineInRange("longRangePullDistance", 15, 9, 30);

					builder.pop();
				}
			}

			public final GlacialStaffSettings glacialStaff;

			public static class GlacialStaffSettings {
				public final IntValue snowballLimit;
				public final IntValue snowballCost;
				public final IntValue snowballWorth;
				public final IntValue snowballDamage;
				public final IntValue snowballDamageBonusFireImmune;
				public final IntValue snowballDamageBonusBlaze;

				GlacialStaffSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Glacial Staff settings").push("glacialStaff");

					snowballLimit = builder
							.comment("Number of snowballs the staff can hold")
							.defineInRange("snowballLimit", 250, 0, ITEM_CAP);

					snowballCost = builder
							.comment("Number of snowballs it costs when the staff is used")
							.defineInRange("snowballCost", 1, 0, 3);

					snowballWorth = builder
							.comment("Number of snowballs that get added to the staff per one that's consumed from player's inventory")
							.defineInRange("snowballWorth", 1, 1, 3);

					snowballDamage = builder
							.comment("The damage that snowballs cause")
							.defineInRange("snowballDamage", 3, 0, 6);

					snowballDamageBonusFireImmune = builder
							.comment("The damage bonus against entities that are immune to fire")
							.defineInRange("snowballDamageBonusFireImmune", 3, 0, 6);

					snowballDamageBonusBlaze = builder
							.comment("The damage bonus against blaze")
							.defineInRange("snowballDamageBonusBlaze", 6, 0, 12);

					builder.pop();
				}
			}

			public final HandgunSettings handgun;

			public static class HandgunSettings {
				public final IntValue maxSkillLevel;

				public HandgunSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Handgun settings").push("handgun");

					maxSkillLevel = builder
							.comment("Experience level at which handgun has the fastest reload time and shortes cooldown between shots")
							.defineInRange("maxSkillLevel", 20, 0, 100);

					builder.pop();
				}
			}

			public final HarvestRodSettings harvestRod;

			public static class HarvestRodSettings {
				public final IntValue boneMealLimit;
				public final IntValue boneMealCost;
				public final IntValue boneMealWorth;
				public final IntValue boneMealLuckPercentChance;
				public final IntValue boneMealLuckRolls;
				public final IntValue aoeRadius;
				public final IntValue aoeCooldown;
				public final IntValue maxCapacityPerPlantable;
				public final IntValue pedestalRange;
				public final IntValue pedestalCooldown;

				HarvestRodSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Harvest Rod settings").push("harvestRod");

					boneMealLimit = builder
							.comment("Number of bonemeal the rod can hold")
							.defineInRange("boneMealLimit", 250, 0, ITEM_CAP);

					boneMealCost = builder
							.comment("Number of bonemeal consumed per use")
							.defineInRange("boneMealCost", 1, 0, 3);

					boneMealWorth = builder
							.comment("Number of bonemeal that gets added to the rod per one that's consumed from player's inventory")
							.defineInRange("boneMealWorth", 1, 1, 3);

					boneMealLuckPercentChance = builder
							.comment("Percent chance that a bonemeal will get applied during a luck roll")
							.defineInRange("boneMealLuckPercentChance", 33, 1, 100);

					boneMealLuckRolls = builder
							.comment("Number of times that a rod may apply additional luck based bonemeal")
							.defineInRange("boneMealLuckRolls", 2, 0, 7);

					aoeRadius = builder
							.comment("Radius in which harvest rod breaks crops, bonemeals/plants/hoes blocks")
							.defineInRange("aoeRadius", 2, 0, 5);

					aoeCooldown = builder
							.comment("Ticks in between bonemealing/planting/hoeing blocks when player is using one of these AOE actions")
							.defineInRange("aoeCooldown", 3, 1, 20);

					maxCapacityPerPlantable = builder
							.comment("Maximum number of units harvest rod can hold per plantable item")
							.defineInRange("maxCapacityPerPlantable", 250, 0, ITEM_CAP);

					pedestalRange = builder
							.comment("Range at which harvest rod will automatically hoe/plant/bonemeal/break crops around pedestals")
							.defineInRange("pedestalRange", 4, 1, 20);

					pedestalCooldown = builder
							.comment("Ticks in between harvest rod actions when in pedestals")
							.defineInRange("pedestalCooldown", 5, 1, 20);

					builder.pop();
				}
			}

			public final HeroMedallionSettings heroMedallion;

			public static class HeroMedallionSettings {
				public final IntValue pedestalCoolDown;
				public final IntValue pedestalRange;
				public final IntValue pedestalRepairStepXP;

				HeroMedallionSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Hero Medallion settings").push("heroMedallion");

					pedestalCoolDown = builder
							.comment("Cooldown between hero medallion tries to fix mending items in nearby pedestals")
							.defineInRange("pedestalCoolDown", 20, 1, 100);

					pedestalRange = builder
							.comment("Range in which pedestals are checked for items with mending enchant that need fixing")
							.defineInRange("pedestalRange", 5, 1, 20);

					pedestalRepairStepXP = builder
							.comment("Maximum amount of xp that is used each time medallion repairs items")
							.defineInRange("pedestalRepairStepXP", 5, 1, 20);

					builder.pop();
				}
			}

			public final IceMagusRodSettings iceMagusRod;

			public static class IceMagusRodSettings {
				public final IntValue snowballLimit;
				public final IntValue snowballCost;
				public final IntValue snowballWorth;
				public final IntValue snowballDamage;
				public final IntValue snowballDamageBonusFireImmune;
				public final IntValue snowballDamageBonusBlaze;

				IceMagusRodSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Ice Magus Rod settings").push("iceMagusRod");

					snowballLimit = builder
							.comment("Number of snowballs the rod can hold")
							.defineInRange("snowballLimit", 250, 0, ITEM_CAP);

					snowballCost = builder
							.comment("Number of snowballs it costs when the rod is used")
							.defineInRange("snowballCost", 1, 0, 3);

					snowballWorth = builder
							.comment("Number of snowballs that get added to the rod per one that's consumed from player's inventory")
							.defineInRange("snowballWorth", 1, 1, 3);

					snowballDamage = builder
							.comment("The damage that snowballs cause")
							.defineInRange("snowballDamage", 2, 0, 4);

					snowballDamageBonusFireImmune = builder
							.comment("Damage bonus against fire immune mobs")
							.defineInRange("snowballDamageBonusFireImmune", 2, 0, 4);

					snowballDamageBonusBlaze = builder
							.comment("Damage bonus against blaze")
							.defineInRange("snowballDamageBonusBlaze", 4, 0, 8);

					builder.pop();
				}
			}

			public final InfernalChaliceSettings infernalChalice;

			public static class InfernalChaliceSettings {
				public final IntValue hungerCostPercent;
				public final IntValue fluidLimit;

				InfernalChaliceSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Infernal Chalice settings").push("infernalChalice");

					hungerCostPercent = builder
							.comment("Percent hunger used to heal player per 1 damage that would be taken otherwise.")
							.defineInRange("hungerCostPercent", 1, 0, 10);

					fluidLimit = builder
							.comment("Millibuckets of lava that the chalice can hold")
							.defineInRange("fluidLimit", 500000, 0, Integer.MAX_VALUE);

					builder.pop();
				}
			}

			public final InfernalClawsSettings infernalClaws;

			public static class InfernalClawsSettings {
				public final IntValue hungerCostPercent;

				InfernalClawsSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Infernal Claws settings").push("infernalClaws");

					hungerCostPercent = builder
							.comment("Percent hunger used to heal player per 1 damage that would be taken otherwise.")
							.defineInRange("hungerCostPercent", 5, 0, 30);

					builder.pop();
				}
			}

			public final InfernalTearSettings infernalTear;

			public static class InfernalTearSettings {
				private static final String ITEM_EXPERIENCE_MATCHER = "([a-z1-9_.-]+:[a-z1-9_/.-]+)\\|\\d+";
				public final BooleanValue absorbWhenCreated;
				@SuppressWarnings("java:S4968") // ? extends String is the type parameter returned from defineList so it can't be just String here
				public final ForgeConfigSpec.ConfigValue<List<? extends String>> itemExperienceList;
				@Nullable
				private Map<String, Integer> itemExperience = null;

				InfernalTearSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Infernal Tear settings").push("infernalTear");

					absorbWhenCreated = builder
							.comment("Whether the infernal tear starts absorbing immediately after it is set to item type")
							.define("absorbWhenCreated", false);
					itemExperienceList = builder.comment("List of items that can be consumed by infernal tear with their experience point value")
							.defineList("entityLootTableList", this::getDefaultInfernalTearMappings, mapping -> ((String) mapping).matches(ITEM_EXPERIENCE_MATCHER));
					builder.pop();
				}

				private List<String> getDefaultInfernalTearMappings() {
					List<String> ret = new ArrayList<>();
					ret.add("minecraft:emerald|63");
					ret.add("minecraft:sandstone|1");
					ret.add("minecraft:gravel|1");
					ret.add("minecraft:diamond|125");
					ret.add("minecraft:gunpowder|8");
					ret.add("minecraft:nether_star|500");
					ret.add("minecraft:iron_ingot|63");
					ret.add("minecraft:charcoal|2");
					ret.add("minecraft:soul_sand|2");
					ret.add("minecraft:lapis_lazuli|8");
					ret.add("minecraft:obsidian|4");
					ret.add("minecraft:end_stone|1");
					ret.add("minecraft:gold_ingot|63");
					ret.add("minecraft:netherrack|1");
					ret.add("minecraft:flint|2");
					ret.add("minecraft:clay|4");
					ret.add("minecraft:chorus_fruit|2");
					ret.add("minecraft:quartz|16");
					ret.add("minecraft:honeycomb|4");
					ret.add("minecraft:netherite_scrap|250");
					return ret;
				}

				public Optional<Integer> getItemExperience(String itemRegistryName) {
					return Optional.ofNullable(getItemExperiences().get(itemRegistryName));
				}

				public Map<String, Integer> getItemExperiences() {
					if (itemExperience == null) {
						itemExperience = new HashMap<>();
						for (String itemAndExperience : itemExperienceList.get()) {
							String[] split = itemAndExperience.split("\\|");
							itemExperience.put(split[0], Integer.valueOf(split[1]));
						}
					}
					return itemExperience;
				}

				public void resetCache() {
					itemExperience = null;
				}
			}

			public final KrakenShellSettings krakenShell;

			public static class KrakenShellSettings {
				public final IntValue hungerCostPercent;

				KrakenShellSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Kraken Shell settings").push("krakenShell");

					hungerCostPercent = builder
							.comment("Percent hunger used to heal player per 1 damage that would be taken otherwise.")
							.defineInRange("hungerCostPercent", 25, 0, 50);

					builder.pop();
				}
			}

			public final LanternOfParanoiaSettings lanternOfParanoia;

			public static class LanternOfParanoiaSettings {
				public final ConfigValue<List<String>> torches;
				public final IntValue minLightLevel;
				public final IntValue placementScanRadius;

				LanternOfParanoiaSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Lantern of Paranoia settings").push("lanternOfParanoia");

					torches = builder
							.comment("List of torches that are supported by the lantern")
							.define("torches", Lists.newArrayList(getItemRegistryName(Items.TORCH)));
					minLightLevel = builder
							.comment("Minimum light level below which the lantern will place torches")
							.defineInRange("minLightLevel", 1, 0, 15);

					placementScanRadius = builder
							.comment("Radius in which the lantern checks for light levels and places torches")
							.defineInRange("placementScanRadius", 6, 1, 15);

					builder.pop();
				}
			}

			public final MidasTouchstoneSettings midasTouchstone;

			public static class MidasTouchstoneSettings {
				public final ConfigValue<List<String>> goldItems;
				public final IntValue glowstoneCost;
				public final IntValue glowstoneWorth;
				public final IntValue glowstoneLimit;

				MidasTouchstoneSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Midas Touchstone settings").push("midasTouchstone");

					goldItems = builder
							.comment("Gold items that can be repaired by the touchstone")
							.define("goldItems", new ArrayList<>());

					glowstoneCost = builder
							.comment("Number of glowstone that the repair costs")
							.defineInRange("glowstoneCost", 1, 0, 3);

					glowstoneWorth = builder
							.comment("Number of glowstone that gets added to the touchstone per one in player's inventory")
							.defineInRange("glowstoneWorth", 4, 1, 12);

					glowstoneLimit = builder
							.comment("Number of glowstone the touchstone can hold")
							.defineInRange("glowstoneLimit", 250, 0, ITEM_CAP);

					builder.pop();
				}
			}

			public final MobCharmSettings mobCharm;

			public static class MobCharmSettings {
				private static final String REGISTRY_NAME_MATCHER = "([a-z1-9_.-]+:[a-z1-9_/.-]+)";

				public final IntValue durability;
				public final IntValue damagePerKill;
				public final IntValue dropDurabilityRepair;
				public final IntValue maxCharmsToDisplay;
				public final IntValue pedestalRange;
				public final BooleanValue keepAlmostDestroyedDisplayed;
				@SuppressWarnings("java:S4968") // ? extends String is the type parameter returned from defineList so it can't be just String here
				public final ConfigValue<List<? extends String>> entityBlockList;

				MobCharmSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Mob Charm settings").push("mobCharm");

					durability = builder
							.comment("Total durability of Mob Charm")
							.defineInRange("durability", 80, 20, 1000);

					damagePerKill = builder
							.comment("Damage that Mob Charm takes when player kills mob it protects them from")
							.defineInRange("damagePerKill", 1, 0, 40);

					dropDurabilityRepair = builder
							.comment("Sets how much durability of Mob Charm gets repaired per special drop")
							.defineInRange("dropDurabilityRepair", 20, 1, 200);

					maxCharmsToDisplay = builder
							.comment("Maximum charms that will get displayed in HUD")
							.defineInRange("maxCharmsToDisplay", 6, 1, 20);

					pedestalRange = builder
							.comment("Range in which mob charm or belt in pedestals will keep monsters from attacking players")
							.defineInRange("pedestalRange", 21, 10, 100);

					keepAlmostDestroyedDisplayed = builder
							.comment("Determines if almost destroyed charms stay displayed in the hud")
							.define("keepAlmostDestroyedDisplayed", true);
					entityBlockList = builder
							.comment("List of hostile entities that are not supposed to have mob charms registered for them")
							.defineList("entityBlockList", this::getDefaultEntityBlockList, entityName -> ((String) entityName).matches(REGISTRY_NAME_MATCHER));
					builder.pop();
				}

				private List<String> getDefaultEntityBlockList() {
					List<String> ret = new ArrayList<>();
					ret.add("minecraft:ender_dragon");
					ret.add("minecraft:wither");
					return ret;
				}
			}

			public final PhoenixDownSettings phoenixDown;

			public static class PhoenixDownSettings {
				public final IntValue hungerCostPercent;
				public final IntValue leapingPotency;
				public final IntValue healPercentageOfMaxLife;
				public final BooleanValue removeNegativeStatus;
				public final BooleanValue giveTemporaryDamageResistance;
				public final BooleanValue giveTemporaryRegeneration;
				public final BooleanValue giveTemporaryFireResistanceIfFireDamageKilledYou;
				public final BooleanValue giveTemporaryWaterBreathingIfDrowningKilledYou;

				PhoenixDownSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Phoenix Down settings").push("PhoenixDown");

					hungerCostPercent = builder
							.comment("Percent hunger used to heal player per 1 damage that would be taken otherwise")
							.defineInRange("hungerCostPercent", 25, 0, 50);

					leapingPotency = builder
							.comment("Potency of the leaping effect")
							.defineInRange("leapingPotency", 1, 0, 5);

					healPercentageOfMaxLife = builder
							.comment("Percent of life that gets healed when the player would die")
							.defineInRange("healPercentageOfMaxLife", 100, 0, 100);

					removeNegativeStatus = builder
							.comment("Whether the player gets negative statuses removed when they were saved by Phoenix Down")
							.define("removeNegativeStatus", true);

					giveTemporaryDamageResistance = builder
							.comment("Whether to give temporary damage resistance when the player would die")
							.define("giveTemporaryDamageResistance", true);

					giveTemporaryRegeneration = builder
							.comment("Whether to give temporary regeneration when the player would die")
							.define("giveTemporaryRegeneration", true);

					giveTemporaryFireResistanceIfFireDamageKilledYou = builder
							.comment("Whether to give temporary fire resistance when the player would die. Applies only when the player is being hurt by fire damage.")
							.define("giveTemporaryFireResistanceIfFireDamageKilledYou", true);

					giveTemporaryWaterBreathingIfDrowningKilledYou = builder
							.comment("Whether to give temporary damage resistance when the player would die. Applies only when the player is drowning.")
							.define("giveTemporaryWaterBreathingIfDrowningKilledYou", true);

					builder.pop();
				}
			}

			public final PyromancerStaffSettings pyromancerStaff;

			public static class PyromancerStaffSettings {
				public final IntValue fireChargeLimit;
				public final IntValue fireChargeCost;
				public final IntValue fireChargeWorth;
				public final IntValue ghastAbsorbWorth;
				public final IntValue blazePowderLimit;
				public final IntValue blazePowderCost;
				public final IntValue blazePowderWorth;
				public final IntValue blazeAbsorbWorth;

				PyromancerStaffSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Pyromancer Staff settings").push("pyromancerStaff");

					fireChargeLimit = builder
							.comment("Number of fire charges the staff can hold")
							.defineInRange("fireChargeLimit", 250, 0, ITEM_CAP);

					fireChargeCost = builder
							.comment("Number of fire charges used when the staff is fired")
							.defineInRange("fireChargeCost", 1, 0, 3);

					fireChargeWorth = builder
							.comment("Number of fire charges that get added to the staff per one that's consumed from player's inventory")
							.defineInRange("fireChargeWorth", 1, 1, 3);

					ghastAbsorbWorth = builder
							.comment("Number of fire charges added to the staff per one that was shot by ghast and gets absorbed by the staff")
							.defineInRange("ghastAbsorbWorth", 1, 0, 3);

					blazePowderLimit = builder
							.comment("Number of blaze powder the staff can hold")
							.defineInRange("blazePowderLimit", 250, 0, ITEM_CAP);

					blazePowderCost = builder
							.comment("Number of blaze powder used when staff is fired")
							.defineInRange("blazePowderCost", 1, 0, 3);

					blazePowderWorth = builder
							.comment("Number of blaze powder that gets added to the staff per one that's consumed from player's inventory")
							.defineInRange("blazePowderWorth", 1, 1, 3);

					blazeAbsorbWorth = builder
							.comment("Number of blaze powder added to the staff per one fireball that was shot by blaze and gets absorbed by the staff")
							.defineInRange("blazeAbsorbWorth", 1, 0, 3);

					builder.pop();
				}
			}

			public final RendingGaleSettings rendingGale;

			public static class RendingGaleSettings {
				public final IntValue chargeLimit;
				public final IntValue castChargeCost;
				public final IntValue boltChargeCost;
				public final IntValue chargeFeatherWorth;
				public final IntValue blockTargetRange;
				public final IntValue pushPullRadius;
				public final BooleanValue canPushProjectiles;
				public final IntValue pedestalFlightRange;
				public final IntValue pedestalCostPerSecond;
				public final ConfigValue<List<String>> pushableEntitiesBlacklist;
				public final ConfigValue<List<String>> pushableProjectilesBlacklist;

				RendingGaleSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Rending Gale settings").push("rendingGale");

					chargeLimit = builder
							.comment("Number of feathers the rending gale can hold")
							.defineInRange("chargeLimit", 30000, 0, Integer.MAX_VALUE);

					castChargeCost = builder
							.comment("Number of feathers used when the rending gale is cast in flight mode")
							.defineInRange("castChargeCost", 1, 0, 3);

					boltChargeCost = builder
							.comment("Number of feathers used to cast a lightning bolt")
							.defineInRange("boltChargeCost", 100, 0, 250);

					chargeFeatherWorth = builder
							.comment("Number of feathers that get added to the rending gale per one that's consumed from player's inventory")
							.defineInRange("chargeFeatherWorth", 100, 1, 250);

					blockTargetRange = builder
							.comment("How far a lightning block can be cast")
							.defineInRange("blockTargetRange", 12, 5, 15);

					pushPullRadius = builder
							.comment("Radius in which entities can be pushed/pulled")
							.defineInRange("pushPullRadius", 10, 1, 20);

					canPushProjectiles = builder
							.comment("Whether the rending gale can push projectiles")
							.define("canPushProjectiles", true);

					pedestalFlightRange = builder
							.comment("Range from pedestals at which players will get buffed with flight")
							.defineInRange("pedestalFlightRange", 30, 10, 100);

					pedestalCostPerSecond = builder
							.comment("Cost per second of buffing players with flight")
							.defineInRange("pedestalCostPerSecond", 5, 1, 20);

					pushableEntitiesBlacklist = builder
							.comment("List of entities that are banned from being pushed by the Rending Gale")
							.define("pushableEntitiesBlacklist", new ArrayList<>());

					pushableProjectilesBlacklist = builder
							.comment("List of projectiles that are banned from being pushed by the Rending Gale")
							.define("pushableProjectilesBlacklist", new ArrayList<>());

					builder.pop();
				}
			}

			public final RodOfLyssaSettings rodOfLyssa;

			public static class RodOfLyssaSettings {
				public final BooleanValue useLeveledFailureRate;
				public final IntValue levelCapForLeveledFormula;
				public final IntValue flatStealFailurePercentRate;
				public final BooleanValue stealFromVacantSlots;
				public final BooleanValue failStealFromVacantSlots;
				public final BooleanValue angerOnStealFailure;
				public final BooleanValue stealFromPlayers;
				private static final String ENTITY_NAME_MATCHER = "[a-z1-9_.-]+:[a-z1-9_/.-]+";
				@SuppressWarnings("java:S4968") // ? extends String is the type parameter returned from defineList so it can't be just String here
				public final ForgeConfigSpec.ConfigValue<List<? extends String>> entityBlockList;
				@Nullable
				private Set<EntityType<?>> blockedEntities = null;

				RodOfLyssaSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Rod of Lyssa settings").push("rodOfLyssa");

					useLeveledFailureRate = builder
							.comment("Whether level influences stealing failure rate of the rod")
							.define("useLeveledFailureRate", true);

					levelCapForLeveledFormula = builder
							.comment("The experience level cap after which the failure rate is at a minimum and doesn't get better")
							.defineInRange("levelCapForLeveledFormula", 100, 1, 900);

					flatStealFailurePercentRate = builder
							.comment("The flat failure rate in case failure rate isn't influenced by player's level")
							.defineInRange("flatStealFailurePercentRate", 10, 0, 100);

					stealFromVacantSlots = builder
							.comment("If set to false it goes through additional 4 accessible slots and looks for items in case the one selected randomly was empty")
							.define("stealFromVacantSlots", true);

					failStealFromVacantSlots = builder
							.comment("Whether stealing from an empty slot triggers failure even if otherwise it would be successful")
							.define("failStealFromVacantSlots", false);

					angerOnStealFailure = builder
							.comment("Whether entities get angry at player if stealing fails")
							.define("angerOnStealFailure", true);

					stealFromPlayers = builder
							.comment("Allows switching stealing from player on and off")
							.define("stealFromPlayers", true);

					entityBlockList = builder.comment("List of entities on which lyssa rod doesn't work - full registry name is required here")
							.defineList("entityBlockList", new ArrayList<>(), mapping -> ((String) mapping).matches(ENTITY_NAME_MATCHER));
					builder.pop();
				}

				public boolean canStealFromEntity(Entity entity) {
					if(blockedEntities == null) {
						initBlockedEntityTypes();
					}
					return !blockedEntities.contains(entity.getType());
				}

				private void initBlockedEntityTypes() {
					blockedEntities = new HashSet<>();
					for (var entityName : entityBlockList.get()) {
						EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityName));
						if (entityType != null) {
							blockedEntities.add(entityType);
						}
					}
				}
			}

			public final SeekerShotSettings seekerShot;

			public static class SeekerShotSettings {
				public final ConfigValue<List<String>> huntableEntitiesBlacklist;

				SeekerShotSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Seeker Shot settings").push("seekerShot");

					huntableEntitiesBlacklist = builder
							.comment("Entities that are banned from being tracked by seeker shot")
							.define("huntableEntitiesBlacklist", new ArrayList<>());

					builder.pop();
				}
			}

			public final SojournerStaffSettings sojournerStaff;

			public static class SojournerStaffSettings {
				public final ConfigValue<List<String>> torches;
				public final IntValue maxCapacityPerItemType;
				public final IntValue maxRange;
				public final IntValue tilePerCostMultiplier;

				SojournerStaffSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Sojourner Staff settings").push("sojournerStaff");

					torches = builder
							.comment("List of torches that are supported by the staff")
							.define("torches", getDefaultTorches());

					maxCapacityPerItemType = builder
							.comment("Number of items the staff can store per item type")
							.defineInRange("maxCapacityPerItemType", 1500, 1, ITEM_CAP);

					maxRange = builder
							.comment("Maximum range at which torches can be placed")
							.defineInRange("maxRange", 30, 1, 30);

					tilePerCostMultiplier = builder
							.comment("Distance after which there is an additional cost for torch placement. The additional cost is the number of times this distance fits in the distance at which the torch is being placed.")
							.defineInRange("tilePerCostMultiplier", 6, 6, 30);

					builder.pop();

				}

				private ArrayList<String> getDefaultTorches() {
					return Lists.newArrayList(
							getItemRegistryName(Items.TORCH),
							getItemRegistryName(Items.SOUL_TORCH),
							getItemRegistryName(Items.LANTERN),
							getItemRegistryName(Items.JACK_O_LANTERN),
							getItemRegistryName(Items.SEA_LANTERN),
							getItemRegistryName(Items.SOUL_LANTERN),
							getItemRegistryName(Items.SHROOMLIGHT),
							getItemRegistryName(Items.GLOWSTONE),
							getItemRegistryName(Items.END_ROD)
					);
				}
			}

			public final TwilightCloakSettings twilightCloak;

			public static class TwilightCloakSettings {
				public final IntValue maxLightLevel;

				TwilightCloakSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Twilight Cloak settings").push("twilightCloak");

					maxLightLevel = builder
							.comment("Maximum light level at which the player is still invisible to the mobs")
							.defineInRange("maxLightLevel", 4, 0, 15);

					builder.pop();
				}
			}

			public final VoidTearSettings voidTear;

			public static class VoidTearSettings {
				public final IntValue itemLimit;
				public final BooleanValue absorbWhenCreated;

				VoidTearSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Void Tear settings").push("voidTear");

					itemLimit = builder
							.comment("Number of items the tear can hold of the item type it is set to")
							.defineInRange("itemLimit", 2000000000, 0, Integer.MAX_VALUE);

					absorbWhenCreated = builder
							.comment("Whether the void tear starts absorbing immediately after it is set to item type")
							.define("absorbWhenCreated", true);

					builder.pop();
				}
			}
		}

		public static class BlockSettings {
			BlockSettings(ForgeConfigSpec.Builder builder) {
				builder.push("blocks");
				altar = new AltarSettings(builder);
				apothecaryCauldron = new ApothecaryCauldronSettings(builder);
				fertileLilypad = new FertileLilypadSettings(builder);
				interdictionTorch = new InterdictionTorchSettings(builder);
				pedestal = new PedestalSettings(builder);
				builder.pop();
			}

			public final AltarSettings altar;

			public static class AltarSettings {
				public final IntValue redstoneCost;
				public final IntValue timeInMinutes;
				public final IntValue maximumTimeVarianceInMinutes;
				public final IntValue outputLightLevelWhileActive;

				AltarSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Altar of Light settings").push("altar");

					redstoneCost = builder
							.comment("Number of redstone it costs to activate altar")
							.defineInRange("redstoneCost", 3, 0, 10);

					timeInMinutes = builder
							.comment("Time in minutes it takes for the altar to create glowstone block")
							.defineInRange("timeInMinutes", 20, 0, 60);

					maximumTimeVarianceInMinutes = builder
							.comment("Maximum time variance in minutes. A random part of it gets added to the Time in minutes.")
							.defineInRange("maximumTimeVarianceInMinutes", 5, 0, 15);

					outputLightLevelWhileActive = builder
							.comment("Light level that the altar outputs while active")
							.defineInRange("outputLightLevelWhileActive", 16, 0, 16);

					builder.pop();
				}
			}

			public final ApothecaryCauldronSettings apothecaryCauldron;

			public static class ApothecaryCauldronSettings {
				public final IntValue redstoneLimit;
				public final IntValue cookTime;
				public final ConfigValue<List<String>> heatSources;
				public final IntValue glowstoneLimit;

				ApothecaryCauldronSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Apothecary Cauldron settings").push("apothecaryCauldron");

					redstoneLimit = builder
							.comment("Limit of redstone that can be used in cauldron to make POTION last longer")
							.defineInRange("redstoneLimit", 3, 0, 5);

					cookTime = builder
							.comment("Time it takes to cook POTION")
							.defineInRange("cookTime", 160, 20, 32000);

					heatSources = builder
							.comment("List of acceptable heat sources")
							.define("heatSources", new ArrayList<>());

					glowstoneLimit = builder
							.comment("Limit of glowstone that can be used in cauldron to make POTION more potent")
							.defineInRange("glowstoneLimit", 2, 0, 4);

					builder.pop();
				}
			}

			public final FertileLilypadSettings fertileLilypad;

			public static class FertileLilypadSettings {
				public final IntValue secondsBetweenGrowthTicks;
				public final IntValue tileRange;
				public final IntValue fullPotencyRange;

				FertileLilypadSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Lilypad of Fertility settings").push("fertileLilypad");

					secondsBetweenGrowthTicks = builder
							.comment("Interval in seconds at which the lilypad causes growth tick updates")
							.defineInRange("secondsBetweenGrowthTicks", 10, 1, 150);

					tileRange = builder
							.comment("Radius in which lilypad causes growh ticks")
							.defineInRange("tileRange", 4, 1, 15);

					fullPotencyRange = builder
							.comment("Radius around lilypad where the growth ticks occur the most often")
							.defineInRange("fullPotencyRange", 1, 1, 15);

					builder.pop();
				}
			}

			public final InterdictionTorchSettings interdictionTorch;

			public static class InterdictionTorchSettings {
				public final IntValue pushRadius;
				public final BooleanValue canPushProjectiles;
				public final ConfigValue<List<String>> pushableEntitiesBlacklist;
				public final ConfigValue<List<String>> pushableProjectilesBlacklist;

				InterdictionTorchSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Interdiction Torch settings").push("interdictionTorch");

					pushRadius = builder
							.comment("Radius in which the torch can push out mobs")
							.defineInRange("pushRadius", 5, 1, 15);

					canPushProjectiles = builder
							.comment("Whether the torch can push projectiles")
							.define("canPushProjectiles", false);

					pushableEntitiesBlacklist = builder
							.comment("List of entities that are banned from being pushed by the torch")
							.define("pushableEntitiesBlacklist", new ArrayList<>());

					pushableProjectilesBlacklist = builder
							.comment("List of projectiles that are banned from being pushed by the torch")
							.define("pushableProjectilesBlacklist", new ArrayList<>());

					builder.pop();
				}
			}

			public final PedestalSettings pedestal;

			public static class PedestalSettings {
				public final IntValue meleeWrapperRange;
				public final IntValue meleeWrapperCooldown;
				public final IntValue bucketWrapperRange;
				public final IntValue bucketWrapperCooldown;
				public final IntValue shearsWrapperRange;
				public final IntValue shearsWrapperCooldown;
				public final IntValue redstoneWrapperRange;
				public final IntValue fishingWrapperSuccessRate;
				public final IntValue fishingWrapperRetractDelay;

				PedestalSettings(ForgeConfigSpec.Builder builder) {
					builder.comment("Pedestal related settings").push("pedestal");

					meleeWrapperRange = builder
							.comment("Range of the melee weapons in which these will attack when in pedestals")
							.defineInRange("meleeWrapperRange", 5, 1, 10);

					meleeWrapperCooldown = builder
							.comment("How long it takes after a melee weapon swing before it can swing again (in ticks)")
							.defineInRange("meleeWrapperCooldown", 5, 1, 200);

					bucketWrapperRange = builder
							.comment("Range at which bucket will pickup liquid blocks or milk cows")
							.defineInRange("bucketWrapperRange", 4, 1, 10);

					bucketWrapperCooldown = builder
							.comment("How long it takes in between bucket actions (in ticks)")
							.defineInRange("bucketWrapperCooldown", 40, 1, 200);

					shearsWrapperRange = builder
							.comment("How long it takes between shearing actions (in ticks)")
							.defineInRange("shearsWrapperRange", 4, 1, 10);

					shearsWrapperCooldown = builder
							.comment("Range at which shears will shear sheep or shearable blocks")
							.defineInRange("shearsWrapperCooldown", 10, 1, 200);

					redstoneWrapperRange = builder
							.comment("Range at which pedestals will get turned on if either redstone block gets put in or redstone dust and transmitting pedestals is powered")
							.defineInRange("redstoneWrapperRange", 10, 1, 200);

					fishingWrapperSuccessRate = builder
							.comment("Success rate of fishing in percent. When unsuccessful it will pull the hook too late to catch a fish.")
							.defineInRange("fishingWrapperSuccessRate", 80, 0, 100);

					fishingWrapperRetractDelay = builder
							.comment("Delay in seconds before it would start fishing again after retracting the hook.")
							.defineInRange("fishingWrapperRetractDelay", 2, 1, 20);

					builder.pop();
				}
			}
		}
	}

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}
}
