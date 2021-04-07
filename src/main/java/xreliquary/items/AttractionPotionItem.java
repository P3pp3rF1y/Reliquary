package xreliquary.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import xreliquary.entities.potion.AttractionPotionEntity;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;

public class AttractionPotionItem extends ItemBase {

	public AttractionPotionItem() {
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
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) {
			return new ActionResult<>(ActionResultType.PASS, stack);
		}
		if (!player.isCreative()) {
			stack.shrink(1);
		}
		world.playSound(null, player.getPosition(), SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
		AttractionPotionEntity attractionPotion = new AttractionPotionEntity(world, player);
		attractionPotion.func_234612_a_(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.7F, 1.0F);
		world.addEntity(attractionPotion);
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}
}
