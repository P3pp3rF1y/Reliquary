package xreliquary.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.items.*;
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
		//handles glacial staff as well
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

	public void handleTomeHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemAlkahestryTome))
			return;

		ItemStack tomeStack = player.getCurrentEquippedItem();
		ItemStack chargeStack = Settings.AlkahestryTome.baseItem.copy();
		chargeStack.stackSize = NBTHelper.getInteger("charge", tomeStack);
		renderStandardTwoItemHUD(mc, player, tomeStack, chargeStack, Settings.HudPositions.alkahestryTome, 0, 0);
	}

	public void handleDestructionCatalystHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemDestructionCatalyst))
			return;

		ItemStack destructionCatalystStack = player.getCurrentEquippedItem();
		ItemStack gunpowderStack = new ItemStack(Items.gunpowder, NBTHelper.getInteger("gunpowder", destructionCatalystStack), 0);
		renderStandardTwoItemHUD(mc, player, destructionCatalystStack, gunpowderStack, Settings.HudPositions.destructionCatalyst, 0, 0);
	}

	public void handleEnderStaffHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemEnderStaff))
			return;

		ItemStack enderStaffStack = player.getCurrentEquippedItem();
		ItemEnderStaff enderStaffItem = (ItemEnderStaff) enderStaffStack.getItem();
		String staffMode = enderStaffItem.getMode(enderStaffStack);
		ItemStack displayItemStack = new ItemStack(Items.ender_pearl, NBTHelper.getInteger("ender_pearls", enderStaffStack), 0);
		if(staffMode.equals("node_warp")) {
			displayItemStack = new ItemStack(ModBlocks.wraithNode, NBTHelper.getInteger("ender_pearls", enderStaffStack), 0);
		} else if(staffMode.equals("long_cast")) {
			displayItemStack = new ItemStack(Items.ender_eye, NBTHelper.getInteger("ender_pearls", enderStaffStack), 0);
		}
		renderStandardTwoItemHUD(mc, player, enderStaffStack, displayItemStack, Settings.HudPositions.enderStaff, 0, 0);
	}

	public void handleIceMagusRodHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		//returns true for Glacial Staff because it extends IceRod.
		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemIceMagusRod))
			return;

		ItemStack iceRodStack = player.getCurrentEquippedItem();
		ItemStack snowballStack = new ItemStack(Items.snowball, NBTHelper.getInteger("snowballs", iceRodStack), 0);
		//still allows for differing HUD positions, like a baws.
		int hudPosition = (player.getCurrentEquippedItem().getItem() instanceof ItemGlacialStaff) ? Settings.HudPositions.glacialStaff : Settings.HudPositions.iceMagusRod;
		renderStandardTwoItemHUD(mc, player, iceRodStack, snowballStack, hudPosition, 0, NBTHelper.getInteger("snowballs", iceRodStack));
	}

	public void handleVoidTearHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemVoidTear))
			return;

		ItemStack voidTearStack = player.getCurrentEquippedItem();
		ItemVoidTear voidTearItem = (ItemVoidTear) voidTearStack.getItem();
		ItemStack containedItemStack = voidTearItem.getContainedItem(voidTearStack);
		renderStandardTwoItemHUD(mc, player, voidTearStack, containedItemStack, Settings.HudPositions.voidTear, 0, 0);
	}

	public void handleMidasTouchstoneHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemMidasTouchstone))
			return;

		ItemStack midasTouchstoneStack = player.getCurrentEquippedItem();
		ItemStack glowstoneStack = new ItemStack(Items.glowstone_dust, NBTHelper.getInteger("glowstone", midasTouchstoneStack), 0);
		renderStandardTwoItemHUD(mc, player, midasTouchstoneStack, glowstoneStack, Settings.HudPositions.midasTouchstone, 0, 0);
	}

	public void handleHarvestRodHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemHarvestRod))
			return;

		ItemStack harvestRodStack = player.getCurrentEquippedItem();
		ItemStack bonemealStack = new ItemStack(Items.dye, NBTHelper.getInteger("bonemeal", harvestRodStack), Reference.WHITE_DYE_META);
		renderStandardTwoItemHUD(mc, player, harvestRodStack, bonemealStack, Settings.HudPositions.harvestRod, 0, 0);
	}

	public void handleInfernalChaliceHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemInfernalChalice))
			return;

		ItemStack infernalChaliceStack = player.getCurrentEquippedItem();
		ItemStack lavaStack = new ItemStack(Items.lava_bucket, NBTHelper.getInteger("fluidStacks", infernalChaliceStack), 0);
		renderStandardTwoItemHUD(mc, player, infernalChaliceStack, lavaStack, Settings.HudPositions.infernalChalice, Colors.get(Colors.BLOOD_RED_COLOR), lavaStack.stackSize / 1000);
	}

	public void handleHeroMedallionHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemHeroMedallion))
			return;

		ItemStack heroMedallionStack = player.getCurrentEquippedItem();
		int experience = NBTHelper.getInteger("experience", heroMedallionStack);
		renderStandardTwoItemHUD(mc, player, heroMedallionStack, null, Settings.HudPositions.heroMedallion, Colors.get(Colors.GREEN), experience);
	}

	public void handlePyromancerStaffHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemPyromancerStaff))
			return;

		ItemStack pyromancerStaffStack = player.getCurrentEquippedItem();

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

		int hudOverlayX = 0;
		int hudOverlayY = 0;

		switch(Settings.HudPositions.pyromancerStaff) {
			case 0: {
				hudOverlayX = 0;
				hudOverlayY = 0;
				break;
			}
			case 1: {
				hudOverlayX = (int) (sr.getScaledWidth() - 16 * overlayScale);
				hudOverlayY = 0;
				break;
			}
			case 2: {
				hudOverlayX = 0;
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

		renderItemIntoGUI(minecraft.getRenderManager().getFontRenderer(), hudStack, hudOverlayX, hudOverlayY, overlayOpacity, overlayScale);

		String friendlyStaffMode = "";
		if(staffMode.equals("eruption"))
			friendlyStaffMode = "ERUPT";

		if(secondaryStack != null && (staffMode.equals("charge"))) {
			renderItem.renderItemAndEffectIntoGUI(secondaryStack, hudOverlayX, hudOverlayY + 24);
			minecraft.getRenderManager().getFontRenderer().drawStringWithShadow(Integer.toString(secondaryStack.stackSize), hudOverlayX + 15, hudOverlayY + 30, color);
		} else if(tertiaryStack != null && (staffMode.equals("eruption") || staffMode.equals("blaze"))) {
			renderItem.renderItemAndEffectIntoGUI(tertiaryStack, hudOverlayX, hudOverlayY + 24);
			minecraft.getRenderManager().getFontRenderer().drawStringWithShadow(Integer.toString(tertiaryStack.stackSize), hudOverlayX + 15, hudOverlayY + 30, color);
			if(staffMode.equals("eruption"))
				minecraft.getRenderManager().getFontRenderer().drawStringWithShadow(friendlyStaffMode, hudOverlayX, hudOverlayY + 18, color);
		} else if(staffMode.equals("flint_and_steel")) {
			renderItem.renderItemAndEffectIntoGUI(new ItemStack(Items.flint_and_steel, 1, 0), hudOverlayX, hudOverlayY + 24);
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	public void handleRendingGaleHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemRendingGale))
			return;

		ItemStack rendingGaleStack = player.getCurrentEquippedItem();
		ItemStack featherStack = new ItemStack(Items.feather, NBTHelper.getInteger("feathers", rendingGaleStack), 0);
		int charge = featherStack.stackSize;
		if(player.isUsingItem()) {
			int count = rendingGaleStack.getItem().getMaxItemUseDuration(rendingGaleStack) - (player.getItemInUseCount() - 1);
			charge -= (count * ItemRendingGale.getChargeCost());
		}
		charge /= 100;
		renderStandardTwoItemHUD(mc, player, rendingGaleStack, featherStack, Settings.HudPositions.rendingGale, 0, Math.max(charge, 0));
	}

	public void handleHandgunHUDCheck(Minecraft mc) {
		// handles rendering the hud for the handgun, WIP
		EntityPlayer player = mc.thePlayer;

		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemHandgun))
			return;

		ItemStack handgunStack = player.getCurrentEquippedItem();
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

		int hudOverlayX = 0;
		int hudOverlayY = 0;

		switch(Settings.HudPositions.handgun) {
			case 0: {
				hudOverlayX = 0;
				hudOverlayY = 0;
				break;
			}
			case 1: {
				hudOverlayX = (int) (sr.getScaledWidth() - 16 * overlayScale);
				hudOverlayY = 0;
				break;
			}
			case 2: {
				hudOverlayX = 0;
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
				renderItemIntoGUI(minecraft.getRenderManager().getFontRenderer(), new ItemStack(ModItems.magazine, 1, 0), hudOverlayX - 8, hudOverlayY + 12, overlayOpacity, overlayScale / 2F);
			}
		} else {
			// renders the number of bullets onto the screen.
			for(int xOffset = 0; xOffset < bulletStack.stackSize; xOffset++) {
				// xOffset * 6 makes the bullets line up, -16 moves them all to
				// the left by a bit
				renderItemIntoGUI(minecraft.getRenderManager().getFontRenderer(), bulletStack, hudOverlayX - 8 - (xOffset * 10), hudOverlayY + 12, 1.0F, overlayScale / 2F);
			}
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	public void handleSojournerHUDCheck(Minecraft mc) {
		// handles rendering the hud for the sojourner's staff so we don't have to use chat messages, because annoying.
		EntityPlayer player = mc.thePlayer;

		if(player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemSojournerStaff))
			return;
		ItemStack sojournerStack = player.getCurrentEquippedItem();
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
		minecraft.fontRendererObj.drawStringWithShadow(Integer.toString(stackSize), hudOverlayX - (leftSide ? 0 :(Integer.toString(stackSize).length() * 6)), hudOverlayY + 29, color);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	public static void renderItemIntoGUI(FontRenderer fontRenderer, ItemStack itemStack, int x, int y, float opacity, float scale) {
		renderItem.renderItemIntoGUI(itemStack, x, y);
	}
}
