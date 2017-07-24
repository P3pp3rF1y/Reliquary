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
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBullet extends ItemBase {

	// 0 = Empty, 1 = Neutral, 2 = Exorcism, 3 = Blaze
	// 4 = Ender, 5 = Concussive, 6 = Buster, 7 = Seeker
	// 8 = Sand, 9 = Storm

	public ItemBullet() {
		super(Names.Items.BULLET);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(64);
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
				return ModItems.bullet.isPotionAttached(stack) ? 1 : 0;
			}
		});
		canRepair = false;
	}

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item." + Names.Items.BULLET + "_" + ist.getItemDamage();
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		if(stack.getMetadata() >= 2)
			LanguageHelper.formatTooltip("item." + Names.Items.BULLET + "_" + stack.getMetadata() + ".tooltip", null, tooltip);
		XRPotionHelper.addPotionTooltip(stack, tooltip);
	}

	private boolean isPotionAttached(ItemStack stack) {
		//noinspection ConstantConditions
		return !XRPotionHelper.getPotionEffectsFromStack(stack).isEmpty();
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs creativeTabs, @Nonnull NonNullList<ItemStack> subItems) {
		for(int meta = 0; meta <= 9; meta++) {
			subItems.add(new ItemStack(this, 1, meta));
		}

		//adding just basic bullets with potions here even though all bullet types can have potions attached
		for(PotionEssence essence : Settings.Potions.uniquePotionEssences) {
			ItemStack neutralBulletWithPotion = new ItemStack(ModItems.bullet, 1, 1);
			XRPotionHelper.addPotionEffectsToStack(neutralBulletWithPotion, XRPotionHelper.changePotionEffectsDuration(essence.getEffects(), 0.2F));

			subItems.add(neutralBulletWithPotion);
		}
	}
}
