package xreliquary.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemPotionEssence extends ItemBase {

	public ItemPotionEssence() {
		super(Names.Items.POTION_ESSENCE);
		this.setMaxStackSize(64);
		this.setHasSubtypes(true);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		if (!isInCreativeTab(tab))
			return;

		for(PotionEssence essence : PotionMap.uniquePotionEssences) {
			ItemStack essenceItem = new ItemStack(this, 1);
			XRPotionHelper.addPotionEffectsToStack(essenceItem, essence.getEffects());

			subItems.add(essenceItem);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		XRPotionHelper.addPotionTooltip(stack, tooltip);
	}
}
