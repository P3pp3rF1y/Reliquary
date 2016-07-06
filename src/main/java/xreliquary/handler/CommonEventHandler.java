package xreliquary.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.*;
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
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import xreliquary.init.ModItems;
import xreliquary.init.ModPotions;
import xreliquary.init.XRRecipes;
import xreliquary.items.ItemToggleable;
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
	public void handleMercyCrossDamage(AttackEntityEvent event) {
		if(event.getEntityPlayer().worldObj.isRemote || !(event.getTarget() instanceof EntityLivingBase))
			return;

		if(event.getEntityPlayer().getHeldItemMainhand() == null || event.getEntityPlayer().getHeldItemMainhand().getItem() != ModItems.mercyCross)
			return;

		EntityLivingBase target = (EntityLivingBase) event.getTarget();

		ModItems.mercyCross.updateAttackDamageModifier(target, event.getEntityPlayer());
	}

	private boolean isUndead(EntityLivingBase e) {
		return e.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
	}

	@SubscribeEvent
	public void blameDrullkus(PlayerEvent.PlayerLoggedInEvent event) {
		// Thanks for the Witch's Hat texture! Also, blame Drullkus for making me add this. :P
		if(event.player.getGameProfile().getName().equals("Drullkus")) {
			if(!event.player.getEntityData().hasKey("gift")) {
				if(event.player.inventory.addItemStackToInventory(new ItemStack(ModItems.witchHat))) {
					event.player.getEntityData().setBoolean("gift", true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityTargetedEvent(LivingSetAttackTargetEvent event) {
		doTwilightCloakCheck(event);
		//doPacifiedDebuffCheck(event);
		doHeartZhuCheckOnSetTarget(event);
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		doTwilightCloakCheck(event);
		doHeartZhuCheckOnUpdate(event);
	}

	private void doHeartZhuCheckOnUpdate(LivingEvent event) {
		if(!(event.getEntity() instanceof EntityLiving))
			return;
		EntityLiving entity = (EntityLiving) event.getEntity();

		if (entity.getAttackTarget() == null || !(entity.getAttackTarget() instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity.getAttackTarget();
		boolean resetTarget = false;

		if (entity instanceof EntityGhast) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.GHAST_META), false);
		} else if (entity instanceof EntityMagmaCube) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.MAGMA_CUBE_META), false);
		} else if (entity instanceof EntitySlime) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.SLIME_META), false);
		}

		if (resetTarget) {
			entity.setAttackTarget(null);
			entity.setRevengeTarget(null);
		}

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

	public void doHeartZhuCheckOnSetTarget(LivingSetAttackTargetEvent event) {
		if(event.getTarget() == null)
			return;
		if(!(event.getTarget() instanceof EntityPlayer))
			return;
		if(!(event.getEntity() instanceof EntityLiving))
			return;

		EntityPlayer player = (EntityPlayer) event.getTarget();
		boolean resetTarget = false;
		EntityLiving entity = (EntityLiving) event.getEntity();

		if (entity instanceof EntityPigZombie) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.ZOMBIE_PIGMAN_META), false);
		} else if (entity instanceof EntityZombie) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.ZOMBIE_META), false);
		} else if (entity instanceof EntitySkeleton) {
			if (((EntitySkeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
				resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.WITHER_SKELETON_META), false);
			} else {
				resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.SKELETON_META), false);
			}
		} else if (entity instanceof EntityCreeper) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.CREEPER_META), false);
		} else if (entity instanceof EntityWitch) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.WITCH_META), false);
		} else if (entity instanceof EntityCaveSpider) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.CAVE_SPIDER_META), false);
		} else if (entity instanceof EntitySpider){
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.SPIDER_META), false);
		} else if (entity instanceof EntityEnderman) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.ENDERMAN_META), false);
		} else if (entity instanceof EntityBlaze) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.BLAZE_META), false);
		} else if (entity instanceof EntityGhast) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.GHAST_META), false);
		} else if (entity instanceof EntityMagmaCube) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.MAGMA_CUBE_META), false);
		} else if (entity instanceof EntitySlime) {
			resetTarget = playerHasItem(player, XRRecipes.nianZhu(Reference.NIAN_ZHU.SLIME_META), false);
		}

		if (resetTarget) {
			entity.setAttackTarget(null);
			entity.setRevengeTarget(null);
		}
	}

	public void doTwilightCloakCheck(LivingEvent event) {
		if(event.getEntity() instanceof EntityLiving) {
			EntityLiving entityLiving = ((EntityLiving) event.getEntity());
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
			if(event.getEntity() instanceof EntityLiving) {
				((EntityLiving) event.getEntity()).setAttackTarget(null);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void beforePlayerHurt(LivingAttackEvent event) {
		Entity entity = event.getEntity();
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
		if(!(event.getSource() == DamageSource.inFire) && !(event.getSource() == DamageSource.onFire))
			return;
		if(player.getFoodStats().getFoodLevel() <= 0)
			return;

		// trades all fire damage for exhaustion (which causes the hunger bar to
		// be depleted).
		player.addExhaustion(event.getAmount() * ((float) Settings.InfernalClaws.hungerCostPercent / 100F));
		event.setCanceled(true);
	}

	public void handleInfernalChaliceCheck(EntityPlayer player, LivingAttackEvent event) {
		if(!playerHasItem(player, ModItems.infernalChalice, false))
			return;
		//TODO: figure out if there's some way to know that the fire was caused by lava, otherwise this is the only way to prevent damage from lava - reason being that most of the damage is from fire caused by lava
		if(event.getSource() != DamageSource.lava && event.getSource() != DamageSource.onFire && event.getSource() != DamageSource.inFire)
			return;
		if(player.getFoodStats().getFoodLevel() <= 0)
			return;
		if(event.getSource() == DamageSource.lava || event.getSource() == DamageSource.onFire || event.getSource() == DamageSource.inFire) {
			player.addExhaustion(event.getAmount() * ((float) Settings.InfernalChalice.hungerCostPercent / 100F));
		}

		event.setCanceled(true);
	}

	public void handleAngelheartVialCheck(EntityPlayer player, LivingAttackEvent event) {
		// I'm rounding because I'm not 100% on whether the health value being a
		// fraction matters for determining death
		// Rounding would be worst case. I'm doing an early abort to keep my
		// indentation shallow.
		if(player.getHealth() > Math.round(event.getAmount()))
			return;
		if(!playerHasItem(player, ModItems.angelheartVial, false))
			return;

		decreaseItemByOne(player, ModItems.angelheartVial);

		// player should see a vial "shatter" effect and hear the glass break to
		// let them know they lost a vial.
		spawnAngelheartVialParticles(player);

		// play some glass breaking effects at the player location
		player.worldObj.playSound(null, player.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F);

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
			player.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, var8, var10, var12, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D, new int[] {
					Item.getIdFromItem(Items.POTIONITEM)});
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
				Particle var31 = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), var8 + var25 * 0.1D, var10 + 0.3D, var12 + var29 * 0.1D, var25, var27, var29);
				if(var31 != null) {
					float var32 = 0.75F + var7.nextFloat() * 0.25F;
					var31.setRBGColorF(red * var32, green * var32, blue * var32);
					var31.multiplyVelocity((float) var39);
				}
			}
		}

		player.worldObj.playSound(null, player.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F);

	}

	public void removeNegativeStatusEffects(EntityPlayer player) {
		player.removePotionEffect(MobEffects.WITHER);
		player.removePotionEffect(MobEffects.HUNGER);
		player.removePotionEffect(MobEffects.POISON);
		player.removePotionEffect(MobEffects.NAUSEA);
		player.removePotionEffect(MobEffects.MINING_FATIGUE);
		player.removePotionEffect(MobEffects.SLOWNESS);
		player.removePotionEffect(MobEffects.BLINDNESS);
		player.removePotionEffect(MobEffects.WEAKNESS);
	}

	public void handlePhoenixDownCheck(EntityPlayer player, LivingAttackEvent event) {
		if(!playerHasItem(player, ModItems.phoenixDown, false))
			return;
		if(player.getHealth() > Math.round(event.getAmount())) {
			if(!(event.getSource() == DamageSource.fall))
				return;
			if(player.getFoodStats().getFoodLevel() <= 0)
				return;

			float hungerDamage = event.getAmount() * ((float) Settings.PhoenixDown.hungerCostPercent / 100F);
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
			if(event.getSource() == DamageSource.lava && Settings.PhoenixDown.giveTemporaryFireResistanceIfFireDamageKilledYou)
				player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 200, 0));
			if(event.getSource() == DamageSource.drown && Settings.PhoenixDown.giveTemporaryWaterBreathingIfDrowningKilledYou) {
				player.setAir(10);
				player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 200, 0));
			}

			// give the player temporary resistance to other damages.
			if(Settings.PhoenixDown.giveTemporaryDamageResistance)
				player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 1));

			// give the player temporary regeneration.
			if(Settings.PhoenixDown.giveTemporaryRegeneration)
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 1));

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
		if(!(event.getSource() == DamageSource.fall))
			return;
		if(player.getFoodStats().getFoodLevel() <= 0)
			return;

		if(player.fallDistance > 0.0F) {
			float hungerDamage = event.getAmount() * ((float) Settings.AngelicFeather.hungerCostPercent / 100F);
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
		if(event.getSource() == DamageSource.drown) {
			float hungerDamage = event.getAmount() * ((float) Settings.KrakenShell.hungerCostPercent / 100F);
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
		if(event.getWorld() instanceof WorldServer)
			XRFakePlayerFactory.unloadWorld((WorldServer) event.getWorld());
	}

	@SubscribeEvent
	public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
		if(event.getEntityLiving().worldObj.isRemote)
			return;

		if(event.getEntityLiving().isPotionActive(ModPotions.potionFlight)) {
			if(event.getEntityLiving() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) event.getEntityLiving();
				playersFlightStatus.put(player.getGameProfile().getId(), true);
				player.capabilities.allowFlying = true;
				player.fallDistance = 0;
				((EntityPlayerMP) player).connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
			}
		} else {
			if(event.getEntityLiving() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) event.getEntityLiving();

				if(!playersFlightStatus.containsKey(player.getGameProfile().getId())) {
					playersFlightStatus.put(player.getGameProfile().getId(), false);
				}

				if(playersFlightStatus.get(player.getGameProfile().getId())) {

					playersFlightStatus.put(player.getGameProfile().getId(), false);

					if(!player.capabilities.isCreativeMode) {
						player.capabilities.allowFlying = false;
						player.capabilities.isFlying = false;
						((EntityPlayerMP) player).connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
					}
				}
			}
		}
	}
}
