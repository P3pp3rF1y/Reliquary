package xreliquary.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.registries.ForgeRegistries;
import xreliquary.blocks.tile.PedestalTileEntity;
import xreliquary.init.ModItems;
import xreliquary.network.PacketHandler;
import xreliquary.network.PacketMobCharmDamage;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;
import xreliquary.util.MobHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.List;

public class MobCharmItem extends ItemBase {
	public MobCharmItem() {
		super("mob_charm", new Properties().maxStackSize(1).maxDamage(Settings.COMMON.items.mobCharm.durability.get()).setNoRepair());
		MinecraftForge.EVENT_BUS.addListener(this::onEntityTargetedEvent);
		MinecraftForge.EVENT_BUS.addListener(this::onLivingUpdate);
		MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(getEntityEggRegistryName(stack));
		if (entityType == null) {
			return;
		}

		tooltip.add(new StringTextComponent(LanguageHelper.getLocalization(getTranslationKey() + ".tooltip", entityType.getName().getString())));
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(getEntityEggRegistryName(stack));
		if (entityType == null) {
			return super.getDisplayName(stack);
		}
		return new StringTextComponent(LanguageHelper.getLocalization(getTranslationKey(), entityType.getName().getString()));
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (!isInGroup(group)) {
			return;
		}

		for (String entityRegistryName : MobCharmRegistry.getRegisteredNames()) {
			items.add(getStackFor(entityRegistryName));
		}
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment.type != EnchantmentType.BREAKABLE && super.canApplyAtEnchantingTable(stack, enchantment);
	}

	private void onEntityTargetedEvent(LivingSetAttackTargetEvent event) {
		if (!(event.getTarget() instanceof PlayerEntity) || event.getTarget() instanceof FakePlayer ||
				!(event.getEntity() instanceof MobEntity)) {
			return;
		}

		MobEntity entity = (MobEntity) event.getEntity();

		MobCharmRegistry.getCharmDefinitionFor(entity).ifPresent(charmDefinition -> {
			if (isMobCharmPresent((PlayerEntity) event.getTarget(), charmDefinition)) {
				MobHelper.resetTarget(entity);
			}
		});
	}

	private void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		if (!(event.getEntity() instanceof MobEntity)) {
			return;
		}
		MobEntity entity = (MobEntity) event.getEntity();

		PlayerEntity player = null;
		if (entity.getAttackTarget() instanceof PlayerEntity && !(entity.getAttackTarget() instanceof FakePlayer)) {
			player = (PlayerEntity) entity.getAttackTarget();
		} else if (entity.getRevengeTarget() instanceof PlayerEntity && !(entity.getRevengeTarget() instanceof FakePlayer)) {
			player = (PlayerEntity) entity.getRevengeTarget();
		}
		if (player == null) {
			return;
		}

