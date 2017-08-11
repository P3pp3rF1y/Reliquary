package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;
import xreliquary.Reliquary;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemDestructionCatalyst extends ItemToggleable {

	public ItemDestructionCatalyst() {
		super(Names.Items.DESTRUCTION_CATALYST);
		this.setMaxStackSize(1);
		canRepair = false;
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	public void addInformation(ItemStack ist, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			return;
		this.formatTooltip(ImmutableMap.of("charge", Integer.toString(NBTHelper.getInteger("gunpowder", ist))), ist, tooltip);
		if(this.isEnabled(ist))
			LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", TextFormatting.GRAY + Items.GUNPOWDER.getItemStackDisplayName(new ItemStack(Items.GUNPOWDER))), tooltip);
		LanguageHelper.formatTooltip("tooltip.absorb", null, tooltip);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if(NBTHelper.getInteger("gunpowder", stack) > gunpowderCost() || player.capabilities.isCreativeMode) {
			doExplosion(world, pos, side, player, stack);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity e, int i, boolean b) {
		if(world.isRemote)
			return;
		EntityPlayer player = null;
		if(e instanceof EntityPlayer) {
			player = (EntityPlayer) e;
		}
		if(player == null)
			return;

		if(this.isEnabled(ist)) {
			if(NBTHelper.getInteger("gunpowder", ist) + gunpowderWorth() < gunpowderLimit()) {
				if(InventoryHelper.consumeItem(new ItemStack(Items.GUNPOWDER), player)) {
					NBTHelper.setInteger("gunpowder", ist, NBTHelper.getInteger("gunpowder", ist) + gunpowderWorth());
				}
			}
		}
	}

	private int getExplosionRadius() {
		return Settings.Items.DestructionCatalyst.explosionRadius;
	}

	private boolean centeredExplosion() {
		return Settings.Items.DestructionCatalyst.centeredExplosion;
	}

	private boolean perfectCube() {
		return Settings.Items.DestructionCatalyst.perfectCube;
	}

	private void doExplosion(World world, BlockPos pos, EnumFacing side, EntityPlayer player, @Nonnull ItemStack stack) {
		boolean destroyedSomething = false;
		boolean playOnce = true;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if(!centeredExplosion()) {
			y = pos.getY() + (side == EnumFacing.DOWN ? getExplosionRadius() : side == EnumFacing.UP ? -getExplosionRadius() : 0);
			z = pos.getZ() + (side == EnumFacing.NORTH ? getExplosionRadius() : side == EnumFacing.SOUTH ? -getExplosionRadius() : 0);
			x = pos.getX() + (side == EnumFacing.WEST ? getExplosionRadius() : side == EnumFacing.EAST ? -getExplosionRadius() : 0);
		}
		for(int xD = -getExplosionRadius(); xD <= getExplosionRadius(); xD++) {
			for(int yD = -getExplosionRadius(); yD <= getExplosionRadius(); yD++) {
				for(int zD = -getExplosionRadius(); zD <= getExplosionRadius(); zD++) {
					if(!perfectCube()) {
						BlockPos origin = new BlockPos(x, y, z);
						BlockPos target = new BlockPos(x + xD, y + yD, z + zD);
						double distance = origin.distanceSq(target);
						if(distance >= getExplosionRadius())
							continue;
					}

					if(isBreakable(RegistryHelper.getBlockRegistryName(world.getBlockState(new BlockPos(x + xD, y + yD, z + zD)).getBlock()))) {
						world.setBlockState(new BlockPos(x + xD, y + yD, z + zD), Blocks.AIR.getDefaultState());
						if(world.rand.nextInt(2) == 0) {
							world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x + xD + (world.rand.nextFloat() - 0.5F), y + yD + (world.rand.nextFloat() - 0.5F), z + zD + (world.rand.nextFloat() - 0.5F), 0.0D, 0.0D, 0.0D);
						}
						destroyedSomething = true;
						if(playOnce) {
							world.playSound(x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F, false);
							playOnce = false;
						}
					}
				}
			}
		}
		if(destroyedSomething && !player.capabilities.isCreativeMode) {
			NBTHelper.setInteger("gunpowder", stack, NBTHelper.getInteger("gunpowder", stack) - gunpowderCost());
		}
	}

	private boolean isBreakable(String id) {
		return ArrayUtils.contains(Settings.Items.DestructionCatalyst.mundaneBlocks, id);
	}

	private int gunpowderCost() {
		return Settings.Items.DestructionCatalyst.gunpowderCost;
	}

	private int gunpowderWorth() {
		return Settings.Items.DestructionCatalyst.gunpowderWorth;
	}

	private int gunpowderLimit() {
		return Settings.Items.DestructionCatalyst.gunpowderLimit;
	}
}
