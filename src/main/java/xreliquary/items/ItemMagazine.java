package xreliquary.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMagazine extends ItemBase {

	public ItemMagazine() {
		super(Names.magazine);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(64);
		canRepair = false;
		this.setHasSubtypes(true);
		this.addPropertyOverride(new ResourceLocation("empty"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return stack.getMetadata() == 0 ? 1 : 0;
			}
		});
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
		if(stack.getMetadata() < 2)
			return;
		//taking tooltip from bullets as it's the same text for magazines
		LanguageHelper.formatTooltip("item." + Names.bullet + "_" + stack.getMetadata() + ".tooltip", null, list);
	}

	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item." + Names.magazine + "_" + ist.getItemDamage();
	}

	@Override
	public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> subItems) {
		for(int meta = 0; meta <= 9; meta++) {
			subItems.add(new ItemStack(item, 1, meta));
		}
	}

}
