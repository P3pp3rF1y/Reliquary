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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;

import java.util.List;

// TODO: Poke a texture artist about making an empty form of the Infernal Tear.
@ContentInit
public class ItemInfernalTear extends ItemToggleable {

    public ItemInfernalTear() {
        super(Names.infernal_tear);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
        this.hasSubtypes = true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey("itemID") && this.isEnabled(stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
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
        ItemStack newStack = super.onItemRightClick(stack, world, player);
        if(player.isSneaking())
            return newStack;

        NBTTagCompound tag = stack.getTagCompound();
        if (tag.hasKey("itemID")) {
            tag.removeTag("itemID");
            tag.removeTag("itemMeta");
        } else {
            ItemStack target = InventoryHelper.getTargetItem(stack, player.inventory);
            tag.setString("itemID", ContentHelper.getIdent(target.getItem()));
            tag.setShort("itemMeta", (short) target.getItemDamage());
            tag.setBoolean("enabled", true);
        }

        player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
        return stack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking())
            return false;

        NBTTagCompound tag = stack.getTagCompound();
        if (tag.hasKey("itemID"))
            return false;

        if (world.getTileEntity(x, y, z) instanceof IInventory) {
            IInventory inventory = (IInventory) world.getTileEntity(x, y, z);

            ItemStack target = InventoryHelper.getTargetItem(stack, inventory);

            if(target != null) {
                tag.setString("itemID", ContentHelper.getIdent(target.getItem()));
                tag.setShort("itemMeta", (short) target.getItemDamage());
                tag.setBoolean("enabled", true);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int i, boolean f) {
        if (world.isRemote)
            return;
        if(stack.getTagCompound() == null || !stack.getTagCompound().hasKey("itemID"))
            return;
        if(!this.isEnabled(stack))
            return;

        EntityPlayer player;
        if (entity instanceof EntityPlayer) {
            player = (EntityPlayer) entity;
        } else {
            return;
        }

        Item item = ContentHandler.getItem(stack.getTagCompound().getString("itemID"));
        ItemStack newStack = new ItemStack(item, 0, (int) stack.getTagCompound().getShort("itemMeta"));
        if (InventoryHelper.consumeItem(newStack, player, newStack.getMaxStackSize())) {
            giveExperience(stack, player);
        }
    }

    public void giveExperience(ItemStack stack, EntityPlayer player) {
        String ident = stack.getTagCompound().getString("itemID");
        if (Alkahestry.getRegistry().containsKey(ident)) {
            AlkahestRecipe recipe = Alkahestry.getRegistry().get(ident);
            // You need above Cobblestone level to get XP.
            if (recipe.yield != 32 && recipe.cost != 4) {
                player.addExperience((int) (Math.round(((double) (1d / (double) recipe.cost) / (double) recipe.yield) * 150)));
            }
        }
    }
}