package reliquary.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.registries.ForgeRegistries;
import reliquary.blocks.tile.PedestalBlockEntity;
import reliquary.init.ModItems;
import reliquary.network.PacketHandler;
import reliquary.network.PacketMobCharmDamage;
import reliquary.pedestal.PedestalRegistry;
import reliquary.reference.Settings;
import reliquary.util.MobHelper;
import reliquary.util.NBTHelper;
import reliquary.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MobCharmItem extends ItemBase {
	public MobCharmItem() {
		super(new Properties().stacksTo(1).setNoRepair());
		MinecraftForge.EVENT_BUS.addListener(this::onEntityTargetedEvent);
		MinecraftForge.EVENT_BUS.addListener(this::onLivingUpdate);
		MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
	}

	@Override
	public boolean canBeDepleted() {
		return true;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return Settings.COMMON.items.mobCharm.durability.get();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(getEntityEggRegistryName(stack));
		if (entityType == null) {
			return;
		}

		tooltip.add(Component.translatable(getDescriptionId() + ".tooltip", entityType.getDescription().getString()).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(getEntityEggRegistryName(stack));
		if (entityType == null) {
			return super.getName(stack);
		}
		return Component.translatable(getDescriptionId(), entityType.getDescription().getString()).withStyle(ChatFormatting.GREEN);
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		for (String entityRegistryName : MobCharmRegistry.getRegisteredNames()) {
			itemConsumer.accept(getStackFor(entityRegistryName));
		}
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment.category != EnchantmentCategory.BREAKABLE && super.canApplyAtEnchantingTable(stack, enchantment);
	}

	private void onEntityTargetedEvent(LivingChangeTargetEvent event) {
		if (!(event.getNewTarget() instanceof Player player) || event.getNewTarget() instanceof FakePlayer ||
				!(event.getEntity() instanceof Mob entity)) {
			return;
		}

		MobCharmRegistry.getCharmDefinitionFor(entity).ifPresent(charmDefinition -> {
			if (isMobCharmPresent(player, charmDefinition)) {
				event.setNewTarget(null);
			}
		});
	}

	private void onLivingUpdate(LivingEvent.LivingTickEvent event) {
		if (!(event.getEntity() instanceof Mob entity)) {
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
		MobCharmRegistry.getCharmDefinitionFor(entity).filter(MobCharmDefinition::resetTargetInLivingTickEvent).ifPresent(charmDefinition -> {
			if (isMobCharmPresent(finalPlayer, charmDefinition)) {
				MobHelper.resetTarget(entity, true);
			}
		});
	}

	private Optional<Player> getRealPlayer(@Nullable LivingEntity livingEntity) {
		return livingEntity instanceof Player p && !(livingEntity instanceof FakePlayer) ? Optional.of(p) : Optional.empty();
	}

	private void onLivingDeath(LivingDeathEvent event) {
		if (event.getSource() == null || event.getSource().getEntity() == null || !(event.getSource().getEntity() instanceof Player player)) {
			return;
		}

		MobCharmRegistry.getCharmDefinitionFor(event.getEntity()).ifPresent(charmDefinition -> {
			if (!charmInventoryHandler.damagePlayersMobCharm(player, charmDefinition.getRegistryName())) {
				damageMobCharmInPedestal(player, charmDefinition.getRegistryName());
			}
		});
	}

	private void damageMobCharmInPedestal(Player player, String entityRegistryName) {
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(player.level().dimension().registry(), player.blockPosition(), Settings.COMMON.items.mobCharm.pedestalRange.get());
		Level world = player.getCommandSenderWorld();

		for (BlockPos pos : pedestalPositions) {
			WorldHelper.getBlockEntity(world, pos, PedestalBlockEntity.class).ifPresent(pedestal -> damageMobCharmInPedestal(player, entityRegistryName, pedestal));
		}
	}

	private void damageMobCharmInPedestal(Player player, String entityRegistryName, PedestalBlockEntity pedestal) {
		if (pedestal.isEnabled()) {
			ItemStack pedestalItem = pedestal.getItem();
			if (isCharmFor(pedestalItem, entityRegistryName)) {
				if (pedestalItem.getDamageValue() + Settings.COMMON.items.mobCharm.damagePerKill.get() > pedestalItem.getMaxDamage()) {
					pedestal.destroyItem();
				} else {
					pedestalItem.setDamageValue(pedestalItem.getDamageValue() + Settings.COMMON.items.mobCharm.damagePerKill.get());
				}
			} else if (pedestalItem.getItem() == ModItems.MOB_CHARM_BELT.get()) {
				ModItems.MOB_CHARM_BELT.get().damageCharm(player, pedestalItem, entityRegistryName);
			}
		}
	}

	private boolean isMobCharmPresent(Player player, MobCharmDefinition charmDefinition) {
		return charmInventoryHandler.playerHasMobCharm(player, charmDefinition) || pedestalWithCharmInRange(player, charmDefinition);
	}

	private boolean isCharmOrBeltFor(ItemStack slotStack, String registryName) {
		return isCharmFor(slotStack, registryName) || (slotStack.getItem() == ModItems.MOB_CHARM_BELT.get() && ModItems.MOB_CHARM_BELT.get().hasCharm(slotStack, registryName));
	}

	static boolean isCharmFor(ItemStack slotStack, String registryName) {
		return slotStack.getItem() == ModItems.MOB_CHARM.get() && getEntityRegistryName(slotStack).equals(registryName);
	}

	private boolean pedestalWithCharmInRange(Player player, MobCharmDefinition charmDefinition) {
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(player.level().dimension().registry(), player.blockPosition(), Settings.COMMON.items.mobCharm.pedestalRange.get());

		Level world = player.getCommandSenderWorld();

		for (BlockPos pos : pedestalPositions) {
			if (WorldHelper.getBlockEntity(world, pos, PedestalBlockEntity.class).map(pedestal -> hasCharm(charmDefinition.getRegistryName(), pedestal)).orElse(false)) {
				return true;
			}
		}

		return false;
	}

	private boolean hasCharm(String entityRegistryName, PedestalBlockEntity pedestal) {
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
		public boolean playerHasMobCharm(Player player, MobCharmDefinition charmDefinition) {
			String registryName = charmDefinition.getRegistryName();

			for (ItemStack slotStack : player.getInventory().items) {
				if (slotStack.isEmpty()) {
					continue;
				}
				if (ModItems.MOB_CHARM.get().isCharmOrBeltFor(slotStack, registryName)) {
					return true;
				}
			}
			return false;
		}

		public boolean damagePlayersMobCharm(Player player, String entityRegistryName) {
			if (player.isCreative()) {
				return true;
			}

			return damageCharmInPlayersInventory(player, entityRegistryName);
		}

		private Boolean damageCharmInPlayersInventory(Player player, String entityRegistryName) {
			for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
				ItemStack stack = player.getInventory().items.get(slot);

				if (stack.isEmpty()) {
					continue;
				}
				if (isCharmFor(stack, entityRegistryName)) {
					if (stack.getDamageValue() + Settings.COMMON.items.mobCharm.damagePerKill.get() > stack.getMaxDamage()) {
						player.getInventory().items.set(slot, ItemStack.EMPTY);
						PacketHandler.sendToClient((ServerPlayer) player, new PacketMobCharmDamage(ItemStack.EMPTY, slot));
					} else {
						stack.setDamageValue(stack.getDamageValue() + Settings.COMMON.items.mobCharm.damagePerKill.get());
						PacketHandler.sendToClient((ServerPlayer) player, new PacketMobCharmDamage(stack, slot));
					}
					return true;
				}
				if (damageMobCharmInBelt((ServerPlayer) player, entityRegistryName, stack)) {
					return true;
				}
			}
			return false;
		}

		protected boolean damageMobCharmInBelt(ServerPlayer player, String entityRegistryName, ItemStack belt) {
			if (belt.getItem() == ModItems.MOB_CHARM_BELT.get()) {
				ItemStack charmStack = ModItems.MOB_CHARM_BELT.get().damageCharm(player, belt, entityRegistryName);

				if (!charmStack.isEmpty()) {
					PacketHandler.sendToClient(player, new PacketMobCharmDamage(charmStack, -1));
					return true;
				}
			}
			return false;
		}
	}
}
