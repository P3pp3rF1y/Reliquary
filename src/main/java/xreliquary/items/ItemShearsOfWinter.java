package xreliquary.items;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ItemShearsOfWinter extends ItemShears {
	public ItemShearsOfWinter() {
		this.setUnlocalizedName(Names.Items.SHEARS_OF_WINTER);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	public boolean onBlockDestroyed(@Nonnull ItemStack stack, World world, IBlockState blockState, BlockPos pos, @Nonnull EntityLivingBase player) {
		//noinspection SimplifiableIfStatement
		if(blockState.getMaterial() != Material.LEAVES && blockState.getBlock() != Blocks.WEB && blockState.getBlock() != Blocks.TALLGRASS && blockState.getBlock() != Blocks.VINE && blockState.getBlock() != Blocks.TRIPWIRE && !(blockState.getBlock() instanceof IShearable)) {
			return super.onBlockDestroyed(stack, world, blockState, pos, player);
		} else {
			return true;
		}
	}

	@Override
	public boolean canHarvestBlock(IBlockState blockState) {
		return blockState.getBlock() == Blocks.WEB || blockState.getBlock() == Blocks.REDSTONE_WIRE || blockState.getBlock() == Blocks.TRIPWIRE;
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState blockState) {
		return blockState.getBlock() != Blocks.WEB && blockState.getMaterial() != Material.LEAVES ? (blockState.getBlock() == Blocks.WOOL ? 5.0F : super.getStrVsBlock(stack, blockState)) : 15.0F;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 2500;
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack ist) {
		return EnumAction.BLOCK;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		player.setActiveHand(hand);
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public void onUsingTick(ItemStack ist, EntityLivingBase entity, int count) {
		//start the blizzard after a short delay, this prevents some abuse.
		if(getMaxItemUseDuration(ist) - count <= 5)
			return;

		if(!(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;

		Vec3d lookVector = player.getLookVec();
		spawnBlizzardParticles(lookVector, player);

		if(entity.world.isRemote)
			return;

		doEntityShearableCheck(ist, player, lookVector);
		if(lookVector.x > 0)
			doPositiveXCheck(ist, player, lookVector);
		else
			doNegativeXCheck(ist, player, lookVector);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack shears, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		this.formatTooltip(shears, tooltip);
	}

	@SideOnly(Side.CLIENT)
	public void formatTooltip(ItemStack stack, List<String> list) {
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			LanguageHelper.formatTooltip(this.getUnlocalizedNameInefficiently(stack) + ".tooltip", null, list);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		return LanguageHelper.getLocalization(this.getUnlocalizedNameInefficiently(stack) + ".name");
	}

	private void doPositiveXCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector) {
		boolean firedOnce = false;

		for(int x = 0; x < (int) (lookVector.x * 10D); x++) {
			firedOnce = true;
			if(lookVector.y > 0)
				doPositiveYCheck(ist, player, lookVector, x);
			else
				doNegativeYCheck(ist, player, lookVector, x);
		}

		if(!firedOnce) {
			for(int x = -2; x <= 2; x++) {
				if(lookVector.y > 0)
					doPositiveYCheck(ist, player, lookVector, x);
				else
					doNegativeYCheck(ist, player, lookVector, x);
			}
		}
	}

	private void doNegativeXCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector) {
		boolean firedOnce = false;

		for(int x = 0; x > (int) (lookVector.x * 10D); x--) {
			firedOnce = true;
			if(lookVector.y > 0)
				doPositiveYCheck(ist, player, lookVector, x);
			else
				doNegativeYCheck(ist, player, lookVector, x);
		}

		if(!firedOnce) {
			for(int x = -2; x <= 2; x++) {
				if(lookVector.y > 0)
					doPositiveYCheck(ist, player, lookVector, x);
				else
					doNegativeYCheck(ist, player, lookVector, x);
			}
		}
	}

	private void doPositiveYCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector, int x) {
		boolean firedOnce = false;

		for(int y = 0; y < (int) (lookVector.y * 10D); y++) {
			firedOnce = true;
			if(lookVector.z > 0)
				doPositiveZCheck(ist, player, lookVector, x, y);
			else
				doNegativeZCheck(ist, player, lookVector, x, y);
		}

		if(!firedOnce) {
			for(int y = -2; y <= 2; y++) {
				if(lookVector.z > 0)
					doPositiveZCheck(ist, player, lookVector, x, y);
				else
					doNegativeZCheck(ist, player, lookVector, x, y);
			}
		}
	}

	private void doNegativeYCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector, int x) {
		boolean firedOnce = false;

		for(int y = 0; y > (int) (lookVector.y * 10D); y--) {
			firedOnce = true;
			if(lookVector.z > 0)
				doPositiveZCheck(ist, player, lookVector, x, y);
			else
				doNegativeZCheck(ist, player, lookVector, x, y);
		}

		if(!firedOnce) {
			for(int y = -2; y <= 2; y++) {
				if(lookVector.z > 0)
					doPositiveZCheck(ist, player, lookVector, x, y);
				else
					doNegativeZCheck(ist, player, lookVector, x, y);
			}
		}
	}

	private void doPositiveZCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector, int x, int y) {
		boolean firedOnce = false;

		for(int z = 0; z < (int) (lookVector.z * 10D); z++) {
			firedOnce = true;
			checkAndBreakBlockAt(x, y, z, player, ist);
		}

		if(!firedOnce) {
			for(int z = -2; z <= 2; z++)
				checkAndBreakBlockAt(x, y, z, player, ist);
		}
	}

	private void doNegativeZCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector, int x, int y) {
		boolean firedOnce = false;

		for(int z = 0; z > (int) (lookVector.z * 10D); z--) {
			firedOnce = true;
			checkAndBreakBlockAt(x, y, z, player, ist);
		}

		if(!firedOnce) {
			for(int z = -2; z <= 2; z++)
				checkAndBreakBlockAt(x, y, z, player, ist);
		}
	}

	private void checkAndBreakBlockAt(int x, int y, int z, EntityPlayer player, ItemStack ist) {
		x += (int) player.posX;
		y += (int) (player.posY + player.getEyeHeight());
		z += (int) player.posZ;

		int distance = (int) player.getDistance((double) x, (double) y, (double) z);
		int probabilityFactor = 5 + distance;
		//chance of block break diminishes over distance
		if(player.world.rand.nextInt(probabilityFactor) == 0) {
			IBlockState blockState = player.world.getBlockState(new BlockPos(x, y, z));
			Block block = blockState.getBlock();
			if(block instanceof IShearable) {
				IShearable target = (IShearable) block;
				ItemStack dummyShears = new ItemStack(Items.SHEARS, 1, 0);
				if(target.isShearable(dummyShears, player.world, new BlockPos(x, y, z))) {
					List<ItemStack> drops = target.onSheared(dummyShears, player.world, new BlockPos(x, y, z), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, ist));
					Random rand = new Random();

					if(player.world.isRemote) {
						if(blockState.getMaterial() != Material.AIR)
							player.world.playEvent(player, 2001, new BlockPos(x, y, z), Block.getStateId(player.world.getBlockState(new BlockPos(x, y, z))));

					} else {
						for(ItemStack stack : drops) {
							float f = 0.7F;
							double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
							double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
							double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
							EntityItem entityitem = new EntityItem(player.world, (double) x + d, (double) y + d1, (double) z + d2, stack);
							entityitem.setPickupDelay(10);
							player.world.spawnEntity(entityitem);
						}

						player.world.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
						StatBase stats = StatList.getBlockStats(block);
						if(stats != null) {
							player.addStat(stats);
						}
						player.addExhaustion(0.01F);
					}
				}
			}
		}
	}

	private void doEntityShearableCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector) {
		if(player.world.isRemote)
			return;
		double lowerX = Math.min(player.posX, player.posX + lookVector.x * 10D);
		double lowerY = Math.min(player.posY + player.getEyeHeight(), player.posY + player.getEyeHeight() + lookVector.y * 10D);
		double lowerZ = Math.min(player.posZ, player.posZ + lookVector.z * 10D);
		double upperX = Math.max(player.posX, player.posX + lookVector.x * 10D);
		double upperY = Math.max(player.posY + player.getEyeHeight(), player.posY + player.getEyeHeight() + lookVector.y * 10D);
		double upperZ = Math.max(player.posZ, player.posZ + lookVector.z * 10D);
		List<EntityLiving> eList = player.world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(lowerX, lowerY, lowerZ, upperX, upperY, upperZ));
		for(EntityLiving e : eList) {
			int distance = (int) player.getDistanceToEntity(e);
			int probabilityFactor = (distance - 3) / 2;
			if(probabilityFactor > 0 && player.world.rand.nextInt(probabilityFactor) != 0)
				continue;
			if(!e.isEntityEqual(player))
				e.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 120, 1));
			if(e instanceof IShearable) {
				IShearable target = (IShearable) e;
				if(target.isShearable(new ItemStack(Items.SHEARS, 1, 0), e.world, new BlockPos((int) e.posX, (int) e.posY, (int) e.posZ))) {
					List<ItemStack> drops = target.onSheared(new ItemStack(Items.SHEARS, 1, 0), e.world, new BlockPos((int) e.posX, (int) e.posY, (int) e.posZ), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, ist));

					Random rand = new Random();
					for(ItemStack stack : drops) {
						EntityItem ent = e.entityDropItem(stack, 1.0F);
						//noinspection ConstantConditions
						ent.motionY += rand.nextFloat() * 0.05F;
						ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
						ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
					}

					player.addExhaustion(0.01F);
				}
			}
		}
	}

	private void spawnBlizzardParticles(Vec3d lookVector, EntityPlayer player) {
		//spawn a whole mess of particles every tick.
		for(int i = 0; i < 16; ++i) {
			float randX = 10F * (player.world.rand.nextFloat() - 0.5F);
			float randY = 10F * (player.world.rand.nextFloat() - 0.5F);
			float randZ = 10F * (player.world.rand.nextFloat() - 0.5F);

			player.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, player.posX + randX, player.posY + randY, player.posZ + randZ, lookVector.x * 5, lookVector.y * 5, lookVector.z * 5, Block.getStateId(Blocks.SNOW_LAYER.getDefaultState()));

		}

	}

}
