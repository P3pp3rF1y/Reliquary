package xreliquary.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import xreliquary.entities.potion.ThrownXRPotionEntity;

public class ThrownPotionItem extends PotionItemBase {
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) {
			return new ActionResult<>(ActionResultType.PASS, stack);
		}
		ThrownXRPotionEntity e = new ThrownXRPotionEntity(world, player, stack);
		e.setDirectionAndMovement(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.5F, 1.0F);

		if (!player.isCreative()) {
			stack.shrink(1);
		}
		world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
		world.addEntity(e);
		return new ActionResult<>(ActionResultType.PASS, stack);
	}
}
