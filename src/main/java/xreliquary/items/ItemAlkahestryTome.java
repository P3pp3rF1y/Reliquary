package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;

import java.util.List;
import java.util.Map;

public class ItemAlkahestryTome extends ItemToggleable {
    public ItemAlkahestryTome() {
        super(Names.alkahestry_tome);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
        this.setMaxDamage(getChargeLimit() + 1); //to always display damage bar
        this.canRepair = false;
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

        for (Map.Entry<String, AlkahestChargeRecipe> entry : Settings.AlkahestryTome.chargingRecipes.entrySet()) {
            AlkahestChargeRecipe recipe = entry.getValue();
            if (NBTHelper.getInteger("charge", ist) + recipe.charge <= getChargeLimit() && InventoryHelper.consumeItem(recipe.item, player) ) {
                NBTHelper.setInteger("charge", ist, NBTHelper.getInteger("charge", ist) + recipe.charge);
            }
        }

        ist.setItemDamage(ist.getMaxDamage() - NBTHelper.getInteger("charge", ist));
    }

    @Override
    public void addInformation(ItemStack ist, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            return;
        this.formatTooltip(ImmutableMap.of("chargeAmount", String.valueOf(NBTHelper.getInteger("charge", ist)), "chargeLimit", String.valueOf(getChargeLimit())), ist, list);

        if(this.isEnabled(ist))
            LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", EnumChatFormatting.RED + Items.redstone.getItemStackDisplayName(Settings.AlkahestryTome.baseItem)), ist, list);
        LanguageHelper.formatTooltip("tooltip.absorb", null, ist, list);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {

        ItemStack stack = new ItemStack(ModItems.alkahestryTome);
        stack.setItemDamage(ModItems.alkahestryTome.getMaxDamage());
        subItems.add(stack);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack copy = stack.copy();

        copy.stackSize = 1;
        return copy;
    }

    private static int getChargeLimit() {
        return Settings.AlkahestryTome.chargeLimit;
    }

}
