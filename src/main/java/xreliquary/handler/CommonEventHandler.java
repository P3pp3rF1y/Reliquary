package xreliquary.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import xreliquary.init.ModItems;
import xreliquary.init.ModPotions;
import xreliquary.init.XRRecipes;
import xreliquary.items.ItemToggleable;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.XRFakePlayerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CommonEventHandler {

	private Map<UUID, Boolean> playersFlightStatus = new HashMap<>();

	@SubscribeEvent
	public void blameDrullkus(PlayerEvent.PlayerLoggedInEvent event) {
		// Thanks for the Witch's Hat texture! Also, blame Drullkus for making me add this. :P
		if(event.player.getGameProfile().getName() == "Drullkus") {
			if(!event.player.getEntityData().hasKey("gift")) {
				if(event.player.inventory.addItemStackToInventory(new ItemStack(ModItems.witchHat))) {
					event.player.getEntityData().setBoolean("gift", true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityLiving(LivingEvent event) {
		doTwilightCloakCheck(event);
		//doPacifiedDebuffCheck(event);
		doHeartZhuCheck(event);
	}

	@SubscribeEvent
	public void onEntityTargetedEvent(LivingSetAttackTargetEvent event) {
		doTwilightCloakCheck(event);
		//doPacifiedDebuffCheck(event);
		doHeartZhuCheck(event);
	}

	//TODO figure out if this needs to be added for serpent staff
	//
	//    @SubscribeEvent
	//    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
	//        if (!event.entityLiving.isPotionActive(PotionSerpentStaff.mobPacificationDebuff))
	//            return;
	//        if (event.entityLiving.getActivePotionEffect(PotionSerpentStaff.mobPacificationDebuff).getDuration()==0) {
	//            event.entityLiving.removePotionEffect(PotionSerpentStaff.mobPacificationDebuff.id);
	//            return;
	//        }
	//    }

	public void doHeartZhuCheck(LivingSetAttackTargetEvent event) {
		if(event.target == null)
			return;
		if(!(event.target instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) event.target;
		doZombieZhuCheck(event.entity, player);
		doSkeletonZhuCheck(event.entity, player);
		doWitherSkeletonZhuCheck(event.entity, player);
		doCreeperZhuCheck(event.entity, player);
	}

	public void doHeartZhuCheck(LivingEvent event) {
		if(event.entity instanceof EntityLiving) {
			EntityLiving entityLiving = ((EntityLiving) event.entity);
			if(entityLiving.getAttackTarget() == null)
				return;
			if(!(entityLiving.getAttackTarget() instanceof EntityPlayer))
				return;
			EntityPlayer player = (EntityPlayer) entityLiving.getAttackTarget();
			doZombieZhuCheck(event.entity, player);
			doSkeletonZhuCheck(event.entity, player);
			doWitherSkeletonZhuCheck(event.entity, player);
			doCreeperZhuCheck(event.entity, player);
		}
	}

	private ItemStack heartZhu(int meta) {
		return XRRecipes.nianZhu(meta);
	}

	public void doZombieZhuCheck(Entity e, EntityPlayer p) {
		if(e instanceof EntityZombie && !(e instanceof EntityPigZombie)) {
			if(playerHasItem(p, heartZhu(Reference.ZOMBIE_ZHU_META), false)) {
				((EntityZombie) e).setAttackTarget(null);
				((EntityZombie) e).setRevengeTarget(null);
			}
		}
	}

	public void doSkeletonZhuCheck(Entity e, EntityPlayer p) {
		if(e instanceof EntitySkeleton && ((EntitySkeleton) e).getSkeletonType() != 1) {
			if(playerHasItem(p, heartZhu(Reference.SKELETON_ZHU_META), false)) {
				((EntitySkeleton) e).setAttackTarget(null);
				((EntitySkeleton) e).setRevengeTarget(null);
			}
		}
	}

	public void doWitherSkeletonZhuCheck(Entity e, EntityPlayer p) {
		if(e instanceof EntitySkeleton && ((EntitySkeleton) e).getSkeletonType() == 1) {
			if(playerHasItem(p, heartZhu(Reference.WITHER_SKELETON_ZHU_META), false)) {
				((EntitySkeleton) e).setAttackTarget(null);
				((EntitySkeleton) e).setRevengeTarget(null);
			}
		}
	}

	public void doCreeperZhuCheck(Entity e, EntityPlayer p) {
		if(e instanceof EntityCreeper) {
			if(playerHasItem(p, heartZhu(Reference.CREEPER_ZHU_META), false)) {
				((EntityCreeper) e).setAttackTarget(null);
				((EntityCreeper) e).setRevengeTarget(null);
			}
		}
	}

	public void doTwilightCloakCheck(LivingEvent event) {
		if(event.entity instanceof EntityLiving) {
			EntityLiving entityLiving = ((EntityLiving) event.entity);
			if(entityLiving.getAttackTarget() == null)
				return;
			if(!(entityLiving.getAttackTarget() instanceof EntityPlayer))
				return;
			EntityPlayer player = (EntityPlayer) entityLiving.getAttackTarget();
			if(!playerHasItem(player, ModItems.twilightCloak, true))
				return;

			//toggled effect, makes player invisible based on light level (configurable)
			if(player.worldObj.getLightFromNeighbors(player.getPosition()) > Settings.TwilightCloak.maxLightLevel)
				return;
			if(event.entity instanceof EntityLiving) {
				((EntityLiving) event.entity).setAttackTarget(null);
			}
		}
	}

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event) {
		if(!Settings.mobDropsEnabled)
			return;

		Entity e = event.entity;
		handleSquidDropsCheck(e, event);
		handleWitchDropsCheck(e, event);
		handleSpiderOrCaveSpiderDropsCheck(e, event);
		handleSkeletonDropsCheck(e, event);
		handleWitherDropsCheck(e, event);
		handleZombieOrZombiePigmanDropsCheck(e, event);
		handleSlimeDropsCheck(e, event);
		handleBlazeOrMagmaCubeDropsCheck(e, event);
		handleGhastOrCreeperDropsCheck(e, event);
		handleEndermanDropsCheck(e, event);
		handleBatsDropsCheck(e, event);
		handleSnowGolemDropsCheck(e, event);
	}

	public void handleEventDropListAddition(Entity e, LivingDropsEvent event, float probabilityBase, float lootingProbabilityIncrement, ItemStack ist) {
		float dropProbability = probabilityBase + (lootingProbabilityIncrement * (float) event.lootingLevel);
		if(e.worldObj.rand.nextFloat() <= dropProbability) {
			if(event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayer && event.source.damageType.equals("player")) {
				EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, ist);
				entityitem.setPickupDelay(10);
				event.drops.add(entityitem);
			}
		}
	}

	private ItemStack ingredient(int meta) {
		return XRRecipes.ingredient(1, meta);
	}

	public void handleSquidDropsCheck(Entity e, LivingDropsEvent event) {
		if(e instanceof EntitySquid)
			handleEventDropListAddition(e, event, Settings.MobDrops.getBaseDrop(Names.squid_beak), Settings.MobDrops.getLootingDrop(Names.squid_beak), ingredient(Reference.SQUID_INGREDIENT_META));
	}

	public void handleWitchDropsCheck(Entity e, LivingDropsEvent event) {
		if(e instanceof EntityWitch)
			handleEventDropListAddition(e, event, Settings.MobDrops.getBaseDrop(Names.witch_hat), Settings.MobDrops.getLootingDrop(Names.witch_hat), new ItemStack(ModItems.witchHat, 1, 0));
	}

	public void handleSpiderOrCaveSpiderDropsCheck(Entity e, LivingDropsEvent event) {
		if(e instanceof EntitySpider) {
			float base = Settings.MobDrops.getBaseDrop((e instanceof EntityCaveSpider) ? Names.cave_spider_fangs : Names.spider_fangs);
			float looting = Settings.MobDrops.getLootingDrop((e instanceof EntityCaveSpider) ? Names.cave_spider_fangs : Names.spider_fangs);
			handleEventDropListAddition(e, event, base, looting, ingredient(Reference.SPIDER_INGREDIENT_META));
		}
	}

	public void handleSkeletonDropsCheck(Entity e, LivingDropsEvent event) {
		if(e instanceof EntitySkeleton && ((EntitySkeleton) e).getSkeletonType() != 1)
			handleEventDropListAddition(e, event, Settings.MobDrops.getBaseDrop(Names.rib_bone), Settings.MobDrops.getLootingDrop(Names.rib_bone), ingredient(Reference.SKELETON_INGREDIENT_META));
	}

	public void handleWitherDropsCheck(Entity e, LivingDropsEvent event) {
		if(e instanceof EntitySkeleton && ((EntitySkeleton) e).getSkeletonType() == 1)
			handleEventDropListAddition(e, event, Settings.MobDrops.getBaseDrop(Names.withered_rib), Settings.MobDrops.getLootingDrop(Names.withered_rib), ingredient(Reference.WITHER_INGREDIENT_META));
	}

	public void handleZombieOrZombiePigmanDropsCheck(Entity e, LivingDropsEvent event) {
		if(e instanceof EntityZombie) {
			float base = Settings.MobDrops.getBaseDrop((e instanceof EntityPigZombie) ? Names.pigman_heart : Names.zombie_heart);
			float looting = Settings.MobDrops.getLootingDrop((e instanceof EntityPigZombie) ? Names.pigman_heart : Names.zombie_heart);
			handleEventDropListAddition(e, event, base, looting, ingredient(Reference.ZOMBIE_INGREDIENT_META));
		}
	}

	public void handleSlimeDropsCheck(Entity e, LivingDropsEvent event) {
		if(e instanceof EntitySlime)
			handleEventDropListAddition(e, event, Settings.MobDrops.getBaseDrop(Names.slime_pearl), Settings.MobDrops.getLootingDrop(Names.slime_pearl), ingredient(Reference.SLIME_INGREDIENT_META));

	}

	public void handleBlazeOrMagmaCubeDropsCheck(Entity e, LivingDropsEvent event) {
		if(e instanceof EntityBlaze || e instanceof EntityMagmaCube) {
			float base = Settings.MobDrops.getBaseDrop((e instanceof EntityMagmaCube) ? Names.magma_cube_molten_core : Names.blaze_molten_core);
			float looting = Settings.MobDrops.getLootingDrop((e instanceof EntityMagmaCube) ? Names.magma_cube_molten_core : Names.blaze_molten_core);
			handleEventDropListAddition(e, event, base, looting, ingredient(Reference.MOLTEN_INGREDIENT_META));
		}
	}

	public void handleGhastOrCreeperDropsCheck(Entity e, LivingDropsEvent event) {
		if(e instanceof EntityCreeper || e instanceof EntityGhast) {
			float base = Settings.MobDrops.getBaseDrop((e instanceof EntityGhast) ? Names.ghast_gland : Names.creeper_gland);
			float looting = Settings.MobDrops.getLootingDrop((e instanceof EntityGhast) ? Names.ghast_gland : Names.creeper_gland);
			handleEventDropListAddition(e, event, base, looting, ingredient(Reference.CREEPER_INGREDIENT_META));
			if(e instanceof EntityCreeper && ((EntityCreeper) e).getPowered()) {
				handleEventDropListAddition(e, event, Settings.MobDrops.getBaseDrop(Names.eye_of_the_storm), Settings.MobDrops.getLootingDrop(Names.eye_of_the_storm), ingredient(Reference.STORM_INGREDIENT_META));
			}
		}
	}

	public void handleEndermanDropsCheck(Entity e, LivingDropsEvent event) {
		if(e instanceof EntityEnderman)
			handleEventDropListAddition(e, event, Settings.MobDrops.getBaseDrop(Names.ender_heart), Settings.MobDrops.getLootingDrop(Names.ender_heart), ingredient(Reference.ENDER_INGREDIENT_META));
	}

	public void handleBatsDropsCheck(Entity e, LivingDropsEvent event) {
		if(e instanceof EntityBat)
			handleEventDropListAddition(e, event, Settings.MobDrops.getBaseDrop(Names.bat_wing), Settings.MobDrops.getLootingDrop(Names.bat_wing), ingredient(Reference.BAT_INGREDIENT_META));
	}

	public void handleSnowGolemDropsCheck(Entity e, LivingDropsEvent event) {
		if(e instanceof EntitySnowman)
			handleEventDropListAddition(e, event, Settings.MobDrops.getBaseDrop(Names.frozen_core), Settings.MobDrops.getLootingDrop(Names.frozen_core), ingredient(Reference.FROZEN_INGREDIENT_META));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void beforePlayerHurt(LivingAttackEvent event) {
		Entity entity = event.entity;
		if(entity == null || !(entity instanceof EntityPlayer))
			return;
		EntityPlayer player = (EntityPlayer) entity;
		handleInfernalClawsCheck(player, event);
		handleInfernalChaliceCheck(player, event);
		handleAngelicFeatherCheck(player, event);
		handleKrakenEyeCheck(player, event);
		// check
		handlePhoenixDownCheck(player, event);
		handleAngelheartVialCheck(player, event);
		if(event.isCanceled())
			event.setResult(null);
	}

	public void handleInfernalClawsCheck(EntityPlayer player, LivingAttackEvent event) {
		if(!playerHasItem(player, ModItems.infernalClaws, false))
			return;
		if(!(event.source == DamageSource.inFire) && !(event.source == DamageSource.onFire))
			return;
		if(player.getFoodStats().getFoodLevel() <= 0)
			return;

		// trades all fire damage for exhaustion (which causes the hunger bar to
		// be depleted).
		player.addExhaustion(event.ammount * ((float) Settings.InfernalClaws.hungerCostPercent / 100F));
		event.setCanceled(true);
	}

	public void handleInfernalChaliceCheck(EntityPlayer player, LivingAttackEvent event) {
		if(!playerHasItem(player, ModItems.infernalChalice, false))
			return;
		//TODO: figure out if there's some way to know that the fire was caused by lava, otherwise this is the only way to prevent damage from lava - reason being that most of the damage is from fire caused by lava
		if(event.source != DamageSource.lava && event.source != DamageSource.onFire && event.source != DamageSource.inFire)
			return;
		if(player.getFoodStats().getFoodLevel() <= 0)
			return;
		if(event.source == DamageSource.lava || event.source == DamageSource.onFire || event.source == DamageSource.inFire) {
			player.addExhaustion(event.ammount * ((float) Settings.InfernalChalice.hungerCostPercent / 100F));
		}

		event.setCanceled(true);
	}

	public void handleAngelheartVialCheck(EntityPlayer player, LivingAttackEvent event) {
		// I'm rounding because I'm not 100% on whether the health value being a
		// fraction matters for determining death
		// Rounding would be worst case. I'm doing an early abort to keep my
		// indentation shallow.
		if(player.getHealth() > Math.round(event.ammount))
			return;
		if(!playerHasItem(player, ModItems.angelheartVial, false))
			return;

		decreaseItemByOne(player, ModItems.angelheartVial);

		// player should see a vial "shatter" effect and hear the glass break to
		// let them know they lost a vial.
		spawnAngelheartVialParticles(player);

		// play some glass breaking effects at the player location
		player.worldObj.playSound(null, player.getPosition(), SoundEvents.block_glass_break, SoundCategory.NEUTRAL, 1.0F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F);

		// gives the player a few hearts, sparing them from death.
		float amountHealed = player.getMaxHealth() * (float) Settings.AngelHeartVial.healPercentageOfMaxLife / 100F;
		player.setHealth(amountHealed);

		// if the player had any negative status effects [vanilla only for now], remove them:
		if(Settings.AngelHeartVial.removeNegativeStatus)
			removeNegativeStatusEffects(player);

		event.setCanceled(true);
	}

	public void spawnAngelheartVialParticles(EntityPlayer player) {
		double var8 = player.posX;
		double var10 = player.posY;
		double var12 = player.posZ;
		Random var7 = player.worldObj.rand;
		for(int var15 = 0; var15 < 8; ++var15) {
			player.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, var8, var10, var12, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D, new int[] {Item.getIdFromItem(Items.potionitem)});
		}

		// purple, for reals.
		float red = 1.0F;
		float green = 0.0F;
		float blue = 1.0F;
		String var19 = "spell";

		for(int var20 = 0; var20 < 100; ++var20) {
			double var39 = var7.nextDouble() * 4.0D;
			double var23 = var7.nextDouble() * Math.PI * 2.0D;
			double var25 = Math.cos(var23) * var39;
			double var27 = 0.01D + var7.nextDouble() * 0.5D;
			double var29 = Math.sin(var23) * var39;
			if(player.worldObj.isRemote) {
				EntityFX var31 = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), var8 + var25 * 0.1D, var10 + 0.3D, var12 + var29 * 0.1D, var25, var27, var29);
				if(var31 != null) {
					float var32 = 0.75F + var7.nextFloat() * 0.25F;
					var31.setRBGColorF(red * var32, green * var32, blue * var32);
					var31.multiplyVelocity((float) var39);
				}
			}
		}

		player.worldObj.playSound(null, player.getPosition(), SoundEvents.block_glass_break, SoundCategory.NEUTRAL, 1.0F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F);

	}

	public void removeNegativeStatusEffects(EntityPlayer player) {
		player.removePotionEffect(MobEffects.wither);
		player.removePotionEffect(MobEffects.hunger);
		player.removePotionEffect(MobEffects.poison);
		player.removePotionEffect(MobEffects.confusion);
		player.removePotionEffect(MobEffects.digSlowdown);
		player.removePotionEffect(MobEffects.moveSlowdown);
		player.removePotionEffect(MobEffects.blindness);
		player.removePotionEffect(MobEffects.weakness);
	}

	public void handlePhoenixDownCheck(EntityPlayer player, LivingAttackEvent event) {
		if(!playerHasItem(player, ModItems.phoenixDown, false))
			return;
		if(player.getHealth() > Math.round(event.ammount)) {
			if(!(event.source == DamageSource.fall))
				return;
			if(player.getFoodStats().getFoodLevel() <= 0)
				return;

			float hungerDamage = event.ammount * ((float) Settings.PhoenixDown.hungerCostPercent / 100F);
			player.addExhaustion(hungerDamage);
			player.getFoodStats().onUpdate(player);

			event.setCanceled(true);

			return;
		} else {

			// item reverts to a normal feather.
			revertPhoenixDownToAngelicFeather(player);

			// gives the player a few hearts, sparing them from death.
			float amountHealed = player.getMaxHealth() * (float) Settings.PhoenixDown.healPercentageOfMaxLife / 100F;
			player.setHealth(amountHealed);

			// if the player had any negative status effects [vanilla only for now], remove them:
			if(Settings.PhoenixDown.removeNegativeStatus)
				removeNegativeStatusEffects(player);

			// added bonus, has some extra effects when drowning or dying to lava
			if(event.source == DamageSource.lava && Settings.PhoenixDown.giveTemporaryFireResistanceIfFireDamageKilledYou)
				player.addPotionEffect(new PotionEffect(MobEffects.fireResistance, 200, 0));
			if(event.source == DamageSource.drown && Settings.PhoenixDown.giveTemporaryWaterBreathingIfDrowningKilledYou) {
				player.setAir(10);
				player.addPotionEffect(new PotionEffect(MobEffects.waterBreathing, 200, 0));
			}

			// give the player temporary resistance to other damages.
			if(Settings.PhoenixDown.giveTemporaryDamageResistance)
				player.addPotionEffect(new PotionEffect(MobEffects.resistance, 200, 1));

			// give the player temporary regeneration.
			if(Settings.PhoenixDown.giveTemporaryRegeneration)
				player.addPotionEffect(new PotionEffect(MobEffects.regeneration, 200, 1));

			// particles, lots of them
			spawnPhoenixResurrectionParticles(player);

			event.setCanceled(true);
		}
	}

	public void spawnPhoenixResurrectionParticles(EntityPlayer player) {
		for(int particles = 0; particles <= 400; particles++) {
			player.worldObj.spawnParticle(EnumParticleTypes.FLAME, player.posX, player.posY, player.posZ, player.worldObj.rand.nextGaussian() * 8, player.worldObj.rand.nextGaussian() * 8, player.worldObj.rand.nextGaussian() * 8);
		}
	}

	public void handleAngelicFeatherCheck(EntityPlayer player, LivingAttackEvent event) {
		if(!playerHasItem(player, ModItems.angelicFeather, false))
			return;
		if(!(event.source == DamageSource.fall))
			return;
		if(player.getFoodStats().getFoodLevel() <= 0)
			return;

		if(player.fallDistance > 0.0F) {
			float hungerDamage = event.ammount * ((float) Settings.AngelicFeather.hungerCostPercent / 100F);
			player.addExhaustion(hungerDamage);
			player.getFoodStats().onUpdate(player);
		}
		event.setCanceled(true);
	}

	public void handleKrakenEyeCheck(EntityPlayer player, LivingAttackEvent event) {
		if(!playerHasItem(player, ModItems.krakenShell, false))
			return;
		if(player.getFoodStats().getFoodLevel() <= 0)
			return;

		// player absorbs drowning damage in exchange for hunger, at a relatively low rate.
		if(event.source == DamageSource.drown) {
			float hungerDamage = event.ammount * ((float) Settings.KrakenShell.hungerCostPercent / 100F);
			player.addExhaustion(hungerDamage);
			event.setCanceled(true);
		}
	}

	private void revertPhoenixDownToAngelicFeather(EntityPlayer player) {
		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if(player.inventory.mainInventory[slot] == null)
				continue;
			if(player.inventory.mainInventory[slot].getItem() == ModItems.phoenixDown) {
				player.inventory.mainInventory[slot] = new ItemStack(ModItems.angelicFeather);
				return;
			}
		}
	}

	private boolean playerHasItem(EntityPlayer player, Item item, boolean checkEnabled) {
		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if(player.inventory.mainInventory[slot] == null)
				continue;
			if(player.inventory.mainInventory[slot].getItem() == item) {
				if(checkEnabled) {
					if(player.inventory.mainInventory[slot].getItem() instanceof ItemToggleable) {
						if(((ItemToggleable) player.inventory.mainInventory[slot].getItem()).isEnabled(player.inventory.mainInventory[slot]))
							return true;
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	private boolean playerHasItem(EntityPlayer player, ItemStack ist, boolean checkEnabled) {
		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if(player.inventory.mainInventory[slot] == null)
				continue;
			if(player.inventory.mainInventory[slot].isItemEqual(ist)) {
				if(checkEnabled) {
					if(player.inventory.mainInventory[slot].getItem() instanceof ItemToggleable) {
						if(((ItemToggleable) player.inventory.mainInventory[slot].getItem()).isEnabled(player.inventory.mainInventory[slot]))
							return true;
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	// pretty much the same as above, specific to angelheart vial. finds it and breaks one.
	private void decreaseItemByOne(EntityPlayer player, Item item) {
		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if(player.inventory.mainInventory[slot] == null)
				continue;
			if(player.inventory.mainInventory[slot].getItem() == item) {
				player.inventory.decrStackSize(slot, 1);
				return;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onDimensionUnload(WorldEvent.Unload event) {
		if(event.world instanceof WorldServer)
			XRFakePlayerFactory.unloadWorld((WorldServer) event.world);
	}

	@SubscribeEvent
	public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
		if(event.entityLiving.worldObj.isRemote)
			return;

		if(event.entityLiving.isPotionActive(ModPotions.potionFlight)) {
			if(event.entityLiving instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) event.entityLiving;
				playersFlightStatus.put(player.getGameProfile().getId(), true);
				player.capabilities.allowFlying = true;
				player.fallDistance = 0;
				((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new SPacketPlayerAbilities(player.capabilities));
			}
		} else {
			if(event.entityLiving instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) event.entityLiving;

				if(!playersFlightStatus.containsKey(player.getGameProfile().getId())) {
					playersFlightStatus.put(player.getGameProfile().getId(), false);
				}

				if(playersFlightStatus.get(player.getGameProfile().getId())) {

					playersFlightStatus.put(player.getGameProfile().getId(), false);

					if(!player.capabilities.isCreativeMode) {
						player.capabilities.allowFlying = false;
						player.capabilities.isFlying = false;
						((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new SPacketPlayerAbilities(player.capabilities));
					}
				}
			}
		}
	}
}
