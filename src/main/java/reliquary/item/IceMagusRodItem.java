package reliquary.item;

import net.minecraft.core.HolderLookup;
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
import org.joml.Vector3f;
import reliquary.entity.SpecialSnowball;
import reliquary.init.ModDataComponents;
import reliquary.reference.Config;
import reliquary.util.TooltipBuilder;

import javax.annotation.Nullable;

public class IceMagusRodItem extends ChargeableItem {
	public static final DustParticleOptions ICE_PARTICLE = new DustParticleOptions(new Vector3f(99 / 255F, 196 / 255F, 253 / 255F), 1);

	public IceMagusRodItem() {
		super(new Properties().stacksTo(1).setNoRepair().rarity(Rarity.EPIC));
	}

	@Override
	protected void addMoreInformation(ItemStack rod, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		tooltipBuilder.charge(this, ".tooltip2", getSnowballs(rod));
		if (isEnabled(rod)) {
			tooltipBuilder.absorbActive(Items.SNOWBALL.getName(new ItemStack(Items.SNOWBALL)).getString());
		} else {
			tooltipBuilder.absorb();
		}
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	private int getSnowballLimit() {
		return this instanceof GlacialStaffItem ? Config.COMMON.items.glacialStaff.snowballLimit.get() : Config.COMMON.items.iceMagusRod.snowballLimit.get();
	}

	int getSnowballCost() {
		return this instanceof GlacialStaffItem ? Config.COMMON.items.glacialStaff.snowballCost.get() : Config.COMMON.items.iceMagusRod.snowballCost.get();
	}

	private int getSnowballWorth() {
		return this instanceof GlacialStaffItem ? Config.COMMON.items.glacialStaff.snowballWorth.get() : Config.COMMON.items.iceMagusRod.snowballWorth.get();
	}

	@Override
	protected boolean isItemValidForContainerSlot(ItemStack containerStack, int slot, ItemStack stack) {
		return slot == 0 && stack.is(Items.SNOWBALL);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		//acts as a cooldown.
		player.swing(hand);
		if (!player.isShiftKeyDown() && (getSnowballs(stack) >= getSnowballCost() || player.isCreative())) {
			level.playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
			SpecialSnowball snowball = new SpecialSnowball(level, player, this instanceof GlacialStaffItem);
			snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.4F, 1.0F);
			level.addFreshEntity(snowball);
			if (!player.isCreative()) {
				useCharge(stack, getSnowballCost());
			}
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		}
		return super.use(level, player, hand);
	}

	@Override
	public void addStoredCharge(ItemStack containerStack, int slot, int chargeToAdd, @Nullable ItemStack chargeStack) {
		containerStack.set(ModDataComponents.SNOWBALLS, Math.max(0, Math.min(getSnowballs(containerStack) + chargeToAdd, getSnowballLimit())));
	}

	@Override
	protected int getSlotWorth(int slot) {
		return slot == 0 ? getSnowballWorth() : 0;
	}

	@Override
	public int getStoredCharge(ItemStack containerStack, int slot) {
		return getSnowballs(containerStack);
	}

	public static int getSnowballs(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.SNOWBALLS, 0);
	}

	@Override
	public void inventoryTick(ItemStack rod, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % 10 != 0) {
			return;
		}
		if (isEnabled(rod)) {
			int snowCharge = getSnowballs(rod);
			consumeAndCharge(rod, 0, player, getSnowballLimit() - snowCharge, 1, 16);
		}
	}
}
