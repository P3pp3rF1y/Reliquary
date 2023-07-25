package reliquary.items;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import reliquary.entities.SpecialSnowballEntity;
import reliquary.reference.Settings;
import reliquary.util.NBTHelper;
import reliquary.util.TooltipBuilder;

import javax.annotation.Nullable;

public class IceMagusRodItem extends ToggleableItem {
	public static final DustParticleOptions ICE_PARTICLE = new DustParticleOptions(new Vector3f(99 / 255F, 196 / 255F, 253 / 255F), 1);
	private static final String SNOWBALLS_TAG = "snowballs";

	public IceMagusRodItem() {
		super(new Properties().stacksTo(1).setNoRepair());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack rod, @Nullable Level world, TooltipBuilder tooltipBuilder) {
		tooltipBuilder.charge(this, ".tooltip2", getSnowballCharge(rod));
		if (isEnabled(rod)) {
			tooltipBuilder.absorbActive(Items.SNOWBALL.getName(new ItemStack(Items.SNOWBALL)).getString());
		} else {
			tooltipBuilder.absorb();
		}
	}

	private static int getSnowballCharge(ItemStack rod) {
		return NBTHelper.getInt(SNOWBALLS_TAG, rod);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	private int getSnowballCap() {
		return this instanceof GlacialStaffItem ? Settings.COMMON.items.glacialStaff.snowballLimit.get() : Settings.COMMON.items.iceMagusRod.snowballLimit.get();
	}

	int getSnowballCost() {
		return this instanceof GlacialStaffItem ? Settings.COMMON.items.glacialStaff.snowballCost.get() : Settings.COMMON.items.iceMagusRod.snowballCost.get();
	}

	private int getSnowballWorth() {
		return this instanceof GlacialStaffItem ? Settings.COMMON.items.glacialStaff.snowballWorth.get() : Settings.COMMON.items.iceMagusRod.snowballWorth.get();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		//acts as a cooldown.
		player.swing(hand);
		if (!player.isShiftKeyDown() && (getSnowballCharge(stack) >= getSnowballCost() || player.isCreative())) {
			level.playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
			SpecialSnowballEntity snowball = new SpecialSnowballEntity(level, player, this instanceof GlacialStaffItem);
			snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.4F, 1.0F);
			level.addFreshEntity(snowball);
			if (!player.isCreative()) {
				NBTHelper.putInt(SNOWBALLS_TAG, stack, getSnowballCharge(stack) - getSnowballCost());
			}
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		}
		return super.use(level, player, hand);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public void inventoryTick(ItemStack rod, Level world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isClientSide || world.getGameTime() % 10 != 0 || !(entity instanceof Player)) {
			return;
		}
		if (isEnabled(rod)) {
			int snowCharge = getSnowballCharge(rod);
			consumeAndCharge((Player) entity, getSnowballCap() - snowCharge, getSnowballWorth(), Items.SNOWBALL, 16,
					chargeToAdd -> NBTHelper.putInt(SNOWBALLS_TAG, rod, snowCharge + chargeToAdd));
		}
	}
}
