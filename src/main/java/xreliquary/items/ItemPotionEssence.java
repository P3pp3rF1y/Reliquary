package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.XRPotionHelper;

import java.util.List;

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
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {

		for(PotionEssence essence : Settings.Potions.uniquePotionEssences) {
			ItemStack essenceItem = new ItemStack(ModItems.potionEssence, 1);
			essenceItem.setTagCompound(essence.writeToNBT());

			subItems.add(essenceItem);
		}
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack ist, EntityPlayer player, List list, boolean flag) {
		PotionEssence essence = new PotionEssence(ist.getTagCompound());
		XRPotionHelper.addPotionInfo(essence, list);
	}
}
