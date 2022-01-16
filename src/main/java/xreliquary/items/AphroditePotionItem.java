package xreliquary.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xreliquary.entities.potion.AphroditePotionEntity;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;

public class AphroditePotionItem extends ItemBase {

	public AphroditePotionItem() {
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
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (world.isClientSide) {
			return new InteractionResultHolder<>(InteractionResult.PASS, stack);
		}
		if (!player.isCreative()) {
			stack.shrink(1);
		}
		world.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		AphroditePotionEntity aphroditePotion = new AphroditePotionEntity(world, player);
		aphroditePotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.7F, 1.0F);
		world.addFreshEntity(aphroditePotion);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}
}
