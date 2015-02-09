package xreliquary.event;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import xreliquary.Reliquary;
import xreliquary.init.XRRecipes;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;

import java.util.Random;

public class CommonEventHandler {

    @SubscribeEvent
    public void blameDrullkus(PlayerEvent.PlayerLoggedInEvent event) {
        // Thanks for the Witch's Hat texture! Also, blame Drullkus for making me add this. :P
        if (event.player.getGameProfile().getName() == "Drullkus") {
            if (!event.player.getEntityData().hasKey("gift")) {
                if (event.player.inventory.addItemStackToInventory(new ItemStack(Reliquary.CONTENT.getItem(Names.witch_hat)))) {
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
        if (event.target == null)
            return;
        if (!(event.target instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) event.target;
        doZombieZhuCheck(event.entity, player);
        doSkeletonZhuCheck(event.entity, player);
        doWitherSkeletonZhuCheck(event.entity, player);
        doCreeperZhuCheck(event.entity, player);
    }

    public void doHeartZhuCheck(LivingEvent event) {
        if (event.entity instanceof EntityLiving) {
            EntityLiving entityLiving = ((EntityLiving) event.entity);
            if (entityLiving.getAttackTarget() == null)
                return;
            if (!(entityLiving.getAttackTarget() instanceof EntityPlayer))
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
        if (e instanceof EntityZombie && !(e instanceof EntityPigZombie)) {
            if (playerHasItem(p, heartZhu(Reference.ZOMBIE_ZHU_META), false)) {
                ((EntityZombie) e).setAttackTarget(null);
                ((EntityZombie) e).setTarget(null);
                ((EntityZombie) e).setRevengeTarget(null);
            }
        }
    }

    public void doSkeletonZhuCheck(Entity e, EntityPlayer p) {
        if (e instanceof EntitySkeleton && ((EntitySkeleton) e).getSkeletonType() != 1) {
            if (playerHasItem(p, heartZhu(Reference.SKELETON_ZHU_META), false)) {
                ((EntitySkeleton) e).setAttackTarget(null);
                ((EntitySkeleton) e).setTarget(null);
                ((EntitySkeleton) e).setRevengeTarget(null);
            }
        }
    }

    public void doWitherSkeletonZhuCheck(Entity e, EntityPlayer p) {
        if (e instanceof EntitySkeleton && ((EntitySkeleton) e).getSkeletonType() == 1) {
            if (playerHasItem(p, heartZhu(Reference.WITHER_SKELETON_ZHU_META), false)) {
                ((EntitySkeleton) e).setAttackTarget(null);
                ((EntitySkeleton) e).setTarget(null);
                ((EntitySkeleton) e).setRevengeTarget(null);
            }
        }
    }

    public void doCreeperZhuCheck(Entity e, EntityPlayer p) {
        if (e instanceof EntityCreeper) {
            if (playerHasItem(p, heartZhu(Reference.CREEPER_ZHU_META), false)) {
                ((EntityCreeper) e).setAttackTarget(null);
                ((EntityCreeper) e).setTarget(null);
                ((EntityCreeper) e).setRevengeTarget(null);
            }
        }
    }


    public void doTwilightCloakCheck(LivingEvent event) {
        if (event.entity instanceof EntityLiving) {
            EntityLiving entityLiving = ((EntityLiving)event.entity);
            if (entityLiving.getAttackTarget() == null)
                return;
            if (!(entityLiving.getAttackTarget() instanceof EntityPlayer))
                return;
            EntityPlayer player = (EntityPlayer)entityLiving.getAttackTarget();
            if (!playerHasItem(player, Reliquary.CONTENT.getItem(Names.twilight_cloak), true))
                return;

            //toggled effect, makes player invisible based on light level (configurable)
            int playerX = MathHelper.floor_double(player.posX);
            int playerY = MathHelper.floor_double(player.boundingBox.minY);
            int playerZ = MathHelper.floor_double(player.posZ);

            if (player.worldObj.getBlockLightValue(playerX, playerY, playerZ) > Reliquary.CONFIG.getInt(Names.twilight_cloak, "max_light_level"))
                return;
            if (event.entity instanceof EntityLiving) {
                ((EntityLiving)event.entity).setAttackTarget(null);
            }
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(Reference.MOD_ID)) {
            Reliquary.CONFIG.save();
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
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
        if (e.worldObj.rand.nextFloat() <= dropProbability) {
            if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayer && event.source.damageType.equals("player")) {
                EntityItem entityitem = new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, ist);
                entityitem.delayBeforeCanPickup = 10;
                event.drops.add(entityitem);
            }
        }
    }

    public float getBaseDrop(String s) {
        return (float)Reliquary.CONFIG.getInt(Names.mob_drop_probability, s + "_base") * 0.01F;
    }

    public float getLootingDrop(String s) {
        return (float)Reliquary.CONFIG.getInt(Names.mob_drop_probability, s + "_looting") * 0.01F;
    }

    private ItemStack ingredient(int meta) {
        return XRRecipes.ingredient(meta);
    }

    public void handleSquidDropsCheck(Entity e, LivingDropsEvent event) {
        if (e instanceof EntitySquid)
            handleEventDropListAddition(e, event, getBaseDrop(Names.squid_beak), getLootingDrop(Names.squid_beak), ingredient(Reference.SQUID_INGREDIENT_META));
    }

    public void handleWitchDropsCheck(Entity e, LivingDropsEvent event) {
        if (e instanceof EntityWitch)
            handleEventDropListAddition(e, event, getBaseDrop(Names.witch_hat), getLootingDrop(Names.witch_hat), new ItemStack(Reliquary.CONTENT.getItem(Names.witch_hat), 1, 0));
    }

    public void handleSpiderOrCaveSpiderDropsCheck(Entity e, LivingDropsEvent event) {
        if (e instanceof EntitySpider) {
            float base = getBaseDrop((e instanceof EntityCaveSpider) ? Names.cave_spider_fangs : Names.spider_fangs);
            float looting = getLootingDrop((e instanceof EntityCaveSpider) ? Names.cave_spider_fangs : Names.spider_fangs);
            handleEventDropListAddition(e, event, base, looting, ingredient(Reference.SPIDER_INGREDIENT_META));
        }
    }

    public void handleSkeletonDropsCheck(Entity e, LivingDropsEvent event) {
        if (e instanceof EntitySkeleton && ((EntitySkeleton) e).getSkeletonType() != 1)
            handleEventDropListAddition(e, event, getBaseDrop(Names.rib_bone), getLootingDrop(Names.rib_bone), ingredient(Reference.SKELETON_INGREDIENT_META));
    }

    public void handleWitherDropsCheck(Entity e, LivingDropsEvent event) {
        if (e instanceof EntitySkeleton && ((EntitySkeleton) e).getSkeletonType() == 1)
            handleEventDropListAddition(e, event, getBaseDrop(Names.withered_rib), getLootingDrop(Names.withered_rib), ingredient(Reference.WITHER_INGREDIENT_META));
    }

    public void handleZombieOrZombiePigmanDropsCheck(Entity e, LivingDropsEvent event) {
        if (e instanceof EntityZombie) {
            float base = getBaseDrop((e instanceof EntityPigZombie) ? Names.pigman_heart : Names.zombie_heart);
            float looting = getLootingDrop((e instanceof EntityPigZombie) ? Names.pigman_heart: Names.zombie_heart);
            handleEventDropListAddition(e, event, base, looting, ingredient(Reference.ZOMBIE_INGREDIENT_META));
        }
    }

    public void handleSlimeDropsCheck(Entity e, LivingDropsEvent event) {
        if (e instanceof EntitySlime)
            handleEventDropListAddition(e, event, getBaseDrop(Names.slime_pearl), getLootingDrop(Names.slime_pearl), ingredient(Reference.SLIME_INGREDIENT_META));

    }

    public void handleBlazeOrMagmaCubeDropsCheck(Entity e, LivingDropsEvent event) {
        if (e instanceof EntityBlaze || e instanceof EntityMagmaCube) {
            float base = getBaseDrop((e instanceof EntityMagmaCube) ? Names.magma_cube_molten_core : Names.blaze_molten_core);
            float looting = getLootingDrop((e instanceof EntityMagmaCube) ? Names.magma_cube_molten_core: Names.blaze_molten_core);
            handleEventDropListAddition(e, event, base, looting, ingredient(Reference.MOLTEN_INGREDIENT_META));
        }
    }

    public void handleGhastOrCreeperDropsCheck(Entity e, LivingDropsEvent event) {
        if (e instanceof EntityCreeper || e instanceof EntityGhast) {
            float base = getBaseDrop((e instanceof EntityGhast) ? Names.ghast_gland : Names.creeper_gland);
            float looting = getLootingDrop((e instanceof EntityGhast) ? Names.ghast_gland: Names.creeper_gland);
            handleEventDropListAddition(e, event, base, looting, ingredient(Reference.CREEPER_INGREDIENT_META));
            if (e instanceof EntityCreeper && ((EntityCreeper) e).getPowered()) {
                handleEventDropListAddition(e, event, getBaseDrop(Names.eye_of_the_storm), getLootingDrop(Names.eye_of_the_storm), ingredient(Reference.STORM_INGREDIENT_META));
            }
        }
    }

    public void handleEndermanDropsCheck(Entity e, LivingDropsEvent event) {
        if (e instanceof EntityEnderman)
            handleEventDropListAddition(e, event, getBaseDrop(Names.ender_heart), getLootingDrop(Names.ender_heart), ingredient(Reference.ENDER_INGREDIENT_META));
    }

    public void handleBatsDropsCheck(Entity e, LivingDropsEvent event) {
        if (e instanceof EntityBat)
            handleEventDropListAddition(e, event, getBaseDrop(Names.bat_wing), getLootingDrop(Names.bat_wing), ingredient(Reference.BAT_INGREDIENT_META));
    }

    public void handleSnowGolemDropsCheck(Entity e, LivingDropsEvent event) {
        if (e instanceof EntitySnowman)
            handleEventDropListAddition(e, event, getBaseDrop(Names.frozen_core), getLootingDrop(Names.frozen_core), ingredient(Reference.FROZEN_INGREDIENT_META));
    }

    @SubscribeEvent
    public void beforePlayerHurt(LivingAttackEvent event) {
        Entity entity = event.entity;
        if (entity == null || !(entity instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) entity;
        handleInfernalClawsCheck(player, event);
        handleInfernalChaliceCheck(player, event);
        handleAngelicFeatherCheck(player, event);
        handleKrakenEyeCheck(player, event);
        // check
        handlePhoenixDownCheck(player, event);
        handleAngelheartVialCheck(player, event);
        if (event.isCanceled())
            event.setResult(null);
    }

    public void handleInfernalClawsCheck(EntityPlayer player, LivingAttackEvent event) {
        if (!playerHasItem(player, Reliquary.CONTENT.getItem(Names.infernal_claws), false))
            return;
        if (!(event.source == DamageSource.inFire) && !(event.source == DamageSource.onFire))
            return;
        if (player.getFoodStats().getFoodLevel() <= 0)
            return;

        // trades all fire damage for exhaustion (which causes the hunger bar to
        // be depleted).
        player.addExhaustion(event.ammount * ((float) Reliquary.CONFIG.getInt(Names.infernal_claws, "hunger_cost_percent") / 100F));
        event.setCanceled(true);
    }

    public void handleInfernalChaliceCheck(EntityPlayer player, LivingAttackEvent event) {
        if (!playerHasItem(player, Reliquary.CONTENT.getItem(Names.infernal_chalice), false))
            return;
        if (event.source != DamageSource.lava)
            return;
        if (player.getFoodStats().getFoodLevel() <= 0)
            return;
        if (!(event.source == DamageSource.lava)) {
            player.addExhaustion(event.ammount * ((float)Reliquary.CONFIG.getInt(Names.infernal_chalice, "hunger_cost_percent") / 100F));
        }

        event.setCanceled(true);
    }

    public void handleAngelheartVialCheck(EntityPlayer player, LivingAttackEvent event) {
        // I'm rounding because I'm not 100% on whether the health value being a
        // fraction matters for determining death
        // Rounding would be worst case. I'm doing an early abort to keep my
        // indentation shallow.
        if (player.getHealth() > Math.round(event.ammount))
            return;
        if (!playerHasItem(player, Reliquary.CONTENT.getItem(Names.angelheart_vial), false))
            return;

        decreaseItemByOne(player, Reliquary.CONTENT.getItem(Names.angelheart_vial));

        // player should see a vial "shatter" effect and hear the glass break to
        // let them know they lost a vial.
        spawnAngelheartVialParticles(player);

        // play some glass breaking effects at the player location
        player.worldObj.playSoundEffect(player.posX + 0.5D, player.posY + 0.5D, player.posZ + 0.5D, "dig.glass", 1.0F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F);

        // gives the player a few hearts, sparing them from death.
        float amountHealed = player.getMaxHealth() * (float)Reliquary.CONFIG.getInt(Names.angelheart_vial, "heal_percentage_of_max_life") / 100F;
        player.setHealth(amountHealed);

        // if the player had any negative status effects [vanilla only for now], remove them:
        if (Reliquary.CONFIG.getBool(Names.angelheart_vial, "remove_negative_status"))
            removeNegativeStatusEffects(player);

        event.setCanceled(true);
    }

    public void spawnAngelheartVialParticles(EntityPlayer player) {
        double var8 = player.posX;
        double var10 = player.posY;
        double var12 = player.posZ;
        String var14 = "iconcrack_" + Item.getIdFromItem(Items.potionitem);
        Random var7 = player.worldObj.rand;
        for (int var15 = 0; var15 < 8; ++var15) {
            player.worldObj.spawnParticle(var14, var8, var10, var12, var7.nextGaussian() * 0.15D, var7.nextDouble() * 0.2D, var7.nextGaussian() * 0.15D);
        }

        // purple, for reals.
        float red = 1.0F;
        float green = 0.0F;
        float blue = 1.0F;
        String var19 = "spell";

        for (int var20 = 0; var20 < 100; ++var20) {
            double var39 = var7.nextDouble() * 4.0D;
            double var23 = var7.nextDouble() * Math.PI * 2.0D;
            double var25 = Math.cos(var23) * var39;
            double var27 = 0.01D + var7.nextDouble() * 0.5D;
            double var29 = Math.sin(var23) * var39;
            if (player.worldObj.isRemote) {
                EntityFX var31 = Minecraft.getMinecraft().renderGlobal.doSpawnParticle(var19, var8 + var25 * 0.1D, var10 + 0.3D, var12 + var29 * 0.1D, var25, var27, var29);
                if (var31 != null) {
                    float var32 = 0.75F + var7.nextFloat() * 0.25F;
                    var31.setRBGColorF(red * var32, green * var32, blue * var32);
                    var31.multiplyVelocity((float) var39);
                }
            }
        }

        player.worldObj.playSoundEffect(player.posX + 0.5D, player.posY + 0.5D, player.posZ + 0.5D, "random.glass", 1.0F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F);

    }

    public void removeNegativeStatusEffects(EntityPlayer player) {
        player.removePotionEffect(Potion.wither.id);
        player.removePotionEffect(Potion.hunger.id);
        player.removePotionEffect(Potion.poison.id);
        player.removePotionEffect(Potion.confusion.id);
        player.removePotionEffect(Potion.digSlowdown.id);
        player.removePotionEffect(Potion.moveSlowdown.id);
        player.removePotionEffect(Potion.blindness.id);
        player.removePotionEffect(Potion.weakness.id);
    }

    public void handlePhoenixDownCheck(EntityPlayer player, LivingAttackEvent event) {
        if (!playerHasItem(player, Reliquary.CONTENT.getItem(Names.phoenix_down), false))
            return;
        if (player.getHealth() > Math.round(event.ammount)) {
            if (!(event.source == DamageSource.fall))
                return;
            if (player.getFoodStats().getFoodLevel() <= 0)
                return;

            float hungerDamage = event.ammount * ((float)Reliquary.CONFIG.getInt(Names.phoenix_down, "hunger_cost_percent") / 100F);
            player.addExhaustion(hungerDamage);
            player.getFoodStats().onUpdate(player);

            event.setCanceled(true);

            return;
        } else {

            // item reverts to a normal feather.
            revertPhoenixDownToAngelicFeather(player);

            // gives the player a few hearts, sparing them from death.
            float amountHealed = player.getMaxHealth() * (float)Reliquary.CONFIG.getInt(Names.phoenix_down, "heal_percentage_of_max_life") / 100F;
            player.setHealth(amountHealed);

            // if the player had any negative status effects [vanilla only for now], remove them:
            if (Reliquary.CONFIG.getBool(Names.phoenix_down, "remove_negative_status"))
                removeNegativeStatusEffects(player);

            // added bonus, has some extra effects when drowning or dying to lava
            if (event.source == DamageSource.lava && Reliquary.CONFIG.getBool(Names.phoenix_down, "give_temporary_fire_resistance_if_fire_damage_killed_you"))
                player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 200, 0));
            if (event.source == DamageSource.drown && Reliquary.CONFIG.getBool(Names.phoenix_down, "give_temporary_water_breathing_if_drowning_killed_you")) {
                player.setAir(10);
                player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 200, 0));
            }

            // give the player temporary resistance to other damages.
            if (Reliquary.CONFIG.getBool(Names.phoenix_down, "give_temporary_damage_resistance"))
                player.addPotionEffect(new PotionEffect(Potion.resistance.id, 200, 1));

            // give the player temporary regeneration.
            if (Reliquary.CONFIG.getBool(Names.phoenix_down, "give_temporary_regeneration"))
                player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 1));

            // particles, lots of them
            spawnPhoenixResurrectionParticles(player);

            event.setCanceled(true);
        }
    }

    public void spawnPhoenixResurrectionParticles(EntityPlayer player) {
        for (int particles = 0; particles <= 400; particles++) {
            player.worldObj.spawnParticle("flame", player.posX, player.posY, player.posZ, player.worldObj.rand.nextGaussian() * 8, player.worldObj.rand.nextGaussian() * 8, player.worldObj.rand.nextGaussian() * 8);
        }
    }

    public void handleAngelicFeatherCheck(EntityPlayer player, LivingAttackEvent event) {
        if (!playerHasItem(player, Reliquary.CONTENT.getItem(Names.angelic_feather), false))
            return;
        if (!(event.source == DamageSource.fall))
            return;
        if (player.getFoodStats().getFoodLevel() <= 0)
            return;

        if (player.fallDistance > 0.0F) {
            float hungerDamage = event.ammount * ((float)Reliquary.CONFIG.getInt(Names.angelic_feather, "hunger_cost_percent") / 100F);
            player.addExhaustion(hungerDamage);
            player.getFoodStats().onUpdate(player);
        }
        event.setCanceled(true);
    }

    public void handleKrakenEyeCheck(EntityPlayer player, LivingAttackEvent event) {
        if (!playerHasItem(player, Reliquary.CONTENT.getItem(Names.kraken_shell), false))
            return;
        if (player.getFoodStats().getFoodLevel() <= 0)
            return;

        // player absorbs drowning damage in exchange for hunger, at a relatively low rate.
        if (event.source == DamageSource.drown) {
            float hungerDamage = event.ammount * ((float)Reliquary.CONFIG.getInt(Names.kraken_shell, "hunger_cost_percent") / 100F);
            player.addExhaustion(hungerDamage);
            event.setCanceled(true);
        }
    }

    private void revertPhoenixDownToAngelicFeather(EntityPlayer player) {
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null)
                continue;
            if (player.inventory.mainInventory[slot].getItem() == Reliquary.CONTENT.getItem(Names.phoenix_down)) {
                player.inventory.mainInventory[slot] = new ItemStack(Reliquary.CONTENT.getItem(Names.angelic_feather));
                return;
            }
        }
    }

    private boolean playerHasItem(EntityPlayer player, Item item, boolean checkEnabled) {
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null)
                continue;
            if (player.inventory.mainInventory[slot].getItem() == item) {
                if (checkEnabled) {
                    if (player.inventory.mainInventory[slot].getItem() instanceof ItemToggleable) {
                        if (((ItemToggleable) player.inventory.mainInventory[slot].getItem()).isEnabled(player.inventory.mainInventory[slot]))
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
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null)
                continue;
            if (player.inventory.mainInventory[slot].isItemEqual(ist)) {
                if (checkEnabled) {
                    if (player.inventory.mainInventory[slot].getItem() instanceof ItemToggleable) {
                        if (((ItemToggleable) player.inventory.mainInventory[slot].getItem()).isEnabled(player.inventory.mainInventory[slot]))
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
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null)
                continue;
            if (player.inventory.mainInventory[slot].getItem() == item) {
                player.inventory.decrStackSize(slot, 1);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onCraftingAlkahest(PlayerEvent.ItemCraftedEvent event) {
        boolean isCharging = false;
        int tome = 9;
        AlkahestRecipe recipe = null;
        for (int count = 0; count < event.craftMatrix.getSizeInventory(); count++) {
            ItemStack stack = event.craftMatrix.getStackInSlot(count);
            if (stack != null) {
                if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(Reliquary.CONTENT.getItem(Names.alkahestry_tome)))) {
                    tome = count;
                } else if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(Items.redstone)) || ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(Blocks.redstone_block))) {
                    isCharging = true;
                } else {
                    if (Alkahestry.getDictionaryKey(stack) == null) {
                        recipe = Alkahestry.getRegistry().get(ContentHelper.getIdent(stack.getItem()));
                    } else {
                        recipe = Alkahestry.getDictionaryKey(stack);
                    }
                }
            }
        }
        if (tome != 9 && isCharging) {
            event.craftMatrix.setInventorySlotContents(tome, null);
        } else if (tome != 9 && !isCharging && recipe != null) {
            ItemStack temp = event.craftMatrix.getStackInSlot(tome);
            temp.setItemDamage(temp.getItemDamage() + recipe.cost);
            event.craftMatrix.setInventorySlotContents(tome, temp);
        }
    }

}
