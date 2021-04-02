package xreliquary.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xreliquary.client.gui.components.Box;
import xreliquary.client.gui.components.Component;
import xreliquary.client.gui.components.ItemStackPane;
import xreliquary.client.gui.components.TextPane;
import xreliquary.client.gui.hud.ChargePane;
import xreliquary.client.gui.hud.ChargeableItemInfoPane;
import xreliquary.client.gui.hud.CharmPane;
import xreliquary.client.gui.hud.DynamicChargePane;
import xreliquary.client.gui.hud.HUDPosition;
import xreliquary.client.gui.hud.HUDRenderrer;
import xreliquary.client.gui.hud.HandgunPane;
import xreliquary.client.gui.hud.HeroMedallionPane;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.items.HarvestRodItem;
import xreliquary.items.VoidTearItem;
import xreliquary.items.util.ILeftClickableItem;
import xreliquary.network.LeftClickedItemPacket;
import xreliquary.network.PacketHandler;
import xreliquary.reference.Colors;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;

import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {
	private ClientEventHandler() {}

	private static final String VOID_TEAR_MODE_TRANSLATION = "item." + Reference.MOD_ID + ".void_tear.mode.";

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void onRenderLiving(RenderLivingEvent.Pre<PlayerEntity, PlayerModel<PlayerEntity>> event) {
		if (event.getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntity();

			boolean handgunInOff = player.getHeldItem(Hand.OFF_HAND).getItem() == ModItems.HANDGUN.get();
			boolean handgunInMain = player.getHeldItem(Hand.MAIN_HAND).getItem() == ModItems.HANDGUN.get();

			if (handgunInOff || handgunInMain) {
				setHandgunArmPoses(event, player, handgunInOff, handgunInMain);
			}
		}
	}

	private static void setHandgunArmPoses(RenderLivingEvent.Pre<PlayerEntity, PlayerModel<PlayerEntity>> event, PlayerEntity player, boolean handgunInOff, boolean handgunInMain) {
		PlayerModel<PlayerEntity> model = event.getRenderer().getEntityModel();

		if (isHandgunActive(player, handgunInMain, handgunInOff)) {
			Hand hand = getActiveHandgunHand(player, handgunInMain, handgunInOff);
			HandSide primaryHand = player.getPrimaryHand();

			if (((hand == Hand.MAIN_HAND && primaryHand == HandSide.RIGHT) || (hand == Hand.OFF_HAND && primaryHand == HandSide.LEFT)) && model.rightArmPose != BipedModel.ArmPose.BOW_AND_ARROW) {
				model.rightArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
			} else if (((hand == Hand.OFF_HAND && primaryHand == HandSide.RIGHT) || (hand == Hand.MAIN_HAND && primaryHand == HandSide.LEFT)) && model.leftArmPose != BipedModel.ArmPose.BOW_AND_ARROW) {
				model.leftArmPose = BipedModel.ArmPose.BOW_AND_ARROW;
			}
		} else {
			if (model.rightArmPose == BipedModel.ArmPose.BOW_AND_ARROW) {
				model.rightArmPose = BipedModel.ArmPose.ITEM;
			}
			if (model.leftArmPose == BipedModel.ArmPose.BOW_AND_ARROW) {
				model.leftArmPose = BipedModel.ArmPose.ITEM;
			}
		}
	}

	private static Hand getActiveHandgunHand(PlayerEntity player, boolean handgunInMain, boolean handgunInOff) {
		if (handgunInMain != handgunInOff) {
			return handgunInMain ? Hand.MAIN_HAND : Hand.OFF_HAND;
		}

		boolean mainValid = isValidTimeFrame(player.world, player.getHeldItemMainhand());
		boolean offValid = isValidTimeFrame(player.world, player.getHeldItemOffhand());

		if (mainValid != offValid) {
			return mainValid ? Hand.MAIN_HAND : Hand.OFF_HAND;
		}

		return ModItems.HANDGUN.get().getCooldown(player.getHeldItemMainhand()) < ModItems.HANDGUN.get().getCooldown(player.getHeldItemOffhand()) ? Hand.MAIN_HAND : Hand.OFF_HAND;
	}

	private static boolean isHandgunActive(PlayerEntity player, boolean handgunInMain, boolean handgunInOff) {
		return handgunInMain && isValidTimeFrame(player.world, player.getHeldItemMainhand()) || handgunInOff && isValidTimeFrame(player.world, player.getHeldItemOffhand());

	}

	private static boolean isValidTimeFrame(World world, ItemStack handgun) {
		long cooldownTime = ModItems.HANDGUN.get().getCooldown(handgun) + 5;

		return cooldownTime - world.getGameTime() <= ModItems.HANDGUN.get().getUseDuration(handgun) && cooldownTime >= world.getGameTime();
	}

	private static final List<Tuple<Component, HUDPosition>> hudComponents = Lists.newArrayList();

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.currentScreen != null || !Minecraft.isGuiEnabled() || !mc.isGameFocused() || mc.player == null) {
			return;
		}

		if (hudComponents.isEmpty()) {
			initHUDComponents();
		}
		renderHUDComponents(new MatrixStack());
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void onMouseLeftClick(InputEvent.MouseInputEvent evt) {
		Minecraft mc = Minecraft.getInstance();
		if (evt.getButton() != 0 || evt.getAction() != 1 || mc.currentScreen != null) {
			return;
		}
		ClientPlayerEntity player = mc.player;
		if (player == null) {
			return;
		}
		ItemStack stack = player.getHeldItemMainhand();
		if (stack.getItem() instanceof ILeftClickableItem && ((ILeftClickableItem) stack.getItem()).onLeftClickItem(stack, player) == ActionResultType.PASS) {
			PacketHandler.sendToServer(new LeftClickedItemPacket());
		}
	}

	private static void renderHUDComponents(MatrixStack matrixStack) {
		for (Tuple<Component, HUDPosition> component : hudComponents) {
			HUDRenderrer.render(matrixStack, component.getA(), component.getB());
		}
	}

	private static void initHUDComponents() {
		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.ALKAHESTRY_TOME.get(), Settings.CLIENT.hudPositions.alkahestryTome.get(), new ItemStack(Items.REDSTONE), is -> NBTHelper.getInt("charge", is)),
				Settings.CLIENT.hudPositions.alkahestryTome.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.DESTRUCTION_CATALYST.get(), Settings.CLIENT.hudPositions.destructionCatalyst.get(), new ItemStack(Items.GUNPOWDER), is -> NBTHelper.getInt("gunpowder", is)),
				Settings.CLIENT.hudPositions.destructionCatalyst.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.MIDAS_TOUCHSTONE.get(), Settings.CLIENT.hudPositions.midasTouchstone.get(), new ItemStack(Items.GLOWSTONE_DUST), is -> NBTHelper.getInt("glowstone", is)),
				Settings.CLIENT.hudPositions.midasTouchstone.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.INFERNAL_CHALICE.get(), Settings.CLIENT.hudPositions.infernalChalice.get(), new ItemStack(Items.LAVA_BUCKET), is -> NBTHelper.getInt("fluidStacks", is) / 1000, Colors.get(Colors.RED)),
				Settings.CLIENT.hudPositions.infernalChalice.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.ICE_MAGUS_ROD.get(), Settings.CLIENT.hudPositions.iceMagusRod.get(), new ItemStack(Items.SNOWBALL), is -> NBTHelper.getInt("snowballs", is)),
				Settings.CLIENT.hudPositions.iceMagusRod.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.GLACIAL_STAFF.get(), Settings.CLIENT.hudPositions.glacialStaff.get(), new ItemStack(Items.SNOWBALL), is -> NBTHelper.getInt("snowballs", is)),
				Settings.CLIENT.hudPositions.glacialStaff.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.ENDER_STAFF.get(), Settings.CLIENT.hudPositions.enderStaff.get(), ModItems.ENDER_STAFF.get()::getMode,
				ImmutableMap.of(
						"cast", new ChargePane(ModItems.ENDER_STAFF.get(), new ItemStack(Items.ENDER_PEARL), is -> ModItems.ENDER_STAFF.get().getPearlCount(is, true)),
						"node_warp", new ChargePane(ModItems.ENDER_STAFF.get(), new ItemStack(ModBlocks.WRAITH_NODE.get()), is -> ModItems.ENDER_STAFF.get().getPearlCount(is, true)),
						"long_cast", new ChargePane(ModItems.ENDER_STAFF.get(), new ItemStack(Items.ENDER_EYE), is -> ModItems.ENDER_STAFF.get().getPearlCount(is, true))
				)), Settings.CLIENT.hudPositions.enderStaff.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.PYROMANCER_STAFF.get(), Settings.CLIENT.hudPositions.pyromancerStaff.get(), ModItems.PYROMANCER_STAFF.get()::getMode,
				ImmutableMap.of(
						"blaze", new ChargePane(ModItems.PYROMANCER_STAFF.get(), new ItemStack(Items.BLAZE_POWDER), is -> ModItems.PYROMANCER_STAFF.get().getInternalStorageItemCount(is, Items.BLAZE_POWDER)),
						"charge", new ChargePane(ModItems.PYROMANCER_STAFF.get(), new ItemStack(Items.FIRE_CHARGE), is -> ModItems.PYROMANCER_STAFF.get().getInternalStorageItemCount(is, Items.FIRE_CHARGE)),
						"eruption", Box.createVertical(Box.Alignment.RIGHT, new TextPane("ERUPT"), new ChargePane(ModItems.PYROMANCER_STAFF.get(), new ItemStack(Items.BLAZE_POWDER), is -> ModItems.PYROMANCER_STAFF.get().getInternalStorageItemCount(is, Items.BLAZE_POWDER))),
						"flint_and_steel", new ItemStackPane(Items.FLINT_AND_STEEL)
				)), Settings.CLIENT.hudPositions.pyromancerStaff.get()));

		ChargePane rendingGaleFeatherPane = new ChargePane(ModItems.RENDING_GALE.get(), new ItemStack(Items.FEATHER), is -> ModItems.RENDING_GALE.get().getFeatherCountClient(is, Minecraft.getInstance().player) / 100);
		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.RENDING_GALE.get(), Settings.CLIENT.hudPositions.rendingGale.get(), ModItems.RENDING_GALE.get()::getMode,
				ImmutableMap.of(
						"push", Box.createVertical(Box.Alignment.RIGHT, new TextPane("PUSH"), rendingGaleFeatherPane),
						"pull", Box.createVertical(Box.Alignment.RIGHT, new TextPane("PULL"), rendingGaleFeatherPane),
						"bolt", Box.createVertical(Box.Alignment.RIGHT, new TextPane("BOLT"), rendingGaleFeatherPane),
						"flight", Box.createVertical(Box.Alignment.RIGHT, new TextPane("FLIGHT"), rendingGaleFeatherPane)
				)), Settings.CLIENT.hudPositions.rendingGale.get()));

		Component contentsPane = new DynamicChargePane(ModItems.VOID_TEAR.get(),
				is -> VoidTearItem.getTearContents(is, true), is -> VoidTearItem.getTearContents(is, true).getCount());
		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.VOID_TEAR.get(), Settings.CLIENT.hudPositions.voidTear.get(), is -> ModItems.VOID_TEAR.get().getMode(is).getString(),
				ImmutableMap.of(
						VoidTearItem.Mode.FULL_INVENTORY.getString(), Box.createVertical(Box.Alignment.RIGHT, new TextPane(LanguageHelper.getLocalization(VOID_TEAR_MODE_TRANSLATION + VoidTearItem.Mode.FULL_INVENTORY.getString().toLowerCase())), contentsPane),
						VoidTearItem.Mode.NO_REFILL.getString(), Box.createVertical(Box.Alignment.RIGHT, new TextPane(LanguageHelper.getLocalization(VOID_TEAR_MODE_TRANSLATION + VoidTearItem.Mode.NO_REFILL.getString().toLowerCase())), contentsPane),
						VoidTearItem.Mode.ONE_STACK.getString(), Box.createVertical(Box.Alignment.RIGHT, new TextPane(LanguageHelper.getLocalization(VOID_TEAR_MODE_TRANSLATION + VoidTearItem.Mode.ONE_STACK.getString().toLowerCase())), contentsPane)
				)) {
			@Override
			public boolean shouldRender() {
				return !VoidTearItem.isEmpty(InventoryHelper.getCorrectItemFromEitherHand(Minecraft.getInstance().player, ModItems.VOID_TEAR.get()), true);
			}
		}, Settings.CLIENT.hudPositions.voidTear.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.HARVEST_ROD.get(), Settings.CLIENT.hudPositions.harvestRod.get(), ModItems.HARVEST_ROD.get()::getMode,
				ImmutableMap.of(
						HarvestRodItem.BONE_MEAL_MODE, new ChargePane(ModItems.HARVEST_ROD.get(), new ItemStack(Items.BONE_MEAL), is -> ModItems.HARVEST_ROD.get().getBoneMealCount(is, true)),
						HarvestRodItem.HOE_MODE, new ItemStackPane(Items.WOODEN_HOE),
						ChargeableItemInfoPane.DYNAMIC_PANE, new DynamicChargePane(ModItems.HARVEST_ROD.get(), is -> ModItems.HARVEST_ROD.get().getCurrentPlantable(is, true), is -> ModItems.HARVEST_ROD.get().getPlantableQuantity(is, ModItems.HARVEST_ROD.get().getCurrentPlantableSlot(is), true))
				)), Settings.CLIENT.hudPositions.harvestRod.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.SOJOURNER_STAFF.get(), Settings.CLIENT.hudPositions.sojournerStaff.get(), is -> ChargeableItemInfoPane.DYNAMIC_PANE,
				ImmutableMap.of(
						ChargeableItemInfoPane.DYNAMIC_PANE, new DynamicChargePane(ModItems.SOJOURNER_STAFF.get(), ModItems.SOJOURNER_STAFF.get()::getCurrentTorch, ModItems.SOJOURNER_STAFF.get()::getTorchCount)
				)), Settings.CLIENT.hudPositions.sojournerStaff.get()));

		hudComponents.add(new Tuple<>(new HeroMedallionPane(), Settings.CLIENT.hudPositions.heroMedallion.get()));

		hudComponents.add(new Tuple<>(Box.createVertical(Box.Alignment.RIGHT, new HandgunPane(Hand.OFF_HAND), new HandgunPane(Hand.MAIN_HAND)), Settings.CLIENT.hudPositions.handgun.get()));

		hudComponents.add(new Tuple<>(new CharmPane(), Settings.CLIENT.hudPositions.mobCharm.get()));
	}
}
