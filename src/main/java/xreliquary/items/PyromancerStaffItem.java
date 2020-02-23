package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RandHelper;
import xreliquary.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PyromancerStaffItem extends ToggleableItem {
	private static final String QUANTITY_TAG = "Quantity";
	private static final String BLAZE_MODE = "blaze";
	private static final String BLAZE_CHARGES_TAG = BLAZE_MODE;
	private static final String ITEMS_TAG = "Items";
	private static final String CHARGE_MODE = "charge";
	private static final String ERUPTION_MODE = "eruption";

	public PyromancerStaffItem() {
		super("pyromancer_staff", new Properties().maxStackSize(1));
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity e, int i, boolean f) {
		if (!(e instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) e;

		doFireballAbsorbEffect(stack, player);

		if (!isEnabled(stack)) {
			doExtinguishEffect(player);
		} else {
			scanForFireChargeAndBlazePowder(stack, player);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack staff, @Nullable World world, List<ITextComponent> tooltip) {
		String charges = "0";
		String blaze = "0";
		CompoundNBT tagCompound = NBTHelper.getTag(staff);
		ListNBT tagList = tagCompound.getList(ITEMS_TAG, 10);
		for (int i = 0; i < tagList.size(); ++i) {
			CompoundNBT tagItemData = tagList.getCompound(i);
			String itemName = tagItemData.getString("Name");
			Item containedItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
			int quantity = tagItemData.getInt(QUANTITY_TAG);

			if (containedItem == Items.BLAZE_POWDER) {
				blaze = Integer.toString(quantity);
			} else if (containedItem == Items.FIRE_CHARGE) {
				charges = Integer.toString(quantity);
			}
		}
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip2", ImmutableMap.of("charges", charges, BLAZE_CHARGES_TAG, blaze), tooltip);
		if (isEnabled(staff)) {
			LanguageHelper.formatTooltip("tooltip.absorb_active",
					ImmutableMap.of("item", TextFormatting.RED + Items.BLAZE_POWDER.getDisplayName(new ItemStack(Items.BLAZE_POWDER)).getString()
							+ TextFormatting.WHITE + " & " + TextFormatting.RED + Items.FIRE_CHARGE.getDisplayName(new ItemStack(Items.FIRE_CHARGE)).getString()), tooltip);
		}

		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return true;
	}

	@Override
	public int getUseDuration(ItemStack par1ItemStack) {
		return 11;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	public String getMode(ItemStack stack) {
		if (NBTHelper.getString("mode", stack).equals("")) {
			setMode(stack, BLAZE_MODE);
		}
		return NBTHelper.getString("mode", stack);
	}

	private void setMode(ItemStack stack, String s) {
		NBTHelper.putString("mode", stack, s);
	}

	private void cycleMode(ItemStack stack) {
		if (getMode(stack).equals(BLAZE_MODE)) {
			setMode(stack, CHARGE_MODE);
		} else if (getMode(stack).equals(CHARGE_MODE)) {
			setMode(stack, ERUPTION_MODE);
		} else if (getMode(stack).equals(ERUPTION_MODE)) {
			setMode(stack, "flint_and_steel");
		} else {
			setMode(stack, BLAZE_MODE);
		}
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entityLiving) {
		if (entityLiving.world.isRemote || !(entityLiving instanceof PlayerEntity)) {
			return true;
		}
		PlayerEntity player = (PlayerEntity) entityLiving;
		if (player.isSneaking()) {
			cycleMode(stack);
		}
		return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking()) {
			super.onItemRightClick(world, player, hand);
		} else {
			if (getMode(stack).equals(BLAZE_MODE)) {
				if (player.isSwingInProgress) {
					return new ActionResult<>(ActionResultType.PASS, stack);
				}
				player.swingArm(hand);
				shootBlazeFireball(player, stack);
			} else if (getMode(stack).equals(CHARGE_MODE)) {
				if (player.isSwingInProgress) {
					return new ActionResult<>(ActionResultType.PASS, stack);
				}
				player.swingArm(hand);
				Vec3d lookVec = player.getLookVec();
				shootGhastFireball(player, stack, lookVec);
			} else {
				player.setActiveHand(hand);
			}
		}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	private void shootGhastFireball(PlayerEntity player, ItemStack stack, Vec3d lookVec) {
		if (removeItemFromInternalStorage(stack, Items.FIRE_CHARGE, getFireChargeCost(), player.world.isRemote, player)) {
			player.world.playEvent(player, 1016, new BlockPos((int) player.posX, (int) player.posY, (int) player.posZ), 0);
			FireballEntity fireball = new FireballEntity(player.world, player, lookVec.x, lookVec.y, lookVec.z);
			fireball.accelerationX = lookVec.x;
			fireball.accelerationY = lookVec.y;
			fireball.accelerationZ = lookVec.z;
			fireball.posX += lookVec.x;
			fireball.posY += lookVec.y;
			fireball.posZ += lookVec.z;
			fireball.posY = player.posY + player.getEyeHeight();
			player.world.addEntity(fireball);

		}
	}

	private void shootBlazeFireball(PlayerEntity player, ItemStack stack) {
		Vec3d lookVec = player.getLookVec();
		//blaze fireball!
		if (removeItemFromInternalStorage(stack, Items.BLAZE_POWDER, getBlazePowderCost(), player.world.isRemote, player)) {
			player.world.playEvent(player, 1018, new BlockPos((int) player.posX, (int) player.posY, (int) player.posZ), 0);
			SmallFireballEntity fireball = new SmallFireballEntity(player.world, player, lookVec.x, lookVec.y, lookVec.z);
			fireball.accelerationX = lookVec.x;
			fireball.accelerationY = lookVec.y;
			fireball.accelerationZ = lookVec.z;
			fireball.posX += lookVec.x;
			fireball.posY += lookVec.y;
			fireball.posZ += lookVec.z;
			fireball.posY = player.posY + player.getEyeHeight();
			player.world.addEntity(fireball);
		}
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
		if (!(entity instanceof PlayerEntity)) {
			return;
		}
		if (getMode(stack).equals(ERUPTION_MODE)) {
			PlayerEntity player = (PlayerEntity) entity;
			RayTraceResult rayTraceResult = player.func_213324_a(12, 1, true);

			if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
				count -= 1;
				count = getUseDuration(stack) - count;

				BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rayTraceResult;
				doEruptionAuxEffects(player, blockRayTraceResult.getPos().getX(), blockRayTraceResult.getPos().getY(), blockRayTraceResult.getPos().getZ());
				if (count % 10 == 0 && removeItemFromInternalStorage(stack, Items.BLAZE_POWDER, getBlazePowderCost(), player.world.isRemote, player)) {
					doEruptionEffect(player, blockRayTraceResult.getPos().getX(), blockRayTraceResult.getPos().getY(), blockRayTraceResult.getPos().getZ());
				}
			}
		}
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		Direction face = context.getFace();
		World world = context.getWorld();

		if (player == null) {
			return ActionResultType.PASS;
		}

		ItemStack stack = player.getHeldItem(context.getHand());
		if (getMode(stack).equals("flint_and_steel")) {
			BlockPos placeFireAt = context.getPos().offset(face);
			if (!player.canPlayerEdit(placeFireAt, face, stack)) {
				return ActionResultType.PASS;
			} else {
				if (world.isAirBlock(placeFireAt)) {
					world.playSound((double) placeFireAt.getX() + 0.5D, (double) placeFireAt.getY() + 0.5D, (double) placeFireAt.getZ() + 0.5D, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F, false);
					world.setBlockState(placeFireAt, Blocks.FIRE.getDefaultState());
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	private void doEruptionAuxEffects(PlayerEntity player, int soundX, int soundY, int soundZ) {
		player.world.playSound((double) soundX + 0.5D, (double) soundY + 0.5D, (double) soundZ + 0.5D, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.NEUTRAL, 0.2F, 0.03F + (0.07F * random.nextFloat()), false);

		for (int particleCount = 0; particleCount < 2; ++particleCount) {
			double randX = (soundX + 0.5D) + (player.world.rand.nextFloat() - 0.5F) * 5D;
			double randZ = (soundZ + 0.5D) + (player.world.rand.nextFloat() - 0.5F) * 5D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.world.addParticle(ParticleTypes.LAVA, randX, soundY + 1D, randZ, 0D, 0D, 0D);
			}
		}
		for (int particleCount = 0; particleCount < 4; ++particleCount) {
			double randX = soundX + 0.5D + (player.world.rand.nextFloat() - 0.5F) * 5D / 2D;
			double randZ = soundZ + 0.5D + (player.world.rand.nextFloat() - 0.5F) * 5D / 2D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.world.addParticle(ParticleTypes.LAVA, randX, soundY + 1D, randZ, 0D, 0D, 0D);
			}
		}
		for (int particleCount = 0; particleCount < 6; ++particleCount) {
			double randX = soundX + 0.5D + (player.world.rand.nextFloat() - 0.5F) * 5D;
			double randZ = soundZ + 0.5D + (player.world.rand.nextFloat() - 0.5F) * 5D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.world.addParticle(ParticleTypes.FLAME, randX, soundY + 1D, randZ, player.world.rand.nextGaussian() * 0.2D, player.world.rand.nextGaussian() * 0.2D, player.world.rand.nextGaussian() * 0.2D);
			}
		}
		for (int particleCount = 0; particleCount < 8; ++particleCount) {
			double randX = soundX + 0.5D + (player.world.rand.nextFloat() - 0.5F) * 5D / 2D;
			double randZ = soundZ + 0.5D + (player.world.rand.nextFloat() - 0.5F) * 5D / 2D;
			if (Math.abs(randX - (soundX + 0.5D)) < 4.0D && Math.abs(randZ - (soundZ + 0.5D)) < 4.0D) {
				player.world.addParticle(ParticleTypes.FLAME, randX, soundY + 1D, randZ, player.world.rand.nextGaussian() * 0.2D, player.world.rand.nextGaussian() * 0.2D, player.world.rand.nextGaussian() * 0.2D);
			}
		}
	}

	private void doEruptionEffect(PlayerEntity player, int x, int y, int z) {
		double lowerX = x - 5D + 0.5D;
		double lowerZ = z - 5D + 0.5D;
		double upperX = x + 5D + 0.5D;
		double upperY = y + 5D;
		double upperZ = z + 5D + 0.5D;
		List<MobEntity> entities = player.world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(lowerX, y, lowerZ, upperX, upperY, upperZ));

		entities.stream().filter(e -> !e.isEntityEqual(player)).forEach(e -> {
			e.setFire(40);
			if (!e.isImmuneToFire()) {
				e.attackEntityFrom(DamageSource.causePlayerDamage(player), 4F);
			}
		});
	}

	private void scanForFireChargeAndBlazePowder(ItemStack staff, PlayerEntity player) {
		List<ItemStack> absorbItems = new ArrayList<>();
		absorbItems.add(new ItemStack(Items.FIRE_CHARGE));
		absorbItems.add(new ItemStack(Items.BLAZE_POWDER));
		absorbItems.stream().filter(absorbItem -> !isInternalStorageFullOfItem(staff, absorbItem.getItem()) && InventoryHelper.consumeItem(absorbItem, player))
				.forEach(absorbItem -> addItemToInternalStorage(staff, absorbItem.getItem(), false));
	}

	private void addItemToInternalStorage(ItemStack stack, Item item, boolean isAbsorb) {
		int quantityIncrease;
		if (item == Items.FIRE_CHARGE) {
			quantityIncrease = isAbsorb ? getGhastAbsorbWorth() : getFireChargeWorth();
		} else {
			quantityIncrease = isAbsorb ? getBlazeAbsorbWorth() : getBlazePowderWorth();
		}
		CompoundNBT tagCompound = NBTHelper.getTag(stack);

		ListNBT tagList = tagCompound.getList(ITEMS_TAG, 10);

		boolean added = false;
		for (int i = 0; i < tagList.size(); ++i) {
			CompoundNBT tagItemData = tagList.getCompound(i);
			String itemName = tagItemData.getString("Name");
			if (itemName.equals(RegistryHelper.getItemRegistryName(item))) {
				int quantity = tagItemData.getInt(QUANTITY_TAG);
				tagItemData.putInt(QUANTITY_TAG, quantity + quantityIncrease);
				added = true;
			}
		}
		if (!added) {
			CompoundNBT newTagData = new CompoundNBT();
			newTagData.putString("Name", RegistryHelper.getItemRegistryName(item));
			newTagData.putInt(QUANTITY_TAG, quantityIncrease);
			tagList.add(newTagData);
		}

		tagCompound.put(ITEMS_TAG, tagList);

		stack.setTag(tagCompound);
	}

	private boolean removeItemFromInternalStorage(ItemStack stack, Item item, int cost, boolean simulate, PlayerEntity player) {
		if (player.isCreative()) {
			return true;
		}
		if (hasItemInInternalStorage(stack, item, cost)) {
			CompoundNBT tagCompound = NBTHelper.getTag(stack);

			ListNBT tagList = tagCompound.getList(ITEMS_TAG, 10);

			ListNBT replacementTagList = new ListNBT();

			for (int i = 0; i < tagList.size(); ++i) {
				CompoundNBT tagItemData = tagList.getCompound(i);
				String itemName = tagItemData.getString("Name");
				if (itemName.equals(RegistryHelper.getItemRegistryName(item))) {
					int quantity = tagItemData.getInt(QUANTITY_TAG);
					if (!simulate) {
						tagItemData.putInt(QUANTITY_TAG, quantity - cost);
					}
				}
				replacementTagList.add(tagItemData);
			}
			tagCompound.put(ITEMS_TAG, replacementTagList);
			stack.setTag(tagCompound);
			return true;
		}
		return false;

	}

	private boolean hasItemInInternalStorage(ItemStack stack, Item item, int cost) {
		CompoundNBT tagCompound = NBTHelper.getTag(stack);
		if (tagCompound.isEmpty()) {
			tagCompound.put(ITEMS_TAG, new ListNBT());
			return false;
		}

		ListNBT tagList = tagCompound.getList(ITEMS_TAG, 10);
		for (int i = 0; i < tagList.size(); ++i) {
			CompoundNBT tagItemData = tagList.getCompound(i);
			String itemName = tagItemData.getString("Name");
			if (itemName.equals(RegistryHelper.getItemRegistryName(item))) {
				int quantity = tagItemData.getInt(QUANTITY_TAG);
				return quantity >= cost;
			}
		}

		return false;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean isInternalStorageFullOfItem(ItemStack stack, Item item) {
		int quantityLimit = item == Items.FIRE_CHARGE ? getFireChargeLimit() : getBlazePowderLimit();
		if (hasItemInInternalStorage(stack, item, 1)) {
			CompoundNBT tagCompound = NBTHelper.getTag(stack);
			ListNBT tagList = tagCompound.getList(ITEMS_TAG, 10);

			for (int i = 0; i < tagList.size(); ++i) {
				CompoundNBT tagItemData = tagList.getCompound(i);
				String itemName = tagItemData.getString("Name");
				if (itemName.equals(RegistryHelper.getItemRegistryName(item))) {
					int quantity = tagItemData.getInt(QUANTITY_TAG);
					return quantity >= quantityLimit;
				}
			}
		}
		return false;
	}

	public int getInternalStorageItemCount(ItemStack staff, Item item) {
		CompoundNBT tagCompound = NBTHelper.getTag(staff);
		ListNBT tagList = tagCompound.getList(ITEMS_TAG, 10);

		for (int i = 0; i < tagList.size(); ++i) {
			CompoundNBT tagItemData = tagList.getCompound(i);
			String itemName = tagItemData.getString("Name");
			if (itemName.equals(RegistryHelper.getItemRegistryName(item))) {
				return tagItemData.getInt(QUANTITY_TAG);
			}
		}
		return 0;
	}

	private int getFireChargeWorth() {
		return Settings.COMMON.items.pyromancerStaff.fireChargeWorth.get();
	}

	private int getFireChargeCost() {
		return Settings.COMMON.items.pyromancerStaff.fireChargeCost.get();
	}

	private int getFireChargeLimit() {
		return Settings.COMMON.items.pyromancerStaff.fireChargeLimit.get();
	}

	private int getBlazePowderWorth() {
		return Settings.COMMON.items.pyromancerStaff.blazePowderWorth.get();
	}

	private int getBlazePowderCost() {
		return Settings.COMMON.items.pyromancerStaff.blazePowderCost.get();
	}

	private int getBlazePowderLimit() {
		return Settings.COMMON.items.pyromancerStaff.blazePowderLimit.get();
	}

	private int getBlazeAbsorbWorth() {
		return Settings.COMMON.items.pyromancerStaff.blazeAbsorbWorth.get();
	}

	private int getGhastAbsorbWorth() {
		return Settings.COMMON.items.pyromancerStaff.ghastAbsorbWorth.get();
	}

	private void doExtinguishEffect(PlayerEntity player) {
		if (player.isBurning()) {
			player.extinguish();
		}
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		for (int xOff = -3; xOff <= 3; xOff++) {
			for (int yOff = -3; yOff <= 3; yOff++) {
				for (int zOff = -3; zOff <= 3; zOff++) {
					Block block = player.world.getBlockState(new BlockPos(x + xOff, y + yOff, z + zOff)).getBlock();
					if (block == Blocks.FIRE) {
						player.world.setBlockState(new BlockPos(x + xOff, y + yOff, z + zOff), Blocks.AIR.getDefaultState());
						player.world.playSound(x + xOff + 0.5D, y + yOff + 0.5D, z + zOff + 0.5D, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.8F, false);
					}
				}
			}
		}
	}

	private void doFireballAbsorbEffect(ItemStack stack, PlayerEntity player) {
		absorbGhastFireballs(stack, player);
		absorbBlazeFireballs(stack, player);
	}

	private void absorbBlazeFireballs(ItemStack stack, PlayerEntity player) {
		List<SmallFireballEntity> blazeFireballs = player.world.getEntitiesWithinAABB(SmallFireballEntity.class, new AxisAlignedBB(player.posX - 3, player.posY - 3, player.posZ - 3, player.posX + 3, player.posY + 3, player.posZ + 3));
		for (SmallFireballEntity fireball : blazeFireballs) {
			if (fireball.shootingEntity == player) {
				continue;
			}
			for (int particles = 0; particles < 4; particles++) {
				player.world.addParticle(RedstoneParticleData.REDSTONE_DUST, fireball.posX, fireball.posY, fireball.posZ, 0.0D, 1.0D, 1.0D);
			}
			player.world.playSound(fireball.posX, fireball.posY, fireball.posZ, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.8F, false);

			if (!isInternalStorageFullOfItem(stack, Items.BLAZE_POWDER) && InventoryHelper.consumeItem(new ItemStack(Items.BLAZE_POWDER), player)) {
				addItemToInternalStorage(stack, Items.BLAZE_POWDER, true);
			}
			fireball.remove();
		}
	}

	private void absorbGhastFireballs(ItemStack stack, PlayerEntity player) {
		List<FireballEntity> ghastFireballs = player.world.getEntitiesWithinAABB(FireballEntity.class, new AxisAlignedBB(player.posX - 5, player.posY - 5, player.posZ - 5, player.posX + 5, player.posY + 5, player.posZ + 5));
		for (FireballEntity fireball : ghastFireballs) {
			if (fireball.shootingEntity != player && player.getDistance(fireball) < 4) {
				if (!isInternalStorageFullOfItem(stack, Items.FIRE_CHARGE) && InventoryHelper.consumeItem(new ItemStack(Items.FIRE_CHARGE), player)) {
					addItemToInternalStorage(stack, Items.FIRE_CHARGE, true);
					player.world.playSound(fireball.posX, fireball.posY, fireball.posZ, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + RandHelper.getRandomMinusOneToOne(player.world.rand) * 0.8F, false);
				}
				fireball.remove();
			}
		}
	}
}
