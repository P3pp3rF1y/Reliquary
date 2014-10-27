package xreliquary.event;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xreliquary.Reliquary;
import xreliquary.client.model.ModelWitchHat;
import xreliquary.items.*;
import xreliquary.lib.Colors;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

public class ClientEventHandler {
    private static RenderItem itemRenderer = new RenderItem();
    private static int time;

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        handleTickIncrement(event);
        handleHandgunHUDCheck();
        handleSojournerHUDCheck();
        handleTomeHUDCheck();
        handleDestructionCatalystHUDCheck();
        handleEnderStaffHUDCheck();
        //handles glacial staff as well
        handleIceMagusRodHUDCheck();
        handleVoidTearHUDCheck();
        handleMidasTouchstoneHUDCheck();
        handleHarvestRodHUDCheck();
        handleInfernalChaliceHUDCheck();
        handleHeroMedallionHUDCheck();
        handlePyromancerStaffHUDCheck();
        handleRendingGaleHUDCheck();
    }

    public void handleTickIncrement(TickEvent.RenderTickEvent event) {
        // this is currently used for nothing but the blinking magazine in the handgun HUD renderer
        if (event.phase != TickEvent.Phase.END)
            return;
        //4096 is just an arbitrary stopping point, I didn't need it to go that high, honestly. left in case we need something weird.
        if (getTime() > 4096) {
            time = 0;
        } else {
            time++;
        }
    }

    public static int getTime() {
        return time;
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

    public void handleTomeHUDCheck() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemAlkahestryTome))
            return;

        ItemStack tomeStack = player.getCurrentEquippedItem();
        ItemStack redstoneStack = new ItemStack(Items.redstone, NBTHelper.getInteger("redstone", tomeStack), 0);
        renderStandardTwoItemHUD(mc, player, tomeStack, redstoneStack, Reliquary.CONFIG.getInt(Names.hud_positions, Names.alkahestry_tome), 0, 0);
    }

    public void handleDestructionCatalystHUDCheck(){
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemDestructionCatalyst))
            return;

        ItemStack destructionCatalystStack = player.getCurrentEquippedItem();
        ItemStack gunpowderStack = new ItemStack(Items.gunpowder, NBTHelper.getInteger("gunpowder", destructionCatalystStack), 0);
        renderStandardTwoItemHUD(mc, player, destructionCatalystStack, gunpowderStack, Reliquary.CONFIG.getInt(Names.hud_positions, Names.destruction_catalyst), 0, 0);
    }

    public void handleEnderStaffHUDCheck(){
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemEnderStaff))
            return;

        ItemStack enderStaffStack = player.getCurrentEquippedItem();
        ItemEnderStaff enderStaffItem = (ItemEnderStaff)enderStaffStack.getItem();
        String staffMode = enderStaffItem.getMode(enderStaffStack);
        ItemStack displayItemStack = new ItemStack(Items.ender_pearl, NBTHelper.getInteger("ender_pearls", enderStaffStack), 0);
        if (staffMode.equals("node_warp")) {
            displayItemStack = new ItemStack(ContentHandler.getBlock(Names.wraith_node), NBTHelper.getInteger("ender_pearls", enderStaffStack), 0);
        } else if (staffMode.equals("long_cast")) {
            displayItemStack = new ItemStack(Items.ender_eye, NBTHelper.getInteger("ender_pearls", enderStaffStack), 0);
        }
        renderStandardTwoItemHUD(mc, player, enderStaffStack, displayItemStack, Reliquary.CONFIG.getInt(Names.hud_positions, Names.ender_staff), 0, 0);
    }

    public void handleIceMagusRodHUDCheck(){
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        //returns true for Glacial Staff because it extends IceRod.
        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemIceRod))
            return;

        ItemStack iceRodStack = player.getCurrentEquippedItem();
        ItemStack snowballStack = new ItemStack(Items.snowball, NBTHelper.getInteger("snowballs", iceRodStack), 0);
        //still allows for differing HUD positions, like a baws.
        String hudSelector = (player.getCurrentEquippedItem().getItem() instanceof ItemGlacialStaff) ? Names.glacial_staff : Names.ice_magus_rod;
        renderStandardTwoItemHUD(mc, player, iceRodStack, snowballStack, Reliquary.CONFIG.getInt(Names.hud_positions, hudSelector), 0, NBTHelper.getInteger("snowballs", iceRodStack));
    }

    public void handleVoidTearHUDCheck(){
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemVoidTear))
            return;

        ItemStack voidTearStack = player.getCurrentEquippedItem();
        ItemVoidTear voidTearItem = (ItemVoidTear)voidTearStack.getItem();
        ItemStack containedItemStack = voidTearItem.getContainedItem(voidTearStack);
        renderStandardTwoItemHUD(mc, player, voidTearStack, containedItemStack, Reliquary.CONFIG.getInt(Names.hud_positions, Names.void_tear), 0, 0);
    }

    public void handleMidasTouchstoneHUDCheck(){
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemMidasTouchstone))
            return;

        ItemStack midasTouchstoneStack = player.getCurrentEquippedItem();
        ItemStack glowstoneStack = new ItemStack(Items.glowstone_dust, NBTHelper.getInteger("glowstone", midasTouchstoneStack), 0);
        renderStandardTwoItemHUD(mc, player, midasTouchstoneStack, glowstoneStack, Reliquary.CONFIG.getInt(Names.hud_positions, Names.midas_touchstone), 0, 0);
    }

    public void handleHarvestRodHUDCheck(){
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemHarvestRod))
            return;

        ItemStack harvestRodStack = player.getCurrentEquippedItem();
        ItemStack bonemealStack = new ItemStack(Items.dye, NBTHelper.getInteger("bonemeal", harvestRodStack), Reference.WHITE_DYE_META);
        renderStandardTwoItemHUD(mc, player, harvestRodStack, bonemealStack, Reliquary.CONFIG.getInt(Names.hud_positions, Names.harvest_rod), 0, 0);
    }

    public void handleInfernalChaliceHUDCheck(){
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemInfernalChalice))
            return;

        ItemStack infernalChaliceStack = player.getCurrentEquippedItem();
        ItemStack lavaStack = new ItemStack(Items.lava_bucket, NBTHelper.getInteger("fluidStacks", infernalChaliceStack), 0);
        renderStandardTwoItemHUD(mc, player, infernalChaliceStack, lavaStack, Reliquary.CONFIG.getInt(Names.hud_positions, Names.infernal_chalice), Colors.get(Colors.BLOOD_RED_COLOR), lavaStack.stackSize / 1000);
    }

    public void handleHeroMedallionHUDCheck(){
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemHeroMedallion))
            return;

        ItemStack heroMedallionStack = player.getCurrentEquippedItem();
        int experience = NBTHelper.getInteger("experience", heroMedallionStack);
        renderStandardTwoItemHUD(mc, player, heroMedallionStack, null, Reliquary.CONFIG.getInt(Names.hud_positions, Names.rending_gale), Colors.get(Colors.GREEN), experience);
    }

    public void handlePyromancerStaffHUDCheck(){
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemPyromancerStaff))
            return;


        ItemStack pyromancerStaffStack = player.getCurrentEquippedItem();

        int charge = 0;
        int blaze = 0;
        NBTTagCompound tagCompound = NBTHelper.getTag(pyromancerStaffStack);
        if (tagCompound != null) {
            NBTTagList tagList = tagCompound.getTagList("Items", 10);
            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound tagItemData = tagList.getCompoundTagAt(i);
                String itemName = tagItemData.getString("Name");
                Item containedItem = ContentHandler.getItem(itemName);
                int quantity = tagItemData.getInteger("Quantity");

                if (containedItem == Items.blaze_powder) {
                    blaze = quantity;
                } else if (containedItem == Items.fire_charge) {
                    charge = quantity;
                }
            }
        }

        String staffMode = ((ItemPyromancerStaff)pyromancerStaffStack.getItem()).getMode(pyromancerStaffStack);

        ItemStack fireChargeStack = new ItemStack(Items.fire_charge, charge, 0);
        ItemStack blazePowderStack = new ItemStack(Items.blaze_powder, blaze, 0);
        renderPyromancerStaffHUD(mc, player, pyromancerStaffStack, fireChargeStack, blazePowderStack, staffMode);
    }

    private static void renderPyromancerStaffHUD(Minecraft minecraft, EntityPlayer player, ItemStack hudStack, ItemStack secondaryStack, ItemStack tertiaryStack, String staffMode) {
        int color = Colors.get(Colors.PURE);

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

        switch (Reliquary.CONFIG.getInt(Names.hud_positions, Names.pyromancer_staff)) {
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

        renderItemIntoGUI(minecraft.fontRenderer, hudStack, hudOverlayX, hudOverlayY, overlayOpacity, overlayScale);

        String friendlyStaffMode = "";
        if (staffMode.equals("eruption"))
            friendlyStaffMode = "ERUPT";

        if (secondaryStack != null && (staffMode.equals("charge"))) {
            itemRenderer.renderItemAndEffectIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), secondaryStack, hudOverlayX, hudOverlayY + 24);
            minecraft.fontRenderer.drawStringWithShadow(Integer.toString(secondaryStack.stackSize),hudOverlayX + 18, hudOverlayY + 30, color);
        } else if (tertiaryStack != null && (staffMode.equals("eruption") || staffMode.equals("blaze"))) {
            itemRenderer.renderItemAndEffectIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), tertiaryStack, hudOverlayX, hudOverlayY + 24);
            minecraft.fontRenderer.drawStringWithShadow(Integer.toString(tertiaryStack.stackSize),hudOverlayX + 18, hudOverlayY + 30, color);
            if (staffMode.equals("eruption"))
                minecraft.fontRenderer.drawStringWithShadow(friendlyStaffMode, hudOverlayX, hudOverlayY + 18, color);
        } else if (staffMode.equals("flint_and_steel")) {
            itemRenderer.renderItemAndEffectIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), new ItemStack(Items.flint_and_steel, 1, 0), hudOverlayX, hudOverlayY + 24);
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    public void handleRendingGaleHUDCheck(){
        Minecraft mc = Minecraft.getMinecraft();
        if (!Minecraft.isGuiEnabled() || !mc.inGameHasFocus)
            return;
        EntityPlayer player = mc.thePlayer;

        if (player == null || player.getCurrentEquippedItem() == null || !(player.getCurrentEquippedItem().getItem() instanceof ItemRendingGale))
            return;

        ItemStack rendingGaleStack = player.getCurrentEquippedItem();
        ItemStack featherStack = new ItemStack(Items.feather, NBTHelper.getInteger("feathers", rendingGaleStack), 0);
        int charge = featherStack.stackSize;
        if (player.isUsingItem()) {
            int count = rendingGaleStack.getItem().getMaxItemUseDuration(rendingGaleStack) - (player.getItemInUseCount() - 1);
             charge -= (count * ItemRendingGale.getChargeCost());
        }
        charge /= 100;
        renderStandardTwoItemHUD(mc, player, rendingGaleStack, featherStack, Reliquary.CONFIG.getInt(Names.hud_positions, Names.rending_gale), 0, Math.max(charge,0));
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

        switch (Reliquary.CONFIG.getInt(Names.hud_positions, Names.handgun)) {
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
        //for use with font renderer, hopefully.
        int amountOfItem = sojournerItem.getTorchCount(sojournerStack);
        Item placementItem = null;
        if (placementItemName != null)
            placementItem = ContentHandler.getItem(placementItemName);

        ItemStack placementStack = null;
        if (placementItem != null) {
            placementStack = new ItemStack(placementItem, amountOfItem, 0);
        }
        renderStandardTwoItemHUD(mc, player, sojournerStack, placementStack, Reliquary.CONFIG.getInt(Names.hud_positions, Names.sojourner_staff), 0, 0);
    }

    private static void renderStandardTwoItemHUD(Minecraft minecraft, EntityPlayer player, ItemStack hudStack, ItemStack secondaryStack, int hudPosition, int colorOverride, int stackSizeOverride) {
        int stackSize = 0;
        if (stackSizeOverride > 0)
            stackSize = stackSizeOverride;
        int color = Colors.get(Colors.PURE);
        if (colorOverride > 0)
            color = colorOverride;
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

        switch (hudPosition) {
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

        renderItemIntoGUI(minecraft.fontRenderer, hudStack, hudOverlayX, hudOverlayY, overlayOpacity, overlayScale);

        boolean skipStackRender = false;

        //special item conditions are handled on a per-item-type basis:
        if (hudStack.getItem() instanceof ItemRendingGale) {
            ItemRendingGale staffItem = (ItemRendingGale)hudStack.getItem();
            String staffMode = staffItem.getMode(hudStack);
            if (staffMode.equals("flight"))
                staffMode = "FLIGHT";
            else if (staffMode.equals("push"))
                staffMode = "PUSH";
            else if (staffMode.equals("pull"))
                staffMode = "PULL";
            else
                staffMode = "BOLT";
            minecraft.fontRenderer.drawStringWithShadow(staffMode,hudOverlayX, hudOverlayY + 18, color);
        }

        if (secondaryStack != null) {
            if (stackSize == 0)
                stackSize = secondaryStack.stackSize;
            itemRenderer.renderItemAndEffectIntoGUI(minecraft.fontRenderer, minecraft.getTextureManager(), secondaryStack, hudOverlayX, hudOverlayY + 24);
        }

        minecraft.fontRenderer.drawStringWithShadow(Integer.toString(stackSize),hudOverlayX + 18, hudOverlayY + 30, color);
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
