package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.Reliquary;
import xreliquary.entities.EntityKrakenSlime;
import xreliquary.reference.Names;

public class ItemSerpentStaff extends ItemBase {

	public ItemSerpentStaff() {
		super(Names.serpent_staff);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(200);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D() {
		return true;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.BLOCK;
	}

	@Override
	public void onUsingTick(ItemStack item, EntityLivingBase entity, int count) {
		if(entity.worldObj.isRemote || !(entity instanceof EntityPlayer) || count % 3 != 0)
			return;

		EntityPlayer player = (EntityPlayer) entity;

		player.worldObj.playSound(null, player.getPosition(), SoundEvents.entity_arrow_shoot, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		player.worldObj.spawnEntityInWorld(new EntityKrakenSlime(player.worldObj, player));
		item.damageItem(1, player);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		//drain effect
		int drain = player.worldObj.rand.nextInt(4);
		if(entity.attackEntityFrom(DamageSource.causePlayerDamage(player), drain)) {
			player.heal(drain);
			stack.damageItem(1, player);
		}
		return false;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 11;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		player.setActiveHand(hand);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

}
