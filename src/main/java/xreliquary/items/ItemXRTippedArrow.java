package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.entities.EntityXRTippedArrow;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemXRTippedArrow extends ItemArrow {

	public ItemXRTippedArrow() {
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setUnlocalizedName(Names.Items.TIPPED_ARROW);
	}

	@Nonnull
	@Override
	public EntityArrow createArrow(@Nonnull World world, @Nonnull ItemStack stack, EntityLivingBase shooter) {
		EntityXRTippedArrow entitytippedarrow = new EntityXRTippedArrow(world, shooter);
		entitytippedarrow.setPotionEffect(stack);
		return entitytippedarrow;
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		return LanguageHelper.getLocalization("item." + Names.Items.TIPPED_ARROW + ".name");
	}

	@Override
	public void getSubItems(@Nonnull Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for(PotionEssence essence : Settings.Potions.uniquePotionEssences) {
			ItemStack tippedArrow = new ItemStack(ModItems.tippedArrow);
			PotionUtils.appendEffects(tippedArrow, XRPotionHelper.changeDuration(essence.getEffects(), 0.125F));

			subItems.add(tippedArrow);
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		PotionUtils.addPotionTooltip(stack, tooltip, 1F);
	}
}
