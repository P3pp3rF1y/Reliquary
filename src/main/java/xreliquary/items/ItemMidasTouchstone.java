package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.util.NBTHelper;

import java.util.List;

@ContentInit
public class ItemMidasTouchstone extends ItemToggleable {

    public ItemMidasTouchstone() {
        super(Names.midas_touchstone);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(257);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
        if (world.isRemote)
            return;
        EntityPlayer player;
        if (e instanceof EntityPlayer) {
            player = (EntityPlayer) e;
        } else
            return;

        //don't drain glowstone if it isn't activated.
        if (this.isEnabled(ist)) {
            if (ist.getItemDamage() == 0 || ist.getItemDamage() > getGlowStoneWorth()) {
                if (InventoryHelper.consumeItem(new ItemStack(Items.glowstone_dust), player)) {
                    ist.setItemDamage(ist.getItemDamage() == 0 ? (ist.getMaxDamage() - 1) - getGlowStoneWorth() : ist.getItemDamage() - getGlowStoneWorth());
                }
            }
        }

        if (getCooldown(ist) == 0) {
            doRepairAndDamageTouchstone(ist, player);
        } else {
            decrementCooldown(ist);
        }
    }

    private void decrementCooldown(ItemStack ist) {
        NBTHelper.setShort("cooldown", ist, NBTHelper.getShort("cooldown", ist) - 1);
    }

    private int getCooldown(ItemStack ist) {
        return NBTHelper.getShort("cooldown", ist);
    }

    private void doRepairAndDamageTouchstone(ItemStack ist, EntityPlayer player) {
        //list of customizable items added through configs that can be repaired by the touchstone.
        List<String> goldItems = (List<String>) Reliquary.CONFIG.get(Names.midas_touchstone, "gold_items");

        for (int slot = 0; slot < player.inventory.armorInventory.length; slot++) {
            if (player.inventory.armorInventory[slot] == null) {
                continue;
            }
            if (!(player.inventory.armorInventory[slot].getItem() instanceof ItemArmor)) {
                continue;
            }
            ItemArmor armor = (ItemArmor) player.inventory.armorInventory[slot].getItem();
            if (armor.getArmorMaterial() != ItemArmor.ArmorMaterial.GOLD && !goldItems.contains(ContentHelper.getIdent(armor))) {
                continue;
            }
            if (player.inventory.armorInventory[slot].getItemDamage() <= 0) {
                continue;
            }
            if (decrementTouchStoneCharge(ist)) {
                player.inventory.armorInventory[slot].setItemDamage(player.inventory.armorInventory[slot].getItemDamage() - 1);
            }
        }
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null) {
                continue;
            }
            if (player.inventory.mainInventory[slot].getItem() instanceof ItemSword) {
                ItemSword sword = (ItemSword) player.inventory.mainInventory[slot].getItem();
                if (sword.getToolMaterialName() != ItemSword.ToolMaterial.GOLD.name() && !goldItems.contains(ContentHelper.getIdent(sword))) {
                    continue;
                }
                if (player.inventory.mainInventory[slot].getItemDamage() <= 0) {
                    continue;
                }
                if (decrementTouchStoneCharge(ist)) {
                    player.inventory.mainInventory[slot].setItemDamage(player.inventory.mainInventory[slot].getItemDamage() - 1);
                }
            } else if (player.inventory.mainInventory[slot].getItem() instanceof ItemTool) {
                ItemTool tool = (ItemTool) player.inventory.mainInventory[slot].getItem();
                if (tool.getToolMaterialName() != ItemSword.ToolMaterial.GOLD.name()  && !goldItems.contains(ContentHelper.getIdent(tool))) {
                    continue;
                }
                if (player.inventory.mainInventory[slot].getItemDamage() <= 0) {
                    continue;
                }
                if (decrementTouchStoneCharge(ist)) {
                    player.inventory.mainInventory[slot].setItemDamage(player.inventory.mainInventory[slot].getItemDamage() - 1);
                }
            } else {
                Item item = player.inventory.mainInventory[slot].getItem();
                if (!goldItems.contains(ContentHelper.getIdent(item))) {
                    continue;
                }
                if (player.inventory.mainInventory[slot].getItemDamage() <= 0 || !item.isDamageable()) {
                    continue;
                }
                if (decrementTouchStoneCharge(ist)) {
                    player.inventory.mainInventory[slot].setItemDamage(player.inventory.mainInventory[slot].getItemDamage() - 1);
                }
            }
        }
        setCooldown(ist);
    }

    private void setCooldown(ItemStack ist) {
        NBTHelper.setShort("cooldown", ist, 4);
    }

    private boolean decrementTouchStoneCharge(ItemStack ist) {
        if (ist.getItemDamage() != 0 && ist.getItemDamage() < ist.getMaxDamage() - getGlowStoneCost()) {
            ist.setItemDamage(ist.getItemDamage() + getGlowStoneCost());
            return true;
        }
        return false;
    }

    private int getGlowStoneCost() {
        return Reliquary.CONFIG.getInt(Names.midas_touchstone, "glowstone_cost");
    }

    private int getGlowStoneWorth() {
        return Reliquary.CONFIG.getInt(Names.midas_touchstone, "glowstone_worth");
    }
}
