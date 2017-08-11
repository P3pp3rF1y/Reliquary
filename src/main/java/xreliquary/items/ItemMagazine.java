package xreliquary.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.util.LanguageHelper;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemMagazine extends ItemBase {

	public ItemMagazine() {
		super(Names.Items.MAGAZINE);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(64);
		canRepair = false;
		this.setHasSubtypes(true);
		this.addPropertyOverride(new ResourceLocation("empty"), new IItemPropertyGetter() {

			@Override
			@SideOnly(Side.CLIENT)
			public float apply(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityIn) {
				return stack.getMetadata() == 0 ? 1 : 0;
			}
		});
		this.addPropertyOverride(new ResourceLocation("potion"), new IItemPropertyGetter() {

			@Override
			@SideOnly(Side.CLIENT)
			public float apply(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityIn) {
				return isPotionAttached(stack) ? 1 : 0;
			}
		});

	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		//taking tooltip from bullets as it's the same text for magazines
		if(stack.getMetadata() >= 2)
			LanguageHelper.formatTooltip("item." + Names.Items.BULLET + "_" + stack.getMetadata() + ".tooltip", null, tooltip);
		XRPotionHelper.addPotionTooltip(stack, tooltip);
	}

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item." + Names.Items.MAGAZINE + "_" + ist.getItemDamage();
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		if (!isInCreativeTab(tab))
			return;

		for(int meta = 0; meta <= 9; meta++) {
			subItems.add(new ItemStack(this, 1, meta));
		}

		//similar to bullets adding just basic magazines with potions here even though all magazine types can have potions attached
		for(PotionEssence essence : PotionMap.uniquePotionEssences) {
			ItemStack neutralMagazineWithPotion = new ItemStack(this, 1, 1);
			XRPotionHelper.addPotionEffectsToStack(neutralMagazineWithPotion, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F));

			subItems.add(neutralMagazineWithPotion);
		}
	}

	private boolean isPotionAttached(ItemStack stack) {
		//noinspection ConstantConditions
		return !XRPotionHelper.getPotionEffectsFromStack(stack).isEmpty();
	}
}
