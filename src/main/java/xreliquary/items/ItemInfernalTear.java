package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import lib.enderwizards.sandstone.util.LanguageHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.RegistryHelper;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;

import java.util.List;

public class ItemInfernalTear extends ItemToggleable {

    public ItemInfernalTear() {
        super(Names.infernal_tear);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean flag) {
        if (world.isRemote || !isEnabled(ist))
            return;
        if (!(e instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer)e;
        String ident = ist.getTagCompound().getString("itemID");

        if (Settings.AlkahestryTome.craftingRecipes.containsKey(ident)) {
            AlkahestCraftRecipe recipe = Settings.AlkahestryTome.craftingRecipes.get(ident);
            // You need above Cobblestone level to get XP.
            if (recipe.yield != 32 && recipe.cost != 4) {
                if(InventoryHelper.consumeItem(this.getStackFromTear(ist), player)) {
                    player.addExperience((int) (Math.round(((1d / (double) recipe.cost) / (double) recipe.yield) * 150)));
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            return;
        this.formatTooltip(null, stack, list);

        if (this.getStackFromTear(stack) == null) {
            LanguageHelper.formatTooltip("tooltip.infernal_tear.tear_empty", null, null, list);
        } else {
            ItemStack contents = this.getStackFromTear(stack);
            String itemName = contents.getDisplayName();
            String holds = itemName;

            LanguageHelper.formatTooltip("tooltip.tear", ImmutableMap.of("item", itemName), stack, list);

            if(this.isEnabled(stack)) {
                LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", EnumChatFormatting.YELLOW + holds), stack, list);
            }
            list.add(LanguageHelper.getLocalization("tooltip.absorb"));
            list.add(LanguageHelper.getLocalization("tooltip.infernal_tear.absorb_unset"));
        }
    }

    public ItemStack getStackFromTear(ItemStack tear) {
        //something awful happened. We either lost data or this is an invalid tear by some other means. Either way, not great.
        if (NBTHelper.getString("itemID", tear).equals(""))
            return null;
        return new ItemStack(Item.itemRegistry.getObject(new ResourceLocation(NBTHelper.getString("itemID", tear))), NBTHelper.getInteger( "itemQuantity", tear ));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        ItemStack newStack = super.onItemRightClick(stack, world, player);
        if(player.isSneaking() && !this.isEnabled(stack))
            return newStack;

        //empty the tear if player is not sneaking and the tear is not empty
        NBTTagCompound tag = newStack.getTagCompound();
        if (!player.isSneaking() && getStackFromTear(newStack) != null) {
            tag.removeTag("itemID");
            tag.removeTag("itemMeta");
            tag.removeTag("enabled");

            return newStack;
        }

        //nothing more to do with a filled tear here
        if (getStackFromTear(newStack) != null) {
            return newStack;
        }

        //if user is sneaking or just enabled the tear, let's fill it
        if (player.isSneaking() || !this.isEnabled(newStack)) {
            ItemStack returnStack = this.buildTear(newStack, player, player.inventory);
            if (returnStack != null)
                return returnStack;
        }

        //by this time the tear is still empty and there wasn't anything to put in it
        // so let's disable it if it got enabled
        if (this.isEnabled(newStack))
            this.toggleEnabled(newStack);
        return newStack;
    }

    private ItemStack buildTear(ItemStack stack, EntityPlayer player, IInventory inventory) {
        ItemStack tear = new ItemStack(this, 1);

        ItemStack target = getTargetAlkahestItem(stack, inventory);
        if(target == null)
            return null;
        NBTHelper.setString("itemID", tear,  RegistryHelper.getItemRegistryName(target.getItem()));

        if(Settings.InfernalTear.absorbWhenCreated)
            NBTHelper.setBoolean("enabled", stack, true);

        return tear;
    }

    protected void addTearToInventory(EntityPlayer player, ItemStack stack) {
        if (!player.inventory.addItemStackToInventory(stack)) {
            EntityItem entity = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, stack);
            player.worldObj.spawnEntityInWorld(entity);
        }
    }

    //TODO: possibly figure out a better way to pass the condition to inventory helper
    public static ItemStack getTargetAlkahestItem(ItemStack self, IInventory inventory) {
        ItemStack targetItem = null;
        int itemQuantity = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) {
                continue;
            }
            if (self.isItemEqual(stack)) {
                continue;
            }
            if (stack.getMaxStackSize() == 1) {
                continue;
            }
            if (stack.getTagCompound() != null) {
                continue;
            }
            if (!Settings.AlkahestryTome.craftingRecipes.containsKey(RegistryHelper.getItemRegistryName(stack.getItem()))) {
                continue;
            }
            if (InventoryHelper.getItemQuantity(stack, inventory) > itemQuantity) {
                itemQuantity = InventoryHelper.getItemQuantity(stack, inventory);
                targetItem = stack.copy();
            }
        }
        inventory.markDirty();
        return targetItem;
    }


}