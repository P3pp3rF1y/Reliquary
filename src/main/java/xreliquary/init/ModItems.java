package xreliquary.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.*;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.Reliquary;
import xreliquary.entities.EntityGlowingWater;
import xreliquary.entities.EntityHolyHandGrenade;
import xreliquary.entities.EntityXRTippedArrow;
import xreliquary.entities.potion.EntityAttractionPotion;
import xreliquary.entities.potion.EntityFertilePotion;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.items.*;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

public class ModItems {

	public static final ItemAlkahestryTome alkahestryTome = new ItemAlkahestryTome();
	public static final ItemMobIngredient mobIngredient = new ItemMobIngredient();
	public static final ItemMercyCross mercyCross = new ItemMercyCross();
	public static final ItemAngelheartVial angelheartVial = new ItemAngelheartVial();
	public static final ItemAngelicFeather angelicFeather = new ItemAngelicFeather();
	public static final ItemAttractionPotion attractionPotion = new ItemAttractionPotion();
	public static final ItemPotionEssence potionEssence = new ItemPotionEssence();
	public static final ItemBullet bullet = new ItemBullet();
	public static final ItemDestructionCatalyst destructionCatalyst = new ItemDestructionCatalyst();
	public static final ItemEmperorChalice emperorChalice = new ItemEmperorChalice();
	public static final ItemEnderStaff enderStaff = new ItemEnderStaff();
	public static final ItemFertilePotion fertilePotion = new ItemFertilePotion();
	public static final ItemFortuneCoin fortuneCoin = new ItemFortuneCoin();
	public static final ItemGlacialStaff glacialStaff = new ItemGlacialStaff();
	public static final ItemGlowingBread glowingBread = new ItemGlowingBread();
	public static final ItemGlowingWater glowingWater = new ItemGlowingWater();
	public static final ItemHolyHandGrenade holyHandGrenade = new ItemHolyHandGrenade();
	public static final ItemGunPart gunPart = new ItemGunPart();
	public static final ItemHandgun handgun = new ItemHandgun();
	public static final ItemHarvestRod harvestRod = new ItemHarvestRod();
	public static final ItemMobCharmFragment mobCharmFragment = new ItemMobCharmFragment();
	public static final ItemHeartZhu heartZhu = new ItemHeartZhu();
	public static final ItemHeroMedallion heroMedallion = new ItemHeroMedallion();
	public static final ItemIceMagusRod iceMagusRod = new ItemIceMagusRod();
	public static final ItemInfernalChalice infernalChalice = new ItemInfernalChalice();
	public static final ItemInfernalClaws infernalClaws = new ItemInfernalClaws();
	public static final ItemInfernalTear infernalTear = new ItemInfernalTear();
	public static final ItemKrakenShell krakenShell = new ItemKrakenShell();
	public static final ItemLanternOfParanoia lanternOfParanoia = new ItemLanternOfParanoia();
	public static final ItemMagazine magazine = new ItemMagazine();
	public static final ItemMagicbane magicbane = new ItemMagicbane();
	public static final ItemMidasTouchstone midasTouchstone = new ItemMidasTouchstone();
	public static final ItemPhoenixDown phoenixDown = new ItemPhoenixDown();
	public static final ItemPyromancerStaff pyromancerStaff = new ItemPyromancerStaff();
	public static final ItemRendingGale rendingGale = new ItemRendingGale();
	public static final ItemRodOfLyssa rodOfLyssa = new ItemRodOfLyssa();
	public static final ItemSalamanderEye salamanderEye = new ItemSalamanderEye();
	public static final ItemSerpentStaff serpentStaff = new ItemSerpentStaff();
	public static final ItemShearsOfWinter shearsOfWinter = new ItemShearsOfWinter();
	public static final ItemSojournerStaff sojournerStaff = new ItemSojournerStaff();
	public static final ItemXRTippedArrow tippedArrow = new ItemXRTippedArrow();
	public static final ItemTwilightCloak twilightCloak = new ItemTwilightCloak();
	public static final ItemVoidTearEmpty emptyVoidTear = new ItemVoidTearEmpty();
	public static final ItemVoidTear filledVoidTear = new ItemVoidTear();
	public static final ItemWitchHat witchHat = new ItemWitchHat();
	public static final ItemWitherlessRose witherlessRose = new ItemWitherlessRose();
	public static final ItemXRPotion potion = new ItemXRPotion();
	public static final ItemMobCharmBelt mobCharmBelt = new ItemMobCharmBelt();
	public static final ItemMobCharm mobCharm = new ItemMobCharm();

