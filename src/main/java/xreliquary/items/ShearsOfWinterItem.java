package xreliquary.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.UseAction;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IShearable;
import xreliquary.Reliquary;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;
import xreliquary.util.RandHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ShearsOfWinterItem extends ShearsItem {
	public ShearsOfWinterItem() {
		super(new Properties().group(Reliquary.ITEM_GROUP).maxDamage(0));
		setRegistryName(new ResourceLocation(Reference.MOD_ID, "shears_of_winter"));
	}

	@Override
	public int getUseDuration(ItemStack par1ItemStack) {
		return 2500;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		player.setActiveHand(hand);
		return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
		//start the blizzard after a short delay, this prevents some abuse.
		if (getUseDuration(stack) - count <= 5 || !(entity instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) entity;

		Vec3d lookVector = player.getLookVec();
		spawnBlizzardParticles(lookVector, player);

		if (entity.world.isRemote) {
			return;
		}

		doEntityShearableCheck(stack, player, lookVector);
		shearBlocks(player, lookVector);
	}

	private void shearBlocks(PlayerEntity player, Vec3d lookVector) {
		BlockPos firstPos = new BlockPos(player.getEyePosition(1));
		BlockPos secondPos = new BlockPos(player.getEyePosition(1).add(lookVector.mul(10, 10, 10)));
		if (firstPos.getX() == secondPos.getX()) {
			firstPos = firstPos.add(-2, 0, 0);
			secondPos = secondPos.add(2, 0, 0);
		}
		if (firstPos.getY() == secondPos.getY()) {
			firstPos = firstPos.add(0, -2, 0);
			secondPos = secondPos.add(0, 2, 0);
		}
		if (firstPos.getZ() == secondPos.getZ()) {
			firstPos = firstPos.add(0, 0, -2);
			secondPos = secondPos.add(0, 0, 2);
		}

		BlockPos.getAllInBox(firstPos, secondPos)
				.forEach(pos -> checkAndBreakBlockAt(player, pos));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack shears, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		LanguageHelper.formatTooltip(getTranslationKey() + ".tooltip", null, tooltip);
	}

	private void checkAndBreakBlockAt(PlayerEntity player, BlockPos pos) {
		int distance = (int) Math.sqrt(pos.distanceSq(player.getPosX(), player.getPosY(), player.getPosZ(), false));
		int probabilityFactor = 5 + distance;
		//chance of block break diminishes over distance
		if (player.world.rand.nextInt(probabilityFactor) == 0) {
			shearBlockAt(pos, player);
		}
	}

	@SuppressWarnings({"squid:CallToDeprecatedMethod", "deprecation"})
	private void shearBlockAt(BlockPos pos, PlayerEntity player) {
		World world = player.world;
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block instanceof IShearable) {
			IShearable target = (IShearable) block;
			ItemStack dummyShears = new ItemStack(Items.SHEARS);
			if (target.isShearable(dummyShears, world, pos) && removeBlock(player, pos, blockState.canHarvestBlock(world, pos, player))) {
				player.addStat(Stats.BLOCK_MINED.get(block));
				player.addExhaustion(0.01F);
				Block.spawnDrops(blockState, world, pos, null, player, dummyShears);
			}
		}
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
		if (entityLiving instanceof PlayerEntity) {
			shearBlockAt(pos, (PlayerEntity) entityLiving);
		}
		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}

	private boolean removeBlock(PlayerEntity player, BlockPos pos, boolean canHarvest) {
		BlockState state = player.world.getBlockState(pos);
		boolean removed = state.removedByPlayer(player.world, pos, player, canHarvest, player.world.getFluidState(pos));
		if (removed) {
			state.getBlock().onPlayerDestroy(player.world, pos, state);
		}
		return removed;
	}

	@SuppressWarnings({"squid:CallToDeprecatedMethod", "deprecation", "squid:S1764"})
	private void doEntityShearableCheck(ItemStack stack, PlayerEntity player, Vec3d lookVector) {
		if (player.world.isRemote) {
			return;
		}
		double lowerX = Math.min(player.getPosX(), player.getPosX() + lookVector.x * 10D);
		double lowerY = Math.min(player.getPosY() + player.getEyeHeight(), player.getPosY() + player.getEyeHeight() + lookVector.y * 10D);
		double lowerZ = Math.min(player.getPosZ(), player.getPosZ() + lookVector.z * 10D);
		double upperX = Math.max(player.getPosX(), player.getPosX() + lookVector.x * 10D);
		double upperY = Math.max(player.getPosY() + player.getEyeHeight(), player.getPosY() + player.getEyeHeight() + lookVector.y * 10D);
		double upperZ = Math.max(player.getPosZ(), player.getPosZ() + lookVector.z * 10D);
		List<MobEntity> eList = player.world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(lowerX, lowerY, lowerZ, upperX, upperY, upperZ));
		Random rand = player.world.rand;
		for (MobEntity e : eList) {
			int distance = (int) player.getDistance(e);
			int probabilityFactor = (distance - 3) / 2;
			if (probabilityFactor > 0 && player.world.rand.nextInt(probabilityFactor) != 0) {
				continue;
			}
			if (!e.isEntityEqual(player)) {
				e.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 120, 1));
			}
			if (e instanceof IShearable) {
				shearEntity(stack, player, rand, e);
			}
		}
	}

	@SuppressWarnings({"squid:CallToDeprecatedMethod", "deprecation", "squid:S1764"})
	private void shearEntity(ItemStack stack, PlayerEntity player, Random rand, MobEntity e) {
		IShearable target = (IShearable) e;
		BlockPos pos = new BlockPos((int) e.getPosX(), (int) e.getPosY(), (int) e.getPosZ());
		if (target.isShearable(new ItemStack(Items.SHEARS), e.world, pos)) {
			List<ItemStack> drops = target.onSheared(stack, e.world, pos,
					EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantments.FORTUNE, stack));
			drops.forEach(d -> {
				ItemEntity ent = e.entityDropItem(d, 1.0F);
				if (ent != null) {
					ent.setMotion(ent.getMotion().add(RandHelper.getRandomMinusOneToOne(rand) * 0.1F, rand.nextFloat() * 0.05F, RandHelper.getRandomMinusOneToOne(rand) * 0.1F));
				}
			});

			player.addExhaustion(0.01F);
		}
	}

	private void spawnBlizzardParticles(Vec3d lookVector, PlayerEntity player) {
		BlockParticleData blockParticleData = new BlockParticleData(ParticleTypes.BLOCK, Blocks.SNOW_BLOCK.getDefaultState());

		for (int i = 0; i < 16; ++i) {
			float randX = 10F * (player.world.rand.nextFloat() - 0.5F);
			float randY = 10F * (player.world.rand.nextFloat() - 0.5F);
			float randZ = 10F * (player.world.rand.nextFloat() - 0.5F);

			player.world.addParticle(blockParticleData, player.getPosX() + randX, player.getPosY() + randY, player.getPosZ() + randZ, lookVector.x * 5, lookVector.y * 5, lookVector.z * 5);
		}
	}

}
