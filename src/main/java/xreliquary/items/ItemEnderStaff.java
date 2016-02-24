package xreliquary.items;


import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.entities.EntityEnderStaffProjectile;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

import java.util.List;

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
        return EnumRarity.EPIC;
    }

    private int getEnderStaffPearlCost() {
        return Settings.EnderStaff.enderPearlCastCost;
    }
    private int getEnderStaffNodeWarpCost() { return Settings.EnderStaff.enderPearlNodeWarpCost; }
    private int getEnderPearlWorth() {
        return Settings.EnderStaff.enderPearlWorth;
    }
    private int getEnderPearlLimit() { return Settings.EnderStaff.enderPearlLimit; }
    private int getNodeWarpCastTime() {
        return Settings.EnderStaff.nodeWarpCastTime;
    }


    @Override
    public float getDigSpeed(ItemStack ist, IBlockState blockState) {
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
            player.worldObj.spawnParticle( EnumParticleTypes.PORTAL, player.posX, player.posY, player.posZ, player.worldObj.rand.nextGaussian(), player.worldObj.rand.nextGaussian(), player.worldObj.rand.nextGaussian());
        }
        if (unadjustedCount == 1) {
            doWraithNodeWarpCheck(ist, player.worldObj, player);
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack ist) {
        return EnumAction.BLOCK;
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
                if (NBTHelper.getInteger("ender_pearls", ist) < getEnderStaffPearlCost() && !player.capabilities.isCreativeMode)
                    return ist;
                player.worldObj.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
                if  (!player.worldObj.isRemote) {
                    player.worldObj.spawnEntityInWorld(new EntityEnderStaffProjectile(player.worldObj, player, !getMode(ist).equals("long_cast")));
                    if (!player.capabilities.isCreativeMode)
                        NBTHelper.setInteger("ender_pearls", ist, NBTHelper.getInteger("ender_pearls", ist) - getEnderStaffPearlCost());
                }
                //setCooldown(ist);
            } else {
                player.setItemInUse(ist, getMaxItemUseDuration(ist));
            }
        }
        return super.onItemRightClick(ist, world, player);
    }

    private ItemStack doWraithNodeWarpCheck(ItemStack stack, World world, EntityPlayer player) {
        if (NBTHelper.getInteger("ender_pearls", stack) < getEnderStaffNodeWarpCost() && !player.capabilities.isCreativeMode)
            return stack;

        if (stack.getTagCompound() != null && stack.getTagCompound().getInteger("dimensionID") != Integer.valueOf(getWorld(player))) {
            if (!world.isRemote) {
                player.addChatComponentMessage( new ChatComponentText( EnumChatFormatting.DARK_RED + "Out of range!" ) );
            }
        } else if (stack.getTagCompound() != null && RegistryHelper.blocksEqual(world.getBlockState(new BlockPos(stack.getTagCompound().getInteger("nodeX" + getWorld(player)), stack.getTagCompound().getInteger("nodeY" + getWorld(player)), stack.getTagCompound().getInteger("nodeZ" + getWorld(player)))).getBlock(), ModBlocks.wraithNode)) {
            if (canTeleport(world, stack.getTagCompound().getInteger("nodeX" + getWorld(player)), stack.getTagCompound().getInteger("nodeY" + getWorld(player)), stack.getTagCompound().getInteger("nodeZ" + getWorld(player)))) {
                teleportPlayer(world, stack.getTagCompound().getInteger("nodeX" + getWorld(player)), stack.getTagCompound().getInteger("nodeY" + getWorld(player)), stack.getTagCompound().getInteger("nodeZ" + getWorld(player)), player);
                //setCooldown(ist);
                if (!player.capabilities.isCreativeMode)
                    NBTHelper.setInteger("ender_pearls", stack, NBTHelper.getInteger("ender_pearls", stack) - getEnderStaffNodeWarpCost());
            }
        } else if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("dimensionID")) {
            stack.getTagCompound().removeTag("dimensionID");
            stack.getTagCompound().removeTag("nodeX");
            stack.getTagCompound().removeTag("nodeY");
            stack.getTagCompound().removeTag("nodeZ");
            stack.getTagCompound().removeTag("cooldown");
            if (!world.isRemote) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "Node dosen't exist!"));
            } else {
                player.playSound( "mob.endermen.death", 1.0f, 1.0f );
            }
        }
        return stack;
    }

    private boolean canTeleport(World world, int x, int y, int z) {
        if (!world.isAirBlock(new BlockPos(x, y + 1, z)) || !world.isAirBlock(new BlockPos(x, y + 2, z)))
            return false;
        return true;
    }

    private void teleportPlayer(World world, int x, int y, int z, EntityPlayer player) {
        player.setPositionAndUpdate(x + 0.5, y + 0.875, z + 0.5);
        player.playSound("mob.endermen.portal", 1.0f, 1.0f);
        for (int particles = 0; particles < 2; particles++) {
            world.spawnParticle(EnumParticleTypes.PORTAL, player.posX, player.posY, player.posZ, world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
        }
        return;
    }

    @Override
    public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean flag) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
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
    public boolean onItemUse(ItemStack ist, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        // if right clicking on a wraith node, bind the eye to that wraith node.
        if ((ist.getTagCompound() == null || !(ist.getTagCompound().hasKey("dimensionID"))) && RegistryHelper.blocksEqual(world.getBlockState(pos).getBlock(), ModBlocks.wraithNode)) {
            setWraithNode(ist, pos, Integer.valueOf(getWorld(player)), player);

            player.playSound("mob.endermen.portal", 1.0f, 1.0f);
            for (int particles = 0; particles < 12; particles++) {
                world.spawnParticle(EnumParticleTypes.PORTAL, pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble(), pos.getZ() + world.rand.nextDouble(), world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
            }
            //setCooldown(ist);
            return true;
        } else {
            return false;

        }
    }

    public void setWraithNode(ItemStack eye, BlockPos pos, int dimensionID, EntityPlayer player) {
        NBTHelper.setInteger("nodeX" + getWorld(player), eye, pos.getX());
        NBTHelper.setInteger("nodeY" + getWorld(player), eye, pos.getY());
        NBTHelper.setInteger("nodeZ" + getWorld(player), eye, pos.getZ());
        NBTHelper.setInteger("dimensionID", eye, dimensionID);
    }

    public String getWorld(EntityPlayer player) {
        return Integer.valueOf(player.worldObj.provider.getDimensionId()).toString();
    }
}
