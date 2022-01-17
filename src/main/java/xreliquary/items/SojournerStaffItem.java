package xreliquary.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.items.util.IScrollableItem;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.NoPlayerBlockItemUseContext;
import xreliquary.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class SojournerStaffItem extends ToggleableItem implements IScrollableItem {
	private static final int COOLDOWN = 10;

	private static final String ITEMS_TAG = "Items";
	private static final String QUANTITY_TAG = "Quantity";
	private static final String CURRENT_INDEX_TAG = "Current";

	public SojournerStaffItem() {
		super(new Properties().stacksTo(1));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isClientSide || world.getGameTime() % COOLDOWN != 0 || !(entity instanceof Player player)) {
			return;
		}

		if (isEnabled(stack)) {
			scanForMatchingTorchesToFillInternalStorage(stack, player);
		}
	}

	@Override
	public InteractionResult onMouseScrolled(ItemStack stack, LivingEntity entityLiving, double scrollDelta) {
		if (entityLiving.level.isClientSide) {
			return InteractionResult.PASS;
		}
		cycleTorchMode(stack, scrollDelta > 0);
		return InteractionResult.SUCCESS;
	}

	private void scanForMatchingTorchesToFillInternalStorage(ItemStack stack, Player player) {
		for (String torch : Settings.COMMON.items.sojournerStaff.torches.get()) {
			consumeAndCharge(player, Settings.COMMON.items.sojournerStaff.maxCapacityPerItemType.get() - getInternalStorageItemCount(stack, torch), 1, ist -> RegistryHelper.getItemRegistryName(ist.getItem()).equals(torch), 16,
					chargeToAdd -> addItemToInternalStorage(stack, torch, chargeToAdd));
		}
	}

	public ItemStack getCurrentTorch(ItemStack stack) {
		return getItem(getCurrentTorchTag(stack));
	}

	public int getTorchCount(ItemStack stack) {
		return getCurrentTorchTag(stack).getInt(QUANTITY_TAG);
	}

	private CompoundTag getCurrentTorchTag(ItemStack stack) {
		CompoundTag tagCompound = NBTHelper.getTag(stack);

		ListTag tagList = getItemListTag(tagCompound);
		int current = getCurrentIndex(tagCompound, tagList);

		return tagList.getCompound(current);
	}

	private ListTag getItemListTag(CompoundTag tagCompound) {
		return tagCompound.getList(ITEMS_TAG, 10);
	}

	private void cycleTorchMode(ItemStack stack, boolean next) {
		ItemStack currentTorch = getCurrentTorch(stack);
		if (currentTorch.isEmpty()) {
			return;
		}
		CompoundTag tagCompound = NBTHelper.getTag(stack);
		ListTag tagList = getItemListTag(tagCompound);
		if (tagList.size() == 1) {
			return;
		}

		int current = getCurrentIndex(tagCompound, tagList);

		tagCompound.putInt(CURRENT_INDEX_TAG, Math.floorMod(current + (next ? 1 : -1), tagList.size()));
	}

	private int getCurrentIndex(CompoundTag tagCompound, ListTag tagList) {
		int current = tagCompound.getInt(CURRENT_INDEX_TAG);
		if (tagList.size() <= current) {
			tagCompound.putInt(CURRENT_INDEX_TAG, 0);
		}
		return current;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack staff, @Nullable Level world, List<Component> tooltip) {
		StringJoiner joiner = new StringJoiner(";");
		iterateItems(staff, tag -> {
			ItemStack containedItem = getItem(tag);
			int quantity = tag.getInt(QUANTITY_TAG);
			joiner.add(containedItem.getHoverName().getString() + ": " + quantity);
		}, () -> false);

		LanguageHelper.formatTooltip(getDescriptionId() + ".tooltip2", Map.of("phrase", joiner.toString(), "placing", getCurrentTorch(staff).getHoverName().getString()), tooltip);

		if (isEnabled(staff)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active", Map.of("item", ChatFormatting.YELLOW + (new ItemStack(Blocks.TORCH).getHoverName().getString())), tooltip);
		}
		LanguageHelper.formatTooltip("tooltip.absorb", tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	private static ItemStack getItem(CompoundTag tagItemData) {
		return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(tagItemData.getString(ITEM_NAME_TAG))));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return placeTorch(context);
	}

	private InteractionResult placeTorch(UseOnContext context) {
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction face = context.getClickedFace();
		ItemStack stack = context.getItemInHand();

		BlockPos placeBlockAt = pos.relative(face);

		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		ItemStack torch = getCurrentTorch(stack);
		if (player == null || torch.isEmpty() || !(torch.getItem() instanceof BlockItem)) {
			return InteractionResult.FAIL;
		}
		if (!player.mayUseItemAt(placeBlockAt, face, stack) || player.isCrouching()) {
			return InteractionResult.PASS;
		}
		player.swing(hand);

		Block blockToPlace = ((BlockItem) torch.getItem()).getBlock();
		NoPlayerBlockItemUseContext placeContext = new NoPlayerBlockItemUseContext(world, placeBlockAt, new ItemStack(blockToPlace), face);
		if (!placeContext.canPlace() || !removeTorches(player, stack, torch, blockToPlace, placeBlockAt)) {
			return InteractionResult.FAIL;
		}
		((BlockItem) torch.getItem()).place(placeContext);
		double gauss = 0.5D + world.random.nextFloat() / 2;
		world.addParticle(ParticleTypes.ENTITY_EFFECT, placeBlockAt.getX() + 0.5D, placeBlockAt.getY() + 0.5D, placeBlockAt.getZ() + 0.5D, gauss, gauss, 0.0F);
		return InteractionResult.SUCCESS;
	}

	private boolean removeTorches(Player player, ItemStack stack, ItemStack torch, Block blockToPlace, BlockPos placeBlockAt) {
		if (!player.isCreative()) {
			int distance = (int) player.getEyePosition(1).distanceTo(new Vec3(placeBlockAt.getX(), placeBlockAt.getY(), placeBlockAt.getZ()));
			int cost = 1 + distance / Settings.COMMON.items.sojournerStaff.tilePerCostMultiplier.get();

			boolean result = removeItemFromInternalStorage(stack, blockToPlace, cost, false, player);
			if (result && blockToPlace != Blocks.TORCH && getInternalStorageItemCount(stack, torch.getItem()) <= 0) {
				removeItemTagInInternalStorage(stack, torch.getItem());
				cycleTorchMode(stack, false);
			}
			return result;
		}
		return true;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if (!player.isShiftKeyDown()) {
			HitResult rayTraceResult = longRayTrace(world, player);
			if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
				placeTorch(new UseOnContext(player, hand, (BlockHitResult) rayTraceResult));
			} else {
				ItemStack staff = player.getItemInHand(hand);
				CompoundTag torchTag = getCurrentTorchTag(staff);
				ItemStack torch = getItem(torchTag);
				int count = torchTag.getInt(QUANTITY_TAG);
				torch.setCount(Math.min(count, torch.getMaxStackSize()));
				player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).ifPresent(playerInventory -> {
					int inserted = InventoryHelper.insertIntoInventory(torch, playerInventory);
					removeItemFromInternalStorage(staff, torch.getItem(), inserted, false, player);
				});
			}
		}
		return super.use(world, player, hand);
	}

	private HitResult longRayTrace(Level worldIn, Player player) {
		float f = player.getXRot();
		float f1 = player.getYRot();
		Vec3 vec3d = player.getEyePosition(1.0F);
		float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
		float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
		float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
		float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = Settings.COMMON.items.sojournerStaff.maxRange.get();
		Vec3 vec3d1 = vec3d.add(f6 * d0, f5 * d0, f7 * d0);
		return worldIn.clip(new ClipContext(vec3d, vec3d1, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player));
	}
}
