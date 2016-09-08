package xreliquary.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.*;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.Reliquary;
import xreliquary.entities.EntityGlowingWater;
import xreliquary.entities.EntityHolyHandGrenade;
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
	public static final ItemTwilightCloak twilightCloak = new ItemTwilightCloak();
	public static final ItemVoidTearEmpty emptyVoidTear = new ItemVoidTearEmpty();
	public static final ItemVoidTear filledVoidTear = new ItemVoidTear();
	public static final ItemWitchHat witchHat = new ItemWitchHat();
	public static final ItemWitherlessRose witherlessRose = new ItemWitherlessRose();
	public static final ItemXRPotion potion = new ItemXRPotion();
	public static final ItemMobCharmBelt mobCharmBelt = new ItemMobCharmBelt();
	public static final ItemMobCharm mobCharm = new ItemMobCharm();

	public static void init() {
		registerItem(alkahestryTome, Names.alkahestry_tome);
		registerItem(mobIngredient, Names.mob_ingredient);
		registerItem(mercyCross, Names.mercy_cross);
		registerItem(angelheartVial, Names.angelheart_vial);
		registerItem(angelicFeather, Names.angelic_feather);
		registerItem(attractionPotion, Names.attraction_potion);
		registerItem(bullet, Names.bullet);
		registerItem(destructionCatalyst, Names.destruction_catalyst);
		registerItem(emperorChalice, Names.emperor_chalice);
		registerItem(enderStaff, Names.ender_staff);
		registerItem(fertilePotion, Names.fertile_potion);
		registerItem(fortuneCoin, Names.fortune_coin);
		registerItem(glacialStaff, Names.glacial_staff);
		registerItem(glowingBread, Names.glowing_bread);
		registerItem(glowingWater, Names.glowing_water);
		registerItem(gunPart, Names.gun_part);
		registerItem(handgun, Names.handgun);
		registerItem(harvestRod, Names.harvest_rod);
		registerItem(mobCharmFragment, Names.mob_charm_fragment);
		registerItem(heartZhu, Names.heart_zhu, false);
		registerItem(heroMedallion, Names.hero_medallion);
		registerItem(holyHandGrenade, Names.holy_hand_grenade);
		registerItem(iceMagusRod, Names.ice_magus_rod);
		registerItem(infernalChalice, Names.infernal_chalice);
		registerItem(infernalClaws, Names.infernal_claws);
		registerItem(infernalTear, Names.infernal_tear);
		registerItem(krakenShell, Names.kraken_shell);
		registerItem(lanternOfParanoia, Names.lantern_of_paranoia);
		registerItem(magazine, Names.magazine);
		registerItem(magicbane, Names.magicbane);
		registerItem(midasTouchstone, Names.midas_touchstone);
		registerItem(mobCharm, Names.mob_charm);
		registerItem(mobCharmBelt, Names.mob_charm_belt);
		registerItem(potionEssence, Names.potion_essence, false);
		registerItem(phoenixDown, Names.phoenix_down);
		registerItem(pyromancerStaff, Names.pyromancer_staff);
		registerItem(rendingGale, Names.rending_gale);
		registerItem(rodOfLyssa, Names.rod_of_lyssa);
		registerItem(salamanderEye, Names.salamander_eye);
		registerItem(serpentStaff, Names.serpent_staff);
		registerItem(shearsOfWinter, Names.shears_of_winter);
		registerItem(sojournerStaff, Names.sojourner_staff);
		registerItem(twilightCloak, Names.twilight_cloak);
		registerItem(emptyVoidTear, Names.void_tear_empty);
		registerItem(filledVoidTear, Names.void_tear);
		registerItem(witchHat, Names.witch_hat);
		registerItem(witherlessRose, Names.witherless_rose);
		registerItem(potion, Names.potion);

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