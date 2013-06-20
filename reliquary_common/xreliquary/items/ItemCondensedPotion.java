package xreliquary.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import xreliquary.Reliquary;
import xreliquary.common.TimeKeeperHandler;
import xreliquary.entities.EntityCondensedFertility;
import xreliquary.entities.EntityCondensedSplashAphrodite;
import xreliquary.entities.EntityCondensedSplashBlindness;
import xreliquary.entities.EntityCondensedSplashConfusion;
import xreliquary.entities.EntityCondensedSplashHarm;
import xreliquary.entities.EntityCondensedSplashPoison;
import xreliquary.entities.EntityCondensedSplashRuin;
import xreliquary.entities.EntityCondensedSplashSlowness;
import xreliquary.entities.EntityCondensedSplashWeakness;
import xreliquary.entities.EntityCondensedSplashWither;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCondensedPotion extends ItemXR {

    protected ItemCondensedPotion(int par1) {
        super(par1);
        this.setMaxDamage(0);
        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
        canRepair = false;
        this.setCreativeTab(Reliquary.tabsXR);
        this.setUnlocalizedName(Names.CONDENSED_POTION_NAME);
    }

    public int emptyVialMeta() {
        return Reference.EMPTY_VIAL_META;
    }

    public void registerLiquidContainer() {
        LiquidContainerRegistry.registerLiquid(new LiquidContainerData(new LiquidStack(Block.waterStill, LiquidContainerRegistry.BUCKET_VOLUME / 8), new ItemStack(XRItems.condensedPotion, CondensedPotion ), new ItemStack(Item.bucketEmpty)));
    }
    
    public int waterVialMeta() {
        return Reference.WATER_META;
    }

    public int panaceaMeta() {
        return Reference.PANACEA_META;
    }

    public int basePotionMeta() {
        return Reference.POTION_META;
    }

    public int baseSplashMeta() {
        return Reference.SPLASH_META;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return !(isEmptyVial(stack) || isBaseSplash(stack)
                || isBasePotion(stack) || isJustWater(stack));
    }

    public boolean isSplash(ItemStack stack) {
        return stack.getItemDamage() > baseSplashMeta()
                && stack.getItemDamage() < emptyVialMeta();
    }

    public boolean isPotion(ItemStack stack) {
        return stack.getItemDamage() > basePotionMeta()
                && stack.getItemDamage() < waterVialMeta();
    }

    public boolean isEmptyVial(ItemStack stack) {
        return stack.getItemDamage() == emptyVialMeta();
    }

    public boolean isBaseSplash(ItemStack stack) {
        return stack.getItemDamage() == baseSplashMeta();
    }

    public boolean isBasePotion(ItemStack stack) {
        return stack.getItemDamage() == basePotionMeta();
    }

    public boolean isPanacea(ItemStack stack) {
        return stack.getItemDamage() == panaceaMeta();
    }

    public boolean isJustWater(ItemStack stack) {
        return stack.getItemDamage() == waterVialMeta();
    }

    @Override
    public void addInformation(ItemStack par1ItemStack,
            EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        for (String description : getDescriptionFrom(par1ItemStack))
            if (description == null) {
                continue;
            } else {
                par3List.add(description);
            }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        if (hasEffect(stack))
            return EnumRarity.epic;
        return EnumRarity.common;
    }

    @Override
    public String getItemDisplayName(ItemStack ist) {
        int potion = ist.getItemDamage();
        if (potion == Reference.SPLASH_META)
            return Names.SPLASH_NAME;
        if (potion == Reference.APHRODITE_META)
            return Names.APHRODITE_NAME;
        if (potion == Reference.POISON_META)
            return Names.POISON_NAME;
        if (potion == Reference.ACID_META)
            return Names.ACID_NAME;
        if (potion == Reference.CONFUSION_META)
            return Names.CONFUSION_NAME;
        if (potion == Reference.SLOWING_META)
            return Names.SLOWING_NAME;
        if (potion == Reference.WEAKNESS_META)
            return Names.WEAKNESS_NAME;
        if (potion == Reference.WITHER_META)
            return Names.WITHER_NAME;
        if (potion == Reference.BLINDING_META)
            return Names.BLINDING_NAME;
        if (potion == Reference.RUINATION_META)
            return Names.RUINATION_NAME;
        if (potion == Reference.FERTILIZER_META)
            return Names.FERTILIZER_NAME;
        if (potion == Reference.EMPTY_VIAL_META)
            return Names.EMPTY_VIAL_NAME;
        if (potion == Reference.POTION_META)
            return Names.POTION_NAME;
        if (potion == Reference.SPEED_META)
            return Names.SPEED_NAME;
        if (potion == Reference.DIGGING_META)
            return Names.DIGGING_NAME;
        if (potion == Reference.STRENGTH_META)
            return Names.STRENGTH_NAME;
        if (potion == Reference.HEALING_META)
            return Names.HEALING_NAME;
        if (potion == Reference.BOUNDING_META)
            return Names.BOUNDING_NAME;
        if (potion == Reference.REGENERATION_META)
            return Names.REGENERATION_NAME;
        if (potion == Reference.RESISTANCE_META)
            return Names.RESISTANCE_NAME;
        if (potion == Reference.FIRE_WARDING_META)
            return Names.FIRE_WARDING_NAME;
        if (potion == Reference.BREATHING_META)
            return Names.BREATHING_NAME;
        if (potion == Reference.INVISIBILITY_META)
            return Names.INVISIBILITY_NAME;
        if (potion == Reference.INFRAVISION_META)
            return Names.INFRAVISION_NAME;
        if (potion == Reference.PROTECTION_META)
            return Names.PROTECTION_NAME;
        if (potion == Reference.POTENCE_META)
            return Names.POTENCE_NAME;
        if (potion == Reference.CELERITY_META)
            return Names.CELERITY_NAME;
        if (potion == Reference.PANACEA_META)
            return Names.PANACEA_NAME;
        if (potion == Reference.WATER_META)
            return Names.WATER_NAME;
        return "CondensedPotion";
    }

    @Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
            List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
        par3List.add(new ItemStack(par1, 1, 2));
        par3List.add(new ItemStack(par1, 1, 3));
        par3List.add(new ItemStack(par1, 1, 4));
        par3List.add(new ItemStack(par1, 1, 5));
        par3List.add(new ItemStack(par1, 1, 6));
        par3List.add(new ItemStack(par1, 1, 7));
        par3List.add(new ItemStack(par1, 1, 8));
        par3List.add(new ItemStack(par1, 1, 9));
        par3List.add(new ItemStack(par1, 1, 10));
        par3List.add(new ItemStack(par1, 1, 11));
        par3List.add(new ItemStack(par1, 1, 12));
        par3List.add(new ItemStack(par1, 1, 13));
        par3List.add(new ItemStack(par1, 1, 14));
        par3List.add(new ItemStack(par1, 1, 15));
        par3List.add(new ItemStack(par1, 1, 16));
        par3List.add(new ItemStack(par1, 1, 17));
        par3List.add(new ItemStack(par1, 1, 18));
        par3List.add(new ItemStack(par1, 1, 19));
        par3List.add(new ItemStack(par1, 1, 20));
        par3List.add(new ItemStack(par1, 1, 21));
        par3List.add(new ItemStack(par1, 1, 22));
        par3List.add(new ItemStack(par1, 1, 23));
        par3List.add(new ItemStack(par1, 1, 24));
        par3List.add(new ItemStack(par1, 1, 25));
        par3List.add(new ItemStack(par1, 1, 26));
        par3List.add(new ItemStack(par1, 1, 27));
        par3List.add(new ItemStack(par1, 1, 28));
    }

    private String[] getDescriptionFrom(ItemStack ist) {
        int potion = ist.getItemDamage();
        String description[] = new String[2];
        switch (potion) {
        case Reference.EMPTY_VIAL_META:
            description[0] = "An empty vial for";
            description[1] = "condensed potions.";
            break;
        case Reference.POTION_META:
            description[0] = "A base potion for";
            description[1] = "condensed potions.";
            break;
        case Reference.SPEED_META:
            description[0] = "Movement increased";
            description[1] = "for 5 minutes.";
            break;
        case Reference.DIGGING_META:
            description[0] = "Dig and break faster";
            description[1] = "for 5 minutes.";
            break;
        case Reference.STRENGTH_META:
            description[0] = "Damage increased by 3";
            description[1] = "for 5 minutes.";
            break;
        case Reference.HEALING_META:
            description[0] = "Heals 6 hearts.";
            description[1] = "(12 damage)";
            break;
        case Reference.BOUNDING_META:
            description[0] = "Higher jumping";
            description[1] = "for 5 minutes.";
            break;
        case Reference.REGENERATION_META:
            description[0] = "Health regeneration";
            description[1] = "for 1 minute.";
            break;
        case Reference.RESISTANCE_META:
            description[0] = "Damage resistance";
            description[1] = "for 5 minutes.";
            break;
        case Reference.FIRE_WARDING_META:
            description[0] = "Fire resistance";
            description[1] = "for 5 minutes.";
            break;
        case Reference.BREATHING_META:
            description[0] = "Water breathing";
            description[1] = "for 5 minutes.";
            break;
        case Reference.INVISIBILITY_META:
            description[0] = "Invisibility";
            description[1] = "for 5 minutes.";
            break;
        case Reference.INFRAVISION_META:
            description[0] = "See in the dark";
            description[1] = "for 5 minutes.";
            break;
        case Reference.PROTECTION_META:
            description[0] = "Resist fire and";
            description[1] = "damage for 3 minutes.";
            break;
        case Reference.POTENCE_META:
            description[0] = "Strength and dig boost";
            description[1] = "for 3 minutes.";
            break;
        case Reference.CELERITY_META:
            description[0] = "Speed and jump boost";
            description[1] = "for 3 minutes.";
            break;
        case Reference.PANACEA_META:
            description[0] = "30 second regen, heals 6 hearts.";
            description[1] = "Cures any ailment.";
            break;
        case Reference.SPLASH_META:
            description[0] = "A base potion for creating";
            description[1] = "condensed splash vials.";
            break;
        case Reference.APHRODITE_META:
            description[0] = "Makes animals";
            description[1] = "want to mate.";
            break;
        case Reference.POISON_META:
            description[0] = "Poisons mobs";
            description[1] = "for 60 seconds.";
            break;
        case Reference.ACID_META:
            description[0] = "Deals 12 damage,";
            description[1] = "even vs. Undead.";
            break;
        case Reference.CONFUSION_META:
            description[0] = "Causes confusion";
            description[1] = "for 60 seconds.";
            break;
        case Reference.SLOWING_META:
            description[0] = "Causes slowness";
            description[1] = "for 60 seconds.";
            break;
        case Reference.WEAKNESS_META:
            description[0] = "Causes weakness";
            description[1] = "for 60 seconds.";
            break;
        case Reference.WITHER_META:
            description[0] = "Causes wither effect";
            description[1] = "for 60 seconds.";
            break;
        case Reference.BLINDING_META:
            description[0] = "Causes blindness";
            description[1] = "for 60 seconds.";
            break;
        case Reference.RUINATION_META:
            description[0] = "Slows, weakens and";
            description[1] = "poisons for 60 seconds.";
            break;
        case Reference.FERTILIZER_META:
            description[0] = "Grows crops in a";
            description[1] = "wide square pattern.";
            break;
        case Reference.WATER_META:
            description[0] = "It's a vial of ";
            description[1] = "plain ol' water.";
            break;
        }
        return description;
    }

    @Override
    public ItemStack onEaten(ItemStack ist, World world, EntityPlayer player) {
        if (!isPotion(ist))
            return ist;
        if (!player.capabilities.isCreativeMode) {
            --ist.stackSize;
        }
        if (!world.isRemote) {
            PotionEffect effects[] = new PotionEffect[2];
            effects = getPotionEffects(ist);
            for (PotionEffect effect : effects) {
                if (effect == null) {
                    continue;
                }
                player.addPotionEffect(effect);
            }
            if (isPanacea(ist)) {
                player.removePotionEffect(Potion.wither.id);
                player.removePotionEffect(Potion.hunger.id);
                player.removePotionEffect(Potion.poison.id);
                player.removePotionEffect(Potion.confusion.id);
                player.removePotionEffect(Potion.digSlowdown.id);
                player.removePotionEffect(Potion.moveSlowdown.id);
                player.removePotionEffect(Potion.blindness.id);
                player.removePotionEffect(Potion.weakness.id);
            }
        }
        if (!player.capabilities.isCreativeMode) {
            if (ist.stackSize <= 0)
                return new ItemStack(this, 1, emptyVialMeta());
            player.inventory.addItemStackToInventory(new ItemStack(this, 1,
                    emptyVialMeta()));
        }
        return ist;
    }

    private Entity getNewPotionEntity(ItemStack ist, World world,
            EntityPlayer player) {
        int potion = ist.getItemDamage();
        switch (potion) {

        case Reference.APHRODITE_META:
            return new EntityCondensedSplashAphrodite(world, player);
        case Reference.POISON_META:
            return new EntityCondensedSplashPoison(world, player);
        case Reference.ACID_META:
            return new EntityCondensedSplashHarm(world, player);
        case Reference.CONFUSION_META:
            return new EntityCondensedSplashConfusion(world, player);
        case Reference.SLOWING_META:
            return new EntityCondensedSplashSlowness(world, player);
        case Reference.WEAKNESS_META:
            return new EntityCondensedSplashWeakness(world, player);
        case Reference.WITHER_META:
            return new EntityCondensedSplashWither(world, player);
        case Reference.BLINDING_META:
            return new EntityCondensedSplashBlindness(world, player);
        case Reference.RUINATION_META:
            return new EntityCondensedSplashRuin(world, player);
        case Reference.FERTILIZER_META:
            return new EntityCondensedFertility(world, player);
        }

        return null;
    }

    private PotionEffect[] getPotionEffects(ItemStack ist) {
        PotionEffect effects[] = new PotionEffect[2];
        int potion = ist.getItemDamage();
        switch (potion) {
        case Reference.SPEED_META:
            effects[0] = new PotionEffect(Potion.moveSpeed.id, 6000, 1);
            break;
        case Reference.DIGGING_META:
            effects[0] = new PotionEffect(Potion.digSpeed.id, 6000, 1);
            break;
        case Reference.STRENGTH_META:
            effects[0] = new PotionEffect(Potion.damageBoost.id, 6000, 1);
            break;
        case Reference.HEALING_META:
            effects[0] = new PotionEffect(Potion.heal.id, 12, 0);
            break;
        case Reference.BOUNDING_META:
            effects[0] = new PotionEffect(Potion.jump.id, 6000, 1);
            break;
        case Reference.REGENERATION_META:
            effects[0] = new PotionEffect(Potion.regeneration.id, 1200, 1);
            break;
        case Reference.RESISTANCE_META:
            effects[0] = new PotionEffect(Potion.resistance.id, 6000, 0);
            break;
        case Reference.FIRE_WARDING_META:
            effects[0] = new PotionEffect(Potion.fireResistance.id, 6000, 0);
            break;
        case Reference.BREATHING_META:
            effects[0] = new PotionEffect(Potion.waterBreathing.id, 6000, 0);
            break;
        case Reference.INVISIBILITY_META:
            effects[0] = new PotionEffect(Potion.invisibility.id, 6000, 0);
            break;
        case Reference.INFRAVISION_META:
            effects[0] = new PotionEffect(Potion.nightVision.id, 6000, 0);
            break;
        case Reference.PROTECTION_META:
            effects[0] = new PotionEffect(Potion.resistance.id, 3600, 1);
            effects[1] = new PotionEffect(Potion.fireResistance.id, 3600, 1);
            break;
        case Reference.POTENCE_META:
            effects[0] = new PotionEffect(Potion.damageBoost.id, 3600, 1);
            effects[1] = new PotionEffect(Potion.digSpeed.id, 3600, 1);
            break;
        case Reference.CELERITY_META:
            effects[0] = new PotionEffect(Potion.jump.id, 3600, 1);
            effects[1] = new PotionEffect(Potion.moveSpeed.id, 3600, 1);
            break;
        case Reference.PANACEA_META:
            effects[0] = new PotionEffect(Potion.heal.id, 6, 0);
            effects[1] = new PotionEffect(Potion.regeneration.id, 600, 1);
            break;
        }
        return effects;
    }

    @SideOnly(Side.CLIENT)
    private Icon iconBase;

    @SideOnly(Side.CLIENT)
    private Icon iconBaseOverlay;

    @SideOnly(Side.CLIENT)
    private Icon iconSplash;

    @SideOnly(Side.CLIENT)
    private Icon iconSplashOverlay;

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        iconBase = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase()
                + ":" + Names.CONDENSED_POTION_NAME);
        iconBaseOverlay = iconRegister.registerIcon(Reference.MOD_ID
                .toLowerCase() + ":" + Names.CONDENSED_POTION_OVERLAY_NAME);

        iconSplash = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase()
                + ":" + Names.CONDENSED_POTION_SPLASH_NAME);
        iconSplashOverlay = iconRegister.registerIcon(Reference.MOD_ID
                .toLowerCase()
                + ":"
                + Names.CONDENSED_POTION_SPLASH_OVERLAY_NAME);
    }

    @Override
    public Icon getIcon(ItemStack itemStack, int renderPass) {
        if (isEmptyVial(itemStack))
            return iconBase;
        if (isPanacea(itemStack) || isPotion(itemStack)
                || isBasePotion(itemStack) || isJustWater(itemStack)) {
            if (renderPass == 1)
                return iconBaseOverlay;
            else
                return iconBase;
        } else {
            if (renderPass == 1)
                return iconSplashOverlay;
            else
                return iconSplash;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
        if (renderPass == 1)
            return getColor(itemStack);
        else
            return Integer.parseInt(Colors.PURE, 16);
    }

    public int getColor(ItemStack itemStack) {
        String red = "";
        String green = "";
        String blue = "";
        int timeFactor = TimeKeeperHandler.getTime();
        if (timeFactor > 43) {
            timeFactor = 87 - timeFactor;
        }
        int potion = itemStack.getItemDamage();
        switch (potion) {
        case Reference.SPEED_META:
            return Integer.parseInt(Colors.SPEED_COLOR, 16);
        case Reference.DIGGING_META:
            return Integer.parseInt(Colors.DIGGING_COLOR, 16);
        case Reference.STRENGTH_META:
            return Integer.parseInt(Colors.STRENGTH_COLOR, 16);
        case Reference.HEALING_META:
            return Integer.parseInt(Colors.HEALING_COLOR, 16);
        case Reference.BOUNDING_META:
            return Integer.parseInt(Colors.BOUNDING_COLOR, 16);
        case Reference.REGENERATION_META:
            return Integer.parseInt(Colors.REGENERATION_COLOR, 16);
        case Reference.RESISTANCE_META:
            return Integer.parseInt(Colors.RESISTANCE_COLOR, 16);
        case Reference.FIRE_WARDING_META:
            return Integer.parseInt(Colors.FIRE_WARDING_COLOR, 16);
        case Reference.BREATHING_META:
            return Integer.parseInt(Colors.BREATHING_COLOR, 16);
        case Reference.INVISIBILITY_META:
            red = Integer.toHexString(timeFactor * 3 + 22);
            green = Integer.toHexString(timeFactor * 3 + 22);
            blue = Integer.toHexString(timeFactor * 3 + 22);
            return Integer.parseInt(String.format("%s%s%s", red, green, blue),
                    16);
        case Reference.INFRAVISION_META:
            return Integer.parseInt(Colors.INFRAVISION_COLOR, 16);
        case Reference.PROTECTION_META:
            red = Integer.toHexString(timeFactor * 3 + 88);
            green = Integer.toHexString(timeFactor * 3 + 88);
            blue = Integer.toHexString(timeFactor * 3 + 88);
            return Integer.parseInt(String.format("%s%s%s", red, green, blue),
                    16);
        case Reference.POTENCE_META:
            red = Integer.toHexString(timeFactor * 4 + 22);
            green = Integer.toHexString(timeFactor * 2 + 22);
            blue = "00";
            return Integer.parseInt(String.format("%s%s%s", red, green, blue),
                    16);
        case Reference.CELERITY_META:
            red = "00";
            green = Integer.toHexString(timeFactor * 4 + 22);
            blue = "FF";
            return Integer.parseInt(String.format("%s%s%s", red, green, blue),
                    16);
        case Reference.PANACEA_META:
            red = Integer.toHexString(timeFactor * 4 + 22);
            green = "00";
            blue = "FF";
            return Integer.parseInt(String.format("%s%s%s", red, green, blue),
                    16);
        case Reference.APHRODITE_META:
            return Integer.parseInt(Colors.APHRODITE_COLOR, 16);
        case Reference.POISON_META:
            return Integer.parseInt(Colors.POISON_COLOR, 16);
        case Reference.ACID_META:
            return Integer.parseInt(Colors.ACID_COLOR, 16);
        case Reference.CONFUSION_META:
            return Integer.parseInt(Colors.CONFUSION_COLOR, 16);
        case Reference.SLOWING_META:
            return Integer.parseInt(Colors.SLOWING_COLOR, 16);
        case Reference.WEAKNESS_META:
            return Integer.parseInt(Colors.WEAKNESS_COLOR, 16);
        case Reference.WITHER_META:
            return Integer.parseInt(Colors.WITHER_COLOR, 16);
        case Reference.BLINDING_META:
            return Integer.parseInt(Colors.BLINDING_COLOR, 16);
        case Reference.RUINATION_META:
            red = Integer.toHexString(timeFactor * 5 + 22);
            green = "FF";
            blue = "00";
            return Integer.parseInt(String.format("%s%s%s", red, green, blue),
                    16);
        case Reference.FERTILIZER_META:
            return Integer.parseInt(Colors.FERTILIZER_COLOR, 16);
        case Reference.WATER_META:
        case Reference.SPLASH_META:
        case Reference.POTION_META:
            return Integer.parseInt(Colors.WATER_COLOR, 16);
        }
        return Integer.parseInt(Colors.PURE, 16);
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 16;
    }

    /**
     * returns the action that specifies what animation to play when the items
     * is being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack ist) {
        if (isPotion(ist))
            return EnumAction.drink;
        return EnumAction.none;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is
     * pressed. Args: itemStack, world, entityPlayer
     */

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world,
            EntityPlayer player) {
        if (this.isEmptyVial(ist)) {
            boolean var11 = isEmptyVial(ist);
            MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(
                    world, player, var11);

            if (mop == null)
                return ist;
            else {
                if (mop.typeOfHit == EnumMovingObjectType.TILE) {
                    int var13 = mop.blockX;
                    int var14 = mop.blockY;
                    int var15 = mop.blockZ;
                    if (world.getBlockMaterial(var13, var14, var15) == Material.water) {
                        if (--ist.stackSize <= 0)
                            return new ItemStack(this, 1, waterVialMeta());

                        if (!player.inventory
                                .addItemStackToInventory(new ItemStack(this, 1,
                                        waterVialMeta()))) {
                            player.dropPlayerItem(new ItemStack(this, 1,
                                    waterVialMeta()));
                        }
                    }
                }
            }
        } else if (!hasEffect(ist))
            return ist;
        else if (isPotion(ist)) {
            player.setItemInUse(ist, this.getMaxItemUseDuration(ist));
        } else if (isSplash(ist)) {
            if (world.isRemote)
                return ist;
            Entity e = getNewPotionEntity(ist, world, player);
            if (e == null)
                return ist;
            if (!player.capabilities.isCreativeMode) {
                --ist.stackSize;
            }
            world.playSoundAtEntity(player, "random.bow", 0.5F,
                    0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            world.spawnEntityInWorld(e);
        }
        return ist;
    }

}
