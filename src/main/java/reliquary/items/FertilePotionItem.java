package reliquary.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import reliquary.entities.potion.FertilePotionEntity;
import reliquary.init.ModItems;
import reliquary.reference.Settings;

public class FertilePotionItem extends ItemBase {
	public FertilePotionItem() {
		super(new Properties(), Settings.COMMON.disable.disablePotions::get);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return new ItemStack(ModItems.EMPTY_POTION_VIAL.get());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (level.isClientSide) {
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		}
		if (!player.isCreative()) {
			stack.shrink(1);
		}
		level.playSound(null, player.blockPosition(), SoundEvents.SPLASH_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));

		FertilePotionEntity fertilePotion = new FertilePotionEntity(level, player);
		fertilePotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.7F, 1.0F);
		level.addFreshEntity(fertilePotion);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}
}
