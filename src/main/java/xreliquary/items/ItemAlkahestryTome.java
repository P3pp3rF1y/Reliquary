package xreliquary.items;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import lib.enderwizards.sandstone.init.ContentInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ContentInit
public class ItemAlkahestryTome extends ItemBase {

	public ItemAlkahestryTome() {
		super(Names.alkahestry_tome);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(Reliquary.CONFIG.getInt(Names.alkahestry_tome, "redstoneLimit") + 1);
		this.setMaxStackSize(1);
		this.canRepair = false;
		this.hasSubtypes = true;
		this.setContainerItem(this);
	}
	
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
		this.formatTooltip(ImmutableMap.of("redstoneAmount", String.valueOf((Reliquary.CONFIG.getInt(Names.alkahestry_tome, "redstoneLimit") - stack.getItemDamage())), "redstoneLimit", String.valueOf(Reliquary.CONFIG.getInt(Names.alkahestry_tome, "redstoneLimit"))), stack, list);
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
		tomeStack.setItemDamage(Reliquary.CONFIG.getInt(Names.alkahestry_tome, "redstoneLimit"));
		list.add(tomeStack);
	}

}
