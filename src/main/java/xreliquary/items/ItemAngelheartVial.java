package xreliquary.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.entities.EntityGlowingWater;
import xreliquary.init.ContentHandler;
import xreliquary.init.XRInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

@XRInit
public class ItemAngelheartVial extends ItemBase {

	public ItemAngelheartVial() {
		super(Reference.MOD_ID, Names.angelheart_vial);
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
        return new ItemStack(ContentHandler.getItem(Names.condensed_potion), 1, Reference.EMPTY_VIAL_META);
    }

    //returns an empty vial when used in crafting recipes.
    @Override
    public boolean hasContainerItem(ItemStack ist) {
        return true;
    }
    //event driven item, see client events.
}
