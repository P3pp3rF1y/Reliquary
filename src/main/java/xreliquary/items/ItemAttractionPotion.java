package xreliquary.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.entities.potion.EntityAttractionPotion;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;

import javax.annotation.Nonnull;

public class ItemAttractionPotion extends ItemBase {

	public ItemAttractionPotion() {
		super(Names.Items.ATTRACTION_POTION);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		canRepair = false;
	}

	@Override
	public boolean hasContainerItem(ItemStack ist) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack getContainerItem(@Nonnull ItemStack stack) {
		return new ItemStack(ModItems.potion, 1, 0);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);
		if(world.isRemote)
			return new ActionResult<>(EnumActionResult.PASS, stack);
		if(!player.capabilities.isCreativeMode) {
			stack.shrink(1);
		}
		world.playSound(null, player.getPosition(), SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		EntityAttractionPotion attractionPotion = new EntityAttractionPotion(world, player);
		attractionPotion.shoot(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.7F, 1.0F);
		world.spawnEntity(attractionPotion);
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

}
