package xreliquary.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;

public class ItemAngelheartVial extends ItemBase {

	public ItemAngelheartVial() {
		super(Names.Items.ANGELHEART_VIAL);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		canRepair = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack ist) {
		return new ItemStack(ModItems.potion, 1, 0);
	}

	// returns an empty vial when used in crafting recipes.
	@Override
	public boolean hasContainerItem(ItemStack ist) {
		return true;
	}
	// event driven item, see client events.
}
