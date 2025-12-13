package reliquary.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import reliquary.block.tile.PedestalBlockEntity;
import reliquary.init.ModDataComponents;
import reliquary.init.ModItems;
import reliquary.network.MobCharmDamagePayload;
import reliquary.pedestal.PedestalRegistry;
import reliquary.reference.Config;
import reliquary.util.MobHelper;
import reliquary.util.PlayerInventoryProvider;
import reliquary.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class MobCharmItem extends ItemBase {
	private final CharmInventoryHandler charmInventoryHandler = new CharmInventoryHandler();
	public MobCharmItem() {
		super(new Properties().stacksTo(1).durability(10).setNoRepair());
		NeoForge.EVENT_BUS.addListener(this::onEntityTargetedEvent);
		NeoForge.EVENT_BUS.addListener(this::onLivingUpdate);
		NeoForge.EVENT_BUS.addListener(this::onLivingDeath);
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return Config.COMMON.items.mobCharm.durability.get();
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		BuiltInRegistries.ENTITY_TYPE.getOptional(getEntityEggRegistryName(stack)).ifPresent(entityType ->
				tooltip.add(Component.translatable(getDescriptionId() + ".tooltip", entityType.getDescription().getString()).withStyle(ChatFormatting.GRAY))
		);
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		return BuiltInRegistries.ENTITY_TYPE.getOptional(getEntityEggRegistryName(stack))
				.map(entityType -> Component.translatable(getDescriptionId(), entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN))
				.orElseGet(() -> super.getName(stack));
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		for (ResourceLocation entityRegistryName : MobCharmRegistry.getRegisteredNames()) {
			itemConsumer.accept(getStackFor(entityRegistryName));
		}
	}

	private void onEntityTargetedEvent(LivingChangeTargetEvent event) {
		if (event.getEntity().level().isClientSide() || !(event.getNewAboutToBeSetTarget() instanceof Player player) || event.getNewAboutToBeSetTarget() instanceof FakePlayer ||
				!(event.getEntity() instanceof Mob entity)) {
			return;
		}

		MobCharmRegistry.getCharmDefinitionFor(entity).ifPresent(charmDefinition -> {
			if (isMobCharmPresent(player, charmDefinition)) {
				event.setCanceled(true);
			}
		});
	}

	private void onLivingUpdate(EntityTickEvent.Pre event) {
		if (event.getEntity().level().isClientSide() || !(event.getEntity() instanceof Mob entity)) {
			return;
		}

		Player player = getRealPlayer(entity.getTarget()).orElse(null);
		if (player == null) {
			player = getRealPlayer(entity.getLastHurtByMob()).orElse(null);
		}
		if (player == null) {
			player = MobHelper.getTargetedPlayerFromMemory(entity).orElse(null);
		}

		if (player == null) {
			return;
		}

		Player finalPlayer = player;
		MobCharmRegistry.getCharmDefinitionFor(entity).ifPresent(charmDefinition -> {
			if (isMobCharmPresent(finalPlayer, charmDefinition)) {
				MobHelper.resetTarget(entity, true);
			}
		});
	}

	private Optional<Player> getRealPlayer(@Nullable LivingEntity livingEntity) {
		return livingEntity instanceof Player p && !(livingEntity instanceof FakePlayer) ? Optional.of(p) : Optional.empty();
	}

	private void onLivingDeath(LivingDeathEvent event) {
		if (event.getSource().getEntity() == null || !(event.getSource().getEntity() instanceof ServerPlayer player)) {
			return;
		}

		MobCharmRegistry.getCharmDefinitionFor(event.getEntity()).ifPresent(charmDefinition -> {
			if (!charmInventoryHandler.damagePlayersMobCharm(player, charmDefinition.getRegistryName())) {
				damageMobCharmInPedestal(player, charmDefinition.getRegistryName());
			}
		});
	}

	private void damageMobCharmInPedestal(Player player, ResourceLocation entityRegistryName) {
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(player.level().dimension().registry(), player.blockPosition(), Config.COMMON.items.mobCharm.pedestalRange.get());
		Level level = player.getCommandSenderWorld();

		for (BlockPos pos : pedestalPositions) {
			WorldHelper.getBlockEntity(level, pos, PedestalBlockEntity.class).ifPresent(pedestal -> damageMobCharmInPedestal(player, entityRegistryName, pedestal));
		}
	}

	private void damageMobCharmInPedestal(Player player, ResourceLocation entityRegistryName, PedestalBlockEntity pedestal) {
		if (pedestal.isEnabled()) {
			ItemStack pedestalItem = pedestal.getItem();
			if (isCharmFor(pedestalItem, entityRegistryName)) {
				if (pedestalItem.getDamageValue() + Config.COMMON.items.mobCharm.damagePerKill.get() > pedestalItem.getMaxDamage()) {
					pedestal.destroyItem();
				} else {
					pedestalItem.setDamageValue(pedestalItem.getDamageValue() + Config.COMMON.items.mobCharm.damagePerKill.get());
				}
			} else if (pedestalItem.getItem() == ModItems.MOB_CHARM_BELT.get()) {
				ModItems.MOB_CHARM_BELT.get().damageCharm(player, pedestalItem, entityRegistryName);
			}
		}
	}

	private boolean isMobCharmPresent(Player player, MobCharmDefinition charmDefinition) {
		return charmInventoryHandler.playerHasMobCharm(player, charmDefinition) || pedestalWithCharmInRange(player, charmDefinition);
	}

	private boolean isCharmOrBeltFor(ItemStack slotStack, ResourceLocation registryName) {
		return isCharmFor(slotStack, registryName) || (slotStack.getItem() == ModItems.MOB_CHARM_BELT.get() && ModItems.MOB_CHARM_BELT.get().hasCharm(slotStack, registryName));
	}

	static boolean isCharmFor(ItemStack slotStack, ResourceLocation registryName) {
		return slotStack.getItem() == ModItems.MOB_CHARM.get() && getEntityEggRegistryName(slotStack).equals(registryName);
	}

	private boolean pedestalWithCharmInRange(Player player, MobCharmDefinition charmDefinition) {
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(player.level().dimension().registry(), player.blockPosition(), Config.COMMON.items.mobCharm.pedestalRange.get());

		Level level = player.getCommandSenderWorld();

		for (BlockPos pos : pedestalPositions) {
			if (WorldHelper.getBlockEntity(level, pos, PedestalBlockEntity.class).map(pedestal -> hasCharm(charmDefinition.getRegistryName(), pedestal)).orElse(false)) {
				return true;
			}
		}

		return false;
	}

	private boolean hasCharm(ResourceLocation entityRegistryName, PedestalBlockEntity pedestal) {
		if (pedestal.isEnabled()) {
			ItemStack pedestalItem = pedestal.getItem();
			return isCharmOrBeltFor(pedestalItem, entityRegistryName);
		}
		return false;
	}

	public static void setEntityRegistryName(ItemStack charm, ResourceLocation regName) {
		charm.set(ModDataComponents.ENTITY_NAME, regName);
	}

	public ItemStack getStackFor(ResourceLocation entityRegistryName) {
		ItemStack ret = new ItemStack(this);
		setEntityRegistryName(ret, entityRegistryName);
		return ret;
	}

	public static ResourceLocation getEntityEggRegistryName(ItemStack charm) {
		return charm.getOrDefault(ModDataComponents.ENTITY_NAME, BuiltInRegistries.ENTITY_TYPE.getDefaultKey());
	}

	public static class CharmInventoryHandler {
		private long lastCharmCacheTime = -1;
		private final Map<UUID, Set<ResourceLocation>> charmsInInventoryCache = new HashMap<>();

		protected Set<ResourceLocation> getCharmRegistryNames(Player player) {
			return PlayerInventoryProvider.get().getFromPlayerInventoryHandlers(player,
					(slotStack, set) -> {
						if (slotStack.isEmpty()) {
							return set;
						}
						if (slotStack.getItem() == ModItems.MOB_CHARM.get()) {
							set.add(getEntityEggRegistryName(slotStack));
						}
						if (slotStack.getItem() == ModItems.MOB_CHARM_BELT.get()) {
							set.addAll(ModItems.MOB_CHARM_BELT.get().getCharmRegistryNames(slotStack));
						}
						return set;
					}, set -> false, HashSet::new);
		}

		public boolean playerHasMobCharm(Player player, MobCharmDefinition charmDefinition) {
			ResourceLocation registryName = charmDefinition.getRegistryName();

			if (lastCharmCacheTime != player.level().getGameTime()) {
				lastCharmCacheTime = player.level().getGameTime();
				charmsInInventoryCache.clear();
			}
			return charmsInInventoryCache.computeIfAbsent(player.getUUID(), u -> getCharmRegistryNames(player)).contains(registryName);
		}


		public boolean damagePlayersMobCharm(ServerPlayer player, ResourceLocation entityRegistryName) {
			if (player.isCreative()) {
				return true;
			}

			return damageCharmInPlayersInventory(player, entityRegistryName);
		}

		private boolean damageCharmInPlayersInventory(ServerPlayer player, ResourceLocation entityRegistryName) {
			return PlayerInventoryProvider.get().getFromPlayerInventoryHandlers(player,
					(slotStack, damaged) -> {
						if (slotStack.isEmpty()) {
							return false;
						}
						if (isCharmFor(slotStack, entityRegistryName)) {
							if (slotStack.getDamageValue() + Config.COMMON.items.mobCharm.damagePerKill.get() > slotStack.getMaxDamage()) {
								slotStack.setCount(0);
								PacketDistributor.sendToPlayer(player, new MobCharmDamagePayload(ItemStack.EMPTY, -1));
							} else {
								slotStack.setDamageValue(slotStack.getDamageValue() + Config.COMMON.items.mobCharm.damagePerKill.get());
								PacketDistributor.sendToPlayer(player, new MobCharmDamagePayload(slotStack, -1));
							}
							return true;
						}
						return damageMobCharmInBelt(player, entityRegistryName, slotStack);
					}, damaged -> damaged, () -> false);
		}

		protected boolean damageMobCharmInBelt(ServerPlayer player, ResourceLocation entityRegistryName, ItemStack belt) {
			if (belt.getItem() == ModItems.MOB_CHARM_BELT.get()) {
				ItemStack charmStack = ModItems.MOB_CHARM_BELT.get().damageCharm(player, belt, entityRegistryName);

				if (!charmStack.isEmpty()) {
					PacketDistributor.sendToPlayer(player, new MobCharmDamagePayload(charmStack, -1));
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public boolean isEnchantable(ItemStack p_41456_) {
		return false;
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return false;
	}
}
