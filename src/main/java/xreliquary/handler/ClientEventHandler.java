package xreliquary.handler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
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
import xreliquary.items.ItemHarvestRod;
import xreliquary.items.ItemVoidTear;
import xreliquary.reference.Colors;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)
public class ClientEventHandler {
	@SubscribeEvent
	public static void onRenderLiving(RenderLivingEvent.Pre event) {
		if(event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();

			boolean handgunInOff = player.getHeldItem(EnumHand.OFF_HAND).getItem() == ModItems.handgun;
			boolean handgunInMain = player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModItems.handgun;

			if(handgunInOff || handgunInMain) {
				ModelBiped model = (ModelBiped) event.getRenderer().getMainModel();

				if(isHandgunActive(player, handgunInMain, handgunInOff)) {
					EnumHand hand = getActiveHandgunHand(player, handgunInMain, handgunInOff);
					EnumHandSide primaryHand = player.getPrimaryHand();

					if(((hand == EnumHand.MAIN_HAND && primaryHand == EnumHandSide.RIGHT) || (hand == EnumHand.OFF_HAND && primaryHand == EnumHandSide.LEFT)) && model.rightArmPose != ModelBiped.ArmPose.BOW_AND_ARROW) {
						model.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
					} else if(((hand == EnumHand.OFF_HAND && primaryHand == EnumHandSide.RIGHT) || (hand == EnumHand.MAIN_HAND && primaryHand == EnumHandSide.LEFT)) && model.leftArmPose != ModelBiped.ArmPose.BOW_AND_ARROW) {
						model.leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
					}
				} else {
					if(model.rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
						model.rightArmPose = ModelBiped.ArmPose.ITEM;
					}
					if(model.leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW) {
						model.leftArmPose = ModelBiped.ArmPose.ITEM;
					}
				}
			}
		}
	}

