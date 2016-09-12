package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
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
			@SuppressWarnings("NullableProblems")
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return stack.getMetadata() == 0 ? 1 : 0;
			}
		});
		this.addPropertyOverride(new ResourceLocation("potion"), new IItemPropertyGetter() {
			@SuppressWarnings("NullableProblems")
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return ModItems.magazine.isPotionAttached(stack) ? 1 : 0;
			}
		});

	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
		//taking tooltip from bullets as it's the same text for magazines
		if(stack.getMetadata() >= 2)
			LanguageHelper.formatTooltip("item." + Names.Items.BULLET + "_" + stack.getMetadata() + ".tooltip", null, list);
		if(isPotionAttached(stack))
			PotionUtils.addPotionTooltip(stack, list, 1F);
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item." + Names.Items.MAGAZINE + "_" + ist.getItemDamage();
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> subItems) {
		for(int meta = 0; meta <= 9; meta++) {
			subItems.add(new ItemStack(item, 1, meta));
		}

		//similar to bullets adding just basic magazines with potions here even though all magazine types can have potions attached
		for(PotionEssence essence : Settings.Potions.uniquePotionEssences) {
			ItemStack neutralMagazineWithPotion = new ItemStack(ModItems.magazine, 1, 1);
			PotionUtils.appendEffects(neutralMagazineWithPotion, XRPotionHelper.changeDuration(essence.getEffects(), 0.2F));

			subItems.add(neutralMagazineWithPotion);
		}
	}

	@SuppressWarnings("ConstantConditions")
	public boolean isPotionAttached(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects");
	}
}
