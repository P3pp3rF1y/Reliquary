package xreliquary.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.init.XRRecipes;
import xreliquary.items.*;
import xreliquary.reference.Colors;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClientEventHandler {
	private static RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
	private static int time;

	private static HashMap<Integer, CharmToDraw> charmsToDraw = new HashMap<>();

	private static synchronized HashMap<Integer, CharmToDraw> getCharmsToDraw() {
		return charmsToDraw;
	}

	private static class CharmToDraw {
		CharmToDraw(byte type, int damage, long time) {
			this.type = type;
			this.damage = damage;
			this.time = time;
		}

		byte type;
		int damage;
		long time;
	}

	public static void addCharmToDraw(byte type, int damage, int slot) {
		int maxMobCharmsToDisplay = Settings.MobCharm.maxCharmsToDisplay;
		synchronized(charmsToDraw) {
			if(charmsToDraw.size() == maxMobCharmsToDisplay) {
				charmsToDraw.remove(0);
			}

			if(charmsToDraw.keySet().contains(slot)) {
				charmsToDraw.remove(slot);
			}

			if(damage > ModItems.mobCharm.getMaxDamage())
				charmsToDraw.remove(slot);

			if(damage <= ModItems.mobCharm.getMaxDamage())
				charmsToDraw.put(slot, new CharmToDraw(type, damage, System.currentTimeMillis()));

		}
	}

	@SubscribeEvent
	public void onRenderLiving(RenderLivingEvent.Pre event) {
		if(event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();

			boolean handgunInOff = player.getHeldItem(EnumHand.OFF_HAND) != null && player.getHeldItem(EnumHand.OFF_HAND).getItem() == ModItems.handgun;
			boolean handgunInMain = player.getHeldItem(EnumHand.MAIN_HAND) != null && player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModItems.handgun;

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

	private EnumHand getActiveHandgunHand(EntityPlayer player, boolean handgunInMain, boolean handgunInOff) {
		if(handgunInMain != handgunInOff) {
			return handgunInMain ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		}

		boolean mainValid = isValidTimeFrame(player.worldObj, player.getHeldItemMainhand());
		boolean offValid = isValidTimeFrame(player.worldObj, player.getHeldItemOffhand());

		if(mainValid != offValid)
			return mainValid ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;

		return ModItems.handgun.getCooldown(player.getHeldItemMainhand()) < ModItems.handgun.getCooldown(player.getHeldItemOffhand()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
	}

	private boolean isHandgunActive(EntityPlayer player, boolean handgunInMain, boolean handgunInOff) {
		if(handgunInMain && isValidTimeFrame(player.worldObj, player.getHeldItemMainhand()))
			return true;

		if(handgunInOff && isValidTimeFrame(player.worldObj, player.getHeldItemOffhand()))
			return true;

		return false;
	}

	private boolean isValidTimeFrame(World world, ItemStack handgun) {
		long cooldownTime = ModItems.handgun.getCooldown(handgun) + 5;

		if(cooldownTime - world.getWorldTime() <= ModItems.handgun.getMaxItemUseDuration(handgun) && cooldownTime >= world.getWorldTime())
			return true;

		return false;
	}

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

		handleMobCharmDisplay(mc);
	}

	private void handleMobCharmDisplay(Minecraft minecraft) {
		int hudOverlayX;
		int hudOverlayY;
		int numberItems = getCharmsToDraw().size();
		int itemSize = 16;
		int borderSpacing = 8;
		int itemSpacing = 2;
		int displayPosition = Settings.MobCharm.displayPosition;

		if(numberItems <= 0)
			return;

		removeExpiredMobCharms();

		ScaledResolution sr = new ScaledResolution(minecraft);

		GlStateManager.pushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableLighting();

		if (displayPosition == 1 || displayPosition == 3) {
			hudOverlayY = sr.getScaledHeight() / 2 - (itemSize / 2) - (Math.max(0, (numberItems - 1) * (itemSize + itemSpacing) / 2));

			if (displayPosition == 1) {
				hudOverlayX = sr.getScaledWidth() - (itemSize + borderSpacing);
			} else {
				hudOverlayX = borderSpacing;
			}
		} else {
			hudOverlayY = borderSpacing;
			hudOverlayX = sr.getScaledWidth() / 2 - (itemSize / 2) - (Math.max(0, (numberItems - 1) * (itemSize + itemSpacing) / 2));
		}

		HashMap<Integer, CharmToDraw> charmsToDrawCopy = new HashMap<>(getCharmsToDraw());
		for(CharmToDraw charmToDraw : charmsToDrawCopy.values()) {
			ItemStack stackToRender = XRRecipes.mobCharm(charmToDraw.type);
			stackToRender.setItemDamage(charmToDraw.damage);
			IBakedModel bakedModel = renderItem.getItemModelWithOverrides(stackToRender, null, null);
			GlStateManager.pushMatrix();
			TextureManager textureManager = minecraft.getTextureManager();
			textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
			GlStateManager.enableRescaleNormal();
			GlStateManager.disableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.translate((float) hudOverlayX, (float) hudOverlayY, 100.0F);
			GlStateManager.translate(8.0F, 8.0F, 0.0F);
			GlStateManager.scale(1.0F, -1.0F, 1.0F);
			GlStateManager.scale(16.0F, 16.0F, 16.0F);

			bakedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedModel, ItemCameraTransforms.TransformType.GUI, false);
			renderItem.renderItem(stackToRender, bakedModel);
			GlStateManager.disableAlpha();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableLighting();
			GlStateManager.popMatrix();
			textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
			renderItem.renderItemOverlayIntoGUI(minecraft.getRenderManager().getFontRenderer(), stackToRender, hudOverlayX, hudOverlayY, null);

			if (displayPosition == 1 || displayPosition == 3)
				hudOverlayY += itemSize + itemSpacing;
			else
				hudOverlayX += itemSize + itemSpacing;
		}
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
	}

	private void removeExpiredMobCharms() {
		int secondsToExpire = 4;
		synchronized(charmsToDraw) {
			for(Iterator<Map.Entry<Integer, CharmToDraw>> iterator = charmsToDraw.entrySet().iterator(); iterator.hasNext(); ){
				Map.Entry<Integer, CharmToDraw> entry = iterator.next();
				if (Settings.MobCharm.keepAlmostDestroyedDisplayed && entry.getValue().damage >= (ModItems.mobCharm.getMaxDamage() * 0.9))
					continue;

				if(entry.getValue().time + secondsToExpire * 1000 < System.currentTimeMillis()) {
					iterator.remove();
				}
			}
		}
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

		ItemStack gunpowderStack = new ItemStack(Items.GUNPOWDER, NBTHelper.getInteger("gunpowder", destructionCatalystStack), 0);
		renderStandardTwoItemHUD(mc, player, destructionCatalystStack, gunpowderStack, Settings.HudPositions.destructionCatalyst, 0, 0);
	}

	public void handleEnderStaffHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack enderStaffStack = getCorrectItemFromEitherHand(player, ModItems.enderStaff);

		if(enderStaffStack == null)
			return;

		ItemEnderStaff enderStaffItem = ModItems.enderStaff;
		String staffMode = enderStaffItem.getMode(enderStaffStack);
		ItemStack displayItemStack = new ItemStack(Items.ENDER_PEARL, enderStaffItem.getPearlCount(enderStaffStack), 0);
		if(staffMode.equals("node_warp")) {
			displayItemStack = new ItemStack(ModBlocks.wraithNode, enderStaffItem.getPearlCount(enderStaffStack), 0);
		} else if(staffMode.equals("long_cast")) {
			displayItemStack = new ItemStack(Items.ENDER_EYE, enderStaffItem.getPearlCount(enderStaffStack), 0);
		}
		renderStandardTwoItemHUD(mc, player, enderStaffStack, displayItemStack, Settings.HudPositions.enderStaff, 0, 0);
	}

	public void handleIceMagusRodHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack iceRodStack = getCorrectItemFromEitherHand(player, ModItems.iceMagusRod);

		if(iceRodStack == null)
			return;

		ItemStack snowballStack = new ItemStack(Items.SNOWBALL, NBTHelper.getInteger("snowballs", iceRodStack), 0);
		//still allows for differing HUD positions, like a baws.
		int hudPosition = Settings.HudPositions.iceMagusRod;
		renderStandardTwoItemHUD(mc, player, iceRodStack, snowballStack, hudPosition, 0, NBTHelper.getInteger("snowballs", iceRodStack));
	}

	public void handleGlacialStaffHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack glacialStaff = getCorrectItemFromEitherHand(player, ModItems.glacialStaff);

		if(glacialStaff == null)
			return;

		ItemStack snowballStack = new ItemStack(Items.SNOWBALL, NBTHelper.getInteger("snowballs", glacialStaff), 0);
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

		ItemStack glowstoneStack = new ItemStack(Items.GLOWSTONE_DUST, NBTHelper.getInteger("glowstone", midasTouchstoneStack), 0);
		renderStandardTwoItemHUD(mc, player, midasTouchstoneStack, glowstoneStack, Settings.HudPositions.midasTouchstone, 0, 0);
	}

	public void handleHarvestRodHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack harvestRodStack = getCorrectItemFromEitherHand(player, ModItems.harvestRod);

		if(harvestRodStack == null)
			return;

		ItemStack secondaryStack = null;
		ItemHarvestRod harvestRod = ModItems.harvestRod;
		if(harvestRod.getMode(harvestRodStack).equals(ModItems.harvestRod.PLANTABLE_MODE)) {
			ItemStack currenPlantable = harvestRod.getCurrentPlantable(harvestRodStack);

			if(currenPlantable != null) {
				secondaryStack = currenPlantable.copy();
				int plantableCount = harvestRod.getPlantableQuantity(harvestRodStack, harvestRod.getCurrentPlantableSlot(harvestRodStack));

				secondaryStack.stackSize = plantableCount;
			}
		} else if(harvestRod.getMode(harvestRodStack).equals(ModItems.harvestRod.BONE_MEAL_MODE)) {
			int boneMealCount = harvestRod.getBoneMealCount(harvestRodStack);

			secondaryStack = new ItemStack(Items.DYE, boneMealCount, Reference.WHITE_DYE_META);
		} else {
			secondaryStack = new ItemStack(Items.WOODEN_HOE);
		}

		renderStandardTwoItemHUD(mc, player, harvestRodStack, secondaryStack, Settings.HudPositions.harvestRod, 0, 0);
	}

	public void handleInfernalChaliceHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack infernalChaliceStack = getCorrectItemFromEitherHand(player, ModItems.infernalChalice);

		if(infernalChaliceStack == null)
			return;

		ItemStack lavaStack = new ItemStack(Items.LAVA_BUCKET, NBTHelper.getInteger("fluidStacks", infernalChaliceStack), 0);
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

				if(containedItem == Items.BLAZE_POWDER) {
					blaze = quantity;
				} else if(containedItem == Items.FIRE_CHARGE) {
					charge = quantity;
				}
			}
		}

		String staffMode = ((ItemPyromancerStaff) pyromancerStaffStack.getItem()).getMode(pyromancerStaffStack);

		ItemStack fireChargeStack = new ItemStack(Items.FIRE_CHARGE, charge, 0);
		ItemStack blazePowderStack = new ItemStack(Items.BLAZE_POWDER, blaze, 0);
		renderPyromancerStaffHUD(mc, player, pyromancerStaffStack, fireChargeStack, blazePowderStack, staffMode);
	}

	private static void renderPyromancerStaffHUD(Minecraft minecraft, EntityPlayer player, ItemStack hudStack, ItemStack secondaryStack, ItemStack tertiaryStack, String staffMode) {
		int color = Colors.get(Colors.PURE);

		float overlayScale = 2.5F;
		float overlayOpacity = 0.75F;

		GlStateManager.pushMatrix();
		ScaledResolution sr = new ScaledResolution(minecraft);
		GlStateManager.clear(256);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);

		GlStateManager.pushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableLighting();

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
			renderItem.renderItemAndEffectIntoGUI(new ItemStack(Items.FLINT_AND_STEEL, 1, 0), hudOverlayX - (leftSide ? 0 : 15), hudOverlayY + 24);
		}

		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	public void handleRendingGaleHUDCheck(Minecraft mc) {
		EntityPlayer player = mc.thePlayer;

		ItemStack rendingGaleStack = getCorrectItemFromEitherHand(player, ModItems.rendingGale);

		if(rendingGaleStack == null)
			return;

		int currentCost = 0;

		if(!player.capabilities.isCreativeMode && player.isHandActive()) {
			int ticksInUse = ModItems.rendingGale.getMaxItemUseDuration(rendingGaleStack) - player.getItemInUseCount();

			if(ModItems.rendingGale.isFlightMode(rendingGaleStack)) {
				currentCost = ModItems.rendingGale.getChargeCost() * ticksInUse;
			} else if(ModItems.rendingGale.isBoltMode(rendingGaleStack)) {
				currentCost = ModItems.rendingGale.getBoltChargeCost() * (ticksInUse / 8);
			}
		}

		ItemStack featherStack = new ItemStack(Items.FEATHER, ModItems.rendingGale.getFeatherCount(rendingGaleStack) - currentCost, 0);

		renderStandardTwoItemHUD(mc, player, rendingGaleStack, featherStack, Settings.HudPositions.rendingGale, 0, Math.max(featherStack.stackSize / 100, 0));
	}

	public void handleHandgunHUDCheck(Minecraft mc) {
		// handles rendering the hud for the handgun, WIP
		EntityPlayer player = mc.thePlayer;

		ItemStack mainHandgunStack = (player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == ModItems.handgun) ? player.getHeldItemMainhand() : null;
		ItemStack offHandgunStack = (player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() == ModItems.handgun) ? player.getHeldItemOffhand() : null;

		if(mainHandgunStack == null && offHandgunStack == null)
			return;

		ItemStack mainBulletStack = null;
		if(mainHandgunStack != null) {
			mainBulletStack = new ItemStack(ModItems.bullet, ModItems.handgun.getBulletCount(mainHandgunStack), ModItems.handgun.getBulletType(mainHandgunStack));
		}
		ItemStack offBulletStack = null;
		if(offHandgunStack != null) {
			offBulletStack = new ItemStack(ModItems.bullet, ModItems.handgun.getBulletCount(offHandgunStack), ModItems.handgun.getBulletType(offHandgunStack));
		}
		renderHandgunHUD(mc, player, mainHandgunStack, mainBulletStack, offHandgunStack, offBulletStack);
	}

	private static void renderHandgunHUD(Minecraft minecraft, EntityPlayer player, ItemStack mainHandgunStack, ItemStack mainBulletStack, ItemStack offHandgunStack, ItemStack offBulletStack) {
		float overlayScale = 2.5F;
		float overlayOpacity = 0.75F;
		float segmentHeight = 6 * overlayScale;

		GlStateManager.pushMatrix();
		ScaledResolution sr = new ScaledResolution(minecraft);
		GlStateManager.clear(256);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);

		GlStateManager.pushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableLighting();

		int hudOverlayX = (int) (16 * overlayScale);
		int hudOverlayY = (int) (6 * overlayScale);
		boolean leftSide = Settings.HudPositions.handgun == 0 || Settings.HudPositions.handgun == 2;
		boolean twoHandguns = mainHandgunStack != null && offHandgunStack != null;

		switch(Settings.HudPositions.handgun) {
			case 0: {
				hudOverlayX = (int) (44 * overlayScale);
				break;
			}
			case 1: {
				hudOverlayX = (int) (sr.getScaledWidth() - 12 * overlayScale);
				break;
			}
			case 2: {
				hudOverlayX = (int) (44 * overlayScale);
				hudOverlayY = (int) (sr.getScaledHeight() - (16 * overlayScale + (twoHandguns ? segmentHeight : 0)));
				break;
			}
			case 3: {
				hudOverlayX = (int) (sr.getScaledWidth() - 12 * overlayScale);
				hudOverlayY = (int) (sr.getScaledHeight() - (16 * overlayScale + (twoHandguns ? segmentHeight : 0)));
				break;
			}
			default: {
				break;
			}
		}

		if(mainHandgunStack != null) {
			renderHandgunAndBullets(EnumHand.MAIN_HAND, minecraft, mainHandgunStack, mainBulletStack, overlayScale, overlayOpacity, hudOverlayX, hudOverlayY);

			hudOverlayY += segmentHeight;
		}

		if(offHandgunStack != null)
			renderHandgunAndBullets(EnumHand.OFF_HAND, minecraft, offHandgunStack, offBulletStack, overlayScale, overlayOpacity, hudOverlayX, hudOverlayY);

		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	private static void renderHandgunAndBullets(EnumHand hand, Minecraft minecraft, ItemStack handgunStack, ItemStack bulletStack, float overlayScale, float overlayOpacity, int hudOverlayX, int hudOverlayY) {
		renderItemIntoGUI(minecraft.getRenderManager().getFontRenderer(), handgunStack, hudOverlayX - (hand == EnumHand.OFF_HAND ? 100 : 0), hudOverlayY, overlayOpacity, overlayScale);

		int adjustedHudOverlayX = hand == EnumHand.MAIN_HAND ? (int) (hudOverlayX - 6 * overlayScale) : (int) (hudOverlayX - 2 * overlayScale);

		// if the gun is empty, displays a blinking empty magazine instead.
		if(bulletStack.stackSize == 0) {
			if(getTime() % 32 > 16) {
				// offsets it a little to the left, it looks silly if you put it
				// over the gun.
				renderItemIntoGUI(minecraft.getRenderManager().getFontRenderer(), new ItemStack(ModItems.magazine, 1, 0), adjustedHudOverlayX, hudOverlayY, overlayOpacity, overlayScale / 2F);
			}
		} else {
			adjustedHudOverlayX = adjustedHudOverlayX - (hand == EnumHand.OFF_HAND ? 10 : 0);

			// renders the number of bullets onto the screen.
			for(int xOffset = 0; xOffset < bulletStack.stackSize; xOffset++) {
				// xOffset * 6 makes the bullets line up, -16 moves them all to
				// the left by a bit

				renderItemIntoGUI(minecraft.getRenderManager().getFontRenderer(), bulletStack, (int) (adjustedHudOverlayX - ((xOffset * 4) * overlayScale)), hudOverlayY, 1.0F, overlayScale / 2F);
			}
		}
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

		GlStateManager.pushMatrix();
		ScaledResolution sr = new ScaledResolution(minecraft);
		GlStateManager.clear(256);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);

		GlStateManager.pushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableLighting();

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

		GlStateManager.disableLighting();
		minecraft.fontRendererObj.drawStringWithShadow(Integer.toString(stackSize), hudOverlayX - (leftSide ? 0 : (Integer.toString(stackSize).length() * 6)), hudOverlayY + 29, color);
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	public static void renderItemIntoGUI(FontRenderer fontRenderer, ItemStack itemStack, int x, int y, float opacity, float scale) {
		renderItem.renderItemIntoGUI(itemStack, x, y);
	}
}