	private static EnumHand getActiveHandgunHand(EntityPlayer player, boolean handgunInMain, boolean handgunInOff) {
		if(handgunInMain != handgunInOff) {
			return handgunInMain ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		}

		boolean mainValid = isValidTimeFrame(player.world, player.getHeldItemMainhand());
		boolean offValid = isValidTimeFrame(player.world, player.getHeldItemOffhand());

		if(mainValid != offValid)
			return mainValid ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;

		return ModItems.handgun.getCooldown(player.getHeldItemMainhand()) < ModItems.handgun.getCooldown(player.getHeldItemOffhand()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
	}

	private static boolean isHandgunActive(EntityPlayer player, boolean handgunInMain, boolean handgunInOff) {
		return handgunInMain && isValidTimeFrame(player.world, player.getHeldItemMainhand()) || handgunInOff && isValidTimeFrame(player.world, player.getHeldItemOffhand());

	}

	private static boolean isValidTimeFrame(World world, ItemStack handgun) {
		long cooldownTime = ModItems.handgun.getCooldown(handgun) + 5;

		return cooldownTime - world.getWorldTime() <= ModItems.handgun.getMaxItemUseDuration(handgun) && cooldownTime >= world.getWorldTime();
	}

	private static final List<Tuple<Component, HUDPosition>> hudComponents = Lists.newArrayList();

	@SubscribeEvent
	public static void onRenderTick(TickEvent.RenderTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if(!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
			return;

		if (hudComponents.isEmpty()) {
			initHUDComponents();
		}
		renderHUDComponents();
	}

	@SubscribeEvent
	public static void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		hudComponents.clear();
	}

	private static void renderHUDComponents() {
		for (Tuple<Component, HUDPosition> component : hudComponents) {
			HUDRenderrer.render(component.getFirst(), component.getSecond());
		}
	}

	private static void initHUDComponents() {
		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.alkahestryTome, Settings.HudPositions.alkahestryTome, new ItemStack(Items.REDSTONE), is -> NBTHelper.getInteger("charge", is)),
				Settings.HudPositions.alkahestryTome));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.destructionCatalyst, Settings.HudPositions.destructionCatalyst, new ItemStack(Items.GUNPOWDER), is -> NBTHelper.getInteger("gunpowder", is)),
				Settings.HudPositions.destructionCatalyst));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.midasTouchstone, Settings.HudPositions.midasTouchstone, new ItemStack(Items.GLOWSTONE_DUST), is -> NBTHelper.getInteger("glowstone", is)),
				Settings.HudPositions.midasTouchstone));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.infernalChalice, Settings.HudPositions.infernalChalice, new ItemStack(Items.LAVA_BUCKET), is -> NBTHelper.getInteger("fluidStacks", is) / 1000, Colors.get(Colors.RED)),
				Settings.HudPositions.infernalChalice));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.iceMagusRod, Settings.HudPositions.iceMagusRod, new ItemStack(Items.SNOWBALL), is -> NBTHelper.getInteger("snowballs", is)),
				Settings.HudPositions.iceMagusRod));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.glacialStaff, Settings.HudPositions.glacialStaff, new ItemStack(Items.SNOWBALL), is -> NBTHelper.getInteger("snowballs", is)),
				Settings.HudPositions.glacialStaff));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.enderStaff, Settings.HudPositions.enderStaff, is -> ModItems.enderStaff.getMode(is),
				ImmutableMap.of(
						"cast", new ChargePane(ModItems.enderStaff, new ItemStack(Items.ENDER_PEARL), is -> ModItems.enderStaff.getPearlCount(is, true)),
						"node_warp", new ChargePane(ModItems.enderStaff, new ItemStack(ModBlocks.wraithNode), is -> ModItems.enderStaff.getPearlCount(is, true)),
						"long_cast", new ChargePane(ModItems.enderStaff, new ItemStack(Items.ENDER_EYE), is -> ModItems.enderStaff.getPearlCount(is, true))
						)),	Settings.HudPositions.enderStaff));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.pyromancerStaff, Settings.HudPositions.pyromancerStaff, is -> ModItems.pyromancerStaff.getMode(is),
				ImmutableMap.of(
						"blaze", new ChargePane(ModItems.pyromancerStaff, new ItemStack(Items.BLAZE_POWDER), is -> ModItems.pyromancerStaff.getInternalStorageItemCount(is, Items.BLAZE_POWDER)),
						"charge", new ChargePane(ModItems.pyromancerStaff, new ItemStack(Items.FIRE_CHARGE), is -> ModItems.pyromancerStaff.getInternalStorageItemCount(is, Items.FIRE_CHARGE)),
						"eruption", Box.createVertical(Box.Alignment.RIGHT, new TextPane("ERUPT") , new ChargePane(ModItems.pyromancerStaff, new ItemStack(Items.BLAZE_POWDER), is -> ModItems.pyromancerStaff.getInternalStorageItemCount(is, Items.BLAZE_POWDER))),
						"flint_and_steel", new ItemStackPane(Items.FLINT_AND_STEEL)
				)),	Settings.HudPositions.pyromancerStaff));

		ChargePane rendingGaleFeatherPane = new ChargePane(ModItems.rendingGale, new ItemStack(Items.FEATHER), is -> ModItems.rendingGale.getFeatherCountClient(is, Minecraft.getMinecraft().player) / 100);
		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.rendingGale, Settings.HudPositions.rendingGale, is -> ModItems.rendingGale.getMode(is),
				ImmutableMap.of(
						"push", Box.createVertical(Box.Alignment.RIGHT, new TextPane("PUSH") , rendingGaleFeatherPane),
						"pull", Box.createVertical(Box.Alignment.RIGHT, new TextPane("PULL") , rendingGaleFeatherPane),
						"bolt", Box.createVertical(Box.Alignment.RIGHT, new TextPane("BOLT") , rendingGaleFeatherPane),
						"flight", Box.createVertical(Box.Alignment.RIGHT, new TextPane("FLIGHT") , rendingGaleFeatherPane)
				)),	Settings.HudPositions.rendingGale));

		Component contentsPane = new DynamicChargePane(ModItems.voidTear,
				is -> ModItems.voidTear.getContainerItem(is, true), is -> ModItems.voidTear.getContainerItem(is, true).getCount());
		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.voidTear, Settings.HudPositions.voidTear, is -> ModItems.voidTear.getMode(is).getName(),
				ImmutableMap.of(
						ItemVoidTear.Mode.FULL_INVENTORY.getName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane(LanguageHelper.getLocalization("item.void_tear.mode." + ItemVoidTear.Mode.FULL_INVENTORY.getName().toLowerCase())) , contentsPane),
						ItemVoidTear.Mode.NO_REFILL.getName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane(LanguageHelper.getLocalization("item.void_tear.mode." + ItemVoidTear.Mode.NO_REFILL.getName().toLowerCase())) , contentsPane),
						ItemVoidTear.Mode.ONE_STACK.getName(), Box.createVertical(Box.Alignment.RIGHT, new TextPane(LanguageHelper.getLocalization("item.void_tear.mode." + ItemVoidTear.Mode.ONE_STACK.getName().toLowerCase())) , contentsPane)
				)) {
			@Override
			public boolean shouldRender() {
				return !ModItems.voidTear.isEmpty(InventoryHelper.getCorrectItemFromEitherHand(Minecraft.getMinecraft().player, ModItems.voidTear), true);
			}
		},	Settings.HudPositions.voidTear));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.harvestRod, Settings.HudPositions.harvestRod, is -> ModItems.harvestRod.getMode(is),
				ImmutableMap.of(
						ItemHarvestRod.BONE_MEAL_MODE, new ChargePane(ModItems.harvestRod, new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage()), is -> ModItems.harvestRod.getBoneMealCount(is, true)),
						ItemHarvestRod.HOE_MODE, new ItemStackPane(Items.WOODEN_HOE),
						ChargeableItemInfoPane.DYNAMIC_PANE, new DynamicChargePane(ModItems.harvestRod, is -> ModItems.harvestRod.getCurrentPlantable(is, true), is -> ModItems.harvestRod.getPlantableQuantity(is, ModItems.harvestRod.getCurrentPlantableSlot(is), true))
				)),	Settings.HudPositions.harvestRod));

		hudComponents.add(new Tuple<>(new ChargeableItemInfoPane(ModItems.sojournerStaff, Settings.HudPositions.sojournerStaff, is -> ChargeableItemInfoPane.DYNAMIC_PANE,
				ImmutableMap.of(
						ChargeableItemInfoPane.DYNAMIC_PANE, new DynamicChargePane(ModItems.sojournerStaff, is -> new ItemStack(RegistryHelper.getItemFromName(ModItems.sojournerStaff.getTorchPlacementMode(is))), is -> ModItems.sojournerStaff.getTorchCount(is))
				)),	Settings.HudPositions.sojournerStaff));

		hudComponents.add(new Tuple<>(new HeroMedallionPane(), Settings.HudPositions.heroMedallion));

		hudComponents.add(new Tuple<>(Box.createVertical(Box.Alignment.RIGHT, new HandgunPane(EnumHand.OFF_HAND), new HandgunPane(EnumHand.MAIN_HAND)), Settings.HudPositions.handgun));

		hudComponents.add(new Tuple<>(new CharmPane(), Settings.HudPositions.mobCharm));
	}
}
