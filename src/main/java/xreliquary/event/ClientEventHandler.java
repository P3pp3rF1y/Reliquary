package xreliquary.event;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xreliquary.Reliquary;
import xreliquary.client.model.ModelWitchHat;
import xreliquary.items.ItemHandgun;
import xreliquary.items.ItemSojournerStaff;
import xreliquary.lib.Names;

public class ClientEventHandler {
    private static RenderItem itemRenderer = new RenderItem();
    private static int time;

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        //handleTickIncrement(event);
        handleHandgunHUDCheck();
        handleSojournerHUDCheck();
        handleTomeHUDCheck();
        handleDestructionCatalystHUDCheck();
        handleEnderStaffHUDCheck();
        handleIceMagusRodHUDCheck();
        handleGlacialStaffHUDCheck();
        handleVoidTearHUDCheck();
        handleMidasTouchstoneHUDCheck();
        handleHarvestRodHUDCheck();
        handleInfernalChaliceHUDCheck();
        handleHeroMedallionHUDCheck();
        handlePyromancerStaffHUDCheck();
        handleRendingGaleHUDCheck();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderPlayer(RenderPlayerEvent.SetArmorModel event) {
        if (event.entityPlayer != null && event.stack != null && event.stack.getItem() == ContentHandler.getItem(Names.witch_hat)) {
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("minecraft:textures/entity/witch.png"));
            ModelBiped model = event.renderer.modelArmor;
            model.bipedHead.showModel = false;
            model.bipedHeadwear.showModel = false;
            model.bipedBody.showModel = false;
            model.bipedRightArm.showModel = false;
            model.bipedLeftArm.showModel = false;
            model.bipedRightLeg.showModel = false;
            model.bipedLeftLeg.showModel = false;
            model = ModelWitchHat.self;
            event.renderer.setRenderPassModel(model);
            model.onGround = event.renderer.modelBipedMain.onGround;
            model.isRiding = event.entityPlayer.isRiding();
            model.isChild = event.renderer.modelBipedMain.isChild;

            event.result = 1;
            return;
        } else if (event.stack != null && event.stack.getItem() == ContentHandler.getItem(Names.witch_hat)) {
            event.result = 0;
            return;
        }
    }


    public void handleTomeHUDCheck() { //todo
    }
    public void handleDestructionCatalystHUDCheck(){ //todo
    }
    public void handleEnderStaffHUDCheck(){ //todo
    }
    public void handleIceMagusRodHUDCheck(){ //todo
    }
    public void handleGlacialStaffHUDCheck(){ //todo
    }
    public void handleVoidTearHUDCheck(){ //todo
    }
    public void handleMidasTouchstoneHUDCheck(){ //todo
    }
    public void handleHarvestRodHUDCheck(){ //todo
    }
    public void handleInfernalChaliceHUDCheck(){ //todo
    }
    public void handleHeroMedallionHUDCheck(){ //todo
    }
    public void handlePyromancerStaffHUDCheck(){ //todo
    }
    public void handleRendingGaleHUDCheck(){ //todo
    }

    public static int getTime() {
        return time;
    }

    public void handleHandgunHUDCheck() {
        // handles rendering the hud for the handgun, WIP
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemHandgun))
            return;

        ItemStack handgunStack = player.getCurrentEquippedItem();
        ItemHandgun handgunItem = (ItemHandgun) handgunStack.getItem();
        ItemStack bulletStack = new ItemStack(ContentHandler.getItem(Names.bullet), handgunItem.getBulletCount(handgunStack), handgunItem.getBulletType(handgunStack));
        renderHandgunHUD(mc, player, handgunStack, bulletStack);
    }

    public void handleSojournerHUDCheck() {
        // handles rendering the hud for the sojourner's staff so we don't have to use chat messages, because annoying.
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemSojournerStaff))
            return;
        ItemStack sojournerStack = player.getCurrentEquippedItem();
        ItemSojournerStaff sojournerItem = (ItemSojournerStaff) sojournerStack.getItem();
        String placementItemName = sojournerItem.getTorchPlacementMode(sojournerStack);
        int amountOfItem = sojournerItem.getTorchCount(sojournerStack, ContentHandler.getItem(placementItemName));
        Item placementItem = null;
        if (placementItemName != null)
            placementItem = ContentHandler.getItem(placementItemName);

        ItemStack placementStack = null;
        if (placementItem != null) {
            placementStack = new ItemStack(placementItem, 1, 0);
        }
        renderSojournerHUD(mc, player, sojournerStack, placementStack);
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

        switch (Reliquary.CONFIG.getInt(Names.handgun, "hud_position")) {
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
                renderItemIntoGUI(minecraft.fontRenderer, bulletStack, hudOverlayX - 8 - (xOffset * 10), hudOverlayY + 12, 1.0F, overlayScale / 2F);
            }
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    private static void renderSojournerHUD(Minecraft minecraft, EntityPlayer player, ItemStack sojournerStack, ItemStack placementStack) {

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

        switch (Reliquary.CONFIG.getInt(Names.sojourner_staff, "hud_position")) {
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

        //render an image of the sojourner's staff
        renderItemIntoGUI(minecraft.fontRenderer, sojournerStack, hudOverlayX, hudOverlayY, overlayOpacity, overlayScale);
        //itemRenderer.renderItemAndEffectIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), sojournerStack, hudOverlayX, hudOverlayY);
        //render the placement item on screen in the GUI
        if (placementStack != null) {
            itemRenderer.renderItemAndEffectIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), placementStack, hudOverlayX + 12, hudOverlayY + 12);
        }
        //    renderItemIntoGUI(minecraft.fontRenderer, placementStack, hudOverlayX + 8, hudOverlayY + 4, 1.0F, overlayScale / 2F);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    public static void renderItemIntoGUI(FontRenderer fontRenderer, ItemStack itemStack, int x, int y, float opacity, float scale) {
        if (itemStack == null)
            return;
        GL11.glDisable(GL11.GL_LIGHTING);
        if (!(itemStack.getItem() instanceof ItemBlock)) {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationItemsTexture);
        } else {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        }
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
}
