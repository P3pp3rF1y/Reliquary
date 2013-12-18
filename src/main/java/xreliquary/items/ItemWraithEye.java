package xreliquary.items;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.blocks.XRBlocks;
import xreliquary.common.TimeKeeperHandler;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWraithEye extends ItemSalamanderEye {

    protected ItemWraithEye(int par1) {
        super(par1);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        canRepair = false;
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setUnlocalizedName(Names.WRAITH_EYE_NAME);
    }

    @SideOnly(Side.CLIENT)
    private Icon iconOverlay[];

    @SideOnly(Side.CLIENT)
    private Icon iconBase;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        iconOverlay = new Icon[4];
        iconBase = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase()
                + ":" + Names.WRAITH_EYE_NAME);        
        for (int i = 0; i < 4; i++) {
            iconOverlay[i] = iconRegister.registerIcon(Reference.MOD_ID
                    .toLowerCase() + ":" + Names.WRAITH_EYE_OVERLAY_NAME + i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public Icon getIcon(ItemStack itemStack, int renderPass) {
        if (renderPass != 1)
            return iconBase;
        else {
            int i = TimeKeeperHandler.getTime();
            i %= 80;
            if (i < 7) {
                // i == 0, open, i == 3, closed.
                if (i > 2) {
                    i = 6 - i;
                }
                return iconOverlay[i];
            } else
                // base - completely open
                return iconBase;
        }
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean f) {
        //make sure to call the Salamander's Eye update function or it loses its inheritance.
        super.onUpdate(ist, world, e, i, f);
        
        //checks to see if cooldown variable > 0 and decrements if true, each tick.
        decrementCooldown(ist);        
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack eye, World world, EntityPlayer par2EntityPlayer) {
    	checkForOldVersions(eye);
    	
        if (this.getShort("cooldown", eye) > 0)
            return eye;
        
        if (eye.getTagCompound() != null && eye.getTagCompound().getInteger("dimensionID") != Integer.valueOf(getWorld(par2EntityPlayer))) {
        	if(!world.isRemote) {
        		par2EntityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.DARK_RED + "Out of range!"));
        	}
        } else if (eye.getTagCompound() != null && world.getBlockId(eye.getTagCompound().getInteger("nodeX" + getWorld(par2EntityPlayer)), eye.getTagCompound().getInteger("nodeY" + getWorld(par2EntityPlayer)), eye.getTagCompound().getInteger("nodeZ" + getWorld(par2EntityPlayer))) == XRBlocks.wraithNode.blockID) {
            
        	if (canTeleport(world, eye.getTagCompound().getInteger("nodeX" + getWorld(par2EntityPlayer)), eye.getTagCompound().getInteger("nodeY" + getWorld(par2EntityPlayer)), eye.getTagCompound().getInteger("nodeZ" + getWorld(par2EntityPlayer)))) {

        		if (findAndRemoveEnderPearl(par2EntityPlayer)) {
                    teleportPlayer(world, eye.getTagCompound().getInteger("nodeX" + getWorld(par2EntityPlayer)), eye.getTagCompound().getInteger("nodeY" + getWorld(par2EntityPlayer)), eye.getTagCompound().getInteger("nodeZ" + getWorld(par2EntityPlayer)), par2EntityPlayer);
                    setCooldown(eye);
                }
            }
        } else if(eye.getTagCompound() != null && eye.getTagCompound().hasKey("dimensionID")) {
    		eye.setTagCompound(null);
        	if(!world.isRemote)
        		par2EntityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.DARK_RED + "Node dosen't exist!"));
        	else
        		par2EntityPlayer.playSound("mob.endermen.death", 1.0f, 1.0f);
        }
        return eye;
    }

    private void setCooldown(ItemStack ist) {
        this.setShort("cooldown", ist, (short) 20);        
    }

    private void decrementCooldown(ItemStack ist) {
        if (this.getShort("cooldown", ist) > 0)
            this.setShort("cooldown", ist, this.getShort("cooldown", ist) - 1);
    }
    
    private boolean findAndRemoveEnderPearl(EntityPlayer player) {
        if (player.capabilities.isCreativeMode)
            return true;
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null) {
                continue;
            }
            if (player.inventory.mainInventory[slot].getItem() == Item.enderPearl) {
                player.inventory.decrStackSize(slot, 1);
                return true;
            }
        }
        return false;
    }

    private boolean canTeleport(World world, int x, int y, int z) {
        if (!world.isAirBlock(x, y + 1, z) || !world.isAirBlock(x, y + 2, z)  )
            return false;
        return true;
    }
    private void teleportPlayer(World world, int x, int y, int z, EntityPlayer player) {
        player.setPositionAndUpdate(x, y, z);
        player.playSound("mob.endermen.portal", 1.0f, 1.0f);
        for (int particles = 0; particles < 2; particles++) {
            world.spawnParticle("portal", player.posX, player.posY, player.posZ, world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
        } 
        return;
    }

    @Override
    public void addInformation(ItemStack eye, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add("Right clicking a Wraith Node binds to it");
        par3List.add("allowing you to return to it at will,");
        par3List.add("for the cost of an Ender Pearl.");
        String details = EnumChatFormatting.DARK_PURPLE + "Currently bound to ";
    	checkForOldVersions(eye);
        if (eye.getTagCompound() != null && eye.getTagCompound().getInteger("dimensionID") != Integer.valueOf(getWorld(par2EntityPlayer))) {
        	details = EnumChatFormatting.DARK_PURPLE + "Out of range!";
        } else if(eye.getTagCompound() != null && eye.getTagCompound().hasKey("nodeX" + getWorld(par2EntityPlayer)) && eye.getTagCompound().hasKey("nodeY" + getWorld(par2EntityPlayer)) && eye.getTagCompound().hasKey("nodeZ" + getWorld(par2EntityPlayer))) {
            details += "X: " + eye.getTagCompound().getInteger("nodeX" + getWorld(par2EntityPlayer)) + " Y: "
                    + eye.getTagCompound().getInteger("nodeY" + getWorld(par2EntityPlayer)) + " Z: " + eye.getTagCompound().getInteger("nodeZ" + getWorld(par2EntityPlayer));
        } else {
        	details += "nowhere.";
        }
        par3List.add(details);
    }

    @Override
    public boolean onItemUse(ItemStack ist, EntityPlayer player, World world,
            int x, int y, int z, int side, float xOff, float yOff, float zOff) {
        // if right clicking on a wraith node, bind the eye to that wraith node.
        if ((ist.getTagCompound() == null || !(ist.getTagCompound().hasKey("dimensionID"))) && world.getBlockId(x, y, z) == XRBlocks.wraithNode.blockID) {
            setWraithNode(ist, x, y, z, Integer.valueOf(getWorld(player)), player);
            
            player.playSound("mob.endermen.portal", 1.0f, 1.0f);
            for (int particles = 0; particles < 12; particles++) {
                world.spawnParticle("portal", x + world.rand.nextDouble(), y + world.rand.nextDouble(), z + world.rand.nextDouble(), world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
            } 
            
            return true;
        } else {
            // onItemRightClick(ist, world, player);
            return false;
        }
    }

    public void setWraithNode(ItemStack eye, int x, int y, int z, int dimensionID, EntityPlayer player) {
        setInteger("nodeX" + getWorld(player), eye, x);
        setInteger("nodeY" + getWorld(player), eye, y);
        setInteger("nodeZ" + getWorld(player), eye, z);
        setInteger("dimensionID", eye, dimensionID);
    }

    public String getWorld(EntityPlayer player) {
        return Integer.valueOf(player.worldObj.provider.dimensionId).toString();
    }
    
    private ItemStack checkForOldVersions(ItemStack eye) {
        if(eye.getTagCompound() != null && !eye.getTagCompound().hasKey("dimensionID")) {
        	Iterator keys = eye.getTagCompound().getTags().iterator();
        	NBTTagCompound dummy = new NBTTagCompound();
        	eye.setTagCompound(new NBTTagCompound());
        	
            while(keys.hasNext()) {
            	NBTBase base = (NBTBase) keys.next();
            	if(base.getName().contains("nodeX")) {
            		dummy.setTag("nodeX", base.copy());
            		eye.getTagCompound().setInteger(base.getName(), (int) dummy.getShort("nodeX"));
            		eye.getTagCompound().setInteger("dimensionID", Integer.valueOf(base.getName().charAt(base.getName().indexOf("X"))));
            	} else if(base.getName().contains("nodeY")) {
            		dummy.setTag("nodeY", base.copy());
            		eye.getTagCompound().setInteger(base.getName(), (int) dummy.getShort("nodeY"));
            	} else if(base.getName().contains("nodeZ")) {
            		dummy.setTag("nodeZ", base.copy());
            		eye.getTagCompound().setInteger(base.getName(), (int) dummy.getShort("nodeZ"));
            	}
            }
        }
        return eye;
    }
}