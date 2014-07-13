package xreliquary.event;

import lib.enderwizards.sandstone.init.ContentHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import xreliquary.Reliquary;
import xreliquary.items.ItemHandgun;
import xreliquary.lib.Names;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ClientEventHandler {

	private static int time;

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		handleTickIncrement(event);
		handleHandgunHUDCheck();
	}

	public void handleTickIncrement(TickEvent.RenderTickEvent event) {
		// handles the color shifting of the twilight cloak, until we can throw
		// it on an animation
		if (event.phase != TickEvent.Phase.END)
			return;
		// used to go arbitrarily all the way to 88, which left us limited on
		// how to handle our ticks.
		// this is a nice even number. Also we don't handle blinking with this
		// anymore so no need for weird math/modulo.
		if (getTime() > 4096) {
			time = 0;
		} else {
			time++;
		}
	}

	public void handleHandgunHUDCheck() {
		// handles rendering the hud for the handgun, WIP
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.thePlayer;

		if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemHandgun))
			return;
		ItemStack handgunStack = player.getCurrentEquippedItem();
		ItemHandgun handgunItem = (ItemHandgun) handgunStack.getItem();
		ItemStack bulletStack = new ItemStack(ContentHandler.getItem(Names.bullet), handgunItem.getBulletCount(handgunStack), handgunItem.getBulletType(handgunStack));
		renderHandgunHUD(mc, player, handgunStack, bulletStack);
	}

	private static void renderHandgunHUD(Minecraft minecraft, EntityPlayer player, ItemStack handgunStack, ItemStack bulletStack) {

		float overlayScale = 2.5F;
		float overlayOpacity = 0.75F;

		GL11.glPushMatrix();
		ScaledResolution sr = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
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

		switch (Reliquary.CONFIG.getInt(Names.handgun, "hudPosition")) {
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

		renderItemIntoGUI(minecraft.fontRenderer, handgunStack, hudOverlayX, hudOverlayY, overlayOpacity, overlayScale);
		// if the gun is empty, displays a blinking empty magazine instead.
		if (bulletStack.stackSize == 0) {
			if (getTime() % 32 > 16) {
				// offsets it a little to the left, it looks silly if you put it
				// over the gun.
				renderItemIntoGUI(minecraft.fontRenderer, new ItemStack(ContentHandler.getItem(Names.magazine), 1, 0), hudOverlayX - 8, hudOverlayY + 12, overlayOpacity, overlayScale / 2F);
			}
		} else {
			// renders the number of bullets onto the screen.
			for (int xOffset = 0; xOffset < bulletStack.stackSize; xOffset++) {
				// xOffset * 6 makes the bullets line up, -16 moves them all to
				// the left by a bit
				renderItemIntoGUI(minecraft.fontRenderer, bulletStack, hudOverlayX - 8 - (xOffset * 12), hudOverlayY + 12, 1.0F, overlayScale / 2F);
			}
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	public static void renderItemIntoGUI(FontRenderer fontRenderer, ItemStack itemStack, int x, int y, float opacity, float scale) {
		GL11.glDisable(GL11.GL_LIGHTING);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationItemsTexture);
		for (int passes = 0; passes < itemStack.getItem().getRenderPasses(itemStack.getItemDamage()); passes++) {
			int overlayColour = itemStack.getItem().getColorFromItemStack(itemStack, passes);
			IIcon icon = itemStack.getItem().getIcon(itemStack, passes);
			float red = (overlayColour >> 16 & 255) / 255.0F;
			float green = (overlayColour >> 8 & 255) / 255.0F;
			float blue = (overlayColour & 255) / 255.0F;
			GL11.glColor4f(red, green, blue, opacity);
			drawTexturedQuad(x, y, icon, 16 * scale, 16 * scale, -90);
			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}

	public static void drawTexturedQuad(int x, int y, IIcon icon, float width, float height, double zLevel) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, zLevel, icon.getMinU(), icon.getMaxV());
		tessellator.addVertexWithUV(x + width, y + height, zLevel, icon.getMaxU(), icon.getMaxV());
		tessellator.addVertexWithUV(x + width, y, zLevel, icon.getMaxU(), icon.getMinV());
		tessellator.addVertexWithUV(x, y, zLevel, icon.getMinU(), icon.getMinV());
		tessellator.draw();
	}

	public static int getTime() {
		return time;
	}

}
