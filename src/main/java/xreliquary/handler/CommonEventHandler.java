package xreliquary.handler;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
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
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import xreliquary.init.ModItems;
import xreliquary.items.ItemToggleable;
import xreliquary.network.PacketHandler;
import xreliquary.network.PacketMobCharmDamage;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.XRFakePlayerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CommonEventHandler {

	private Map<UUID, Boolean> playersFlightStatus = new HashMap<>();

	@SubscribeEvent
	public void handleMercyCrossDamage(AttackEntityEvent event) {
		if(event.getEntityPlayer().world.isRemote || !(event.getTarget() instanceof EntityLivingBase))
			return;

		if(event.getEntityPlayer().getHeldItemMainhand().getItem() != ModItems.mercyCross)
			return;

		EntityLivingBase target = (EntityLivingBase) event.getTarget();

		ModItems.mercyCross.updateAttackDamageModifier(target, event.getEntityPlayer());
	}

	@SubscribeEvent
	public void preventMendingAndUnbreaking(AnvilUpdateEvent event) {
		if(event.getLeft().isEmpty() || event.getRight().isEmpty())
			return;

		if (event.getLeft().getItem() == ModItems.mobCharm)
			event.setCanceled(true);
		
		if (event.getLeft().getItem() == ModItems.alkahestryTome) {
			ItemStack mendingBook = Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(Enchantments.MENDING, Enchantments.MENDING.getMaxLevel()));
			if(ItemStack.areItemStacksEqual(event.getRight(), mendingBook)) {
				event.setCanceled(true);
			}
		}
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
		doMobCharmCheckOnSetTarget(event);
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		doTwilightCloakCheck(event);
		doMobCharmCheckOnUpdate(event);
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		if(event.getSource() == null || event.getSource().getEntity() == null || !(event.getSource().getEntity() instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) event.getSource().getEntity();

		damagePlayersMobCharm(player, event.getEntity());
	}

	private void damagePlayersMobCharm(EntityPlayer player, Entity entity) {
		if(player.capabilities.isCreativeMode)
			return;

		byte mobCharmType = ModItems.mobCharm.getMobCharmTypeForEntity(entity);

		for(int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			ItemStack stack = player.inventory.mainInventory.get(slot);

			if(stack.isEmpty())
				continue;
			if(stack.getItem() == ModItems.mobCharm && ModItems.mobCharm.getType(stack) == mobCharmType) {
				if(stack.getItemDamage() + Settings.MobCharm.damagePerKill > stack.getMaxDamage()) {
					player.inventory.mainInventory.set(slot, ItemStack.EMPTY);
					PacketHandler.networkWrapper.sendTo(new PacketMobCharmDamage(mobCharmType, stack.getMaxDamage() + 1, slot), (EntityPlayerMP) player);
				} else {
					stack.setItemDamage(stack.getItemDamage() + Settings.MobCharm.damagePerKill);
					PacketHandler.networkWrapper.sendTo(new PacketMobCharmDamage(mobCharmType, stack.getItemDamage(), slot), (EntityPlayerMP) player);
				}
				return;
			}
			if(damageMobCharmInBelt((EntityPlayerMP) player, mobCharmType, stack))
				return;
		}

		if(Loader.isModLoaded(Compatibility.MOD_ID.BAUBLES)) {
			IBaublesItemHandler inventoryBaubles = BaublesApi.getBaublesHandler(player);

			for(int i = 0; i < inventoryBaubles.getSlots(); i++) {
				ItemStack baubleStack = inventoryBaubles.getStackInSlot(i);

				if(baubleStack.isEmpty())
					continue;

				if(damageMobCharmInBelt((EntityPlayerMP) player, mobCharmType, baubleStack))
					return;
			}
		}
	}

	private boolean damageMobCharmInBelt(EntityPlayerMP player, byte mobCharmType, ItemStack slotStack) {
		if(slotStack.getItem() == ModItems.mobCharmBelt) {
			int damage = ModItems.mobCharmBelt.damageCharmType(slotStack, mobCharmType);

			if(damage > -1) {
				PacketHandler.networkWrapper.sendTo(new PacketMobCharmDamage(mobCharmType, damage, -mobCharmType), player);
				return true;
			}
		}
		return false;
	}

	private void doMobCharmCheckOnUpdate(LivingEvent event) {
		if(!(event.getEntity() instanceof EntityLiving))
			return;
		EntityLiving entity = (EntityLiving) event.getEntity();

		if(entity.getAttackTarget() == null || !(entity.getAttackTarget() instanceof EntityPlayer) || entity.getAttackTarget() instanceof FakePlayer)
			return;

		EntityPlayer player = (EntityPlayer) entity.getAttackTarget();
		boolean resetTarget = false;

		if(entity instanceof EntityGhast) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.GHAST_META);
		} else if(entity instanceof EntityMagmaCube) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.MAGMA_CUBE_META);
		} else if(entity instanceof EntitySlime) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.SLIME_META);
		} else if(entity instanceof EntityEnderman) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.ENDERMAN_META);
		} else if(entity instanceof EntityPigZombie) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.ZOMBIE_PIGMAN_META);
		}

		if(resetTarget) {
			entity.setAttackTarget(null);
			entity.setRevengeTarget(null);
			if(entity instanceof EntityPigZombie) {
				//need to reset ai task because it doesn't get reset with setAttackTarget or setRevengeTarget and keeps player as target
				for (EntityAITasks.EntityAITaskEntry aiTask : entity.targetTasks.taskEntries) {
					if (aiTask.action instanceof EntityAIHurtByTarget) {
						aiTask.action.resetTask();
						break;
					}
				}

				//also need to reset anger target because apparently setRevengeTarget doesn't set this to null
				resetAngerTarget((EntityPigZombie) entity);
			}
		}
	}

	private static final Field SET_ANGER_TARGET = ReflectionHelper.findField(EntityPigZombie.class, "field_175459_bn", "angerTargetUUID");

	private void resetAngerTarget(EntityPigZombie zombiePigman) {
		try {
			SET_ANGER_TARGET.set(zombiePigman, null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
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

	private void doMobCharmCheckOnSetTarget(LivingSetAttackTargetEvent event) {
		if(event.getTarget() == null)
			return;
		if(!(event.getTarget() instanceof EntityPlayer) || event.getTarget() instanceof FakePlayer)
			return;
		if(!(event.getEntity() instanceof EntityLiving))
			return;

		EntityPlayer player = (EntityPlayer) event.getTarget();
		boolean resetTarget = false;
		EntityLiving entity = (EntityLiving) event.getEntity();

		if(entity instanceof EntityZombie) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.ZOMBIE_META);
		} else if(entity instanceof EntityWitherSkeleton) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.WITHER_SKELETON_META);
		} else if(entity instanceof EntitySkeleton || entity instanceof EntityStray) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.SKELETON_META);
		} else if(entity instanceof EntityCreeper) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.CREEPER_META);
		} else if(entity instanceof EntityWitch) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.WITCH_META);
		} else if(entity instanceof EntityCaveSpider) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.CAVE_SPIDER_META);
		} else if(entity instanceof EntitySpider) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.SPIDER_META);
		} else if(entity instanceof EntityEnderman) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.ENDERMAN_META);
		} else if(entity instanceof EntityBlaze) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.BLAZE_META);
		} else if(entity instanceof EntityGhast) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.GHAST_META);
		} else if(entity instanceof EntityMagmaCube) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.MAGMA_CUBE_META);
		} else if(entity instanceof EntitySlime) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.SLIME_META);
		} else if(entity instanceof EntityGuardian) {
			resetTarget = playerHasMobCharm(player, Reference.MOB_CHARM.GUARDIAN_META);
		}

		if(resetTarget) {
			entity.setAttackTarget(null);
			entity.setRevengeTarget(null);
		}
	}

	private void doTwilightCloakCheck(LivingEvent event) {
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
			if(player.world.getLightFromNeighbors(player.getPosition()) > Settings.TwilightCloak.maxLightLevel)
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
		handleWitherlessRose(player, event);
		if(event.isCanceled())
			event.setResult(null);
	}

	private void handleWitherlessRose(EntityPlayer player, LivingAttackEvent event) {
		if(event.getSource() == DamageSource.wither && playerHasItem(player, ModItems.witherlessRose, false))
			event.setCanceled(true);
	}

	private void handleInfernalClawsCheck(EntityPlayer player, LivingAttackEvent event) {
		if(!playerHasItem(player, ModItems.infernalClaws, false))
			return;
		if(!(event.getSource() == DamageSource.IN_FIRE) && !(event.getSource() == DamageSource.ON_FIRE))
			return;
		if(player.getFoodStats().getFoodLevel() <= 0)
			return;

		// trades all fire damage for exhaustion (which causes the hunger bar to
		// be depleted).
		player.addExhaustion(event.getAmount() * ((float) Settings.InfernalClaws.hungerCostPercent / 100F));
		event.setCanceled(true);
	}

	private void handleInfernalChaliceCheck(EntityPlayer player, LivingAttackEvent event) {
		if(!playerHasItem(player, ModItems.infernalChalice, false))
			return;
		//TODO: figure out if there's some way to know that the fire was caused by lava, otherwise this is the only way to prevent damage from lava - reason being that most of the damage is from fire caused by lava
		if(event.getSource() != DamageSource.LAVA && event.getSource() != DamageSource.ON_FIRE && event.getSource() != DamageSource.IN_FIRE)
			return;
		if(player.getFoodStats().getFoodLevel() <= 0)
			return;
		if(event.getSource() == DamageSource.LAVA || event.getSource() == DamageSource.ON_FIRE || event.getSource() == DamageSource.IN_FIRE) {
			player.addExhaustion(event.getAmount() * ((float) Settings.InfernalChalice.hungerCostPercent / 100F));
		}

		event.setCanceled(true);
	}

	private void handleAngelheartVialCheck(EntityPlayer player, LivingAttackEvent event) {
		// I'm rounding because I'm not 100% on whether the health value being a
		// fraction matters for determining death
		// Rounding would be worst case. I'm doing an early abort to keep my
		// indentation shallow.
		if(player.getHealth() > Math.round(event.getAmount()))
			return;
		if(!playerHasItem(player, ModItems.angelheartVial, false))
			return;

		decreaseAngelHeartByOne(player);

		// player should see a vial "shatter" effect and hear the glass break to
		// let them know they lost a vial.
		spawnAngelheartVialParticles(player);

		// play some glass breaking effects at the player location
		player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, player.world.rand.nextFloat() * 0.1F + 0.9F);

		// gives the player a few hearts, sparing them from death.
		float amountHealed = player.getMaxHealth() * (float) Settings.AngelHeartVial.healPercentageOfMaxLife / 100F;
		player.setHealth(amountHealed);

		// if the player had any negative status effects [vanilla only for now], remove them:
		if(Settings.AngelHeartVial.removeNegativeStatus)
			removeNegativeStatusEffects(player);

		event.setCanceled(true);
	}

	private void spawnAngelheartVialParticles(EntityPlayer player) {
		double var8 = player.posX;
		double var10 = player.posY;
		double var12 = player.posZ;
		Random var7 = player.world.rand;
		for(int var15 = 0; var15 < 8; ++var15) {
			player.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, var8, var10, var12, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D, Item.getIdFromItem(Items.POTIONITEM));
		}

		// purple, for reals.
		float red = 1.0F;
		float green = 0.0F;
		float blue = 1.0F;

		for(int var20 = 0; var20 < 100; ++var20) {
			double var39 = var7.nextDouble() * 4.0D;
			double var23 = var7.nextDouble() * Math.PI * 2.0D;
			double var25 = Math.cos(var23) * var39;
			double var27 = 0.01D + var7.nextDouble() * 0.5D;
			double var29 = Math.sin(var23) * var39;
			if(player.world.isRemote) {
				Particle var31 = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), var8 + var25 * 0.1D, var10 + 0.3D, var12 + var29 * 0.1D, var25, var27, var29);
				if(var31 != null) {
					float var32 = 0.75F + var7.nextFloat() * 0.25F;
					var31.setRBGColorF(red * var32, green * var32, blue * var32);
					var31.multiplyVelocity((float) var39);
				}
			}
		}

		player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1.0F, player.world.rand.nextFloat() * 0.1F + 0.9F);

	}

	private void removeNegativeStatusEffects(EntityPlayer player) {
		player.removePotionEffect(MobEffects.WITHER);
		player.removePotionEffect(MobEffects.HUNGER);
		player.removePotionEffect(MobEffects.POISON);
		player.removePotionEffect(MobEffects.NAUSEA);
		player.removePotionEffect(MobEffects.MINING_FATIGUE);
		player.removePotionEffect(MobEffects.SLOWNESS);
		player.removePotionEffect(MobEffects.BLINDNESS);
		player.removePotionEffect(MobEffects.WEAKNESS);
	}

	private void handlePhoenixDownCheck(EntityPlayer player, LivingAttackEvent event) {
		if(!playerHasItem(player, ModItems.phoenixDown, false))
			return;
		if(player.getHealth() > Math.round(event.getAmount())) {
			if(!(event.getSource() == DamageSource.FALL))
				return;
			if(player.getFoodStats().getFoodLevel() <= 0)
				return;

			float hungerDamage = event.getAmount() * ((float) Settings.PhoenixDown.hungerCostPercent / 100F);
			player.addExhaustion(hungerDamage);
			player.getFoodStats().onUpdate(player);

			event.setCanceled(true);

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
			if(event.getSource() == DamageSource.LAVA && Settings.PhoenixDown.giveTemporaryFireResistanceIfFireDamageKilledYou)
				player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 200, 0));
			if(event.getSource() == DamageSource.DROWN && Settings.PhoenixDown.giveTemporaryWaterBreathingIfDrowningKilledYou) {
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

	private void spawnPhoenixResurrectionParticles(EntityPlayer player) {
		for(int particles = 0; particles <= 400; particles++) {
			player.world.spawnParticle(EnumParticleTypes.FLAME, player.posX, player.posY, player.posZ, player.world.rand.nextGaussian() * 8, player.world.rand.nextGaussian() * 8, player.world.rand.nextGaussian() * 8);
		}
	}

	private void handleAngelicFeatherCheck(EntityPlayer player, LivingAttackEvent event) {
		if(!playerHasItem(player, ModItems.angelicFeather, false))
			return;
		if(!(event.getSource() == DamageSource.FALL))
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

	private void handleKrakenEyeCheck(EntityPlayer player, LivingAttackEvent event) {
		if(!playerHasItem(player, ModItems.krakenShell, false))
			return;
		if(player.getFoodStats().getFoodLevel() <= 0)
			return;

		// player absorbs drowning damage in exchange for hunger, at a relatively low rate.
		if(event.getSource() == DamageSource.DROWN) {
			float hungerDamage = event.getAmount() * ((float) Settings.KrakenShell.hungerCostPercent / 100F);
			player.addExhaustion(hungerDamage);
			event.setCanceled(true);
		}
	}

	private void revertPhoenixDownToAngelicFeather(EntityPlayer player) {
		for(int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if(player.inventory.mainInventory.get(slot).isEmpty())
				continue;
			if(player.inventory.mainInventory.get(slot).getItem() == ModItems.phoenixDown) {
				player.inventory.mainInventory.set(slot, new ItemStack(ModItems.angelicFeather));
				return;
			}
		}
	}

	private boolean playerHasItem(EntityPlayer player, Item item, boolean checkEnabled) {
		for(ItemStack stack : player.inventory.mainInventory) {
			if(stack.isEmpty())
				continue;
			if(stack.getItem() == item) {
				if(checkEnabled) {
					if(stack.getItem() instanceof ItemToggleable) {
						return ((ItemToggleable) stack.getItem()).isEnabled(stack);
					}
				}
				return true;
			}
		}
		return false;
	}

	private boolean playerHasMobCharm(EntityPlayer player, byte type) {

		for(ItemStack slotStack : player.inventory.mainInventory) {
			if(slotStack.isEmpty())
				continue;
			if(slotStack.getItem() == ModItems.mobCharm && ModItems.mobCharm.getType(slotStack) == type)
				return true;
			if(slotStack.getItem() == ModItems.mobCharmBelt && ModItems.mobCharmBelt.hasCharmType(slotStack, type))
				return true;
		}

		if(Loader.isModLoaded(Compatibility.MOD_ID.BAUBLES)) {
			IBaublesItemHandler inventoryBaubles = BaublesApi.getBaublesHandler(player);

			for(int i = 0; i < inventoryBaubles.getSlots(); i++) {
				ItemStack baubleStack = inventoryBaubles.getStackInSlot(i);
				if(!baubleStack.isEmpty() && baubleStack.getItem() == ModItems.mobCharmBelt && ModItems.mobCharmBelt.hasCharmType(baubleStack, type))
					return true;
			}
		}

		return false;
	}

	// pretty much the same as above, specific to angelheart vial. finds it and breaks one.
	private void decreaseAngelHeartByOne(EntityPlayer player) {
		for(int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if(player.inventory.mainInventory.get(slot).isEmpty())
				continue;
			if(player.inventory.mainInventory.get(slot).getItem() == ModItems.angelheartVial) {
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
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if(event.side == Side.CLIENT)
			return;

		EntityPlayer player = event.player;

		if(player.isHandActive() && player.getActiveItemStack().getItem() == ModItems.rendingGale && ModItems.rendingGale.isFlightMode(player.getActiveItemStack()) && ModItems.rendingGale.hasFlightCharge(player, player.getActiveItemStack())) {
			playersFlightStatus.put(player.getGameProfile().getId(), true);
			player.capabilities.allowFlying = true;
			((EntityPlayerMP) player).connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
		} else {
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
