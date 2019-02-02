package xreliquary.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import xreliquary.Reliquary;
import xreliquary.entities.EntityGlowingWater;
import xreliquary.entities.EntityHolyHandGrenade;
import xreliquary.entities.EntityXRTippedArrow;
import xreliquary.entities.potion.EntityAttractionPotion;
import xreliquary.entities.potion.EntityFertilePotion;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.items.ItemAlkahestryTome;
import xreliquary.items.ItemAngelheartVial;
import xreliquary.items.ItemAngelicFeather;
import xreliquary.items.ItemAttractionPotion;
import xreliquary.items.ItemBullet;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.items.ItemEmperorChalice;
import xreliquary.items.ItemEnderStaff;
import xreliquary.items.ItemFertilePotion;
import xreliquary.items.ItemFortuneCoin;
import xreliquary.items.ItemGlacialStaff;
import xreliquary.items.ItemGlowingBread;
import xreliquary.items.ItemGlowingWater;
import xreliquary.items.ItemGunPart;
import xreliquary.items.ItemHandgun;
import xreliquary.items.ItemHarvestRod;
import xreliquary.items.ItemHeroMedallion;
import xreliquary.items.ItemHolyHandGrenade;
import xreliquary.items.ItemIceMagusRod;
import xreliquary.items.ItemInfernalChalice;
import xreliquary.items.ItemInfernalClaws;
import xreliquary.items.ItemInfernalTear;
import xreliquary.items.ItemKrakenShell;
import xreliquary.items.ItemLanternOfParanoia;
import xreliquary.items.ItemMagazine;
import xreliquary.items.ItemMagicbane;
import xreliquary.items.ItemMercyCross;
import xreliquary.items.ItemMidasTouchstone;
import xreliquary.items.ItemMobCharm;
import xreliquary.items.ItemMobCharmBelt;
import xreliquary.items.ItemMobCharmFragment;
import xreliquary.items.ItemMobIngredient;
import xreliquary.items.ItemPhoenixDown;
import xreliquary.items.ItemPotionEssence;
import xreliquary.items.ItemPyromancerStaff;
import xreliquary.items.ItemRendingGale;
import xreliquary.items.ItemRodOfLyssa;
import xreliquary.items.ItemSalamanderEye;
import xreliquary.items.ItemSerpentStaff;
import xreliquary.items.ItemShearsOfWinter;
import xreliquary.items.ItemSojournerStaff;
import xreliquary.items.ItemTwilightCloak;
import xreliquary.items.ItemVoidTear;
import xreliquary.items.ItemWitchHat;
import xreliquary.items.ItemWitherlessRose;
import xreliquary.items.ItemXRPotion;
import xreliquary.items.ItemXRTippedArrow;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModItems {

	public static ItemAlkahestryTome alkahestryTome;
	public static ItemMobIngredient mobIngredient;
	public static ItemMercyCross mercyCross;
	public static ItemAngelheartVial angelheartVial;
	public static ItemAngelicFeather angelicFeather;
	public static ItemAttractionPotion attractionPotion;
	public static ItemPotionEssence potionEssence;
	public static ItemBullet bullet;
	public static ItemDestructionCatalyst destructionCatalyst;
	public static ItemEmperorChalice emperorChalice;
	public static ItemEnderStaff enderStaff;
	public static ItemFertilePotion fertilePotion;
	public static ItemFortuneCoin fortuneCoin;
	public static ItemGlacialStaff glacialStaff;
	public static ItemGlowingBread glowingBread;
	public static ItemGlowingWater glowingWater;
	public static ItemHolyHandGrenade holyHandGrenade;
	public static ItemGunPart gunPart;
	public static ItemHandgun handgun;
	public static ItemHarvestRod harvestRod;
	public static ItemMobCharmFragment mobCharmFragment;
	public static ItemHeroMedallion heroMedallion;
	public static ItemIceMagusRod iceMagusRod;
	public static ItemInfernalChalice infernalChalice;
	public static ItemInfernalClaws infernalClaws;
	public static ItemInfernalTear infernalTear;
	public static ItemKrakenShell krakenShell;
	public static ItemLanternOfParanoia lanternOfParanoia;
	public static ItemMagazine magazine;
	public static ItemMagicbane magicbane;
	public static ItemMidasTouchstone midasTouchstone;
	public static ItemPhoenixDown phoenixDown;
	public static ItemPyromancerStaff pyromancerStaff;
	public static ItemRendingGale rendingGale;
	public static ItemRodOfLyssa rodOfLyssa;
	public static ItemSalamanderEye salamanderEye;
	public static ItemSerpentStaff serpentStaff;
	public static ItemShearsOfWinter shearsOfWinter;
	public static ItemSojournerStaff sojournerStaff;
	public static ItemXRTippedArrow tippedArrow;
	public static ItemTwilightCloak twilightCloak;
	public static ItemVoidTear voidTear;
	public static ItemWitchHat witchHat;
	public static ItemWitherlessRose witherlessRose;
	public static ItemXRPotion potion;
	public static ItemMobCharmBelt mobCharmBelt;
	public static ItemMobCharm mobCharm;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
	  if(Settings.Disable.enableAlkahestry) {
	    alkahestryTome = registerItem(registry, new ItemAlkahestryTome(), Names.Items.ALKAHESTRY_TOME);
	  }
	  mobIngredient = registerItem(registry, new ItemMobIngredient(), Names.Items.MOB_INGREDIENT);
		mercyCross = registerItem(registry, new ItemMercyCross(), Names.Items.MERCY_CROSS);
		angelheartVial = registerItem(registry, new ItemAngelheartVial(), Names.Items.ANGELHEART_VIAL);
		angelicFeather = registerItem(registry, new ItemAngelicFeather(), Names.Items.ANGELIC_FEATHER);

		destructionCatalyst = registerItem(registry, new ItemDestructionCatalyst(), Names.Items.DESTRUCTION_CATALYST);
		emperorChalice = registerItem(registry, new ItemEmperorChalice(), Names.Items.EMPEROR_CHALICE);
		enderStaff = registerItem(registry, new ItemEnderStaff(), Names.Items.ENDER_STAFF);
		fortuneCoin = registerItem(registry,  new ItemFortuneCoin(), Names.Items.FORTUNE_COIN);
		glacialStaff = registerItem(registry, new ItemGlacialStaff(), Names.Items.GLACIAL_STAFF);
		glowingBread = registerItem(registry, new ItemGlowingBread(), Names.Items.GLOWING_BREAD);
		glowingWater = registerItem(registry, new ItemGlowingWater(), Names.Items.GLOWING_WATER);
		harvestRod = registerItem(registry, new ItemHarvestRod(), Names.Items.HARVEST_ROD);
		mobCharmFragment = registerItem(registry, new ItemMobCharmFragment(), Names.Items.MOB_CHARM_FRAGMENT);
		heroMedallion = registerItem(registry, new ItemHeroMedallion(), Names.Items.HERO_MEDALLION);
		holyHandGrenade = registerItem(registry, new ItemHolyHandGrenade(), Names.Items.HOLY_HAND_GRENADE);
		iceMagusRod = registerItem(registry, new ItemIceMagusRod(), Names.Items.ICE_MAGUS_ROD);
		infernalChalice = registerItem(registry, new ItemInfernalChalice(), Names.Items.INFERNAL_CHALICE);
		infernalClaws = registerItem(registry, new ItemInfernalClaws(), Names.Items.INFERNAL_CLAWS);
		infernalTear = registerItem(registry, new ItemInfernalTear(), Names.Items.INFERNAL_TEAR);
		krakenShell = registerItem(registry, new ItemKrakenShell(), Names.Items.KRAKEN_SHELL);
		lanternOfParanoia = registerItem(registry, new ItemLanternOfParanoia(), Names.Items.LANTERN_OF_PARANOIA);
		magicbane = registerItem(registry, new ItemMagicbane(), Names.Items.MAGICBANE);
		midasTouchstone = registerItem(registry, new ItemMidasTouchstone(), Names.Items.MIDAS_TOUCHSTONE);
		mobCharm = registerItem(registry, new ItemMobCharm(), Names.Items.MOB_CHARM, false);
		mobCharmBelt = registerItem(registry, new ItemMobCharmBelt(), Names.Items.MOB_CHARM_BELT);
		phoenixDown = registerItem(registry, new ItemPhoenixDown(), Names.Items.PHOENIX_DOWN);
		pyromancerStaff = registerItem(registry, new ItemPyromancerStaff(), Names.Items.PYROMANCER_STAFF);
		rendingGale = registerItem(registry, new ItemRendingGale(), Names.Items.RENDING_GALE);
		rodOfLyssa = registerItem(registry, new ItemRodOfLyssa(), Names.Items.ROD_OF_LYSSA);
		salamanderEye = registerItem(registry, new ItemSalamanderEye(), Names.Items.SALAMANDER_EYE);
		serpentStaff = registerItem(registry, new ItemSerpentStaff(), Names.Items.SERPENT_STAFF);
		shearsOfWinter = registerItem(registry, new ItemShearsOfWinter(), Names.Items.SHEARS_OF_WINTER);
		sojournerStaff = registerItem(registry, new ItemSojournerStaff(), Names.Items.SOJOURNER_STAFF);
		twilightCloak = registerItem(registry, new ItemTwilightCloak(), Names.Items.TWILIGHT_CLOAK);
		voidTear = registerItem(registry, new ItemVoidTear(), Names.Items.VOID_TEAR);
		witchHat = registerItem(registry, new ItemWitchHat(), Names.Items.WITCH_HAT);
		witherlessRose = registerItem(registry, new ItemWitherlessRose(), Names.Items.WITHERLESS_ROSE);
		if(Settings.Disable.enableHandgun) {
      bullet = registerItem(registry, new ItemBullet(), Names.Items.BULLET);
      magazine = registerItem(registry, new ItemMagazine(), Names.Items.MAGAZINE);
      gunPart = registerItem(registry, new ItemGunPart(), Names.Items.GUN_PART);
      handgun = registerItem(registry, new ItemHandgun(), Names.Items.HANDGUN);
    }
    if(Settings.Disable.enablePotions) {
      attractionPotion = registerItem(registry, new ItemAttractionPotion(), Names.Items.ATTRACTION_POTION);
      fertilePotion = registerItem(registry, new ItemFertilePotion(), Names.Items.FERTILE_POTION);
      potionEssence = registerItem(registry, new ItemPotionEssence(), Names.Items.POTION_ESSENCE, false);
      potion = registerItem(registry, new ItemXRPotion(), Names.Items.POTION, false);
      tippedArrow = registerItem(registry, new ItemXRTippedArrow(), Names.Items.TIPPED_ARROW, false);
    
  		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.potion, new BehaviorDefaultProjectileDispense() {
  			@Override
  			ProjectileEntityFactory getProjectileEntityFactory() {
  				return (world, position, stack) -> new EntityThrownXRPotion(world, position.getX(), position.getY(), position.getZ(), stack);
  			}
  
  			@Override
  			boolean canShoot(ItemStack stack) {
  				return ModItems.potion.isSplash(stack) || ModItems.potion.isLingering(stack);
  			}
  		});

      BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.attractionPotion, new BehaviorDefaultProjectileDispense() {
        @Override
        ProjectileEntityFactory getProjectileEntityFactory() {
          return (world, position, stack) -> new EntityAttractionPotion(world, position.getX(), position.getY(), position.getZ());
        }
      });

      BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.fertilePotion, new BehaviorDefaultProjectileDispense() {
        @Override
        ProjectileEntityFactory getProjectileEntityFactory() {
          return (world, position, stack) -> new EntityFertilePotion(world, position.getX(), position.getY(), position.getZ());
        }
      });

      BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.tippedArrow, new BehaviorProjectileDispense() {
        @Nonnull
        @Override
        protected IProjectile getProjectileEntity(@Nonnull World world, @Nonnull IPosition position, @Nonnull ItemStack stack) {
          EntityXRTippedArrow entitytippedarrow = new EntityXRTippedArrow(world, position.getX(), position.getY(), position.getZ());
          entitytippedarrow.setPotionEffect(stack);
          entitytippedarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
          return entitytippedarrow;
        }
      });
    }
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.glowingWater, new BehaviorDefaultProjectileDispense() {
			@Override
			ProjectileEntityFactory getProjectileEntityFactory() {
				return (world, position, stack) -> new EntityGlowingWater(world, position.getX(), position.getY(), position.getZ());
			}
		});


		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.holyHandGrenade, new BehaviorDefaultProjectileDispense() {
			@Override
			ProjectileEntityFactory getProjectileEntityFactory() {
				return (world, position, stack) -> new EntityHolyHandGrenade(world, position.getX(), position.getY(), position.getZ(), stack.getDisplayName());
			}
		});

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.holyHandGrenade, new BehaviorDefaultProjectileDispense() {
			@Override
			ProjectileEntityFactory getProjectileEntityFactory() {
				return (world, position, stack) -> new EntityHolyHandGrenade(world, position.getX(), position.getY(), position.getZ(), stack.getDisplayName());
			}
		});
	}

	private static <T extends Item> T registerItem(IForgeRegistry<Item> registry, T item, String name) {
		return registerItem(registry, item, name, true);
	}

	private static <T extends Item> T registerItem(IForgeRegistry<Item> registry, T item, String name, boolean registerInJEI) {
		item.setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
		registry.register(item);

		if(registerInJEI)
			Reliquary.PROXY.registerJEI(item, name);

		return item;
	}

	private abstract static class BehaviorDefaultProjectileDispense implements IBehaviorDispenseItem {

		abstract ProjectileEntityFactory getProjectileEntityFactory();

		boolean canShoot(ItemStack stack) {
			return true;
		}

		@Nonnull
		@Override
		public ItemStack dispense(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
			if(!canShoot(stack)) {
				return new BehaviorDefaultDispenseItem().dispense(source, stack);
			}

			return (new BehaviorProjectileDispense() {
				@Nonnull
				@Override
				protected IProjectile getProjectileEntity(@Nonnull World world, @Nonnull IPosition position, @Nonnull ItemStack stack) {
					return getProjectileEntityFactory().createProjectileEntity(world, position, stack);
				}

				@Override
				protected float getProjectileInaccuracy() {
					return super.getProjectileInaccuracy() * 0.5F;
				}

				@Override
				protected float getProjectileVelocity() {
					return super.getProjectileVelocity() * 1.25F;
				}
			}).dispense(source, stack);
		}
	}

	private interface ProjectileEntityFactory {
		IProjectile createProjectileEntity(World world, IPosition position, ItemStack stack);
	}

}
