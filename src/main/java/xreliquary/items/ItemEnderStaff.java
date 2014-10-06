package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.entities.EntityEnderStaffProjectile;
import xreliquary.lib.Names;
import xreliquary.util.NBTHelper;

import java.util.List;

@ContentInit
public class ItemEnderStaff extends ItemToggleable {

    public ItemEnderStaff() {
        super(Names.ender_staff);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
        this.setMaxDamage(257);
        canRepair = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }


    @Override
    public boolean isFull3D() {
        return true;
    }

    private int getEnderStaffPearlCost() {
        return Reliquary.CONFIG.getInt(Names.ender_staff, "ender_pearl_cast_cost");
    }

    private int getEnderStaffNodeWarpCost() {
        return Reliquary.CONFIG.getInt(Names.ender_staff, "ender_pearl_node_warp_cost");
    }

    private int getEnderPearlWorth() {
        return Reliquary.CONFIG.getInt(Names.ender_staff, "ender_pearl_worth");
    }

    private int getNodeWarpCastTime() {
        return Reliquary.CONFIG.getInt(Names.ender_staff, "node_warp_cast_time");
    }


    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack ist) {
        if (entityLiving.worldObj.isRemote)
            return false;
        if (!(entityLiving instanceof EntityPlayer))
            return false;
        if (getCooldown(ist) > 0)
            return false;
        EntityPlayer player = (EntityPlayer)entityLiving;

        if (ist.getItemDamage() == 0)
            return false;
        if (ist.getItemDamage() < ist.getMaxDamage() - getEnderStaffPearlCost()) {
            player.worldObj.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            // if the player is sneaking, it fires a "reduced gravity" ender
            // pearl, for a longer range/shallow arc.
            player.worldObj.spawnEntityInWorld(new EntityEnderStaffProjectile(player.worldObj, player, !player.isSneaking()));
            ist.setItemDamage(ist.getItemDamage() >= (ist.getMaxDamage() - 1) - getEnderStaffPearlCost() ? 0 : ist.getItemDamage() + getEnderStaffPearlCost());
            setCooldown(ist);
        }
        return true;
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
        if (world.isRemote)
            return;

        // checks to see if cooldown variable > 0 and decrements if true, each tick.
        decrementCooldown(ist);

        if (!this.isEnabled(ist))
            return;
        EntityPlayer player = null;
        if (e instanceof EntityPlayer) {
            player = (EntityPlayer) e;
        }
        if (player == null)
            return;
        if (ist.getItemDamage() == 0 || ist.getItemDamage() > getEnderPearlWorth()) {
            if (InventoryHelper.consumeItem(new ItemStack(Items.ender_pearl), player)) {
                ist.setItemDamage(ist.getItemDamage() == 0 ? ist.getMaxDamage() - getEnderPearlWorth() : ist.getItemDamage() - getEnderPearlWorth());
            }
        }
    }

    @Override
    public void onUsingTick(ItemStack ist, EntityPlayer player, int unadjustedCount) {
        for (int particles = 0; particles < 2; particles++) {
            player.worldObj.spawnParticle("portal", player.posX, player.posY, player.posZ, player.worldObj.rand.nextGaussian(), player.worldObj.rand.nextGaussian(), player.worldObj.rand.nextGaussian());
        }
        if (unadjustedCount == 1) {
            doWraithNodeWarpCheck(ist, player.worldObj, player);
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack ist) {
        return EnumAction.block;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return this.getNodeWarpCastTime();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (!player.isSneaking()) {
            player.setItemInUse(ist, getMaxItemUseDuration(ist));
        }
        return super.onItemRightClick(ist, world, player);
    }

    private ItemStack doWraithNodeWarpCheck(ItemStack ist, World world, EntityPlayer player) {
        if (getCooldown(ist) > 0)
            return ist;
        if (ist.getItemDamage() == 0)
            return ist;

        if (ist.getTagCompound() != null && ist.getTagCompound().getInteger("dimensionID") != Integer.valueOf(getWorld(player))) {
            if (!world.isRemote) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "Out of range!"));
            }
        } else if (ist.getTagCompound() != null && ContentHelper.areBlocksEqual(world.getBlock(ist.getTagCompound().getInteger("nodeX" + getWorld(player)), ist.getTagCompound().getInteger("nodeY" + getWorld(player)), ist.getTagCompound().getInteger("nodeZ" + getWorld(player))), ContentHandler.getBlock(Names.wraith_node))) {
            if (canTeleport(world, ist.getTagCompound().getInteger("nodeX" + getWorld(player)), ist.getTagCompound().getInteger("nodeY" + getWorld(player)), ist.getTagCompound().getInteger("nodeZ" + getWorld(player)))) {
                if (ist.getItemDamage() < ist.getMaxDamage() - getEnderStaffNodeWarpCost()) {
                    teleportPlayer(world, ist.getTagCompound().getInteger("nodeX" + getWorld(player)), ist.getTagCompound().getInteger("nodeY" + getWorld(player)), ist.getTagCompound().getInteger("nodeZ" + getWorld(player)), player);
                    setCooldown(ist);
                    ist.setItemDamage(ist.getItemDamage() >= (ist.getMaxDamage() - 1) - getEnderStaffPearlCost() ? 0 : ist.getItemDamage() + getEnderStaffNodeWarpCost());
                }
            }
        } else if (ist.getTagCompound() != null && ist.getTagCompound().hasKey("dimensionID")) {
            ist.getTagCompound().removeTag("dimensionID");
            ist.getTagCompound().removeTag("nodeX");
            ist.getTagCompound().removeTag("nodeY");
            ist.getTagCompound().removeTag("nodeZ");
            ist.getTagCompound().removeTag("cooldown");
            if (!world.isRemote) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "Node dosen't exist!"));
            } else {
                player.playSound("mob.endermen.death", 1.0f, 1.0f);
            }
        }
        return ist;
    }


    private int getCooldown(ItemStack ist) {
        return NBTHelper.getShort("cooldown", ist);
    }

    private void setCooldown(ItemStack ist) {
        NBTHelper.setShort("cooldown", ist, (short) 20);
    }

    private void decrementCooldown(ItemStack ist) {
        if (NBTHelper.getShort("cooldown", ist) > 0)
            NBTHelper.setShort("cooldown", ist, NBTHelper.getShort("cooldown", ist) - 1);
    }

    private boolean canTeleport(World world, int x, int y, int z) {
        if (!world.isAirBlock(x, y + 1, z) || !world.isAirBlock(x, y + 2, z))
            return false;
        return true;
    }

    private void teleportPlayer(World world, int x, int y, int z, EntityPlayer player) {
        player.setPositionAndUpdate(x + 0.5, y + 0.875, z + 0.5);
        player.playSound("mob.endermen.portal", 1.0f, 1.0f);
        for (int particles = 0; particles < 2; particles++) {
            world.spawnParticle("portal", player.posX, player.posY, player.posZ, world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
        }
        return;
    }

    @Override
    public void addInformation(ItemStack eye, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        //added spacing here to make sure the tooltips didn't come out with weird punctuation derps.
        String phrase = "Currently bound to ";
        String position = "";
        if (eye.getTagCompound() != null && eye.getTagCompound().getInteger("dimensionID") != Integer.valueOf(getWorld(par2EntityPlayer))) {
            phrase = "Out of range!";
        } else if (eye.getTagCompound() != null && eye.getTagCompound().hasKey("nodeX" + getWorld(par2EntityPlayer)) && eye.getTagCompound().hasKey("nodeY" + getWorld(par2EntityPlayer)) && eye.getTagCompound().hasKey("nodeZ" + getWorld(par2EntityPlayer))) {
            position = "X: " + eye.getTagCompound().getInteger("nodeX" + getWorld(par2EntityPlayer)) + " Y: " + eye.getTagCompound().getInteger("nodeY" + getWorld(par2EntityPlayer)) + " Z: " + eye.getTagCompound().getInteger("nodeZ" + getWorld(par2EntityPlayer));
        } else {
            position = "nowhere.";
        }
        this.formatTooltip(ImmutableMap.of("phrase", phrase, "position", position), eye, list);
    }

    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float xOff, float yOff, float zOff) {
        // if right clicking on a wraith node, bind the eye to that wraith node.
        if ((ist.getTagCompound() == null || !(ist.getTagCompound().hasKey("dimensionID"))) && ContentHelper.areBlocksEqual(world.getBlock(x, y, z), ContentHandler.getBlock(Names.wraith_node))) {
            setWraithNode(ist, x, y, z, Integer.valueOf(getWorld(player)), player);

            player.playSound("mob.endermen.portal", 1.0f, 1.0f);
            for (int particles = 0; particles < 12; particles++) {
                world.spawnParticle("portal", x + world.rand.nextDouble(), y + world.rand.nextDouble(), z + world.rand.nextDouble(), world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
            }
            setCooldown(ist);
            return true;
        } else {
            return false;

        }
    }

    public void setWraithNode(ItemStack eye, int x, int y, int z, int dimensionID, EntityPlayer player) {
        NBTHelper.setInteger("nodeX" + getWorld(player), eye, x);
        NBTHelper.setInteger("nodeY" + getWorld(player), eye, y);
        NBTHelper.setInteger("nodeZ" + getWorld(player), eye, z);
        NBTHelper.setInteger("dimensionID", eye, dimensionID);
    }

    public String getWorld(EntityPlayer player) {
        return Integer.valueOf(player.worldObj.provider.dimensionId).toString();
    }
}
