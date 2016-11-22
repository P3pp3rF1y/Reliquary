package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemPotionEssence extends ItemBase {

	public ItemPotionEssence() {
		super(Names.Items.POTION_ESSENCE);
		this.setMaxStackSize(64);
		this.setHasSubtypes(true);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	public void getSubItems(@Nonnull Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {

		for(PotionEssence essence : Settings.Potions.uniquePotionEssences) {
			ItemStack essenceItem = new ItemStack(ModItems.potionEssence, 1);
			essenceItem.setTagCompound(essence.writeToNBT());

			subItems.add(essenceItem);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
		PotionEssence essence = new PotionEssence(stack.getTagCompound());
		XRPotionHelper.addPotionInfo(essence, list);
	}
}
