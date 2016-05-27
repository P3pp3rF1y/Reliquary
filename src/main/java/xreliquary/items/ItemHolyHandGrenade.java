package xreliquary.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.entities.EntityHolyHandGrenade;
import xreliquary.reference.Names;

public class ItemHolyHandGrenade extends ItemBase {

	public ItemHolyHandGrenade() {
		super(Names.holy_hand_grenade);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		canRepair = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if(world.isRemote)
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);

		if(!player.capabilities.isCreativeMode) {
			--stack.stackSize;
		}

		world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		EntityHolyHandGrenade grenade = new EntityHolyHandGrenade(world, player);
		grenade.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.9F, 1.0F);
		world.spawnEntityInWorld(grenade);

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

}
