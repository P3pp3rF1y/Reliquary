package xreliquary.items;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import xreliquary.Reliquary;
import xreliquary.blocks.BlockPedestal;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.init.ModItems;
import xreliquary.network.PacketHandler;
import xreliquary.network.PacketMobCharmDamage;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Xeno on 10/11/2014.
 */
public class ItemMobCharm extends ItemBase {
	private static final String TYPE_TAG = "type";

	public ItemMobCharm() {
		super(Names.Items.MOB_CHARM);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(Settings.MobCharm.durability);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.canRepair = false;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item." + Names.Items.MOB_CHARM + "_" + getType(ist);
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> subItems) {
		for(byte i = 0; i < Reference.MOB_CHARM.COUNT_TYPES; i++) {
			ItemStack subItem = new ItemStack(item);
			setType(subItem, i);
			subItems.add(subItem);
		}
	}

	public byte getType(ItemStack stack) {
		if(stack.getItem() != ModItems.mobCharm || stack.getTagCompound() == null || !stack.getTagCompound().hasKey(TYPE_TAG))
			return -1;

		return stack.getTagCompound().getByte(TYPE_TAG);
	}

	public void setType(ItemStack stack, byte type) {
		NBTTagCompound compound = stack.getTagCompound();

		if(compound == null)
			compound = new NBTTagCompound();

		compound.setByte(TYPE_TAG, type);

		stack.setTagCompound(compound);
	}

