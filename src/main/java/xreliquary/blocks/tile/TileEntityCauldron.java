package xreliquary.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCauldron extends TileEntity {

	public TileEntityCauldron() {
		//TODO initialization stuff, as needed.
	}

	@Override
	public void updateEntity() {
		//TODO check for items, max of one essence per potion batch (alternatively could do this with block activation method)
        //TODO push any additional essences back out or at the nearest player (convenience), if being handled here instead of in block activation method.
        //TODO boil water (check for heat source) and brew the potion, may need custom particles, or could use vanilla bubbles/smoke (steam?)
        //TODO change the color or brightness of the water to signal the potion is finished, maybe throw off particles based on what kind of potion it is
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
        //TODO remember the itemstack (essence or potion type), the amount of water, maybe?
        //TODO remember if it's cooking, the amount of time, etc.

	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
        //TODO see above
	}

}
