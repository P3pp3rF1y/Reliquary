package xreliquary.items;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
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
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
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

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Xeno on 10/11/2014.
 */
public class ItemShearsOfWinter extends ItemShears {
	public ItemShearsOfWinter() {
		this.setUnlocalizedName(Names.shears_of_winter);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState blockState, BlockPos pos, EntityLivingBase player) {
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

	@Override
	public EnumAction getItemUseAction(ItemStack ist) {
		return EnumAction.BLOCK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack ist, World world, EntityPlayer player, EnumHand hand) {
		player.setActiveHand(hand);
		return new ActionResult<>(EnumActionResult.SUCCESS, ist);
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

		if(entity.worldObj.isRemote)
			return;

		doEntityShearableCheck(ist, player, lookVector);
		if(lookVector.xCoord > 0)
			doPositiveXCheck(ist, player, lookVector);
		else
			doNegativeXCheck(ist, player, lookVector);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean whatDoesThisEvenDo) {
		this.formatTooltip(stack, list);
	}

	@SideOnly(Side.CLIENT)
	public void formatTooltip(ItemStack stack, List<String> list) {
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			LanguageHelper.formatTooltip(this.getUnlocalizedNameInefficiently(stack) + ".tooltip", null, stack, list);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		return LanguageHelper.getLocalization(this.getUnlocalizedNameInefficiently(stack) + ".name");
	}

	public void doPositiveXCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector) {
		boolean firedOnce = false;

		for(int x = 0; x < (int) (lookVector.xCoord * 10D); x++) {
			firedOnce = true;
			if(lookVector.yCoord > 0)
				doPositiveYCheck(ist, player, lookVector, x);
			else
				doNegativeYCheck(ist, player, lookVector, x);
		}

		if(!firedOnce) {
			for(int x = -2; x <= 2; x++) {
				if(lookVector.yCoord > 0)
					doPositiveYCheck(ist, player, lookVector, x);
				else
					doNegativeYCheck(ist, player, lookVector, x);
			}
		}
	}