	private byte getMobCharmTypeForEntity(Entity entity) {
		if(entity instanceof EntityGhast) {
			return Reference.MOB_CHARM.GHAST_META;
		} else if(entity instanceof EntityMagmaCube) {
			return Reference.MOB_CHARM.MAGMA_CUBE_META;
		} else if(entity instanceof EntitySlime) {
			return Reference.MOB_CHARM.SLIME_META;
		} else if(entity instanceof EntityPigZombie) {
			return Reference.MOB_CHARM.ZOMBIE_PIGMAN_META;
		} else if(entity instanceof EntityZombie) {
			return Reference.MOB_CHARM.ZOMBIE_META;
		} else if(entity instanceof EntitySkeleton) {
			if(((EntitySkeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
				return Reference.MOB_CHARM.WITHER_SKELETON_META;
			} else {
				return Reference.MOB_CHARM.SKELETON_META;
			}
		} else if(entity instanceof EntityCreeper) {
			return Reference.MOB_CHARM.CREEPER_META;
		} else if(entity instanceof EntityWitch) {
			return Reference.MOB_CHARM.WITCH_META;
		} else if(entity instanceof EntityCaveSpider) {
			return Reference.MOB_CHARM.CAVE_SPIDER_META;
		} else if(entity instanceof EntitySpider) {
			return Reference.MOB_CHARM.SPIDER_META;
		} else if(entity instanceof EntityEnderman) {
			return Reference.MOB_CHARM.ENDERMAN_META;
		} else if(entity instanceof EntityBlaze) {
			return Reference.MOB_CHARM.BLAZE_META;
		} else if(entity instanceof EntityGuardian && !((EntityGuardian) entity).isElder()) {
			return Reference.MOB_CHARM.GUARDIAN_META;
		}

		return -1;
	}

	@SubscribeEvent
	public void onEntityTargetedEvent(LivingSetAttackTargetEvent event) {
		if(event.getTarget() == null)
			return;
		if(!(event.getTarget() instanceof EntityPlayer) || event.getTarget() instanceof FakePlayer)
			return;
		if(!(event.getEntity() instanceof EntityLiving))
			return;

		EntityPlayer player = (EntityPlayer) event.getTarget();
		boolean resetTarget = false;
		EntityLiving entity = (EntityLiving) event.getEntity();

		if(entity instanceof EntityZombie && !(entity instanceof EntityPigZombie)) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.ZOMBIE_META);
		} else if(entity instanceof EntitySkeleton) {
			if(((EntitySkeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
				resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.WITHER_SKELETON_META);
			} else {
				resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.SKELETON_META);
			}
		} else if(entity instanceof EntityCreeper) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.CREEPER_META);
		} else if(entity instanceof EntityWitch) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.WITCH_META);
		} else if(entity instanceof EntityCaveSpider) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.CAVE_SPIDER_META);
		} else if(entity instanceof EntitySpider) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.SPIDER_META);
		} else if(entity instanceof EntityEnderman) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.ENDERMAN_META);
		} else if(entity instanceof EntityBlaze) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.BLAZE_META);
		} else if(entity instanceof EntityGhast) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.GHAST_META);
		} else if(entity instanceof EntityMagmaCube) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.MAGMA_CUBE_META);
		} else if(entity instanceof EntitySlime) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.SLIME_META);
		}else if(entity instanceof EntityGuardian && !((EntityGuardian) entity).isElder()) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.GUARDIAN_META);
		}

		if(resetTarget) {
			entity.setAttackTarget(null);
			entity.setRevengeTarget(null);
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		if(!(event.getEntity() instanceof EntityLiving))
			return;
		EntityLiving entity = (EntityLiving) event.getEntity();

		if(entity.getAttackTarget() == null || !(entity.getAttackTarget() instanceof EntityPlayer) || entity.getAttackTarget() instanceof FakePlayer)
			return;

		EntityPlayer player = (EntityPlayer) entity.getAttackTarget();
		boolean resetTarget = false;

		if(entity instanceof EntityGhast) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.GHAST_META);
		} else if(entity instanceof EntityMagmaCube) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.MAGMA_CUBE_META);
		} else if(entity instanceof EntitySlime) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.SLIME_META);
		} else if(entity instanceof EntityEnderman) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.ENDERMAN_META);
		} else if(entity instanceof EntityPigZombie) {
			resetTarget = isMobCharmPresent(player, Reference.MOB_CHARM.ZOMBIE_PIGMAN_META);
		}

		if(resetTarget) {
			entity.setAttackTarget(null);
			entity.setRevengeTarget(null);
			if(entity instanceof EntityPigZombie) {
				//need to reset ai task because it doesn't get reset with setAttackTarget or setRevengeTarget and keeps player as target
				for (EntityAITasks.EntityAITaskEntry aiTask : entity.targetTasks.taskEntries) {
					if (aiTask.action instanceof EntityAIHurtByTarget) {
						aiTask.action.resetTask();
						break;
					}
				}

				//also need to reset anger target because apparently setRevengeTarget doesn't set this to null
				resetAngerTarget((EntityPigZombie) entity);
			}
		}
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		if(event.getSource() == null || event.getSource().getEntity() == null || !(event.getSource().getEntity() instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) event.getSource().getEntity();

		if (!damagePlayersMobCharm(player, event.getEntity()))
			damageMobCharmInPedestal(player, event.getEntity());
	}

	private void damageMobCharmInPedestal(EntityPlayer player, Entity entity) {
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(player.dimension, player.getPosition(), Settings.MobCharm.pedestalRange);
		byte mobCharmType = ModItems.mobCharm.getMobCharmTypeForEntity(entity);
		World world = player.getEntityWorld();

		for(BlockPos pos : pedestalPositions) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof TileEntityPedestal) {
				IBlockState blockState = world.getBlockState(pos);
				TileEntityPedestal pedestal = (TileEntityPedestal) te;
				if (blockState.getValue(BlockPedestal.ENABLED)) { //TODO this needs a field / method in TEPedestal instead of having to load blockstate
					for(int slot = 0; slot < pedestal.getSizeInventory(); slot++) {
						ItemStack slotStack = pedestal.getStackInSlot(slot);
						if(slotStack != null) {
							if(slotStack.getItem() == ModItems.mobCharm && ModItems.mobCharm.getType(slotStack) == mobCharmType) {
								if(slotStack.getItemDamage() + Settings.MobCharm.damagePerKill > slotStack.getMaxDamage()) {
									((TileEntityPedestal) te).setInventorySlotContents(slot, null);
								} else {
									slotStack.setItemDamage(slotStack.getItemDamage() + Settings.MobCharm.damagePerKill);
								}
								return;
							} else if(slotStack.getItem() == ModItems.mobCharmBelt) {
								int damage = ModItems.mobCharmBelt.damageCharmType(slotStack, mobCharmType);

								if(damage > -1) {
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean damagePlayersMobCharm(EntityPlayer player, Entity entity) {
		if (player.capabilities.isCreativeMode)
			return true;

		byte mobCharmType = ModItems.mobCharm.getMobCharmTypeForEntity(entity);

		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if(player.inventory.mainInventory[slot] == null)
				continue;
			ItemStack slotStack = player.inventory.mainInventory[slot];

			if(slotStack.getItem() == ModItems.mobCharm && ModItems.mobCharm.getType(slotStack) == mobCharmType) {
				ItemStack playersMobCharm = player.inventory.mainInventory[slot];
				if(playersMobCharm.getItemDamage() + Settings.MobCharm.damagePerKill > playersMobCharm.getMaxDamage()) {
					player.inventory.mainInventory[slot] = null;
					PacketHandler.networkWrapper.sendTo(new PacketMobCharmDamage(mobCharmType, ModItems.mobCharm.getMaxDamage() + 1, slot), (EntityPlayerMP) player);
				} else {
					playersMobCharm.setItemDamage(playersMobCharm.getItemDamage() + Settings.MobCharm.damagePerKill);
					PacketHandler.networkWrapper.sendTo(new PacketMobCharmDamage(mobCharmType, playersMobCharm.getItemDamage(), slot), (EntityPlayerMP) player);
				}
				return true;
			}
			if(damageMobCharmInBelt((EntityPlayerMP) player, mobCharmType, slotStack))
				return true;
		}

		if(Loader.isModLoaded(Compatibility.MOD_ID.BAUBLES)) {
			IBaublesItemHandler inventoryBaubles = BaublesApi.getBaublesHandler(player);

			for(int i = 0; i < inventoryBaubles.getSlots(); i++) {
				ItemStack baubleStack = inventoryBaubles.getStackInSlot(i);

				if(baubleStack == null)
					continue;

				if(damageMobCharmInBelt((EntityPlayerMP) player, mobCharmType, baubleStack))
					return true;
			}
		}

		return false;
	}

	private boolean damageMobCharmInBelt(EntityPlayerMP player, byte mobCharmType, ItemStack slotStack) {
		if(slotStack.getItem() == ModItems.mobCharmBelt) {
			int damage = ModItems.mobCharmBelt.damageCharmType(slotStack, mobCharmType);

			if(damage > -1) {
				PacketHandler.networkWrapper.sendTo(new PacketMobCharmDamage(mobCharmType, damage, -mobCharmType), player);
				return true;
			}
		}
		return false;
	}

	private static final Field SET_ANGER_TARGET = ReflectionHelper
			.findField(EntityPigZombie.class, "field_175459_bn", "angerTargetUUID");

	private void resetAngerTarget(EntityPigZombie zombiePigman) {
		try {
			SET_ANGER_TARGET.set(zombiePigman, null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private boolean isMobCharmPresent(EntityPlayer player, byte type) {
		return playerHasMobCharm(player, type) || pedestalWithCharmInRange(player, type);
	}

	private boolean playerHasMobCharm(EntityPlayer player, byte type) {
		for(int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
			if(player.inventory.mainInventory[slot] == null)
				continue;
			ItemStack slotStack = player.inventory.mainInventory[slot];
			if(slotStack.getItem() == ModItems.mobCharm && ModItems.mobCharm.getType(slotStack) == type)
				return true;
			if(slotStack.getItem() == ModItems.mobCharmBelt && ModItems.mobCharmBelt.hasCharmType(slotStack, type))
				return true;
		}

		if(Loader.isModLoaded(Compatibility.MOD_ID.BAUBLES)) {
			IBaublesItemHandler inventoryBaubles = BaublesApi.getBaublesHandler(player);

			for(int i = 0; i < inventoryBaubles.getSlots(); i++) {
				ItemStack baubleStack = inventoryBaubles.getStackInSlot(i);
				if(baubleStack != null && baubleStack.getItem() == ModItems.mobCharmBelt && ModItems.mobCharmBelt.hasCharmType(baubleStack, type))
					return true;
			}
		}

		return false;
	}

	private boolean pedestalWithCharmInRange(EntityPlayer player, byte type) {
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(player.dimension, player.getPosition(), Settings.MobCharm.pedestalRange);

		World world = player.getEntityWorld();
		for(BlockPos pos : pedestalPositions) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof TileEntityPedestal) {
				IBlockState blockState = world.getBlockState(pos);
				TileEntityPedestal pedestal = (TileEntityPedestal) te;
				if (blockState.getValue(BlockPedestal.ENABLED)) { //TODO this needs a field / method in TEPedestal instead of having to load blockstate
					for(int slot = 0; slot < pedestal.getSizeInventory(); slot++) {
						ItemStack slotStack = pedestal.getStackInSlot(slot);
						if(slotStack != null) {
							if(slotStack.getItem() == ModItems.mobCharm && ModItems.mobCharm.getType(slotStack) == type)
								return true;
							if(slotStack.getItem() == ModItems.mobCharmBelt && ModItems.mobCharmBelt.hasCharmType(slotStack, type))
								return true;
						}
					}
				}
			}
		}

		return false;
	}
}
