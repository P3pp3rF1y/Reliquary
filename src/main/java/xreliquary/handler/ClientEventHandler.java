package xreliquary.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModCapabilities;
import xreliquary.init.ModItems;
import xreliquary.items.*;
import xreliquary.items.util.IHarvestRodCache;
import xreliquary.reference.Colors;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

public class ClientEventHandler {
	private static RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
	private static int time;

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if(!Minecraft.isGuiEnabled() || !mc.inGameHasFocus || mc.getRenderManager().getFontRenderer() == null)
			return;

		handleTickIncrement(event);
		handleHandgunHUDCheck(mc);
		handleSojournerHUDCheck(mc);
		handleTomeHUDCheck(mc);
		handleDestructionCatalystHUDCheck(mc);
		handleEnderStaffHUDCheck(mc);
		handleGlacialStaffHUDCheck(mc);
		handleIceMagusRodHUDCheck(mc);
		handleVoidTearHUDCheck(mc);
		handleMidasTouchstoneHUDCheck(mc);
		handleHarvestRodHUDCheck(mc);
		handleInfernalChaliceHUDCheck(mc);
		handleHeroMedallionHUDCheck(mc);
		handlePyromancerStaffHUDCheck(mc);
		handleRendingGaleHUDCheck(mc);
	}

	public void handleTickIncrement(TickEvent.RenderTickEvent event) {
		// this is currently used for nothing but the blinking magazine in the handgun HUD renderer
		if(event.phase != TickEvent.Phase.END)
			return;
		//4096 is just an arbitrary stopping point, I didn't need it to go that high, honestly. left in case we need something weird.
		if(getTime() > 4096) {
			time = 0;
		} else {
			time++;
		}
	}

	public static int getTime() {
		return time;
	}

	private EnumHand getHandHoldingCorrectItem(EntityPlayer player, Item item) {
		if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == item) {
			return EnumHand.MAIN_HAND;
		}

		if(player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() == item) {
			return EnumHand.OFF_HAND;
		}
		return null;
	}

	private ItemStack getCorrectItemFromEitherHand(EntityPlayer player, Item item) {
		if(player == null)
			return null;

		EnumHand itemInHand = getHandHoldingCorrectItem(player, item);

		if(itemInHand == null)
			return null;

		return player.getHeldItem(itemInHand);
	}

	public void handleTomeHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack tomeStack = getCorrectItemFromEitherHand(player, ModItems.alkahestryTome);

		if(tomeStack == null)
			return;

		ItemStack chargeStack = Settings.AlkahestryTome.baseItem.copy();
		chargeStack.stackSize = NBTHelper.getInteger("charge", tomeStack);
		renderStandardTwoItemHUD(mc, player, tomeStack, chargeStack, Settings.HudPositions.alkahestryTome, 0, 0);
	}

	public void handleDestructionCatalystHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack destructionCatalystStack = getCorrectItemFromEitherHand(player, ModItems.destructionCatalyst);

		if(destructionCatalystStack == null)
			return;

		ItemStack gunpowderStack = new ItemStack(Items.gunpowder, NBTHelper.getInteger("gunpowder", destructionCatalystStack), 0);
		renderStandardTwoItemHUD(mc, player, destructionCatalystStack, gunpowderStack, Settings.HudPositions.destructionCatalyst, 0, 0);
	}

	public void handleEnderStaffHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack enderStaffStack = getCorrectItemFromEitherHand(player, ModItems.enderStaff);

		if(enderStaffStack == null)
			return;

		ItemEnderStaff enderStaffItem = ModItems.enderStaff;
		String staffMode = enderStaffItem.getMode(enderStaffStack);
		ItemStack displayItemStack = new ItemStack(Items.ender_pearl, enderStaffItem.getPearlCount(enderStaffStack), 0);
		if(staffMode.equals("node_warp")) {
			displayItemStack = new ItemStack(ModBlocks.wraithNode, enderStaffItem.getPearlCount(enderStaffStack), 0);
		} else if(staffMode.equals("long_cast")) {
			displayItemStack = new ItemStack(Items.ender_eye, enderStaffItem.getPearlCount(enderStaffStack), 0);
		}
		renderStandardTwoItemHUD(mc, player, enderStaffStack, displayItemStack, Settings.HudPositions.enderStaff, 0, 0);
	}

	public void handleIceMagusRodHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack iceRodStack = getCorrectItemFromEitherHand(player, ModItems.iceMagusRod);

		if(iceRodStack == null)
			return;

		ItemStack snowballStack = new ItemStack(Items.snowball, NBTHelper.getInteger("snowballs", iceRodStack), 0);
		//still allows for differing HUD positions, like a baws.
		int hudPosition = Settings.HudPositions.iceMagusRod;
		renderStandardTwoItemHUD(mc, player, iceRodStack, snowballStack, hudPosition, 0, NBTHelper.getInteger("snowballs", iceRodStack));
	}

	public void handleGlacialStaffHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack glacialStaff = getCorrectItemFromEitherHand(player, ModItems.glacialStaff);

		if(glacialStaff == null)
			return;

		ItemStack snowballStack = new ItemStack(Items.snowball, NBTHelper.getInteger("snowballs", glacialStaff), 0);
		//still allows for differing HUD positions, like a baws.
		int hudPosition = Settings.HudPositions.glacialStaff;
		renderStandardTwoItemHUD(mc, player, glacialStaff, snowballStack, hudPosition, 0, NBTHelper.getInteger("snowballs", glacialStaff));
	}

	public void handleVoidTearHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack voidTearStack = getCorrectItemFromEitherHand(player, ModItems.filledVoidTear);

		if(voidTearStack == null)
			return;

		ItemVoidTear voidTearItem = (ItemVoidTear) voidTearStack.getItem();
		ItemStack containedItemStack = voidTearItem.getContainedItem(voidTearStack);
		renderStandardTwoItemHUD(mc, player, voidTearStack, containedItemStack, Settings.HudPositions.voidTear, 0, 0);
	}

	public void handleMidasTouchstoneHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack midasTouchstoneStack = getCorrectItemFromEitherHand(player, ModItems.midasTouchstone);

		if(midasTouchstoneStack == null)
			return;

		ItemStack glowstoneStack = new ItemStack(Items.glowstone_dust, NBTHelper.getInteger("glowstone", midasTouchstoneStack), 0);
		renderStandardTwoItemHUD(mc, player, midasTouchstoneStack, glowstoneStack, Settings.HudPositions.midasTouchstone, 0, 0);
	}

	public void handleHarvestRodHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack harvestRodStack = getCorrectItemFromEitherHand(player, ModItems.harvestRod);

		if(harvestRodStack == null)
			return;

		IHarvestRodCache cache = harvestRodStack.getCapability(ModCapabilities.HARVEST_ROD_CACHE, null);

		ItemStack secondaryStack;
		ItemHarvestRod harvestRod = ModItems.harvestRod;
		if(harvestRod.getMode(harvestRodStack).equals(ModItems.harvestRod.PLANTABLE_MODE)) {
			secondaryStack = harvestRod.getPlantableItems(harvestRodStack).get(harvestRod.getCurrentPlantableIndex(harvestRodStack)).copy();
			int plantableCount = harvestRod.getPlantableQuantity(harvestRodStack, harvestRod.getCurrentPlantableIndex(harvestRodStack));

			if(cache != null && player.isHandActive()) {
				plantableCount -= cache.getTimesUsed();
			}

			secondaryStack.stackSize = plantableCount;
		} else if(harvestRod.getMode(harvestRodStack).equals(ModItems.harvestRod.BONE_MEAL_MODE)) {
			int boneMealCount = harvestRod.getBoneMealCount(harvestRodStack);

			if(cache != null && player.isHandActive()) {
				boneMealCount -= cache.getTimesUsed();
			}

			secondaryStack = new ItemStack(Items.dye, boneMealCount, Reference.WHITE_DYE_META);
		} else {
			secondaryStack = new ItemStack(Items.wooden_hoe);
		}

		renderStandardTwoItemHUD(mc, player, harvestRodStack, secondaryStack, Settings.HudPositions.harvestRod, 0, 0);
	}

	public void handleInfernalChaliceHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack infernalChaliceStack = getCorrectItemFromEitherHand(player, ModItems.infernalChalice);

		if(infernalChaliceStack == null)
			return;

		ItemStack lavaStack = new ItemStack(Items.lava_bucket, NBTHelper.getInteger("fluidStacks", infernalChaliceStack), 0);
		renderStandardTwoItemHUD(mc, player, infernalChaliceStack, lavaStack, Settings.HudPositions.infernalChalice, Colors.get(Colors.BLOOD_RED_COLOR), lavaStack.stackSize / 1000);
	}

	public void handleHeroMedallionHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack heroMedallionStack = getCorrectItemFromEitherHand(player, ModItems.heroMedallion);

		if(heroMedallionStack == null)
			return;

		int experience = NBTHelper.getInteger("experience", heroMedallionStack);
		renderStandardTwoItemHUD(mc, player, heroMedallionStack, null, Settings.HudPositions.heroMedallion, Colors.get(Colors.GREEN), experience);
	}

	public void handlePyromancerStaffHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack pyromancerStaffStack = getCorrectItemFromEitherHand(player, ModItems.pyromancerStaff);

		if(pyromancerStaffStack == null)
			return;

		int charge = 0;
		int blaze = 0;
		NBTTagCompound tagCompound = NBTHelper.getTag(pyromancerStaffStack);
		if(tagCompound != null) {
			NBTTagList tagList = tagCompound.getTagList("Items", 10);
			for(int i = 0; i < tagList.tagCount(); ++i) {
				NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
				String itemName = tagItemData.getString("Name");
				Item containedItem = RegistryHelper.getItemFromName(itemName);
				int quantity = tagItemData.getInteger("Quantity");

				if(containedItem == Items.blaze_powder) {
					blaze = quantity;
				} else if(containedItem == Items.fire_charge) {
					charge = quantity;
				}
			}
		}

		String staffMode = ((ItemPyromancerStaff) pyromancerStaffStack.getItem()).getMode(pyromancerStaffStack);

		ItemStack fireChargeStack = new ItemStack(Items.fire_charge, charge, 0);
		ItemStack blazePowderStack = new ItemStack(Items.blaze_powder, blaze, 0);
		renderPyromancerStaffHUD(mc, player, pyromancerStaffStack, fireChargeStack, blazePowderStack, staffMode);
	}

	private static void renderPyromancerStaffHUD(Minecraft minecraft, EntityPlayer player, ItemStack hudStack, ItemStack secondaryStack, ItemStack tertiaryStack, String staffMode) {
		int color = Colors.get(Colors.PURE);

		float overlayScale = 2.5F;
		float overlayOpacity = 0.75F;

		GL11.glPushMatrix();
		ScaledResolution sr = new ScaledResolution(minecraft);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

		GL11.glPushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_LIGHTING);

		int hudOverlayX = 8;
		int hudOverlayY = 8;

		boolean leftSide = Settings.HudPositions.pyromancerStaff == 0 || Settings.HudPositions.pyromancerStaff == 2;
		switch(Settings.HudPositions.pyromancerStaff) {
			case 1: {
				hudOverlayX = (int) (sr.getScaledWidth() - 8);
				break;
			}
			case 2: {
				hudOverlayY = (int) (sr.getScaledHeight() - 18 * overlayScale);
				break;
			}
			case 3: {
				hudOverlayX = (int) (sr.getScaledWidth() - 8);
				hudOverlayY = (int) (sr.getScaledHeight() - 18 * overlayScale);
				break;
			}
			default: {
				break;
			}
		}

		renderItemIntoGUI(minecraft.getRenderManager().getFontRenderer(), hudStack, hudOverlayX - (leftSide ? 0 : 15), hudOverlayY, overlayOpacity, overlayScale);

		String friendlyStaffMode = "";
		if(staffMode.equals("eruption"))
			friendlyStaffMode = "ERUPT";

		if(secondaryStack != null && (staffMode.equals("charge"))) {
			renderItem.renderItemAndEffectIntoGUI(secondaryStack, hudOverlayX + (leftSide ? 0 : -(16 + (Integer.toString(secondaryStack.stackSize).length() * 6))), hudOverlayY + 24);
			minecraft.getRenderManager().getFontRenderer().drawStringWithShadow(Integer.toString(secondaryStack.stackSize), hudOverlayX + (leftSide ? 16 : -(Integer.toString(secondaryStack.stackSize).length() * 6)), hudOverlayY + 30, color);
		} else if(tertiaryStack != null && (staffMode.equals("eruption") || staffMode.equals("blaze"))) {
			renderItem.renderItemAndEffectIntoGUI(tertiaryStack, hudOverlayX + (leftSide ? 0 : -(16 + (Integer.toString(tertiaryStack.stackSize).length() * 6))), hudOverlayY + 24);
			minecraft.getRenderManager().getFontRenderer().drawStringWithShadow(Integer.toString(tertiaryStack.stackSize), hudOverlayX + (leftSide ? 16 : -(Integer.toString(tertiaryStack.stackSize).length() * 6)), hudOverlayY + 30, color);
			if(staffMode.equals("eruption"))
				minecraft.getRenderManager().getFontRenderer().drawStringWithShadow(friendlyStaffMode, hudOverlayX - (leftSide ? 0 : (friendlyStaffMode.length() * 6)), hudOverlayY + 18, color);
		} else if(staffMode.equals("flint_and_steel")) {
			renderItem.renderItemAndEffectIntoGUI(new ItemStack(Items.flint_and_steel, 1, 0), hudOverlayX - (leftSide ? 0 : 15), hudOverlayY + 24);
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	public void handleRendingGaleHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack rendingGaleStack = getCorrectItemFromEitherHand(player, ModItems.rendingGale);

		if(rendingGaleStack == null)
			return;

		ItemStack featherStack = new ItemStack(Items.feather, ModItems.rendingGale.getFeatherCount(rendingGaleStack), 0);
		renderStandardTwoItemHUD(mc, player, rendingGaleStack, featherStack, Settings.HudPositions.rendingGale, 0, Math.max(featherStack.stackSize/100, 0));
	}

	public void handleHandgunHUDCheck(Minecraft mc) {
		// handles rendering the hud for the handgun, WIP
		EntityPlayer player = mc.thePlayer;

		ItemStack handgunStack = getCorrectItemFromEitherHand(player, ModItems.handgun);

		if(handgunStack == null)
			return;

		ItemHandgun handgunItem = (ItemHandgun) handgunStack.getItem();
		ItemStack bulletStack = new ItemStack(ModItems.bullet, handgunItem.getBulletCount(handgunStack), handgunItem.getBulletType(handgunStack));
		renderHandgunHUD(mc, player, handgunStack, bulletStack);
	}

	private static void renderHandgunHUD(Minecraft minecraft, EntityPlayer player, ItemStack handgunStack, ItemStack bulletStack) {
		float overlayScale = 2.5F;
		float overlayOpacity = 0.75F;

		GL11.glPushMatrix();
		ScaledResolution sr = new ScaledResolution(minecraft);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

		GL11.glPushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_LIGHTING);

		int hudOverlayX = 8;
		int hudOverlayY = 8;
		boolean leftSide = Settings.HudPositions.handgun == 0 || Settings.HudPositions.handgun == 2;

		switch(Settings.HudPositions.handgun) {
			case 0: {
				break;
			}
			case 1: {
				hudOverlayX = (int) (sr.getScaledWidth() - 16 * overlayScale);
				break;
			}
			case 2: {
				hudOverlayY = (int) (sr.getScaledHeight() - 16 * overlayScale);
				break;
			}
			case 3: {
				hudOverlayX = (int) (sr.getScaledWidth() - 16 * overlayScale);
				hudOverlayY = (int) (sr.getScaledHeight() - 16 * overlayScale);
				break;
			}
			default: {
				break;
			}
		}

		renderItemIntoGUI(minecraft.getRenderManager().getFontRenderer(), handgunStack, hudOverlayX, hudOverlayY, overlayOpacity, overlayScale);
		// if the gun is empty, displays a blinking empty magazine instead.
		if(bulletStack.stackSize == 0) {
			if(getTime() % 32 > 16) {
				// offsets it a little to the left, it looks silly if you put it
				// over the gun.
				renderItemIntoGUI(minecraft.getRenderManager().getFontRenderer(), new ItemStack(ModItems.magazine, 1, 0), hudOverlayX - (leftSide ? 0 : 8), hudOverlayY + 12, overlayOpacity, overlayScale / 2F);
			}
		} else {
			// renders the number of bullets onto the screen.
			for(int xOffset = 0; xOffset < bulletStack.stackSize; xOffset++) {
				// xOffset * 6 makes the bullets line up, -16 moves them all to
				// the left by a bit

				renderItemIntoGUI(minecraft.getRenderManager().getFontRenderer(), bulletStack, hudOverlayX - (leftSide ? 0 : 8) - 1 * (leftSide ? -1 : 1) * (xOffset * 10), hudOverlayY + 12, 1.0F, overlayScale / 2F);
			}
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	public void handleSojournerHUDCheck(Minecraft mc) {
		// handles rendering the hud for the sojourner's staff so we don't have to use chat messages, because annoying.
		EntityPlayer player = mc.thePlayer;

		ItemStack sojournerStack = getCorrectItemFromEitherHand(player, ModItems.sojournerStaff);

		if(sojournerStack == null)
			return;

		ItemSojournerStaff sojournerItem = (ItemSojournerStaff) sojournerStack.getItem();
		String placementItemName = sojournerItem.getTorchPlacementMode(sojournerStack);
		//for use with font renderer, hopefully.
		int amountOfItem = sojournerItem.getTorchCount(sojournerStack);
		Item placementItem = null;
		if(placementItemName != null)
			placementItem = RegistryHelper.getItemFromName(placementItemName);

		ItemStack placementStack = null;
		if(placementItem != null) {
			placementStack = new ItemStack(placementItem, amountOfItem, 0);
		}
		renderStandardTwoItemHUD(mc, player, sojournerStack, placementStack, Settings.HudPositions.sojournerStaff, 0, 0);
	}

	private static void renderStandardTwoItemHUD(Minecraft minecraft, EntityPlayer player, ItemStack hudStack, ItemStack secondaryStack, int hudPosition, int colorOverride, int stackSizeOverride) {
		int stackSize = 0;
		if(stackSizeOverride > 0)
			stackSize = stackSizeOverride;
		int color = Colors.get(Colors.PURE);
		if(colorOverride > 0)
			color = colorOverride;
		float overlayScale = 2.5F;
		float overlayOpacity = 0.75F;

		GL11.glPushMatrix();
		ScaledResolution sr = new ScaledResolution(minecraft);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

		GL11.glPushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_LIGHTING);

		int hudOverlayX = 8;
		int hudOverlayY = 8;

		boolean leftSide = hudPosition == 0 || hudPosition == 2;
		switch(hudPosition) {
			case 1: {
				hudOverlayX = (int) (sr.getScaledWidth() - 8);
				break;
			}
			case 2: {
				hudOverlayY = (int) (sr.getScaledHeight() - (18 * overlayScale));
				break;
			}
			case 3: {
				hudOverlayX = (int) (sr.getScaledWidth() - 8);
				hudOverlayY = (int) (sr.getScaledHeight() - (18 * overlayScale));
				break;
			}
			default: {
				break;
			}
		}

		renderItemIntoGUI(minecraft.getRenderManager().getFontRenderer(), hudStack, hudOverlayX - (leftSide ? 0 : 15), hudOverlayY, overlayOpacity, overlayScale);

		//TODO add rending gale modes translations
		//special item conditions are handled on a per-item-type basis:
		if(hudStack.getItem() instanceof ItemRendingGale) {
			ItemRendingGale staffItem = (ItemRendingGale) hudStack.getItem();
			String staffMode = staffItem.getMode(hudStack);
			if(staffMode.equals("flight"))
				staffMode = "FLIGHT";
			else if(staffMode.equals("push"))
				staffMode = "PUSH";
			else if(staffMode.equals("pull"))
				staffMode = "PULL";
			else
				staffMode = "BOLT";
			minecraft.getRenderManager().getFontRenderer().drawStringWithShadow(staffMode, hudOverlayX - (leftSide ? 0 : staffMode.length() * 6), hudOverlayY + 18, color);
		}

		if(secondaryStack != null) {
			if(stackSize == 0)
				stackSize = secondaryStack.stackSize;
			renderItem.renderItemAndEffectIntoGUI(secondaryStack, hudOverlayX - (leftSide ? 0 : 16 + (Integer.toString(stackSize).length() * 6)), hudOverlayY + 25);
			hudOverlayX = hudOverlayX + (leftSide ? 16 : 0);
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		minecraft.fontRendererObj.drawStringWithShadow(Integer.toString(stackSize), hudOverlayX - (leftSide ? 0 : (Integer.toString(stackSize).length() * 6)), hudOverlayY + 29, color);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	public static void renderItemIntoGUI(FontRenderer fontRenderer, ItemStack itemStack, int x, int y, float opacity, float scale) {
		renderItem.renderItemIntoGUI(itemStack, x, y);
	}
}
