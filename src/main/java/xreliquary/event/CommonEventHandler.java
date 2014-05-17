package xreliquary.event;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import xreliquary.init.ContentHandler;
import xreliquary.lib.Names;
import xreliquary.util.ObjectUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xreliquary.items.IVoidUpgradable;
import xreliquary.items.ItemVoidSatchel;
import xreliquary.items.ItemVoidTear;
import xreliquary.items.alkahestry.Alkahestry;
import xreliquary.lib.Reference;
import xreliquary.util.AlkahestRecipe;

import java.util.Random;

public class CommonEventHandler {
	@SubscribeEvent
	public void onCraftingPotion(PlayerEvent.ItemCraftedEvent event) {
		for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
			if (event.craftMatrix.getStackInSlot(slot) == null) {
				continue;
			}
			if (event.craftMatrix.getStackInSlot(slot).getItem() == ContentHandler.getItem(Names.glowing_water))
				if (!event.player.inventory.addItemStackToInventory(new ItemStack(ContentHandler.getItem(Names.condensed_potion), 1, Reference.EMPTY_VIAL_META))) {
					event.player.entityDropItem(new ItemStack(ContentHandler.getItem(Names.condensed_potion), 1, Reference.EMPTY_VIAL_META), 0.1F);
				}
		}
	}

    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
       Entity entity = event.entity;
       if (entity == null || !(entity instanceof EntityPlayer)) return;
       EntityPlayer player = (EntityPlayer)entity;
        handleDragonClawsCheck(player, event);
        handleFiredrinkerCheck(player, event);
        handleAngelheartVialCheck(player, event);
        handlePhoenixDownCheck(player, event);
        handleAngelicFeatherCheck(player, event);
        handleKrakenEyeCheck(player, event);
    }

    public void handleDragonClawsCheck(EntityPlayer player, LivingHurtEvent event) {
        if (!playerHasItem(player, ContentHandler.getItem(Names.dragon_claws))) return;
        if (event.source == DamageSource.inFire || event.source == DamageSource.onFire) {
            //trades all fire damage for exhaustion (which causes the hunger bar to be depleted).
            player.addExhaustion(event.ammount * 0.5F);
        }
        event.setCanceled(true);
    }

    public void handleFiredrinkerCheck(EntityPlayer player, LivingHurtEvent event) {
        if (!playerHasItem(player, ContentHandler.getItem(Names.claws_of_the_firedrinker))) return;
        if (event.source == DamageSource.inFire || event.source == DamageSource.onFire) {
            //trades all fire damage for food saturation (which causes the hunger bar to be regenerated).
            FoodStats food = player.getFoodStats();
            if (food.getSaturationLevel() + (event.ammount * 0.5F) >= food.getFoodLevel()) {
                //regenerate a little hunger if the saturation level maxes out, wrap food saturation
                food.addStats(1, (food.getSaturationLevel() + (event.ammount * 0.5F)) - food.getFoodLevel());
            } else {
                //add a little food saturation.
                food.addStats(0, (event.ammount * 0.5F));
            }
            player.addExhaustion(event.ammount * 0.5F);
        }
        if (event.source == DamageSource.lava) {
            //trades all lava damage for exhaustion (which causes the hunger bar to be depleted rapidly).
            //3.0F is what you lose when you regerate half a heart. I'm cutting that rate in half to make the item powerful.
            player.addExhaustion(event.ammount * 1.5F);
        }
        event.setCanceled(true);
    }

    public void handleAngelheartVialCheck(EntityPlayer player, LivingHurtEvent event) {
        //I'm rounding because I'm not 100% on whether the health value being a fraction matters for determining death
        //Rounding would be worst case. I'm doing an early abort to keep my indentation shallow.
        if (player.getHealth() > Math.round(event.ammount)) return;
        if (!playerHasItem(player, ContentHandler.getItem(Names.angelheart_vial))) return;

        //player should see a vial "shatter" effect and hear the glass break to let them know they lost a vial.
        spawnAngelheartVialParticles(player);

        //gives the player a few hearts, sparing them from death.
        player.setHealth(4);

        //if the player had any negative status effects [vanilla only for now], remove them:
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

            //purple, for reals.
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

    public void handlePhoenixDownCheck(EntityPlayer player, LivingHurtEvent event) {
        //I'm rounding because I'm not 100% on whether the health value being a fraction matters for determining death
        //Rounding would be worst case. I'm doing an early abort to keep my indentation shallow.
        if (player.getHealth() > Math.round(event.ammount)) return;
        if (!playerHasItem(player, ContentHandler.getItem(Names.phoenix_down))) return;

        //item reverts to a normal feather.
        revertPhoenixDownToAngelicFeather(player);
        event.setCanceled(true);

        //max player health, whatever it is.
        player.setHealth(player.getMaxHealth());

        //added bonus, has some extra effects when drowning or dying to lava, and grants you temporary damage resistance
        //there are some things the feather can't prevent (falling out of the world comes to mind)
        if (event.source == DamageSource.lava)
            //ten seconds of fire resistance, should hopefully be enough to get you out of the lava.
            player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 200, 0));
        if (event.source == DamageSource.drown) {
            //no clue if air is 10 or 20. In most cases this should be plenty, not to mention 10 seconds of water breathing.
            player.setAir(10);
            player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 200, 0));
        }

        //give the player temporary resistance to other damages.
        player.addPotionEffect(new PotionEffect(Potion.resistance.id, 200, 1));

        //give the player temporary regeneration.
        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 1));

        //if the player had any negative status effects [vanilla only for now], remove them:
        removeNegativeStatusEffects(player);

        //particles, lots of them
        spawnPhoenixResurrectionParticles(player);
    }

    public void spawnPhoenixResurrectionParticles(EntityPlayer player) {
        for (int particles = 0; particles <= 400; particles++) {
            player.worldObj.spawnParticle("flame", player.posX, player.posY, player.posZ, player.worldObj.rand.nextGaussian() * 8,  player.worldObj.rand.nextGaussian() * 8,  player.worldObj.rand.nextGaussian() * 8);
        }
    }

    public void handleAngelicFeatherCheck(EntityPlayer player, LivingHurtEvent event) {
        if (!playerHasItem(player, ContentHandler.getItem(Names.phoenix_down)) && !playerHasItem(player, ContentHandler.getItem(Names.angelic_feather))) return;
        if (event.source == DamageSource.fall) {
            if (player.fallDistance > 0.0F) {
                //trades fallDistance for exhaustion (which causes the hunger bar to be depleted).
                player.addExhaustion(player.fallDistance * 2.0F);
                player.fallDistance = 0.0F;
            }
        }
        event.setCanceled(true);
    }

    public void handleKrakenEyeCheck(EntityPlayer player, LivingHurtEvent event) {
        if (!playerHasItem(player, ContentHandler.getItem(Names.kraken_shell))) return;
        //player absorbs drowning damage in exchange for hunger, at a relatively low rate.
        if (event.source == DamageSource.drown) {
            float hungerDamage = 0.5F * (float)event.ammount;
            player.addExhaustion(hungerDamage);
            event.setCanceled(true);
        }
    }


    private void revertPhoenixDownToAngelicFeather(EntityPlayer player) {
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null) continue;
            if (player.inventory.mainInventory[slot].getItem() == ContentHandler.getItem(Names.phoenix_down)) {
                player.inventory.mainInventory[slot] = new ItemStack(ContentHandler.getItem(Names.angelic_feather));
                return;
            }
        }
    }

   private boolean playerHasItem(EntityPlayer player, Item item) {
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null) continue;
            if (player.inventory.mainInventory[slot].getItem() == item) {
                return true;
            }
        }
       return false;
    }

    @SubscribeEvent
    public void onCraftingAlkahest(PlayerEvent.ItemCraftedEvent event) {
        boolean isCharging = false;
        int tome = 9;
        AlkahestRecipe recipe = null;
        for (int count = 0; count < event.craftMatrix.getSizeInventory(); count++) {
            ItemStack stack = event.craftMatrix.getStackInSlot(count);
            if (stack != null) {
                if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(ContentHandler.getItem(Names.alkahest_tome)))) {
                    tome = count;
                } else if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(Items.redstone)) || ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getBlockIdentifier(Blocks.redstone_block))) {
                    isCharging = true;
                } else {
                    if (Alkahestry.getDictionaryKey(stack) == null) {
                        recipe = Alkahestry.getRegistry().get(ObjectUtils.getItemIdentifier(stack.getItem()));
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

    @SubscribeEvent
    public void onSatchelUpgrade(PlayerEvent.ItemCraftedEvent event) {
            if (event.crafting == null)
                return;
            if (!(event.crafting.getItem() instanceof IVoidUpgradable))
                return;
            for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
                if (event.craftMatrix.getStackInSlot(slot) == null) {
                    continue;
                }
                if (event.craftMatrix.getStackInSlot(slot).getItem() != ContentHandler.getItem(Names.void_tear) && ObjectUtils.areItemsEqual(event.craftMatrix.getStackInSlot(slot).getItem(), Item.getItemFromBlock(ContentHandler.getBlock(Names.wraith_node)))) {
                    continue;
                }
                if (event.crafting.getItem() instanceof ItemVoidSatchel) {

                    ItemStack tear = event.craftMatrix.getStackInSlot(slot);

                    if (!(tear.getItem() instanceof ItemVoidTear))
                        continue;

                    NBTTagCompound tearData = tear.getTagCompound();

                    if (tearData == null)
                        continue;

                    String type = tearData.getString("itemID");
                    int meta = tearData.getShort("itemMeta");
                    int quantity = tearData.getShort("itemQuantity");
                    int leftover = 0;
                    int capacity = Reference.CAPACITY_UPGRADE_INCREMENT;
                    if (quantity > capacity) {
                        leftover = quantity - capacity;
                        quantity = capacity;
                    }
                    NBTTagCompound satchelData = new NBTTagCompound();
                    satchelData.setString("itemID", type);
                    satchelData.setShort("itemMeta", (short) meta);
                    satchelData.setShort("itemQuantity", (short) quantity);
                    satchelData.setShort("capacity", (short) capacity);
                    event.crafting.setTagCompound(satchelData);
                    if (leftover > 0) {
                        event.player.worldObj.playSoundAtEntity(event.player, "random.glass", 0.1F, 0.5F * ((event.player.worldObj.rand.nextFloat() - event.player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
                        while (leftover > 0) {
                            ItemStack spillage = new ItemStack((Item) Item.itemRegistry.getObject(type), 1, meta);
                            if (leftover > spillage.getMaxStackSize()) {
                                spillage.stackSize = spillage.getMaxStackSize();
                                leftover -= spillage.getMaxStackSize();
                            } else {
                                spillage.stackSize = leftover;
                                leftover = 0;
                            }
                            if (event.player.worldObj.isRemote) {
                                continue;
                            }
                            EntityItem item = new EntityItem(event.player.worldObj, event.player.posX, event.player.posY, event.player.posZ, spillage);
                            event.player.worldObj.spawnEntityInWorld(item);
                        }
                    }
                }
            }
            // handles upgrades for VoidSatchel
            for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
                if (event.craftMatrix.getStackInSlot(slot) == null) {
                    continue;
                }
                if (event.craftMatrix.getStackInSlot(slot).getItem() != ContentHandler.getItem(Names.void_satchel)) {
                    continue;
                }
                ItemStack oldSatchel = event.craftMatrix.getStackInSlot(slot);
                NBTTagCompound oldSatchelData = oldSatchel.getTagCompound();
                if (oldSatchelData == null) {
                    continue;
                }
                int type = oldSatchelData.getShort("itemID");
                int meta = oldSatchelData.getShort("itemMeta");
                int quantity = oldSatchelData.getShort("itemQuantity");
                int capacity = oldSatchelData.getShort("capacity");
                if (capacity >= 32000) {
                    for (int slot0 = 0; slot0 < event.craftMatrix.getSizeInventory(); slot++) {
                        if (event.craftMatrix.getStackInSlot(slot0) == null) {
                            continue;
                        }
                        if (event.craftMatrix.getStackInSlot(slot0).getItem() == ContentHandler.getItem(Names.void_tear_empty)) {
                            event.craftMatrix.getStackInSlot(slot0).stackSize++;
                        }
                    }
                } else {
                    capacity += 3 * Reference.CAPACITY_UPGRADE_INCREMENT;
                }
                NBTTagCompound satchelData = new NBTTagCompound();
                satchelData.setShort("itemID", (short) type);
                satchelData.setShort("itemMeta", (short) meta);
                satchelData.setShort("itemQuantity", (short) quantity);
                satchelData.setShort("capacity", (short) capacity);
                event.crafting.setTagCompound(satchelData);
            }
    }

    @SubscribeEvent
    public void satchelHandler(PlayerEvent.ItemCraftedEvent event) {
        if (event.crafting == null)
            return;
        if (event.crafting.getItem() != ContentHandler.getItem(Names.void_satchel))
            return;
        // handles creation of VoidSatchel
        for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
            if (event.craftMatrix.getStackInSlot(slot) == null) {
                continue;
            }
            if (event.craftMatrix.getStackInSlot(slot).getItem() != ContentHandler.getItem(Names.void_tear)) {
                continue;
            }
            ItemStack tear = event.craftMatrix.getStackInSlot(slot);
            NBTTagCompound tearData = tear.getTagCompound();
            if (tearData == null) {
                continue;
            }
            String type = tearData.getString("itemID");
            int meta = tearData.getShort("itemMeta");
            int quantity = tearData.getShort("itemQuantity");
            int leftover = 0;
            int capacity = Reference.CAPACITY_UPGRADE_INCREMENT;
            if (quantity > capacity) {
                leftover = quantity - capacity;
                quantity = capacity;
            }
            NBTTagCompound satchelData = new NBTTagCompound();
            satchelData.setString("itemID", type);
            satchelData.setShort("itemMeta", (short) meta);
            satchelData.setShort("itemQuantity", (short) quantity);
            satchelData.setShort("capacity", (short) capacity);
            event.crafting.setTagCompound(satchelData);
            if (leftover > 0) {
                event.player.worldObj.playSoundAtEntity(event.player, "random.glass", 0.1F, 0.5F * ((event.player.worldObj.rand.nextFloat() - event.player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
                while (leftover > 0) {
                    ItemStack spillage = new ItemStack((Item) Item.itemRegistry.getObject(type), 1, meta);
                    if (leftover > spillage.getMaxStackSize()) {
                        spillage.stackSize = spillage.getMaxStackSize();
                        leftover -= spillage.getMaxStackSize();
                    } else {
                        spillage.stackSize = leftover;
                        leftover = 0;
                    }
                    if (event.player.worldObj.isRemote) {
                        continue;
                    }
                    EntityItem item = new EntityItem(event.player.worldObj, event.player.posX, event.player.posY, event.player.posZ, spillage);
                    event.player.worldObj.spawnEntityInWorld(item);
                }
            }
        }
        // handles upgrades for VoidSatchel
        for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
            if (event.craftMatrix.getStackInSlot(slot) == null) {
                continue;
            }
            if (event.craftMatrix.getStackInSlot(slot).getItem() != ContentHandler.getItem(Names.void_satchel)) {
                continue;
            }
            ItemStack oldSatchel = event.craftMatrix.getStackInSlot(slot);
            NBTTagCompound oldSatchelData = oldSatchel.getTagCompound();
            if (oldSatchelData == null) {
                continue;
            }
            int type = oldSatchelData.getShort("itemID");
            int meta = oldSatchelData.getShort("itemMeta");
            int quantity = oldSatchelData.getShort("itemQuantity");
            int capacity = oldSatchelData.getShort("capacity");
            if (capacity >= 32000) {
                for (int slot0 = 0; slot0 < event.craftMatrix.getSizeInventory(); slot++) {
                    if (event.craftMatrix.getStackInSlot(slot0) == null) {
                        continue;
                    }
                    if (event.craftMatrix.getStackInSlot(slot0).getItem() == ContentHandler.getItem(Names.void_tear_empty)) {
                        event.craftMatrix.getStackInSlot(slot0).stackSize++;
                    }
                }
            } else {
                capacity += 3 * Reference.CAPACITY_UPGRADE_INCREMENT;
            }
            NBTTagCompound satchelData = new NBTTagCompound();
            satchelData.setShort("itemID", (short) type);
            satchelData.setShort("itemMeta", (short) meta);
            satchelData.setShort("itemQuantity", (short) quantity);
            satchelData.setShort("capacity", (short) capacity);
            event.crafting.setTagCompound(satchelData);
        }
    }


}