	public void doNegativeXCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector) {
		boolean firedOnce = false;

		for(int x = 0; x > (int) (lookVector.xCoord * 10D); x--) {
			firedOnce = true;
			if(lookVector.yCoord > 0)
				doPositiveYCheck(ist, player, lookVector, x);
			else
				doNegativeYCheck(ist, player, lookVector, x);
		}

		if(!firedOnce) {
			for(int x = -2; x <= 2; x++) {
				if(lookVector.yCoord > 0)
					doPositiveYCheck(ist, player, lookVector, x);
				else
					doNegativeYCheck(ist, player, lookVector, x);
			}
		}
	}

	public void doPositiveYCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector, int x) {
		boolean firedOnce = false;

		for(int y = 0; y < (int) (lookVector.yCoord * 10D); y++) {
			firedOnce = true;
			if(lookVector.zCoord > 0)
				doPositiveZCheck(ist, player, lookVector, x, y);
			else
				doNegativeZCheck(ist, player, lookVector, x, y);
		}

		if(!firedOnce) {
			for(int y = -2; y <= 2; y++) {
				if(lookVector.zCoord > 0)
					doPositiveZCheck(ist, player, lookVector, x, y);
				else
					doNegativeZCheck(ist, player, lookVector, x, y);
			}
		}
	}

	public void doNegativeYCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector, int x) {
		boolean firedOnce = false;

		for(int y = 0; y > (int) (lookVector.yCoord * 10D); y--) {
			firedOnce = true;
			if(lookVector.zCoord > 0)
				doPositiveZCheck(ist, player, lookVector, x, y);
			else
				doNegativeZCheck(ist, player, lookVector, x, y);
		}

		if(!firedOnce) {
			for(int y = -2; y <= 2; y++) {
				if(lookVector.zCoord > 0)
					doPositiveZCheck(ist, player, lookVector, x, y);
				else
					doNegativeZCheck(ist, player, lookVector, x, y);
			}
		}
	}

	public void doPositiveZCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector, int x, int y) {
		boolean firedOnce = false;

		for(int z = 0; z < (int) (lookVector.zCoord * 10D); z++) {
			firedOnce = true;
			checkAndBreakBlockAt(x, y, z, player, ist);
		}

		if(!firedOnce) {
			for(int z = -2; z <= 2; z++)
				checkAndBreakBlockAt(x, y, z, player, ist);
		}
	}

	public void doNegativeZCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector, int x, int y) {
		boolean firedOnce = false;

		for(int z = 0; z > (int) (lookVector.zCoord * 10D); z--) {
			firedOnce = true;
			checkAndBreakBlockAt(x, y, z, player, ist);
		}

		if(!firedOnce) {
			for(int z = -2; z <= 2; z++)
				checkAndBreakBlockAt(x, y, z, player, ist);
		}
	}

	public void checkAndBreakBlockAt(int x, int y, int z, EntityPlayer player, ItemStack ist) {
		x += (int) player.posX;
		y += (int) (player.posY + player.getEyeHeight());
		z += (int) player.posZ;

		int distance = (int) player.getDistance((double) x, (double) y, (double) z);
		int probabilityFactor = 5 + distance;
		//chance of block break diminishes over distance
		if(player.worldObj.rand.nextInt(probabilityFactor) == 0) {
			IBlockState blockState = player.worldObj.getBlockState(new BlockPos(x, y, z));
			Block block = blockState.getBlock();
			if(block instanceof IShearable) {
				IShearable target = (IShearable) block;
				if(target.isShearable(new ItemStack(Items.SHEARS, 1, 0), player.worldObj, new BlockPos(x, y, z))) {
					//this commented portion causes the item to be sheared instead of just broken.
					//ArrayList<ItemStack> drops = target.onSheared(new ItemStack(Items.shears, 1, 0), player.worldObj, x, y, z,
					//        EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, ist));
					List<ItemStack> drops = block.getDrops(player.worldObj, new BlockPos(x, y, z), player.worldObj.getBlockState(new BlockPos(x, y, z)), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, ist));
					Random rand = new Random();

					if(player.worldObj.isRemote) {
						if(block.getMaterial(blockState) != Material.AIR)
							player.worldObj.playEvent(player, 2001, new BlockPos(x, y, z), Block.getStateId(player.worldObj.getBlockState(new BlockPos(x, y, z))));

					} else {
						for(ItemStack stack : drops) {
							float f = 0.7F;
							double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
							double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
							double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
							EntityItem entityitem = new EntityItem(player.worldObj, (double) x + d, (double) y + d1, (double) z + d2, stack);
							entityitem.setPickupDelay(10);
							player.worldObj.spawnEntityInWorld(entityitem);
						}

						player.worldObj.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
						player.addStat(StatList.getBlockStats(block));
						player.addExhaustion(0.01F);
					}
				}
			}
		}
	}

	public void doEntityShearableCheck(ItemStack ist, EntityPlayer player, Vec3d lookVector) {
		if(player.worldObj.isRemote)
			return;
		double lowerX = Math.min(player.posX, player.posX + lookVector.xCoord * 10D);
		double lowerY = Math.min(player.posY + player.getEyeHeight(), player.posY + player.getEyeHeight() + lookVector.yCoord * 10D);
		double lowerZ = Math.min(player.posZ, player.posZ + lookVector.zCoord * 10D);
		double upperX = Math.max(player.posX, player.posX + lookVector.xCoord * 10D);
		double upperY = Math.max(player.posY + player.getEyeHeight(), player.posY + player.getEyeHeight() + lookVector.yCoord * 10D);
		double upperZ = Math.max(player.posZ, player.posZ + lookVector.zCoord * 10D);
		List eList = player.worldObj.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(lowerX, lowerY, lowerZ, upperX, upperY, upperZ));
		Iterator iterator = eList.iterator();
		while(iterator.hasNext()) {
			Entity e = (Entity) iterator.next();

			int distance = (int) player.getDistanceToEntity(e);
			int probabilityFactor = (distance - 3) / 2;
			if(probabilityFactor > 0 && player.worldObj.rand.nextInt(probabilityFactor) != 0)
				continue;
			if(e instanceof EntityLivingBase && !e.isEntityEqual(player))
				((EntityLivingBase) e).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 120, 1));
			if(e instanceof IShearable) {
				IShearable target = (IShearable) e;
				if(target.isShearable(new ItemStack(Items.SHEARS, 1, 0), e.worldObj, new BlockPos((int) e.posX, (int) e.posY, (int) e.posZ))) {
					List<ItemStack> drops = target.onSheared(new ItemStack(Items.SHEARS, 1, 0), e.worldObj, new BlockPos((int) e.posX, (int) e.posY, (int) e.posZ), EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, ist));

					Random rand = new Random();
					for(ItemStack stack : drops) {
						EntityItem ent = e.entityDropItem(stack, 1.0F);
						ent.motionY += rand.nextFloat() * 0.05F;
						ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
						ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
					}

					player.addExhaustion(0.01F);
				}
			}
		}
	}

	public void spawnBlizzardParticles(Vec3d lookVector, EntityPlayer player) {
		//spawn a whole mess of particles every tick.
		for(int i = 0; i < 16; ++i) {
			float randX = 10F * (player.worldObj.rand.nextFloat() - 0.5F);
			float randY = 10F * (player.worldObj.rand.nextFloat() - 0.5F);
			float randZ = 10F * (player.worldObj.rand.nextFloat() - 0.5F);

			player.worldObj.spawnParticle(EnumParticleTypes.BLOCK_DUST, player.posX + randX, player.posY + randY, player.posZ + randZ, lookVector.xCoord * 5, lookVector.yCoord * 5, lookVector.zCoord * 5, Block.getStateId(Blocks.SNOW_LAYER.getStateFromMeta(0)));

		}

	}

}