	public static void init() {
		registerItem(alkahestryTome, Names.Items.ALKAHESTRY_TOME);
		registerItem(mobIngredient, Names.Items.MOB_INGREDIENT);
		registerItem(mercyCross, Names.Items.MERCY_CROSS);
		registerItem(angelheartVial, Names.Items.ANGELHEART_VIAL);
		registerItem(angelicFeather, Names.Items.ANGELIC_FEATHER);
		registerItem(attractionPotion, Names.Items.ATTRACTION_POTION);
		registerItem(bullet, Names.Items.BULLET);
		registerItem(destructionCatalyst, Names.Items.DESTRUCTION_CATALYST);
		registerItem(emperorChalice, Names.Items.EMPEROR_CHALICE);
		registerItem(enderStaff, Names.Items.ENDER_STAFF);
		registerItem(fertilePotion, Names.Items.FERTILE_POTION);
		registerItem(fortuneCoin, Names.Items.FORTUNE_COIN);
		registerItem(glacialStaff, Names.Items.GLACIAL_STAFF);
		registerItem(glowingBread, Names.Items.GLOWING_BREAD);
		registerItem(glowingWater, Names.Items.GLOWING_WATER);
		registerItem(gunPart, Names.Items.GUN_PART);
		registerItem(handgun, Names.Items.HANDGUN);
		registerItem(harvestRod, Names.Items.HARVEST_ROD);
		registerItem(mobCharmFragment, Names.Items.MOB_CHARM_FRAGMENT);
		registerItem(heartZhu, Names.Items.HEART_ZHU, false);
		registerItem(heroMedallion, Names.Items.HERO_MEDALLION);
		registerItem(holyHandGrenade, Names.Items.HOLY_HAND_GRENADE);
		registerItem(iceMagusRod, Names.Items.ICE_MAGUS_ROD);
		registerItem(infernalChalice, Names.Items.INFERNAL_CHALICE);
		registerItem(infernalClaws, Names.Items.INFERNAL_CLAWS);
		registerItem(infernalTear, Names.Items.INFERNAL_TEAR);
		registerItem(krakenShell, Names.Items.KRAKEN_SHELL);
		registerItem(lanternOfParanoia, Names.Items.LANTERN_OF_PARANOIA);
		registerItem(magazine, Names.Items.MAGAZINE);
		registerItem(magicbane, Names.Items.MAGICBANE);
		registerItem(midasTouchstone, Names.Items.MIDAS_TOUCHSTONE);
		registerItem(mobCharm, Names.Items.MOB_CHARM, false);
		registerItem(mobCharmBelt, Names.Items.MOB_CHARM_BELT);
		registerItem(potionEssence, Names.Items.POTION_ESSENCE, false);
		registerItem(phoenixDown, Names.Items.PHOENIX_DOWN);
		registerItem(pyromancerStaff, Names.Items.PYROMANCER_STAFF);
		registerItem(rendingGale, Names.Items.RENDING_GALE);
		registerItem(rodOfLyssa, Names.Items.ROD_OF_LYSSA);
		registerItem(salamanderEye, Names.Items.SALAMANDER_EYE);
		registerItem(serpentStaff, Names.Items.SERPENT_STAFF);
		registerItem(shearsOfWinter, Names.Items.SHEARS_OF_WINTER);
		registerItem(sojournerStaff, Names.Items.SOJOURNER_STAFF);
		registerItem(tippedArrow, Names.Items.TIPPED_ARROW, false);
		registerItem(twilightCloak, Names.Items.TWILIGHT_CLOAK);
		registerItem(emptyVoidTear, Names.Items.VOID_TEAR_EMPTY);
		registerItem(filledVoidTear, Names.Items.VOID_TEAR);
		registerItem(witchHat, Names.Items.WITCH_HAT);
		registerItem(witherlessRose, Names.Items.WITHERLESS_ROSE);
		registerItem(potion, Names.Items.POTION, false);

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.potion, new IBehaviorDispenseItem() {
			public ItemStack dispense(IBlockSource source, final ItemStack stack) {
				if(!ModItems.potion.getSplash(stack) && !ModItems.potion.getLingering(stack))
					return new BehaviorDefaultDispenseItem().dispense(source, stack);

				return (new BehaviorProjectileDispense() {
					protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
						return new EntityThrownXRPotion(worldIn, position.getX(), position.getY(), position.getZ(), stack);
					}

					protected float getProjectileInaccuracy() {
						return super.getProjectileInaccuracy() * 0.5F;
					}

					protected float getProjectileVelocity() {
						return super.getProjectileVelocity() * 1.25F;
					}
				}).dispense(source, stack);
			}
		});

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.glowingWater, new IBehaviorDispenseItem() {
			public ItemStack dispense(IBlockSource source, final ItemStack stack) {
				return (new BehaviorProjectileDispense() {
					protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
						return new EntityGlowingWater(worldIn, position.getX(), position.getY(), position.getZ());
					}

					protected float getProjectileInaccuracy() {
						return super.getProjectileInaccuracy() * 0.5F;
					}

					protected float getProjectileVelocity() {
						return super.getProjectileVelocity() * 1.25F;
					}
				}).dispense(source, stack);
			}
		});

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.attractionPotion, new IBehaviorDispenseItem() {
			public ItemStack dispense(IBlockSource source, final ItemStack stack) {
				return (new BehaviorProjectileDispense() {
					protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
						return new EntityAttractionPotion(worldIn, position.getX(), position.getY(), position.getZ());
					}

					protected float getProjectileInaccuracy() {
						return super.getProjectileInaccuracy() * 0.5F;
					}

					protected float getProjectileVelocity() {
						return super.getProjectileVelocity() * 1.25F;
					}
				}).dispense(source, stack);
			}
		});

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.fertilePotion, new IBehaviorDispenseItem() {
			public ItemStack dispense(IBlockSource source, final ItemStack stack) {
				return (new BehaviorProjectileDispense() {
					protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
						return new EntityFertilePotion(worldIn, position.getX(), position.getY(), position.getZ());
					}

					protected float getProjectileInaccuracy() {
						return super.getProjectileInaccuracy() * 0.5F;
					}

					protected float getProjectileVelocity() {
						return super.getProjectileVelocity() * 1.25F;
					}
				}).dispense(source, stack);
			}
		});

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.holyHandGrenade, new IBehaviorDispenseItem() {
			public ItemStack dispense(IBlockSource source, final ItemStack stack) {
				return (new BehaviorProjectileDispense() {
					protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
						return new EntityHolyHandGrenade(worldIn, position.getX(), position.getY(), position.getZ(), stackIn.getDisplayName());
					}

					protected float getProjectileInaccuracy() {
						return super.getProjectileInaccuracy() * 0.5F;
					}

					protected float getProjectileVelocity() {
						return super.getProjectileVelocity() * 1.25F;
					}
				}).dispense(source, stack);
			}
		});

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.holyHandGrenade, new IBehaviorDispenseItem() {
			public ItemStack dispense(IBlockSource source, final ItemStack stack) {
				return (new BehaviorProjectileDispense() {
					protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
						return new EntityHolyHandGrenade(worldIn, position.getX(), position.getY(), position.getZ(), stackIn.getDisplayName());
					}

					protected float getProjectileInaccuracy() {
						return super.getProjectileInaccuracy() * 0.5F;
					}

					protected float getProjectileVelocity() {
						return super.getProjectileVelocity() * 1.25F;
					}
				}).dispense(source, stack);
			}
		});

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.tippedArrow, new BehaviorProjectileDispense() {

			@Override
			protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
				EntityXRTippedArrow entitytippedarrow = new EntityXRTippedArrow(world, position.getX(), position.getY(), position.getZ());
				entitytippedarrow.setPotionEffect(stack);
				entitytippedarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
				return entitytippedarrow;
			}
		});
	}

	private static void registerItem(Item item, String name) {
		registerItem(item, name, true);
	}

	private static void registerItem(Item item, String name, boolean registerInJEI) {

		if(Settings.disabledItemsBlocks.contains(name))
			return;

		item.setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
		GameRegistry.register(item);

		if(registerInJEI)
			Reliquary.PROXY.registerJEI(item, name);
	}

}