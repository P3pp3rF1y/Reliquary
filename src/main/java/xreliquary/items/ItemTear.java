package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import lib.enderwizards.sandstone.util.LanguageHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Reference;

import java.util.List;

public class ItemTear extends ItemToggleable {

    @SideOnly(Side.CLIENT)
    protected IIcon emptyIcon;

    protected boolean useAmount = false;
    protected boolean absorbByDefault = true;

    public ItemTear(String langName) {
        super(langName);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return stack.getItemDamage() == 1 ? 1 : 16;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if(meta == 0)
            return emptyIcon;
        return itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        emptyIcon = register.registerIcon(Reference.MOD_ID + ":" + this.getUnlocalizedName().substring(5) + "_empty");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getUnlocalizedName(ItemStack stack) {
        if(stack.getItemDamage() == 0)
            return super.getUnlocalizedName(stack) + "_empty";
        return super.getUnlocalizedName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        this.formatTooltip(null, stack, list);

        NBTTagCompound tag = stack.getTagCompound();
        if (stack.getItemDamage() == 0 || this.getStackFromTear(stack) == null) {
            list.add(LanguageHelper.getLocalization("tooltip.tear_empty"));
        } else {
            ItemStack contents = this.getStackFromTear(stack);
            String itemName = contents.getDisplayName();
            String holds = itemName;

            if(tag.hasKey("itemQuantity")) {
                LanguageHelper.formatTooltip("tooltip.tear_quantity", ImmutableMap.of("item", itemName, "amount", String.valueOf(tag.getShort("itemQuantity"))), stack, list);
            } else {
                LanguageHelper.formatTooltip("tooltip.tear", ImmutableMap.of("item", itemName), stack, list);
            }

            if(this.isEnabled(stack)) {
                LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", EnumChatFormatting.YELLOW + holds), stack, list);
                list.add(LanguageHelper.getLocalization("tooltip.absorb_tear"));
            }
            list.add(LanguageHelper.getLocalization("tooltip.absorb"));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return stack.getItemDamage() == 1 && this.isEnabled(stack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        ItemStack newStack = super.onItemRightClick(stack, world, player);
        if(player.isSneaking() && stack.getItemDamage() == 1)
            return newStack;

        NBTTagCompound tag = stack.getTagCompound();
        if (stack.getItemDamage() == 1) {
            if(this.shouldEmpty(stack, player)) {
                tag.removeTag("itemID");
                tag.removeTag("itemMeta");

                if(tag.hasKey("itemQuantity"))
                    tag.removeTag("itemQuantity");
                stack.setItemDamage(0);
            }
        } else {
            ItemStack returnStack = this.buildTear(stack, player, player.inventory, player.inventory.mainInventory.length);
            if(returnStack != null)
                return returnStack;
        }

        player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
        return stack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

        if (world.getTileEntity(x, y, z) instanceof IInventory) {
            IInventory inventory = (IInventory) world.getTileEntity(x, y, z);

            if(stack.getItemDamage() == 1) {
                if(this.shouldEmpty(stack, player, inventory, 0)) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, this.newItemStack());
                }
                return true;
            }

            ItemStack returnStack = this.buildTear(stack, player, inventory, 0);
            if(returnStack != null)
                player.inventory.setInventorySlotContents(player.inventory.currentItem, returnStack);
        }
        return false;
    }


    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int i, boolean f) {
        if (world.isRemote || stack.getItemDamage() == 0 || !this.isEnabled(stack))
            return;

        EntityPlayer player;
        if (entity instanceof EntityPlayer) {
            player = (EntityPlayer) entity;
        } else {
            return;
        }

        ItemStack contents = this.getStackFromTear(stack);
        if (InventoryHelper.consumeItem(contents, player, contents.getMaxStackSize())) {
            this.onAbsorb(stack, player);
        }
    }

    protected ItemStack buildTear(ItemStack stack, EntityPlayer player, IInventory inventory, int limit) {
        ItemStack tear = this.newItemStack();
        NBTTagCompound tT = tear.getTagCompound();

        ItemStack target = InventoryHelper.getTargetItem(stack, inventory, false);
        if(target == null)
            return null;
        tT.setString("itemID", ContentHelper.getIdent(target.getItem()));
        tT.setShort("itemMeta", (short) target.getItemDamage());

        if(useAmount) {
            int quantity = InventoryHelper.getItemQuantity(target, inventory, limit > 0 ? limit : 0);
            InventoryHelper.removeItem(target, inventory, quantity, limit > 0 ? limit : 0);
            tT.setShort("itemQuantity", (short) quantity);
        } else {
            InventoryHelper.removeItem(target, inventory, 1, limit > 0 ? limit : 0);
        }

        if(this.absorbByDefault)
            tT.setBoolean("enabled", true);
        tear.setItemDamage(1);

        --stack.stackSize;
        if (stack.stackSize == 0) {
            return tear;
        } else {
            addTearToInventory(player, tear);
        }
        return null;
    }

    protected void addTearToInventory(EntityPlayer player, ItemStack stack) {
        if (!player.inventory.addItemStackToInventory(stack)) {
            EntityItem entity = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, stack);
            player.worldObj.spawnEntityInWorld(entity);
        }
    }

    public ItemStack getStackFromTear(ItemStack tear) {
        NBTTagCompound tag = tear.getTagCompound();
        if(tag == null)
            return null;
        return new ItemStack((Item) Item.itemRegistry.getObject(tag.getString("itemID")), 1, tag.getShort("itemMeta"));
    }

    protected boolean shouldEmpty(ItemStack stack, EntityPlayer player) {
        return this.shouldEmpty(stack, player, player.inventory, player.inventory.mainInventory.length);
    }

    protected boolean shouldEmpty(ItemStack stack, EntityPlayer player, IInventory inventory, int limit) {
        return true;
    }

    protected void onAbsorb(ItemStack stack, EntityPlayer player) {
    }
}
