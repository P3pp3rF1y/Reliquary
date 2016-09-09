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
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return stack.getMetadata() == 0 ? 1 : 0;
			}
		});
		canRepair = false;
	}

	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item." + Names.Items.BULLET + "_" + ist.getItemDamage();
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
		if (stack.getMetadata() < 2)
			return;
		LanguageHelper.formatTooltip("item." + Names.Items.BULLET + "_" + stack.getMetadata() + ".tooltip", null, list);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs creativeTabs, List<ItemStack> subItems) {
		for(int meta=0; meta<=9;meta++) {
			subItems.add(new ItemStack(item, 1, meta));
		}
	}
}
