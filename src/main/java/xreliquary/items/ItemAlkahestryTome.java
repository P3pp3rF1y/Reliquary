package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.InventoryHelper;
import lib.enderwizards.sandstone.util.LanguageHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityAltar;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.List;

@ContentInit
public class ItemAlkahestryTome extends ItemToggleable {

    public ItemAlkahestryTome() {
        super(Names.alkahestry_tome);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
        this.canRepair = false;
        this.hasSubtypes = true;
        this.setContainerItem(this);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        ItemStack newStack = super.onItemRightClick(stack, world, player);
        if(player.isSneaking())
            return newStack;

        player.playSound(Reference.BOOK_SOUND, 1.0f, 1.0f);
        player.openGui(Reliquary.INSTANCE, 0, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        return stack;
    }

//    @Override
//    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
//        return world.getTileEntity(x, y, z) instanceof TileEntityAltar;
//    }

    @Override
    public void onUpdate(ItemStack ist, World world, Entity entity, int i, boolean f) {
        if (world.isRemote)
            return;
        if(!this.isEnabled(ist))
            return;

        EntityPlayer player;
        if (entity instanceof EntityPlayer) {
            player = (EntityPlayer) entity;
        } else {
            return;
        }
        //redstone handler
        if(NBTHelper.getInteger("redstone", ist) + 9 <= getRedstoneLimit() && InventoryHelper.consumeItem(Blocks.redstone_block, player)) {
            NBTHelper.setInteger("redstone", ist, NBTHelper.getInteger("redstone", ist) + 9);
        } else if(NBTHelper.getInteger("redstone", ist) + 1 <= getRedstoneLimit() && InventoryHelper.consumeItem(Items.redstone, player)) {
            NBTHelper.setInteger("redstone", ist, NBTHelper.getInteger("redstone", ist) + 1);
        }

        //glowstone handler
        if(NBTHelper.getInteger("redstone", ist) + 4 <= getRedstoneLimit() && InventoryHelper.consumeItem(Blocks.glowstone, player)) {
            NBTHelper.setInteger("redstone", ist, NBTHelper.getInteger("redstone", ist) + 4);
        } else if(NBTHelper.getInteger("redstone", ist) + 1 <= getRedstoneLimit() && InventoryHelper.consumeItem(Items.glowstone_dust, player)) {
            NBTHelper.setInteger("redstone", ist, NBTHelper.getInteger("redstone", ist) + 1);
        }
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
        stack = null;
        return false;
    }

    @Override
    public void addInformation(ItemStack ist, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            return;
        this.formatTooltip(ImmutableMap.of("redstoneAmount", String.valueOf(NBTHelper.getInteger("redstone", ist)), "redstoneLimit", String.valueOf(getRedstoneLimit())), ist, list);

        if(this.isEnabled(ist))
            LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", EnumChatFormatting.RED + Items.redstone.getItemStackDisplayName(new ItemStack(Items.redstone))), ist, list);
        LanguageHelper.formatTooltip("tooltip.absorb", null, ist, list);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack copy = stack.copy();

        copy.stackSize = 1;
        return copy;
    }

    private int getRedstoneLimit() {
        return Reliquary.CONFIG.getInt(Names.alkahestry_tome, "redstone_limit");
    }
}
