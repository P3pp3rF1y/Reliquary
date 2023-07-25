package reliquary.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import reliquary.common.gui.AlkahestTomeMenu;
import reliquary.crafting.AlkahestryRecipeRegistry;
import reliquary.init.ModItems;
import reliquary.reference.Reference;

import java.util.List;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class AlkahestryTomeGui extends GuiBase<AlkahestTomeMenu> {
	private static final ResourceLocation BOOK_TEX = new ResourceLocation(Reference.MOD_ID, "textures/gui/book.png");

	public AlkahestryTomeGui(AlkahestTomeMenu container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
		drawTitleText(guiGraphics);
		drawTomeText(guiGraphics, font);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		guiGraphics.blit(BOOK_TEX, (width - 146) / 2, (height - 179) / 2, 0, 0, 146, 179);
		guiGraphics.blit(BOOK_TEX, ((width - 16) / 2) + 19, ((height - 179) / 2) + 148, 0, 180, 10, 10);
		guiGraphics.blit(BOOK_TEX, ((width - 16) / 2) - 14, ((height - 179) / 2) + 148, 10, 180, 10, 10);

		drawItemStack(guiGraphics, new ItemStack(ModItems.ALKAHESTRY_TOME.get()), (width - 16) / 2, ((height - 179) / 2) + 145);
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}
		RegistryAccess registryAccess = level.registryAccess();
		AlkahestryRecipeRegistry.getDrainRecipe().ifPresent(drainRecipe -> {
			drawItemStack(guiGraphics, drainRecipe.getResultItem(registryAccess), ((width - 16) / 2) - 32, ((height - 179) / 2) + 145);
			drawItemStack(guiGraphics, drainRecipe.getResultItem(registryAccess), ((width - 16) / 2) + 32, ((height - 179) / 2) + 145);
		});
	}

	private void drawTomeText(GuiGraphics guigraphics, Font font) {
		String values = Language.getInstance().getOrDefault("gui.reliquary.alkahestry_tome.text");
		int y = 36 + font.lineHeight;
		for (String value : values.split("\n")) {
			List<FormattedCharSequence> splitText = font.split(Component.literal(value), 100);
			for (FormattedCharSequence text : splitText) {
				int x = (146 - font.width(text)) / 2;
				guigraphics.drawString(font, text, x + 15, y, 0, false);
				y += font.lineHeight;
			}
		}
	}

	private void drawTitleText(GuiGraphics guiGraphics) {
		String values = "Perform basic,\nintermediate or\nadvanced Alkahestry.";
		int count = 1;
		for (String value : values.split("\n")) {
			int x = (146 - font.width(value)) / 2;
			int y = 4 + (count * font.lineHeight);
			guiGraphics.drawString(font, value, x + 15, y, 0, false);
			count++;
		}
	}
}