		PlayerEntity finalPlayer = player;
		MobCharmRegistry.getCharmDefinitionFor(entity).filter(MobCharmDefinition::resetTargetInLivingUpdateEvent).ifPresent(charmDefinition -> {
			if (isMobCharmPresent(finalPlayer, charmDefinition)) {
				MobHelper.resetTarget(entity, true);
			}
		});
	}

	private void onLivingDeath(LivingDeathEvent event) {
		if (event.getSource() == null || event.getSource().getTrueSource() == null || !(event.getSource().getTrueSource() instanceof PlayerEntity)) {
			return;
		}

		PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();

		MobCharmRegistry.getCharmDefinitionFor(event.getEntity()).ifPresent(charmDefinition -> {
			if (!charmInventoryHandler.damagePlayersMobCharm(player, charmDefinition.getRegistryName())) {
				damageMobCharmInPedestal(player, charmDefinition.getRegistryName());
			}
		});
	}

	private void damageMobCharmInPedestal(PlayerEntity player, String entityRegistryName) {
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(player.world.getDimensionKey().getRegistryName(), player.getPosition(), Settings.COMMON.items.mobCharm.pedestalRange.get());
		World world = player.getEntityWorld();

		for (BlockPos pos : pedestalPositions) {
			WorldHelper.getTile(world, pos, PedestalTileEntity.class).ifPresent(pedestal -> damageMobCharmInPedestal(player, entityRegistryName, pedestal));
		}
	}

	private void damageMobCharmInPedestal(PlayerEntity player, String entityRegistryName, PedestalTileEntity pedestal) {
		if (pedestal.isEnabled()) {
			ItemStack pedestalItem = pedestal.getItem();
			if (isCharmFor(pedestalItem, entityRegistryName)) {
				if (pedestalItem.getDamage() + Settings.COMMON.items.mobCharm.damagePerKill.get() > pedestalItem.getMaxDamage()) {
					pedestal.destroyItem();
				} else {
					pedestalItem.setDamage(pedestalItem.getDamage() + Settings.COMMON.items.mobCharm.damagePerKill.get());
				}
			} else if (pedestalItem.getItem() == ModItems.MOB_CHARM_BELT) {
				ModItems.MOB_CHARM_BELT.damageCharm(player, pedestalItem, entityRegistryName);
			}
		}
	}

	private boolean isMobCharmPresent(PlayerEntity player, MobCharmDefinition charmDefinition) {
		return charmInventoryHandler.playerHasMobCharm(player, charmDefinition) || pedestalWithCharmInRange(player, charmDefinition);
	}

	private boolean isCharmOrBeltFor(ItemStack slotStack, String registryName) {
		return isCharmFor(slotStack, registryName) || (slotStack.getItem() == ModItems.MOB_CHARM_BELT && ModItems.MOB_CHARM_BELT.hasCharm(slotStack, registryName));
	}

	static boolean isCharmFor(ItemStack slotStack, String registryName) {
		return slotStack.getItem() == ModItems.MOB_CHARM && getEntityRegistryName(slotStack).equals(registryName);
	}

	private boolean pedestalWithCharmInRange(PlayerEntity player, MobCharmDefinition charmDefinition) {
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(player.world.getDimensionKey().getRegistryName(), player.getPosition(), Settings.COMMON.items.mobCharm.pedestalRange.get());

		World world = player.getEntityWorld();

		for (BlockPos pos : pedestalPositions) {
			if (WorldHelper.getTile(world, pos, PedestalTileEntity.class).map(pedestal -> hasCharm(charmDefinition.getRegistryName(), pedestal)).orElse(false)) {
				return true;
			}
		}

		return false;
	}

	private boolean hasCharm(String entityRegistryName, PedestalTileEntity pedestal) {
		if (pedestal.isEnabled()) {
			ItemStack pedestalItem = pedestal.getItem();
			return isCharmOrBeltFor(pedestalItem, entityRegistryName);
		}
		return false;
	}

	static String getEntityRegistryName(ItemStack charm) {
		return NBTHelper.getString("entity", charm);
	}

	public static void setEntityRegistryName(ItemStack charm, String regName) {
		NBTHelper.putString("entity", charm, regName);
	}

	public ItemStack getStackFor(String entityRegistryName) {
		ItemStack ret = new ItemStack(this);
		setEntityRegistryName(ret, entityRegistryName);
		return ret;
	}

	public static ResourceLocation getEntityEggRegistryName(ItemStack charm) {
		return new ResourceLocation(getEntityRegistryName(charm));
	}

	private CharmInventoryHandler charmInventoryHandler = new CharmInventoryHandler();

	public void setCharmInventoryHandler(CharmInventoryHandler charmInventoryHandler) {
		this.charmInventoryHandler = charmInventoryHandler;
	}

	public static class CharmInventoryHandler {
		public boolean playerHasMobCharm(PlayerEntity player, MobCharmDefinition charmDefinition) {
			String registryName = charmDefinition.getRegistryName();

			for (ItemStack slotStack : player.inventory.mainInventory) {
				if (slotStack.isEmpty()) {
					continue;
				}
				if (ModItems.MOB_CHARM.isCharmOrBeltFor(slotStack, registryName)) {
					return true;
				}
			}
			return false;
		}

		@SuppressWarnings("BooleanMethodIsAlwaysInverted")
		public boolean damagePlayersMobCharm(PlayerEntity player, String entityRegistryName) {
			if (player.isCreative()) {
				return true;
			}

			return damageCharmInPlayersInventory(player, entityRegistryName);
		}

		private Boolean damageCharmInPlayersInventory(PlayerEntity player, String entityRegistryName) {
			for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
				ItemStack stack = player.inventory.mainInventory.get(slot);

				if (stack.isEmpty()) {
					continue;
				}
				if (isCharmFor(stack, entityRegistryName)) {
					if (stack.getDamage() + Settings.COMMON.items.mobCharm.damagePerKill.get() > stack.getMaxDamage()) {
						player.inventory.mainInventory.set(slot, ItemStack.EMPTY);
						PacketHandler.sendToClient((ServerPlayerEntity) player, new PacketMobCharmDamage(ItemStack.EMPTY, slot));
					} else {
						stack.setDamage(stack.getDamage() + Settings.COMMON.items.mobCharm.damagePerKill.get());
						PacketHandler.sendToClient((ServerPlayerEntity) player, new PacketMobCharmDamage(stack, slot));
					}
					return true;
				}
				if (damageMobCharmInBelt((ServerPlayerEntity) player, entityRegistryName, stack)) {
					return true;
				}
			}
			return false;
		}

		protected boolean damageMobCharmInBelt(ServerPlayerEntity player, String entityRegistryName, ItemStack belt) {
			if (belt.getItem() == ModItems.MOB_CHARM_BELT) {
				ItemStack charmStack = ModItems.MOB_CHARM_BELT.damageCharm(player, belt, entityRegistryName);

				if (!charmStack.isEmpty()) {
					PacketHandler.sendToClient(player, new PacketMobCharmDamage(charmStack, -1));
					return true;
				}
			}
			return false;
		}
	}
}
