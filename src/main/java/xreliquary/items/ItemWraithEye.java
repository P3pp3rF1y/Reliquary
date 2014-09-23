package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.NBTHelper;

import java.util.List;

@ContentInit
public class ItemWraithEye extends ItemSalamanderEye {

    public ItemWraithEye() {
        super(Names.wraith_eye);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.wraith_eye);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public IIcon getIcon(ItemStack itemStack, int renderPass) {
        return this.itemIcon;
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
        // make sure to call the Salamander's Eye update function or it loses
        // its inheritance.
        super.onUpdate(ist, world, e, i, f);

        // checks to see if cooldown variable > 0 and decrements if true, each
        // tick.
        decrementCooldown(ist);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack eye, World world, EntityPlayer player) {
        if (NBTHelper.getShort("cooldown", eye) > 0)
            return eye;

        if (eye.getTagCompound() != null && eye.getTagCompound().getInteger("dimensionID") != Integer.valueOf(getWorld(player))) {
            if (!world.isRemote) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "Out of range!"));
            }
        } else if (eye.getTagCompound() != null && ContentHelper.areBlocksEqual(world.getBlock(eye.getTagCompound().getInteger("nodeX" + getWorld(player)), eye.getTagCompound().getInteger("nodeY" + getWorld(player)), eye.getTagCompound().getInteger("nodeZ" + getWorld(player))), ContentHandler.getBlock(Names.wraith_node))) {

            if (canTeleport(world, eye.getTagCompound().getInteger("nodeX" + getWorld(player)), eye.getTagCompound().getInteger("nodeY" + getWorld(player)), eye.getTagCompound().getInteger("nodeZ" + getWorld(player)))) {
                if (InventoryHelper.consumeItem(new ItemStack(Items.ender_pearl), player)) {
                    teleportPlayer(world, eye.getTagCompound().getInteger("nodeX" + getWorld(player)), eye.getTagCompound().getInteger("nodeY" + getWorld(player)), eye.getTagCompound().getInteger("nodeZ" + getWorld(player)), player);
                    setCooldown(eye);
                }
            }
        } else if (eye.getTagCompound() != null && eye.getTagCompound().hasKey("dimensionID")) {
            //eye.setTagCompound(null);
            eye.getTagCompound().removeTag("dimensionID");
            eye.getTagCompound().removeTag("nodeX");
            eye.getTagCompound().removeTag("nodeY");
            eye.getTagCompound().removeTag("nodeZ");
            eye.getTagCompound().removeTag("cooldown");
            if (!world.isRemote) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "Node dosen't exist!"));
            } else {
                player.playSound("mob.endermen.death", 1.0f, 1.0f);
            }
        }
        return eye;
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