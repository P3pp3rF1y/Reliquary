package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import lib.enderwizards.sandstone.util.LanguageHelper;
import net.minecraft.block.Block;
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
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.entities.EntityEnderStaffProjectile;
import xreliquary.lib.Names;
import lib.enderwizards.sandstone.util.NBTHelper;

import java.util.List;

@ContentInit
public class ItemEnderStaff extends ItemToggleable {

    public ItemEnderStaff() {
        super(Names.ender_staff);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
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
    private int getEnderStaffNodeWarpCost() { return Reliquary.CONFIG.getInt(Names.ender_staff, "ender_pearl_node_warp_cost"); }
    private int getEnderPearlWorth() {
        return Reliquary.CONFIG.getInt(Names.ender_staff, "ender_pearl_worth");
    }
    private int getEnderPearlLimit() {        return Reliquary.CONFIG.getInt(Names.ender_staff, "ender_pearl_limit");    }
    private int getNodeWarpCastTime() {
        return Reliquary.CONFIG.getInt(Names.ender_staff, "node_warp_cast_time");
    }


    @Override
    public float getDigSpeed(ItemStack ist, Block block, int meta) {
        //temporarily sets the item damage to 1, this prevents it from being rapid fired due to swing animations during an attempt to break blocks, hopefully.
        if (ist.getItemDamage() == 0)
            ist.setItemDamage(1);
        return 0F;
    }


    public String getMode(ItemStack ist) {
        if (NBTHelper.getString("mode", ist).equals("")) {
            setMode(ist, "cast");
        }
        return NBTHelper.getString("mode", ist);
    }

    public void setMode(ItemStack ist, String s) {
        NBTHelper.setString("mode", ist, s);
    }

    public void cycleMode(ItemStack ist) {
        if (getMode(ist).equals("cast"))
            setMode(ist, "long_cast");
        else if (getMode(ist).equals("long_cast"))
            setMode(ist, "node_warp");
        else
            setMode(ist, "cast");
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack ist) {
        if (entityLiving.worldObj.isRemote)
            return true;
        if (!(entityLiving instanceof EntityPlayer))
            return true;
        EntityPlayer player = (EntityPlayer)entityLiving;
        if (player.isSneaking()) {
            cycleMode(ist);
        }
        return false;
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
        if (world.isRemote)
            return;

        // checks to see if cooldown variable > 0 and decrements if true, each tick.
       //decrementCooldown(ist);

        if (!this.isEnabled(ist))
            return;
        EntityPlayer player = null;
        if (e instanceof EntityPlayer) {
            player = (EntityPlayer) e;
        }
        if (player == null)
            return;
        if (NBTHelper.getInteger("ender_pearls", ist) + getEnderPearlWorth() <= getEnderPearlLimit()) {
            if (InventoryHelper.consumeItem(new ItemStack(Items.ender_pearl), player)) {
                NBTHelper.setInteger("ender_pearls", ist, NBTHelper.getInteger("ender_pearls", ist) + getEnderPearlWorth());
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
            if (getMode(ist).equals("cast") || getMode(ist).equals("long_cast")) {
                if (player.isSwingInProgress)
                    return ist;
                player.swingItem();
                if (NBTHelper.getInteger("ender_pearls", ist) < getEnderStaffPearlCost())
                    return ist;
                player.worldObj.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
                if  (!player.worldObj.isRemote) {
                    player.worldObj.spawnEntityInWorld(new EntityEnderStaffProjectile(player.worldObj, player, !getMode(ist).equals("long_cast")));
                    NBTHelper.setInteger("ender_pearls", ist, NBTHelper.getInteger("ender_pearls", ist) - getEnderStaffPearlCost());
                }
                //setCooldown(ist);
            } else {
                player.setItemInUse(ist, getMaxItemUseDuration(ist));
            }
        }
        return super.onItemRightClick(ist, world, player);
    }

    private ItemStack doWraithNodeWarpCheck(ItemStack ist, World world, EntityPlayer player) {
        //if (getCooldown(ist) > 0)
        //    return ist;
        if (NBTHelper.getInteger("ender_pearls", ist) < getEnderStaffNodeWarpCost())
            return ist;

        if (ist.getTagCompound() != null && ist.getTagCompound().getInteger("dimensionID") != Integer.valueOf(getWorld(player))) {
            if (!world.isRemote) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "Out of range!"));
            }
        } else if (ist.getTagCompound() != null && ContentHelper.areBlocksEqual(world.getBlock(ist.getTagCompound().getInteger("nodeX" + getWorld(player)), ist.getTagCompound().getInteger("nodeY" + getWorld(player)), ist.getTagCompound().getInteger("nodeZ" + getWorld(player))), ContentHandler.getBlock(Names.wraith_node))) {
            if (canTeleport(world, ist.getTagCompound().getInteger("nodeX" + getWorld(player)), ist.getTagCompound().getInteger("nodeY" + getWorld(player)), ist.getTagCompound().getInteger("nodeZ" + getWorld(player)))) {
                teleportPlayer(world, ist.getTagCompound().getInteger("nodeX" + getWorld(player)), ist.getTagCompound().getInteger("nodeY" + getWorld(player)), ist.getTagCompound().getInteger("nodeZ" + getWorld(player)), player);
                //setCooldown(ist);
                NBTHelper.setInteger("ender_pearls", ist, NBTHelper.getInteger("ender_pearls", ist) - getEnderStaffNodeWarpCost());
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
    public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean flag) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            return;
        //added spacing here to make sure the tooltips didn't come out with weird punctuation derps.
        String charge = Integer.toString(NBTHelper.getInteger("ender_pearls", ist));
        String phrase = "Currently bound to ";
        String position = "";
        if (ist.getTagCompound() != null && ist.getTagCompound().getInteger("dimensionID") != Integer.valueOf(getWorld(player))) {
            phrase = "Out of range!";
        } else if (ist.getTagCompound() != null && ist.getTagCompound().hasKey("nodeX" + getWorld(player)) && ist.getTagCompound().hasKey("nodeY" + getWorld(player)) && ist.getTagCompound().hasKey("nodeZ" + getWorld(player))) {
            position = "X: " + ist.getTagCompound().getInteger("nodeX" + getWorld(player)) + " Y: " + ist.getTagCompound().getInteger("nodeY" + getWorld(player)) + " Z: " + ist.getTagCompound().getInteger("nodeZ" + getWorld(player));
        } else {
            position = "nowhere.";
        }
        this.formatTooltip(ImmutableMap.of("phrase", phrase, "position", position, "charge", charge), ist, list);
        if(this.isEnabled(ist))
            LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", EnumChatFormatting.GREEN + Items.ender_pearl.getItemStackDisplayName(new ItemStack(Items.ender_pearl))), ist, list);
        LanguageHelper.formatTooltip("tooltip.absorb", null, ist, list);
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
            //setCooldown(ist);
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
