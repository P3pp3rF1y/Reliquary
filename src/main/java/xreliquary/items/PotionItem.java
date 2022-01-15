package xreliquary.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.util.potions.XRPotionHelper;

public class PotionItem extends PotionItemBase {
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack par1ItemStack) {
		return 16;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!XRPotionHelper.getPotionEffectsFromStack(stack).isEmpty()) {
			player.setActiveHand(hand);
			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		} else {
			return new ActionResult<>(ActionResultType.PASS, stack);
		}
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
		if (!(entity instanceof PlayerEntity) || world.isRemote) {
			return stack;
		}

		PlayerEntity player = (PlayerEntity) entity;

		XRPotionHelper.applyEffectsToEntity(XRPotionHelper.getPotionEffectsFromStack(stack), player, null, player);

		if (!player.isCreative()) {
			stack.shrink(1);
			ItemStack emptyVial = new ItemStack(ModItems.EMPTY_POTION_VIAL.get());
			if (stack.getCount() <= 0) {
				return emptyVial;
			} else {
				player.inventory.addItemStackToInventory(emptyVial);
			}
		}
		return stack;
	}

}
