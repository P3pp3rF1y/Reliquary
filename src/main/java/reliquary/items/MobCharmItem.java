package reliquary.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.registries.ForgeRegistries;
import reliquary.blocks.tile.PedestalBlockEntity;
import reliquary.init.ModItems;
import reliquary.network.PacketHandler;
import reliquary.network.PacketMobCharmDamage;
import reliquary.pedestal.PedestalRegistry;
import reliquary.reference.Settings;
import reliquary.util.LanguageHelper;
import reliquary.util.MobHelper;
import reliquary.util.NBTHelper;
import reliquary.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.List;

public class MobCharmItem extends ItemBase {
	public MobCharmItem() {
		super(new Properties().stacksTo(1).durability(Settings.COMMON.items.mobCharm.durability.get()).setNoRepair());
		MinecraftForge.EVENT_BUS.addListener(this::onEntityTargetedEvent);
		MinecraftForge.EVENT_BUS.addListener(this::onLivingUpdate);
		MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(getEntityEggRegistryName(stack));
		if (entityType == null) {
			return;
		}

		tooltip.add(new TranslatableComponent(getDescriptionId() + ".tooltip", entityType.getDescription().getString()).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public Component getName(ItemStack stack) {
		EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(getEntityEggRegistryName(stack));
		if (entityType == null) {
			return super.getName(stack);
		}
		return new TextComponent(LanguageHelper.getLocalization(getDescriptionId(), entityType.getDescription().getString()));
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (!allowdedIn(group)) {
			return;
		}

		for (String entityRegistryName : MobCharmRegistry.getRegisteredNames()) {
			items.add(getStackFor(entityRegistryName));
		}
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment.category != EnchantmentCategory.BREAKABLE && super.canApplyAtEnchantingTable(stack, enchantment);
	}

	private void onEntityTargetedEvent(LivingSetAttackTargetEvent event) {
		if (!(event.getTarget() instanceof Player) || event.getTarget() instanceof FakePlayer ||
				!(event.getEntity() instanceof Mob)) {
			return;
		}

		Mob entity = (Mob) event.getEntity();

		MobCharmRegistry.getCharmDefinitionFor(entity).ifPresent(charmDefinition -> {
			if (isMobCharmPresent((Player) event.getTarget(), charmDefinition)) {
				MobHelper.resetTarget(entity);
			}
		});
	}

	private void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		if (!(event.getEntity() instanceof Mob)) {
			return;
		}
		Mob entity = (Mob) event.getEntity();

		Player player = null;
		if (entity.getTarget() instanceof Player && !(entity.getTarget() instanceof FakePlayer)) {
			player = (Player) entity.getTarget();
		} else if (entity.getLastHurtByMob() instanceof Player && !(entity.getLastHurtByMob() instanceof FakePlayer)) {
			player = (Player) entity.getLastHurtByMob();
		}
		if (player == null) {
			return;
		}

		Player finalPlayer = player;
		MobCharmRegistry.getCharmDefinitionFor(entity).filter(MobCharmDefinition::resetTargetInLivingUpdateEvent).ifPresent(charmDefinition -> {
			if (isMobCharmPresent(finalPlayer, charmDefinition)) {
				MobHelper.resetTarget(entity, true);
			}
		});
	}

	private void onLivingDeath(LivingDeathEvent event) {
		if (event.getSource() == null || event.getSource().getEntity() == null || !(event.getSource().getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getSource().getEntity();

		MobCharmRegistry.getCharmDefinitionFor(event.getEntity()).ifPresent(charmDefinition -> {
			if (!charmInventoryHandler.damagePlayersMobCharm(player, charmDefinition.getRegistryName())) {
				damageMobCharmInPedestal(player, charmDefinition.getRegistryName());
			}
		});
	}

	private void damageMobCharmInPedestal(Player player, String entityRegistryName) {
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(player.level.dimension().getRegistryName(), player.blockPosition(), Settings.COMMON.items.mobCharm.pedestalRange.get());
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
		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(player.level.dimension().getRegistryName(), player.blockPosition(), Settings.COMMON.items.mobCharm.pedestalRange.get());

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
