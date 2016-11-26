package xreliquary.items;

import net.minecraftforge.fml.common.Optional;
import xreliquary.reference.Compatibility;

@Optional.Interface(iface = "baubles.api.IBauble", modid = Compatibility.MOD_ID.BAUBLES, striprefs = true)
abstract class ItemBauble extends ItemBase /*implements IBauble*/ {

	ItemBauble(String langName) {
		super(langName);
	}

/* TODO readd with baubles
	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public abstract BaubleType getBaubleType(ItemStack stack);

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public abstract void onWornTick(ItemStack stack, EntityLivingBase player);

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public void onEquipped(ItemStack stack, EntityLivingBase player) {
	}

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public void onUnequipped(ItemStack stack, EntityLivingBase player) {
		// Nothing?
	}

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public boolean canEquip(ItemStack stack, EntityLivingBase player) {
		return true;
	}

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
		return true;
	}
*/
}
