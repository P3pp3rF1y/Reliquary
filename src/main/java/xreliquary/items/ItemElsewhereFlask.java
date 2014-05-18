package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.init.XRInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.List;

@XRInit
public class ItemElsewhereFlask extends ItemBase {

	public ItemElsewhereFlask() {
		super(Reference.MOD_ID, Names.elsewhere_flask);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		this.canRepair = false;
		this.setContainerItem(this);
	}

    //this is tricky because tooltips have limited real estate.
    //we need to know what's in the flask though.. so we use shortened names.

    //dig, run, jump, hit, breath, fire, heal, cure [from panacea], regen, armor, vanish, vision

    //potion uses are measured in "sips". you always attempt to drink one of every potion in the flask.
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		player.playSound(Reference.BOOK_SOUND, 1.0f, 1.0f);
		player.openGui(Reliquary.INSTANCE, 0, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		return stack;
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack ist) {
		ist = null;
		return false;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		this.formatTooltip(ImmutableMap.of("redstoneAmount", String.valueOf((Reliquary.PROXY.tomeRedstoneLimit - stack.getItemDamage())), "redstoneLimit", String.valueOf(Reliquary.PROXY.tomeRedstoneLimit)), stack, list);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.epic;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		ItemStack copy = stack.copy();
		
		copy.stackSize = 1;
		return copy;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tabs, List list) {
		ItemStack tomeStack = new ItemStack(item, 1, 0);
		tomeStack.setItemDamage(Reliquary.PROXY.tomeRedstoneLimit);
		list.add(tomeStack);
	}

}
