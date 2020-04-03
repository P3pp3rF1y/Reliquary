package xreliquary.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
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

	@SubscribeEvent
	public static void onRenderLiving(RenderLivingEvent.Pre event) {
		if (event.getEntity() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntity();

			boolean handgunInOff = player.getHeldItem(Hand.OFF_HAND).getItem() == ModItems.HANDGUN;
			boolean handgunInMain = player.getHeldItem(Hand.MAIN_HAND).getItem() == ModItems.HANDGUN;

			if (handgunInOff || handgunInMain) {
				setHandgunArmPoses(event, player, handgunInOff, handgunInMain);
			}
		}
	}

	private static void setHandgunArmPoses(RenderLivingEvent.Pre event, PlayerEntity player, boolean handgunInOff, boolean handgunInMain) {
		BipedModel model = (BipedModel) event.getRenderer().getEntityModel();

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

		return ModItems.HANDGUN.getCooldown(player.getHeldItemMainhand()) < ModItems.HANDGUN.getCooldown(player.getHeldItemOffhand()) ? Hand.MAIN_HAND : Hand.OFF_HAND;
	}

	private static boolean isHandgunActive(PlayerEntity player, boolean handgunInMain, boolean handgunInOff) {
		return handgunInMain && isValidTimeFrame(player.world, player.getHeldItemMainhand()) || handgunInOff && isValidTimeFrame(player.world, player.getHeldItemOffhand());

	}

	private static boolean isValidTimeFrame(World world, ItemStack handgun) {
		long cooldownTime = ModItems.HANDGUN.getCooldown(handgun) + 5;

		return cooldownTime - world.getGameTime() <= ModItems.HANDGUN.getUseDuration(handgun) && cooldownTime >= world.getGameTime();
	}

	private static final List<Tuple<Component, HUDPosition>> hudComponents = Lists.newArrayList();

	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.currentScreen != null || !Minecraft.isGuiEnabled() || !mc.isGameFocused() || mc.player == null) {
			return;
		}

		if (hudComponents.isEmpty()) {
			initHUDComponents();
		}
		renderHUDComponents();
	}

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
		if (stack.getItem() instanceof ILeftClickableItem) {
			if (((ILeftClickableItem) stack.getItem()).onLeftClickItem(stack, player) == ActionResultType.PASS) {
				PacketHandler.sendToServer(new LeftClickedItemPacket());
			}
		}
	}

	private static void renderHUDComponents() {
		for (Tuple<Component, HUDPosition> component : hudComponents) {
			HUDRenderrer.render(component.getA(), component.getB());
		}
	}

	private static void initHUDComponents() {
		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.ALKAHESTRY_TOME, Settings.CLIENT.hudPositions.alkahestryTome.get(), new ItemStack(Items.REDSTONE), is -> NBTHelper.getInt("charge", is)),
				Settings.CLIENT.hudPositions.alkahestryTome.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.DESTRUCTION_CATALYST, Settings.CLIENT.hudPositions.destructionCatalyst.get(), new ItemStack(Items.GUNPOWDER), is -> NBTHelper.getInt("gunpowder", is)),
				Settings.CLIENT.hudPositions.destructionCatalyst.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.MIDAS_TOUCHSTONE, Settings.CLIENT.hudPositions.midasTouchstone.get(), new ItemStack(Items.GLOWSTONE_DUST), is -> NBTHelper.getInt("glowstone", is)),
				Settings.CLIENT.hudPositions.midasTouchstone.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.INFERNAL_CHALICE, Settings.CLIENT.hudPositions.infernalChalice.get(), new ItemStack(Items.LAVA_BUCKET), is -> NBTHelper.getInt("fluidStacks", is) / 1000, Colors.get(Colors.RED)),
				Settings.CLIENT.hudPositions.infernalChalice.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.ICE_MAGUS_ROD, Settings.CLIENT.hudPositions.iceMagusRod.get(), new ItemStack(Items.SNOWBALL), is -> NBTHelper.getInt("snowballs", is)),
				Settings.CLIENT.hudPositions.iceMagusRod.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.GLACIAL_STAFF, Settings.CLIENT.hudPositions.glacialStaff.get(), new ItemStack(Items.SNOWBALL), is -> NBTHelper.getInt("snowballs", is)),
				Settings.CLIENT.hudPositions.glacialStaff.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.ENDER_STAFF, Settings.CLIENT.hudPositions.enderStaff.get(), ModItems.ENDER_STAFF::getMode,
				ImmutableMap.of(
						"cast", new ChargePane(ModItems.ENDER_STAFF, new ItemStack(Items.ENDER_PEARL), is -> ModItems.ENDER_STAFF.getPearlCount(is, true)),
						"node_warp", new ChargePane(ModItems.ENDER_STAFF, new ItemStack(ModBlocks.WRAITH_NODE), is -> ModItems.ENDER_STAFF.getPearlCount(is, true)),
						"long_cast", new ChargePane(ModItems.ENDER_STAFF, new ItemStack(Items.ENDER_EYE), is -> ModItems.ENDER_STAFF.getPearlCount(is, true))
				)), Settings.CLIENT.hudPositions.enderStaff.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.PYROMANCER_STAFF, Settings.CLIENT.hudPositions.pyromancerStaff.get(), ModItems.PYROMANCER_STAFF::getMode,
				ImmutableMap.of(
						"blaze", new ChargePane(ModItems.PYROMANCER_STAFF, new ItemStack(Items.BLAZE_POWDER), is -> ModItems.PYROMANCER_STAFF.getInternalStorageItemCount(is, Items.BLAZE_POWDER)),
						"charge", new ChargePane(ModItems.PYROMANCER_STAFF, new ItemStack(Items.FIRE_CHARGE), is -> ModItems.PYROMANCER_STAFF.getInternalStorageItemCount(is, Items.FIRE_CHARGE)),
						"eruption", Box.createVertical(Box.Alignment.RIGHT, new TextPane("ERUPT"), new ChargePane(ModItems.PYROMANCER_STAFF, new ItemStack(Items.BLAZE_POWDER), is -> ModItems.PYROMANCER_STAFF.getInternalStorageItemCount(is, Items.BLAZE_POWDER))),
						"flint_and_steel", new ItemStackPane(Items.FLINT_AND_STEEL)
				)), Settings.CLIENT.hudPositions.pyromancerStaff.get()));

		ChargePane rendingGaleFeatherPane = new ChargePane(ModItems.RENDING_GALE, new ItemStack(Items.FEATHER), is -> ModItems.RENDING_GALE.getFeatherCountClient(is, Minecraft.getInstance().player) / 100);
		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.RENDING_GALE, Settings.CLIENT.hudPositions.rendingGale.get(), ModItems.RENDING_GALE::getMode,
				ImmutableMap.of(
						"push", Box.createVertical(Box.Alignment.RIGHT, new TextPane("PUSH"), rendingGaleFeatherPane),
						"pull", Box.createVertical(Box.Alignment.RIGHT, new TextPane("PULL"), rendingGaleFeatherPane),
						"bolt", Box.createVertical(Box.Alignment.RIGHT, new TextPane("BOLT"), rendingGaleFeatherPane),
						"flight", Box.createVertical(Box.Alignment.RIGHT, new TextPane("FLIGHT"), rendingGaleFeatherPane)
				)), Settings.CLIENT.hudPositions.rendingGale.get()));

		Component contentsPane = new DynamicChargePane(ModItems.VOID_TEAR,
				is -> ModItems.VOID_TEAR.getContainerItem(is, true), is -> ModItems.VOID_TEAR.getContainerItem(is, true).getCount());
		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.VOID_TEAR, Settings.CLIENT.hudPositions.voidTear.get(), is -> ModItems.VOID_TEAR.getMode(is).getName(),
				ImmutableMap.of(
						VoidTearItem.Mode.FULL_INVENTORY.getName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane(LanguageHelper.getLocalization(VOID_TEAR_MODE_TRANSLATION + VoidTearItem.Mode.FULL_INVENTORY.getName().toLowerCase())), contentsPane),
						VoidTearItem.Mode.NO_REFILL.getName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane(LanguageHelper.getLocalization(VOID_TEAR_MODE_TRANSLATION + VoidTearItem.Mode.NO_REFILL.getName().toLowerCase())), contentsPane),
						VoidTearItem.Mode.ONE_STACK.getName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane(LanguageHelper.getLocalization(VOID_TEAR_MODE_TRANSLATION + VoidTearItem.Mode.ONE_STACK.getName().toLowerCase())), contentsPane)
				)) {
			@Override
			public boolean shouldRender() {
				return !ModItems.VOID_TEAR.isEmpty(InventoryHelper.getCorrectItemFromEitherHand(Minecraft.getInstance().player, ModItems.VOID_TEAR), true);
			}
		}, Settings.CLIENT.hudPositions.voidTear.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.HARVEST_ROD, Settings.CLIENT.hudPositions.harvestRod.get(), ModItems.HARVEST_ROD::getMode,
				ImmutableMap.of(
						HarvestRodItem.BONE_MEAL_MODE, new ChargePane(ModItems.HARVEST_ROD, new ItemStack(Items.BONE_MEAL), is -> ModItems.HARVEST_ROD.getBoneMealCount(is, true)),
						HarvestRodItem.HOE_MODE, new ItemStackPane(Items.WOODEN_HOE),
						ChargeableItemInfoPane.DYNAMIC_PANE, new DynamicChargePane(ModItems.HARVEST_ROD, is -> ModItems.HARVEST_ROD.getCurrentPlantable(is, true), is -> ModItems.HARVEST_ROD.getPlantableQuantity(is, ModItems.HARVEST_ROD.getCurrentPlantableSlot(is), true))
				)), Settings.CLIENT.hudPositions.harvestRod.get()));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.SOJOURNER_STAFF, Settings.CLIENT.hudPositions.sojournerStaff.get(), is -> ChargeableItemInfoPane.DYNAMIC_PANE,
				ImmutableMap.of(
						ChargeableItemInfoPane.DYNAMIC_PANE, new DynamicChargePane(ModItems.SOJOURNER_STAFF, ModItems.SOJOURNER_STAFF::getCurrentTorch, ModItems.SOJOURNER_STAFF::getTorchCount)
				)), Settings.CLIENT.hudPositions.sojournerStaff.get()));

		hudComponents.add(new Tuple<>(new HeroMedallionPane(), Settings.CLIENT.hudPositions.heroMedallion.get()));

		hudComponents.add(new Tuple<>(Box.createVertical(Box.Alignment.RIGHT, new HandgunPane(Hand.OFF_HAND), new HandgunPane(Hand.MAIN_HAND)), Settings.CLIENT.hudPositions.handgun.get()));

		hudComponents.add(new Tuple<>(new CharmPane(), Settings.CLIENT.hudPositions.mobCharm.get()));
	}
}
