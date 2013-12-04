package xreliquary.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMidasTouchstone extends ItemXR {

    public ItemMidasTouchstone(int par1) {
        super(par1);
        this.setMaxDamage(257);
        this.setMaxStackSize(1);
        canRepair = false;
        this.setCreativeTab(Reliquary.tabsXR);
        this.setUnlocalizedName(Names.TOUCHSTONE_NAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public void addInformation(ItemStack par1ItemStack,
            EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add("Repairs items made of gold;");
        par3List.add("consumes glowstone dust.");
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
        if (world.isRemote)
            return;
        EntityPlayer player = null;
        if (e instanceof EntityPlayer) {
            player = (EntityPlayer) e;
        } else
            return;
        if (ist.getItemDamage() == 0 || ist.getItemDamage() > 4) {
            if (findAndConsumeGlowstoneDust(ist, player)) {
                ist.setItemDamage(ist.getItemDamage() == 0 ? 251 : ist
                        .getItemDamage() - 4);
            }
        }
        if (getCooldown(ist) == 0) {
            doRepairAndDamageTouchstone(ist, player);
        } else {
            decrementCooldown(ist);
        }
    }

    private void decrementCooldown(ItemStack ist) {
        this.setShort("cooldown", ist, this.getShort("cooldown", ist) - 1);
    }

    private int getCooldown(ItemStack ist) {
        return this.getShort("cooldown", ist);
    }

    private boolean findAndConsumeGlowstoneDust(ItemStack ist,
            EntityPlayer player) {
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null) {
                continue;
            }
            if (player.inventory.mainInventory[slot].getItem() == Item.glowstone) {
                player.inventory.decrStackSize(slot, 1);
                return true;
            }
        }
        return false;
    }

    private void doRepairAndDamageTouchstone(ItemStack ist, EntityPlayer player) {
        for (int slot = 0; slot < player.inventory.armorInventory.length; slot++) {
            if (player.inventory.armorInventory[slot] == null) {
                continue;
            }
            if (!(player.inventory.armorInventory[slot].getItem() instanceof ItemArmor)) {
                continue;
            }
            ItemArmor armor = (ItemArmor) player.inventory.armorInventory[slot]
                    .getItem();
            if (armor.getArmorMaterial() != EnumArmorMaterial.GOLD) {
                continue;
            }
            if (player.inventory.armorInventory[slot].getItemDamage() <= 0) {
                continue;
            }
            if (decrementTouchStoneCharge(ist)) {
                player.inventory.armorInventory[slot]
                        .setItemDamage(player.inventory.armorInventory[slot]
                                .getItemDamage() - 1);
            }
        }
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null) {
                continue;
            }
            if (player.inventory.mainInventory[slot].getItem() instanceof ItemSword) {
                ItemSword sword = (ItemSword) player.inventory.mainInventory[slot]
                        .getItem();
                if (sword.getToolMaterialName() != "GOLD") {
                    continue;
                }
                if (player.inventory.mainInventory[slot].getItemDamage() <= 0) {
                    continue;
                }
                if (decrementTouchStoneCharge(ist)) {
                    player.inventory.mainInventory[slot]
                            .setItemDamage(player.inventory.mainInventory[slot]
                                    .getItemDamage() - 1);
                }
            } else if (player.inventory.mainInventory[slot].getItem() instanceof ItemTool) {
                ItemTool tool = (ItemTool) player.inventory.mainInventory[slot]
                        .getItem();
                if (tool.getToolMaterialName() != "GOLD") {
                    continue;
                }
                if (player.inventory.mainInventory[slot].getItemDamage() <= 0) {
                    continue;
                }
                if (decrementTouchStoneCharge(ist)) {
                    player.inventory.mainInventory[slot]
                            .setItemDamage(player.inventory.mainInventory[slot]
                                    .getItemDamage() - 1);
                }
            }
        }
        setCooldown(ist);
    }

    private void setCooldown(ItemStack ist) {
        this.setShort("cooldown", ist, 4);
    }

    private boolean decrementTouchStoneCharge(ItemStack ist) {
        if (ist.getItemDamage() != 0
                && ist.getItemDamage() < ist.getMaxDamage() - 1) {
            ist.setItemDamage(ist.getItemDamage() + 1);
            return true;
        }
        return false;
    }
}
