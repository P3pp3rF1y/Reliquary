package xreliquary.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.entities.HolyHandGrenadeEntity;

public class HolyHandGrenadeItem extends ItemBase {

	public HolyHandGrenadeItem() {
		super(new Properties());
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}


	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player,  Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(world.isRemote) {
			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}

		if(!player.isCreative()) {
			stack.shrink(1);
		}

		world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
		HolyHandGrenadeEntity grenade = new HolyHandGrenadeEntity(world, player, stack.getDisplayName().getString());
		grenade.setDirectionAndMovement(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.9F, 1.0F);
		world.addEntity(grenade);

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

}
