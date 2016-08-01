package xreliquary.items;

import baubles.api.BaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.common.gui.GUIHandler;
import xreliquary.init.ModSounds;
import xreliquary.reference.Names;

public class ItemMobCharmBelt extends ItemBauble {
	public ItemMobCharmBelt() {
		super(Names.mob_charm_belt);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	public BaubleType getBaubleType(ItemStack stack) {
		return BaubleType.BELT;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {

	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if(player.isSneaking())
			return new ActionResult<>(EnumActionResult.PASS, stack);

		player.openGui(Reliquary.INSTANCE, GUIHandler.MOB_CHARM_BELT, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
}
