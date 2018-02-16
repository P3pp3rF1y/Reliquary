package xreliquary.items;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.block.subtile.functional.SubTileSolegnolia;
import xreliquary.Reliquary;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalActionItem;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.client.ClientProxy;
import xreliquary.init.ModFluids;
import xreliquary.init.ModItems;
import xreliquary.network.PacketFortuneCoinTogglePressed;
import xreliquary.network.PacketHandler;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.XpHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = Compatibility.MOD_ID.BAUBLES, striprefs = true)
public class ItemFortuneCoin extends ItemBase implements IPedestalActionItem, IBauble {

	public ItemFortuneCoin() {
		super(Names.Items.FORTUNE_COIN);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		canRepair = false;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public void onEquipped(ItemStack stack, EntityLivingBase player) {
/*
	TODO add back if baubles stops triggering this on every GUI open
		if(player.world.isRemote)
			player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 2.2F));
*/

	}

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public BaubleType getBaubleType(ItemStack stack) {
		return BaubleType.AMULET;
	}

	@Override
	@Optional.Method(modid = Compatibility.MOD_ID.BAUBLES)
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		this.onUpdate(stack, player.world, player, 0, false);
	}

	@Override
	protected void addMoreInformation(ItemStack stack, @Nullable World world, List<String> tooltip) {
		LanguageHelper.formatTooltip(getUnlocalizedNameInefficiently(stack) + ".tooltip2", tooltip);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return isEnabled(stack);
	}

	private boolean isEnabled(ItemStack stack) {
		return NBTHelper.getBoolean("enabled", stack);
	}

