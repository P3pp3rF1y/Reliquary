package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import xreliquary.reference.Names;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemGlacialStaff extends ItemIceMagusRod {
	public ItemGlacialStaff() {
		super(Names.Items.GLACIAL_STAFF);
	}

	@Override
	public void addInformation(ItemStack ist, EntityPlayer player, List<String> list, boolean par4) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;
		this.formatTooltip(ImmutableMap.of("charge", Integer.toString(NBTHelper.getInteger("snowballs", ist))), ist, list);
		if(this.isEnabled(ist))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.BLUE + Items.SNOWBALL.getItemStackDisplayName(new ItemStack(Items.SNOWBALL))), list);
		LanguageHelper.formatTooltip("tooltip.absorb", null, list);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack ist, EntityPlayer player, Entity e) {
		if(e instanceof EntityLivingBase && NBTHelper.getInteger("snowballs", ist) >= getSnowballCost()) {
			EntityLivingBase livingBase = (EntityLivingBase) e;
			PotionEffect slow = new PotionEffect(MobEffects.SLOWNESS, 30, 0);

			//if the creature is slowed already, refresh the duration and increase the amplifier by 1.
			//5 hits is all it takes to max out the amplitude.
			if(livingBase.getActivePotionEffect(MobEffects.SLOWNESS) != null)
				//noinspection ConstantConditions
				slow = new PotionEffect(MobEffects.SLOWNESS, Math.min(livingBase.getActivePotionEffect(MobEffects.SLOWNESS).getDuration() + 30, 300),
						Math.min(livingBase.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier() + 1, 4));

			((EntityLivingBase) e).addPotionEffect(slow);
			e.attackEntityFrom(DamageSource.causePlayerDamage(player), slow.getAmplifier());
			NBTHelper.setInteger("snowballs", ist, NBTHelper.getInteger("snowballs", ist) - getSnowballCost());
		}
		return super.onLeftClickEntity(ist, player, e);
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
		super.onUpdate(ist, world, e, i, b);
		EntityPlayer player = null;
		if(e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		}
		if(player == null)
			return;

		int x = MathHelper.floor(player.posX);
		int y = MathHelper.floor(player.getEntityBoundingBox().minY) - 1;
		int z = MathHelper.floor(player.posZ);

		if(this.isEnabled(ist)) {
			for(int xOff = -2; xOff <= 2; xOff++) {
				for(int zOff = -2; zOff <= 2; zOff++) {
					if(Math.abs(xOff) == 2 && Math.abs(zOff) == 2)
						continue;
					doFreezeCheck(ist, x, y, z, world, xOff, zOff);
				}
			}
		}

		if (!world.isRemote) {
			for(BlockPos pos : getBlockLocations(ist)) {
				int xOff = Math.abs(MathHelper.floor(player.posX) - pos.getX());
				int yOff = Math.abs(MathHelper.floor(player.posY) - pos.getY());
				int zOff = Math.abs(MathHelper.floor(player.posZ) - pos.getZ());

				if(xOff < 3 && yOff < 3 && zOff < 3 && !(xOff == 2 && zOff == 2))
					continue;

				doThawCheck(ist, pos.getX(), pos.getY(), pos.getZ(), world);
			}
		}
	}

	private BlockPos[] getBlockLocations(ItemStack ist) {
		NBTTagCompound tagCompound = ist.getTagCompound();
		if(tagCompound == null) {
			tagCompound = new NBTTagCompound();
		}

		if(!tagCompound.hasKey("BlockLocations"))
			tagCompound.setTag("BlockLocations", new NBTTagList());
		NBTTagList tagList = tagCompound.getTagList("BlockLocations", 10);

		BlockPos[] locations = new BlockPos[tagList.tagCount()];

		for(int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound nbtLocation = (NBTTagCompound) tagList.get(i);
			locations[i] = new BlockPos(nbtLocation.getInteger("x"), nbtLocation.getInteger("y"), nbtLocation.getInteger("z"));
		}

		return locations;
	}

	private void doFreezeCheck(@Nonnull ItemStack ist, int x, int y, int z, World world, int xOff, int zOff) {
		x += xOff;
		z += zOff;
		IBlockState blockState = world.getBlockState(new BlockPos(x, y, z));
		if(blockState.getMaterial() == Material.WATER && blockState.getValue(BlockLiquid.LEVEL) == 0 && world.isAirBlock(new BlockPos(x, y + 1, z))) {
			addFrozenBlockToList(ist, x, y, z);
			world.setBlockState(new BlockPos(x, y, z), Blocks.PACKED_ICE.getDefaultState());

			for(int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = world.rand.nextFloat();
				float yVel = world.rand.nextFloat() + 0.5F;
				float zVel = world.rand.nextFloat();
				world.spawnParticle(EnumParticleTypes.REDSTONE, x + xVel, y + yVel, z + zVel, 0.75F, 0.75F, 1.0F);
			}
		} else if(blockState.getMaterial() == Material.LAVA && blockState.getValue(BlockLiquid.LEVEL) == 0) {
			addFrozenBlockToList(ist, x, y, z);
			world.setBlockState(new BlockPos(x, y, z), Blocks.OBSIDIAN.getDefaultState());
			for(int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
				float xVel = world.rand.nextFloat();
				float yVel = world.rand.nextFloat() + 0.5F;
				float zVel = world.rand.nextFloat();
				world.spawnParticle(world.rand.nextInt(3) == 0 ? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.SMOKE_NORMAL, x + xVel, y + yVel, z + zVel, 0.0D, 0.2D, 0.0D);

			}

		}
	}

	private void doThawCheck(@Nonnull ItemStack ist, int x, int y, int z, World world) {
		IBlockState blockState = world.getBlockState(new BlockPos(x, y, z));
		if(blockState == Blocks.PACKED_ICE.getDefaultState()) {
			if(removeFrozenBlockFromList(ist, x, y, z)) {
				world.setBlockState(new BlockPos(x, y, z), Blocks.WATER.getDefaultState());
				for(int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
					float xVel = world.rand.nextFloat();
					float yVel = world.rand.nextFloat() + 0.5F;
					float zVel = world.rand.nextFloat();
					world.spawnParticle(world.rand.nextInt(3) == 0 ? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.SMOKE_NORMAL, x + xVel, y + yVel, z + zVel, 0.0D, 0.2D, 0.0D);

				}
			}
		} else if(blockState == Blocks.OBSIDIAN.getDefaultState()) {
			if(removeFrozenBlockFromList(ist, x, y, z)) {
				world.setBlockState(new BlockPos(x, y, z), Blocks.LAVA.getDefaultState());

				float red = 1.0F;
				float green = 0.0F;
				float blue = 0.0F;

				for(int particleNum = world.rand.nextInt(3); particleNum < 2; ++particleNum) {
					float xVel = world.rand.nextFloat();
					float yVel = world.rand.nextFloat() + 0.5F;
					float zVel = world.rand.nextFloat();
					world.spawnParticle(EnumParticleTypes.REDSTONE, x + xVel, y + yVel, z + zVel, red, green, blue);
				}
			}
		}
	}

	private void addFrozenBlockToList(ItemStack ist, int x, int y, int z) {
		NBTTagCompound tagCompound = ist.getTagCompound();
		if(tagCompound == null) {
			tagCompound = new NBTTagCompound();
		}

		if(!tagCompound.hasKey("BlockLocations"))
			tagCompound.setTag("BlockLocations", new NBTTagList());
		NBTTagList tagList = tagCompound.getTagList("BlockLocations", 10);

		NBTTagCompound newTagData = new NBTTagCompound();
		newTagData.setInteger("x", x);
		newTagData.setInteger("y", y);
		newTagData.setInteger("z", z);

		tagList.appendTag(newTagData);

		tagCompound.setTag("BlockLocations", tagList);

		ist.setTagCompound(tagCompound);
	}

	private boolean removeFrozenBlockFromList(ItemStack ist, int x, int y, int z) {
		NBTTagCompound tagCompound = ist.getTagCompound();
		if(tagCompound == null) {
			tagCompound = new NBTTagCompound();
		}

		if(!tagCompound.hasKey("BlockLocations"))
			tagCompound.setTag("BlockLocations", new NBTTagList());
		NBTTagList tagList = tagCompound.getTagList("BlockLocations", 10);

		boolean removedBlock = false;

		for(int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
			if(tagItemData.getInteger("x") == x && tagItemData.getInteger("y") == y && tagItemData.getInteger("z") == z) {
				tagItemData.setBoolean("remove", true);
				removedBlock = true;
			}
		}

		NBTTagList newTagList = new NBTTagList();
		for(int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
			if(!tagItemData.getBoolean("remove")) {
				NBTTagCompound newTagData = new NBTTagCompound();
				newTagData.setInteger("x", tagItemData.getInteger("x"));
				newTagData.setInteger("y", tagItemData.getInteger("y"));
				newTagData.setInteger("z", tagItemData.getInteger("z"));
				newTagList.appendTag(newTagData);
			}
		}

		tagCompound.setTag("BlockLocations", newTagList);
		ist.setTagCompound(tagCompound);
		return removedBlock;
	}
}
