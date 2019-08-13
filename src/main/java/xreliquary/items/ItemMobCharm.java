package xreliquary.items;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.Reliquary;
import xreliquary.blocks.BlockPedestal;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.init.ModItems;
import xreliquary.network.PacketHandler;
import xreliquary.network.PacketMobCharmDamage;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.MobHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ItemMobCharm extends ItemBase {
	public static final int ZOMBIE_META = 0;
	public static final int SKELETON_META = 1;
	public static final int WITHER_SKELETON_META = 2;
	public static final int CREEPER_META = 3;
	public static final int WITCH_META = 4;
	public static final int ZOMBIE_PIGMAN_META = 5;
	public static final int CAVE_SPIDER_META = 6;
	public static final int SPIDER_META = 7;
	public static final int ENDERMAN_META = 8;
	public static final int GHAST_META = 9;
	public static final int SLIME_META = 10;
	public static final int MAGMA_CUBE_META = 11;
	public static final int BLAZE_META = 12;
	public static final int GUARDIAN_META = 13;
	private static final String TYPE_TAG = "type";

	public static final Map<Integer, MobCharmDefinition> CHARM_DEFINITIONS =
			new ImmutableMap.Builder<Integer, MobCharmDefinition>()
					.put(ZOMBIE_META, new MobCharmDefinition(ZOMBIE_META, e -> e instanceof EntityZombie && !(e instanceof EntityPigZombie), "zombie"))
					.put(SKELETON_META, new MobCharmDefinition(SKELETON_META, e -> e instanceof EntitySkeleton || e instanceof EntityStray, "skeleton"))
					.put(WITHER_SKELETON_META, new MobCharmDefinition(WITHER_SKELETON_META, e -> e instanceof EntityWitherSkeleton, "wither_skeleton"))
					.put(CREEPER_META, new MobCharmDefinition(CREEPER_META, e -> e instanceof EntityCreeper, "creeper"))
					.put(WITCH_META, new MobCharmDefinition(WITCH_META, e -> e instanceof EntityWitch, "witch"))
					.put(ZOMBIE_PIGMAN_META, new MobCharmDefinition(ZOMBIE_PIGMAN_META, e -> e instanceof EntityPigZombie, "zombie_pigman"))
					.put(CAVE_SPIDER_META, new MobCharmDefinition(CAVE_SPIDER_META, e -> e instanceof EntityCaveSpider, "cave_spider"))
					.put(SPIDER_META, new MobCharmDefinition(SPIDER_META, e -> e instanceof EntitySpider && !(e instanceof EntityCaveSpider), "spider"))
					.put(ENDERMAN_META, new MobCharmDefinition(ENDERMAN_META, e -> e instanceof EntityEnderman, "enderman"))
					.put(GHAST_META, new MobCharmDefinition(GHAST_META, e -> e instanceof EntityGhast, "ghast"))
					.put(SLIME_META, new MobCharmDefinition(SLIME_META, e -> e instanceof EntitySlime && !(e instanceof EntityMagmaCube), "slime"))
					.put(MAGMA_CUBE_META, new MobCharmDefinition(MAGMA_CUBE_META, e -> e instanceof EntityMagmaCube, "magma_cube"))
					.put(BLAZE_META, new MobCharmDefinition(BLAZE_META, e -> e instanceof EntityBlaze, "blaze"))
					.put(GUARDIAN_META, new MobCharmDefinition(GUARDIAN_META, e -> e instanceof EntityGuardian, "guardian"))
					.build();

	public ItemMobCharm() {
		super(Names.Items.MOB_CHARM);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(Settings.Items.MobCharm.durability);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.canRepair = false;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment.type == EnumEnchantmentType.BREAKABLE ? false : super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack ist) {
		return "item." + Names.Items.MOB_CHARM + "_" + getType(ist);
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		if (!isInCreativeTab(tab))
			return;

		for (byte i = 0; i < CHARM_DEFINITIONS.size(); i++) {
			ItemStack subItem = new ItemStack(this);
			setType(subItem, i);
			subItems.add(subItem);
		}
	}

	public byte getType(ItemStack stack) {
		if (stack.getItem() != this || stack.getTagCompound() == null || !stack.getTagCompound().hasKey(TYPE_TAG))
			return -1;

		return stack.getTagCompound().getByte(TYPE_TAG);
	}

	private void setType(ItemStack stack, byte type) {
		NBTTagCompound compound = stack.getTagCompound();

		if (compound == null)
			compound = new NBTTagCompound();

		compound.setByte(TYPE_TAG, type);

		stack.setTagCompound(compound);
	}

	public static ItemStack getCharmStack(byte meta) {
		ItemStack mobCharm = new ItemStack(ModItems.mobCharm);

		ModItems.mobCharm.setType(mobCharm, meta);

		return mobCharm;
	}

	private int getMobCharmTypeForEntity(Entity entity) {
		for (MobCharmDefinition def : CHARM_DEFINITIONS.values()) {
			if (def.appliesTo(entity)) {
				return def.getMeta();
			}
		}
		return -1;
	}

	@SubscribeEvent
	public void onEntityTargetedEvent(LivingSetAttackTargetEvent event) {
		if (event.getTarget() == null)
			return;
		if (!(event.getTarget() instanceof EntityPlayer) || event.getTarget() instanceof FakePlayer)
			return;
		if (!(event.getEntity() instanceof EntityLiving))
			return;

		EntityPlayer player = (EntityPlayer) event.getTarget();
		EntityLiving entity = (EntityLiving) event.getEntity();

		if (isMobCharmPresent(player, getMobCharmTypeForEntity(entity))) {
			MobHelper.resetTarget(entity);
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		if (!(event.getEntity() instanceof EntityLiving))
			return;
		EntityLiving entity = (EntityLiving) event.getEntity();

		EntityPlayer player;
		if (isPlayer(entity.getAttackTarget())) {
			player = (EntityPlayer) entity.getAttackTarget();
		} else if (isPlayer(entity.getRevengeTarget())) {
			player = (EntityPlayer) entity.getRevengeTarget();
		} else {
			return;
		}

		if (isMobCharmPresent(player, getMobCharmTypeForEntity(entity))) {
			MobHelper.resetTarget(entity, true, true);
		}
	}

	private boolean isPlayer(EntityLivingBase target) {
		return target instanceof EntityPlayer && !(target instanceof FakePlayer);
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		if (event.getSource() == null || event.getSource().getTrueSource() == null || !(event.getSource().getTrueSource() instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();

		if (!damagePlayersMobCharm(player, event.getEntity()))
			damageMobCharmInPedestal(player, event.getEntity());
	}

	private void damageMobCharmInPedestal(EntityPlayer player, Entity entity) {
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(player.dimension, player.getPosition(), Settings.Items.MobCharm.pedestalRange);
		int mobCharmType = getMobCharmTypeForEntity(entity);
		World world = player.getEntityWorld();

		for (BlockPos pos : pedestalPositions) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof TileEntityPedestal) {
				IBlockState blockState = world.getBlockState(pos);
				TileEntityPedestal pedestal = (TileEntityPedestal) te;
				if (blockState.getValue(BlockPedestal.ENABLED)) { //TODO this needs a field / method in TEPedestal instead of having to load blockstate
					for (int slot = 0; slot < pedestal.getSizeInventory(); slot++) {
						ItemStack slotStack = pedestal.getStackInSlot(slot);
						if (slotStack.getItem() == this && getType(slotStack) == mobCharmType) {
							if (slotStack.getItemDamage() + Settings.Items.MobCharm.damagePerKill > slotStack.getMaxDamage()) {
								((TileEntityPedestal) te).setInventorySlotContents(slot, ItemStack.EMPTY);
							} else {
								slotStack.setItemDamage(slotStack.getItemDamage() + Settings.Items.MobCharm.damagePerKill);
							}
							return;
						} else if (slotStack.getItem() == ModItems.mobCharmBelt) {
							int damage = ModItems.mobCharmBelt.damageCharmType(slotStack, mobCharmType);

							if (damage > -1) {
								return;
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

		int mobCharmType = getMobCharmTypeForEntity(entity);

		for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			ItemStack stack = player.inventory.mainInventory.get(slot);

			if (stack.isEmpty())
				continue;
			if (stack.getItem() == this && getType(stack) == mobCharmType) {
				if (stack.getItemDamage() + Settings.Items.MobCharm.damagePerKill > stack.getMaxDamage()) {
					player.inventory.mainInventory.set(slot, ItemStack.EMPTY);
					PacketHandler.networkWrapper.sendTo(new PacketMobCharmDamage(mobCharmType, stack.getMaxDamage() + 1, slot), (EntityPlayerMP) player);
				} else {
					stack.setItemDamage(stack.getItemDamage() + Settings.Items.MobCharm.damagePerKill);
					PacketHandler.networkWrapper.sendTo(new PacketMobCharmDamage(mobCharmType, stack.getItemDamage(), slot), (EntityPlayerMP) player);
				}
				return true;
			}
			if (damageMobCharmInBelt((EntityPlayerMP) player, mobCharmType, stack))
				return true;
		}

		if (Loader.isModLoaded(Compatibility.MOD_ID.BAUBLES)) {
			IBaublesItemHandler inventoryBaubles = BaublesApi.getBaublesHandler(player);

			for (int i = 0; i < inventoryBaubles.getSlots(); i++) {
				ItemStack baubleStack = inventoryBaubles.getStackInSlot(i);

				if (baubleStack.isEmpty())
					continue;

				if (damageMobCharmInBelt((EntityPlayerMP) player, mobCharmType, baubleStack))
					return true;
			}
		}

		return false;
	}

	private boolean damageMobCharmInBelt(EntityPlayerMP player, int mobCharmType, ItemStack slotStack) {
		if (slotStack.getItem() == ModItems.mobCharmBelt) {
			int damage = ModItems.mobCharmBelt.damageCharmType(slotStack, mobCharmType);

			if (damage > -1) {
				PacketHandler.networkWrapper.sendTo(new PacketMobCharmDamage(mobCharmType, damage, -mobCharmType), player);
				return true;
			}
		}
		return false;
	}

	private boolean isMobCharmPresent(EntityPlayer player, int type) {
		return playerHasMobCharm(player, type) || pedestalWithCharmInRange(player, type);
	}

	private boolean playerHasMobCharm(EntityPlayer player, int type) {
		if (type < 0) {
			return false;
		}

		for (ItemStack slotStack : player.inventory.mainInventory) {
			if (slotStack.isEmpty())
				continue;
			if (slotStack.getItem() == this && getType(slotStack) == type)
				return true;
			if (slotStack.getItem() == ModItems.mobCharmBelt && ModItems.mobCharmBelt.hasCharmType(slotStack, type))
				return true;
		}

		if (Loader.isModLoaded(Compatibility.MOD_ID.BAUBLES)) {
			IBaublesItemHandler inventoryBaubles = BaublesApi.getBaublesHandler(player);

			for (int i = 0; i < inventoryBaubles.getSlots(); i++) {
				ItemStack baubleStack = inventoryBaubles.getStackInSlot(i);
				if (!baubleStack.isEmpty() && baubleStack.getItem() == ModItems.mobCharmBelt && ModItems.mobCharmBelt.hasCharmType(baubleStack, type))
					return true;
			}
		}

		return false;
	}

	private boolean pedestalWithCharmInRange(EntityPlayer player, int type) {
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(player.dimension, player.getPosition(), Settings.Items.MobCharm.pedestalRange);

		World world = player.getEntityWorld();
		for (BlockPos pos : pedestalPositions) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof TileEntityPedestal) {
				IBlockState blockState = world.getBlockState(pos);
				TileEntityPedestal pedestal = (TileEntityPedestal) te;
				if (blockState.getValue(BlockPedestal.ENABLED)) { //TODO this needs a field / method in TEPedestal instead of having to load blockstate
					for (int slot = 0; slot < pedestal.getSizeInventory(); slot++) {
						ItemStack slotStack = pedestal.getStackInSlot(slot);
						if (slotStack.getItem() == this && getType(slotStack) == type)
							return true;
						if (slotStack.getItem() == ModItems.mobCharmBelt && ModItems.mobCharmBelt.hasCharmType(slotStack, type))
							return true;
					}
				}
			}
		}

		return false;
	}

	public static class MobCharmDefinition {
		private int meta;
		private final Function<Entity, Boolean> appliesToEntity;
		private String eggEntityName;

		public MobCharmDefinition(int meta, Function<Entity, Boolean> appliesToEntity, String eggEntityName) {
			this.meta = meta;
			this.appliesToEntity = appliesToEntity;
			this.eggEntityName = eggEntityName;
		}

		public int getMeta() {
			return meta;
		}

		public String getEggEntityName() {
			return eggEntityName;
		}

		public boolean appliesTo(Entity e) {
			return appliesToEntity.apply(e);
		}
	}
}