	@Override
	public void onUpdate(ItemStack ist, World world, Entity entity, int i, boolean f) {
		if(world.isRemote)
			return;
		if(!disabledAudio())
			if(NBTHelper.getShort("soundTimer", ist) > 0) {
				if(NBTHelper.getShort("soundTimer", ist) % 2 == 0) {
					world.playSound(null, entity.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
				}
				NBTHelper.setShort("soundTimer", ist, (short) (NBTHelper.getShort("soundTimer", ist) - 1));
			}
		if(!isEnabled(ist))
			return;
		EntityPlayer player = null;
		if(entity instanceof EntityPlayer) {
			player = (EntityPlayer) entity;
		}
		if(player == null || player.isSpectator())
			return;
		scanForEntitiesInRange(world, player, getStandardPullDistance());
	}

	private void scanForEntitiesInRange(World world, EntityPlayer player, double d) {
		List<BlockPos> disablePositions = getDisablePositions(world, player.getPosition());
		List<EntityItem> iList = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(player.posX - d, player.posY - d, player.posZ - d, player.posX + d, player.posY + d, player.posZ + d));
		for(EntityItem item : iList) {
			//if entity is marked not to be picked up by magnets leave it alone - IE thing but may be more than that
			if(!canPickupItem(item, disablePositions)) {
				continue;
			}

			if(!checkForRoom(item.getItem(), player)) {
				continue;
			}

			item.setPickupDelay(0);
			if(player.getDistance(item) < 1.5D) {
				continue;
			}
			teleportEntityToPlayer(item, player);
			break;
		}
		List<EntityXPOrb> iList2 = world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(player.posX - d, player.posY - d, player.posZ - d, player.posX + d, player.posY + d, player.posZ + d));
		for(EntityXPOrb item : iList2) {
			if(player.xpCooldown > 0) {
				player.xpCooldown = 0;
			}
			if(player.getDistance(item) < 1.5D) {
				continue;
			}
			teleportEntityToPlayer(item, player);
			break;
		}
	}

	private boolean canPickupItem(EntityItem item, List<BlockPos> disablePositions) {
		if (item.getEntityData().getBoolean("PreventRemoteMovement"))
			return false;
		if (isInDisabledRange(item, disablePositions))
			return false;
		if (Compatibility.isLoaded(Compatibility.MOD_ID.BOTANIA)) {
			if (SubTileSolegnolia.hasSolegnoliaAround(item))
				return false;
		}
		return true;
	}

	private boolean isInDisabledRange(EntityItem item, List<BlockPos> disablePositions) {
		for (BlockPos disablePos : disablePositions) {
			if (Math.abs(item.getPosition().getX() - disablePos.getX()) < 5
				&& Math.abs(item.getPosition().getY() - disablePos.getY()) < 5
				&& Math.abs(item.getPosition().getZ() - disablePos.getZ()) < 5)
				return true;
		}
		return false;
	}

	private List<BlockPos> getDisablePositions(World world, BlockPos coinPos) {
		List<BlockPos> disablePositions = new ArrayList<>();
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(world.provider.getDimension(), coinPos, 10);

		for (BlockPos pos : pedestalPositions) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntityPedestal) {
				TileEntityPedestal pedestal = (TileEntityPedestal) te;

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

	private void teleportEntityToPlayer(Entity item, EntityPlayer player) {
		player.world.spawnParticle(EnumParticleTypes.SPELL_MOB, item.posX + 0.5D + player.world.rand.nextGaussian() / 8, item.posY + 0.2D, item.posZ + 0.5D + player.world.rand.nextGaussian() / 8, 0.9D, 0.9D, 0.0D);
		player.getLookVec();
		double x = player.posX + player.getLookVec().x * 0.2D;
		double y = player.posY;
		double z = player.posZ + player.getLookVec().z * 0.2D;
		item.setPosition(x, y, z);
		if(!disabledAudio()) {
			player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, 0.5F * ((player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.7F + 1.8F));
		}
	}

	private boolean checkForRoom(@Nonnull ItemStack stackToPickup, EntityPlayer player) {
		int remaining = stackToPickup.getCount();
		for(ItemStack inventoryStack : player.inventory.mainInventory) {
			if(inventoryStack.isEmpty())
				return true;

			if(inventoryStack.getItem() == stackToPickup.getItem() && inventoryStack.getItemDamage() == stackToPickup.getItemDamage()) {
				if(inventoryStack.getCount() + remaining <= inventoryStack.getMaxStackSize())
					return true;
				else {
					remaining -= (inventoryStack.getMaxStackSize() - inventoryStack.getCount());
				}
			} else if(inventoryStack.getItem() == ModItems.voidTear && ModItems.voidTear.isEnabled(inventoryStack) && ModItems.voidTear.canAbsorbStack(stackToPickup, inventoryStack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onUsingTick(ItemStack ist, EntityLivingBase entity, int count) {
		if(!(entity instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entity;

		scanForEntitiesInRange(player.world, player, getLongRangePullDistance());
	}

	private double getLongRangePullDistance() {
		return (double) Settings.Items.FortuneCoin.longRangePullDistance;
	}

	private double getStandardPullDistance() {
		return (double) Settings.Items.FortuneCoin.standardPullDistance;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 64;
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(player.isSneaking()) {
			if(!disabledAudio()) {
				NBTHelper.setShort("soundTimer", stack, (short) 6);
			}
			toggle(stack);
		} else {
			player.setActiveHand(hand);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	private boolean disabledAudio() {
		return Settings.Items.FortuneCoin.disableAudio;
	}

	@Override
	public void update(@Nonnull ItemStack stack, IPedestal pedestal) {
		World world = pedestal.getTheWorld();
		if(world.isRemote)
			return;

		if(isEnabled(stack)) {
			BlockPos pos = pedestal.getBlockPos();
			double d = getStandardPullDistance();

			List<BlockPos> disablePositions = getDisablePositions(world, pos);
			List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.getX() - d, pos.getY() - d, pos.getZ() - d, pos.getX() + d, pos.getY() + d, pos.getZ() + d));
			for(EntityItem entityItem : entities) {

				//if entity is marked not to be picked up by magnets leave it alone - IE thing but may be more than that
				if(!canPickupItem(entityItem, disablePositions)) {
					continue;
				}

				int numberAdded = pedestal.addToConnectedInventory(entityItem.getItem().copy());
				if(numberAdded > 0) {
					entityItem.getItem().setCount(entityItem.getItem().getCount() - numberAdded);

					if(entityItem.getItem().getCount() <= 0)
						entityItem.setDead();
				} else {
					pedestal.setActionCoolDown(20);
				}
			}

			List<EntityXPOrb> XPOrbs = world.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(pos.getX() - d, pos.getY() - d, pos.getZ() - d, pos.getX() + d, pos.getY() + d, pos.getZ() + d));
			for(EntityXPOrb xpOrb : XPOrbs) {
				int amountToTransfer = XpHelper.experienceToLiquid(xpOrb.xpValue);
				int amountAdded = pedestal.fillConnectedTank(new FluidStack(ModFluids.xpJuice(), amountToTransfer));

				if(amountAdded > 0) {
					xpOrb.setDead();

					if(amountToTransfer > amountAdded) {
						world.spawnEntity(new EntityXPOrb(world, pos.getX(), pos.getY(), pos.getZ(), XpHelper.liquidToExperience(amountToTransfer - amountAdded)));
					}
				} else {
					pedestal.setActionCoolDown(20);
				}
			}
		}
	}

	@Override
	public void onRemoved(@Nonnull ItemStack stack, IPedestal pedestal) {
	}

	@Override
	public void stop(@Nonnull ItemStack stack, IPedestal pedestal) {
	}

	public void toggle(ItemStack stack) {
		NBTHelper.setBoolean("enabled", stack, !isEnabled(stack));
	}

	/* EVENT HANDLING */
	@SubscribeEvent
	@SideOnly (Side.CLIENT)
	public void handleKeyInputEvent(TickEvent.ClientTickEvent event) {
		if(ClientProxy.FORTUNE_COIN_TOGGLE_KEYBIND.isPressed()) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			for (int slot=0; slot < player.inventory.mainInventory.size(); slot++) {
				ItemStack stack = player.inventory.mainInventory.get(slot);
				if(stack.getItem() == this) {
					PacketHandler.networkWrapper.sendToServer(new PacketFortuneCoinTogglePressed(PacketFortuneCoinTogglePressed.InventoryType.MAIN, slot));

					toggle(stack);
					return;
				}
			}
			if(player.inventory.offHandInventory.get(0).getItem() == this) {
				PacketHandler.networkWrapper.sendToServer(new PacketFortuneCoinTogglePressed(PacketFortuneCoinTogglePressed.InventoryType.OFF_HAND, 0));
				toggle(player.inventory.offHandInventory.get(0));
				return;
			}

			if(Loader.isModLoaded(Compatibility.MOD_ID.BAUBLES)) {
				IBaublesItemHandler inventoryBaubles = BaublesApi.getBaublesHandler(player);

				for(int slot = 0; slot < inventoryBaubles.getSlots(); slot++) {
					ItemStack baubleStack = inventoryBaubles.getStackInSlot(slot);

					if (baubleStack.getItem() == this) {
						PacketHandler.networkWrapper.sendToServer(new PacketFortuneCoinTogglePressed(PacketFortuneCoinTogglePressed.InventoryType.BAUBLES, slot));
						return;
					}
				}
			}
		}
	}
}
