package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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


// TODO: Look into the equivalent of an onItemPickup event.
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
        if (stack.getTagCompound().hasKey("itemID")) {
            stack.getTagCompound().removeTag("itemID");
            stack.getTagCompound().removeTag("itemMeta");
        } else {
            ItemStack target = InventoryHelper.getTargetItem(stack, player.inventory);
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
            Item item = ContentHandler.getItem(stack.getTagCompound().getString("itemID"));
            ItemStack newStack = new ItemStack(item, 0, (int) stack.getTagCompound().getShort("itemMeta"));
            if (InventoryHelper.consumeItem(newStack, player, newStack.getMaxStackSize())) {
                giveExperience(stack, player);
            }
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

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        ItemStack stack = new ItemStack(item, 1);
        stack.setTagCompound(new NBTTagCompound());
        list.add(stack);
    }

}
