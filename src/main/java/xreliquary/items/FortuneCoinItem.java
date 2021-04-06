package xreliquary.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItem;
import xreliquary.blocks.tile.PedestalTileEntity;
import xreliquary.init.ModFluids;
import xreliquary.init.ModItems;
import xreliquary.items.util.IBaubleItem;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RandHelper;
import xreliquary.util.XpHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FortuneCoinItem extends ItemBase implements IPedestalActionItem, IBaubleItem {
	private static final String SOUND_TIMER_TAG = "soundTimer";

	public FortuneCoinItem() {
		super(new Properties().maxStackSize(1));
	}

	@Override
	public void onEquipped(String identifier, LivingEntity player) {
		if (player.world.isRemote) {
			player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.7F + 2.2F));
		}
	}

	@Override
	public IBaubleItem.Type getBaubleType() {
		return Type.NECKLACE;
	}

	@Override
	public void onWornTick(ItemStack stack, LivingEntity player) {
		inventoryTick(stack, player.world, player, 0, false);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2", tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return isEnabled(stack);
	}

	public boolean isEnabled(ItemStack stack) {
		return NBTHelper.getBoolean("enabled", stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isRemote) {
			return;
		}
		if (enabledAudio() && NBTHelper.getShort(SOUND_TIMER_TAG, stack) > 0) {
			if (NBTHelper.getShort(SOUND_TIMER_TAG, stack) % 2 == 0) {
				world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F,
						0.5F * RandHelper.getRandomMinusOneToOne(world.rand) * 0.7F + 1.8F);
			}
			NBTHelper.putShort(SOUND_TIMER_TAG, stack, (short) (NBTHelper.getShort(SOUND_TIMER_TAG, stack) - 1));
		}
		if (!isEnabled(stack)) {
			return;
		}
		PlayerEntity player = null;
		if (entity instanceof PlayerEntity) {
			player = (PlayerEntity) entity;
		}
		if (player == null || player.isSpectator()) {
			return;
		}
		scanForEntitiesInRange(world, player, getStandardPullDistance());
	}

	private void scanForEntitiesInRange(World world, PlayerEntity player, double d) {
		List<BlockPos> disablePositions = getDisablePositions(world, player.getPosition());
		List<ItemEntity> iList = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(player.getPosX() - d, player.getPosY() - d, player.getPosZ() - d, player.getPosX() + d, player.getPosY() + d, player.getPosZ() + d));
		for (ItemEntity item : iList) {
			if (canPickupItem(item, disablePositions) && checkForRoom(item.getItem(), player)) {
				item.setPickupDelay(0);
				if (player.getDistance(item) >= 1.5D) {
					teleportEntityToPlayer(item, player);
					break;
				}
			}
		}
		List<ExperienceOrbEntity> iList2 = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, new AxisAlignedBB(player.getPosX() - d, player.getPosY() - d, player.getPosZ() - d, player.getPosX() + d, player.getPosY() + d, player.getPosZ() + d));
		for (ExperienceOrbEntity item : iList2) {
			if (player.xpCooldown > 0) {
				player.xpCooldown = 0;
			}
			if (player.getDistance(item) >= 1.5D) {
				teleportEntityToPlayer(item, player);
				break;
			}
		}
	}

	private boolean canPickupItem(ItemEntity item, List<BlockPos> disablePositions) {
		return !item.getPersistentData().getBoolean("PreventRemoteMovement") && !isInDisabledRange(item, disablePositions);
		/* TODO readd when botania is in 1.14?
		if (Compatibility.isLoaded(Compatibility.MOD_ID.BOTANIA)) {
			if (SubTileSolegnolia.hasSolegnoliaAround(item))
				return false;
		}
*/
	}

	private boolean isInDisabledRange(ItemEntity item, List<BlockPos> disablePositions) {
		for (BlockPos disablePos : disablePositions) {
			if (Math.abs(item.getPosition().getX() - disablePos.getX()) < 5
					&& Math.abs(item.getPosition().getY() - disablePos.getY()) < 5
					&& Math.abs(item.getPosition().getZ() - disablePos.getZ()) < 5) {
				return true;
			}
		}
		return false;
	}

	private List<BlockPos> getDisablePositions(World world, BlockPos coinPos) {
		List<BlockPos> disablePositions = new ArrayList<>();
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(world.getDimensionKey().getRegistryName(), coinPos, 10);

		for (BlockPos pos : pedestalPositions) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof PedestalTileEntity) {
				PedestalTileEntity pedestal = (PedestalTileEntity) te;

				if (pedestal.switchedOn()) {
					ItemStack stack = pedestal.getStackInSlot(0);
					if (!stack.isEmpty() && stack.getItem() == this && !isEnabled(stack)) {
						disablePositions.add(pos);
					}
				}
			}
		}
		return disablePositions;
	}

	private void teleportEntityToPlayer(Entity item, PlayerEntity player) {
		player.world.addParticle(ParticleTypes.ENTITY_EFFECT, item.getPosX() + 0.5D + player.world.rand.nextGaussian() / 8, item.getPosY() + 0.2D, item.getPosZ() + 0.5D + player.world.rand.nextGaussian() / 8, 0.9D, 0.9D, 0.0D);
		player.getLookVec();
		double x = player.getPosX() + player.getLookVec().x * 0.2D;
		double y = player.getPosY();
		double z = player.getPosZ() + player.getLookVec().z * 0.2D;
		item.setPosition(x, y, z);
		if (enabledAudio()) {
			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.7F + 1.8F));
		}
	}

	private boolean checkForRoom(ItemStack stackToPickup, PlayerEntity player) {
		int remaining = stackToPickup.getCount();
		for (ItemStack inventoryStack : player.inventory.mainInventory) {
			if (inventoryStack.isEmpty()) {
				return true;
			}

			if (inventoryStack.getItem() == stackToPickup.getItem() && inventoryStack.getDamage() == stackToPickup.getDamage()) {
				if (inventoryStack.getCount() + remaining <= inventoryStack.getMaxStackSize()) {
					return true;
				} else {
					remaining -= (inventoryStack.getMaxStackSize() - inventoryStack.getCount());
				}
			} else if (inventoryStack.getItem() == ModItems.VOID_TEAR.get() && ModItems.VOID_TEAR.get().isEnabled(inventoryStack) && ModItems.VOID_TEAR.get().canAbsorbStack(stackToPickup, inventoryStack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
		if (!(entity instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entity;

		scanForEntitiesInRange(player.world, player, getLongRangePullDistance());
	}

	private double getLongRangePullDistance() {
		return (double) Settings.COMMON.items.fortuneCoin.longRangePullDistance.get();
	}

	private double getStandardPullDistance() {
		return (double) Settings.COMMON.items.fortuneCoin.standardPullDistance.get();
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 64;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (player.isSneaking()) {
			if (enabledAudio()) {
				NBTHelper.putShort(SOUND_TIMER_TAG, stack, (short) 6);
			}
			toggle(stack);
		} else {
			player.setActiveHand(hand);
		}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	private boolean enabledAudio() {
		return Settings.COMMON.items.fortuneCoin.enabledAudio.get();
	}

	@Override
	public void update(ItemStack stack, IPedestal pedestal) {
		World world = pedestal.getTheWorld();
		if (world.isRemote) {
			return;
		}

		if (isEnabled(stack)) {
			BlockPos pos = pedestal.getBlockPos();
			double d = getStandardPullDistance();

			List<BlockPos> disablePositions = getDisablePositions(world, pos);
			List<ItemEntity> entities = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos.getX() - d, pos.getY() - d, pos.getZ() - d, pos.getX() + d, pos.getY() + d, pos.getZ() + d));
			for (ItemEntity entityItem : entities) {

				//if entity is marked not to be picked up by magnets leave it alone - IE thing but may be more than that
				if (!canPickupItem(entityItem, disablePositions)) {
					continue;
				}

				int numberAdded = pedestal.addToConnectedInventory(entityItem.getItem().copy());
				if (numberAdded > 0) {
					entityItem.getItem().setCount(entityItem.getItem().getCount() - numberAdded);

					if (entityItem.getItem().getCount() <= 0) {
						entityItem.remove();
					}
				} else {
					pedestal.setActionCoolDown(20);
				}
			}

			List<ExperienceOrbEntity> xpOrbs = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, new AxisAlignedBB(pos.getX() - d, pos.getY() - d, pos.getZ() - d, pos.getX() + d, pos.getY() + d, pos.getZ() + d));
			for (ExperienceOrbEntity xpOrb : xpOrbs) {
				int amountToTransfer = XpHelper.experienceToLiquid(xpOrb.xpValue);
				int amountAdded = pedestal.fillConnectedTank(new FluidStack(ModFluids.XP_JUICE_STILL.get(), amountToTransfer));

				if (amountAdded > 0) {
					xpOrb.remove();

					if (amountToTransfer > amountAdded) {
						world.addEntity(new ExperienceOrbEntity(world, pos.getX(), pos.getY(), pos.getZ(), XpHelper.liquidToExperience(amountToTransfer - amountAdded)));
					}
				} else {
					pedestal.setActionCoolDown(20);
				}
			}
		}
	}

	@Override
	public void onRemoved(ItemStack stack, IPedestal pedestal) {
		//noop
	}

	@Override
	public void stop(ItemStack stack, IPedestal pedestal) {
		//noop
	}

	public void toggle(ItemStack stack) {
		NBTHelper.putBoolean("enabled", stack, !isEnabled(stack));
	}
}
