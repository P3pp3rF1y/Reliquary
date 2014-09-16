package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;

import java.util.List;

@ContentInit
public class ItemInfernalTear extends ItemBase {

	public ItemInfernalTear() {
		super(Names.infernal_tear);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.hasSubtypes = true;
	}

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey("itemID");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        NBTTagCompound tag = stack.getTagCompound();
        String holds;
        if (tag == null || !tag.hasKey("itemID") || new ItemStack((Item) Item.itemRegistry.getObject(tag.getString("itemID")), 1, tag.getShort("itemMeta")).getItem() == null) {
            holds = "nothing";
        } else {
            ItemStack contents = new ItemStack((Item) Item.itemRegistry.getObject(tag.getString("itemID")), 1, tag.getShort("itemMeta"));
            String itemName = contents.getDisplayName();
            holds = "" + EnumChatFormatting.YELLOW + itemName;
        }
        this.formatTooltip(ImmutableMap.of("holds", holds), stack, list);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(stack.getTagCompound().hasKey("itemID")) {
            stack.getTagCompound().removeTag("itemID");
            stack.getTagCompound().removeTag("itemMeta");
        } else {
            ItemStack target = getTargetItem(player.inventory);
            stack.getTagCompound().setString("itemID", ContentHelper.getIdent(target.getItem()));
            stack.getTagCompound().setShort("itemMeta", (short) target.getItemDamage());
        }

        player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
        return stack;
    }

    // TODO: Do something on shift right-click?
    // TODO: Do the click-on-chest thing with the Infernal Tear.

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        return false;
    }

    public ItemStack getTargetItem(IInventory inventory) {
        ItemStack targetItem = null;
        int itemQuantity = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack ist = inventory.getStackInSlot(slot);
            if (ist == null) {
                continue;
            }
            if (ContentHelper.getIdent(ist.getItem()).equals(ContentHelper.getIdent(this))) {
                continue;
            }
            if (ist.getMaxStackSize() == 1) {
                continue;
            }
            if (ist.getTagCompound() != null) {
                continue;
            }
            if (getQuantityInInventory(ist, inventory) > itemQuantity) {
                itemQuantity = getQuantityInInventory(ist, inventory);
                inventory.decrStackSize(slot, 1);
                targetItem = ist.copy();
            }
        }
        return targetItem;
    }

    public int getQuantityInInventory(ItemStack ist, IInventory inventory) {
        int itemQuantity = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) {
                continue;
            }
            if (ist.isItemEqual(stack)) {
                itemQuantity += stack.stackSize;
            }
        }
        return itemQuantity;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int i, boolean f) {
        if (world.isRemote)
            return;
        EntityPlayer player;
        if (entity instanceof EntityPlayer) {
            player = (EntityPlayer) entity;
        } else
            return;
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("itemID")) {
            if (findAndConsume(stack, player)) {
                String ident = stack.getTagCompound().getString("itemID");
                if(Alkahestry.getRegistry().containsKey(ident)) {
                    AlkahestRecipe recipe = Alkahestry.getRegistry().get(ident);
                    if(recipe.yield != 32 && recipe.cost != 4) {
                        player.addExperience((int) (Math.ceil((double) ((double) recipe.yield / (double) recipe.cost) * 125)));
                    }
                }
            }
        }
    }

    private boolean findAndConsume(ItemStack stack, EntityPlayer player) {
        int suggestedSlot = -1;
        int count = 0;
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null) {
                continue;
            }
            if (ContentHelper.getIdent(player.inventory.mainInventory[slot].getItem()).equals(stack.getTagCompound().getString("itemID")) && player.inventory.mainInventory[slot].getItemDamage() == stack.getTagCompound().getShort("itemMeta")) {
                count += player.inventory.mainInventory[slot].stackSize;
                if(suggestedSlot == -1) {
                    suggestedSlot = slot;
                }
            }
        }
        if(suggestedSlot != -1 && count > 64) {
            player.inventory.decrStackSize(suggestedSlot, 1);
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list)  {
        ItemStack stack = new ItemStack(item, 1);
        stack.setTagCompound(new NBTTagCompound());
        list.add(stack);
    }

}
