package xreliquary.items;

import com.google.common.collect.HashMultimap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.XRPotionHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Xeno on 11/8/2014.
 */
public class ItemPotionEssence extends ItemBase {

    public ItemPotionEssence() {
        super(Names.potion_essence);
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack ist, int renderPass) {
        return getColor(ist);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {

        for(PotionEssence essence : Settings.Potions.uniquePotionEssences) {
            ItemStack essenceItem = new ItemStack(ModItems.potionEssence, 1);
            essenceItem.setTagCompound(essence.writeToNBT());

            subItems.add(essenceItem);
        }
    }

    public int getColor(ItemStack itemStack) {
        //basically we're just using vanillas right now. This is hilarious in comparison to the old method, which is a mile long.
        return PotionHelper.calcPotionLiquidColor(new PotionEssence(itemStack.getTagCompound()).getEffects());
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean flag) {
        PotionEssence essence = new PotionEssence(ist.getTagCompound());
        XRPotionHelper.addPotionInfo(essence, list);
    }
}
