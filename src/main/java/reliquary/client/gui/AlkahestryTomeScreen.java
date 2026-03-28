package reliquary.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import reliquary.Reliquary;
import reliquary.common.gui.AlkahestTomeMenu;
import reliquary.crafting.AlkahestryRecipeRegistry;
import reliquary.init.ModItems;

import java.util.List;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class AlkahestryTomeScreen extends BaseScreen<AlkahestTomeMenu> {
	private static final Identifier BOOK_TEX = Reliquary.getIdentifier("textures/gui/book.png");
	public static final int BLACK_COLOR = ARGB.opaque(0);

	public AlkahestryTomeScreen(AlkahestTomeMenu container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor guiGraphics, int x, int y) {
		drawTitleText(guiGraphics);
		drawTomeText(guiGraphics, font);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor guiGraphics, int x, int y, float partialTicks) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BOOK_TEX, (width - 146) / 2, (height - 179) / 2, 0, 0, 146, 179, 256, 256);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BOOK_TEX, ((width - 16) / 2) + 19, ((height - 179) / 2) + 148, 0, 180, 10, 10, 256, 256);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BOOK_TEX, ((width - 16) / 2) - 14, ((height - 179) / 2) + 148, 10, 180, 10, 10, 256, 256);

		drawItemStack(guiGraphics, new ItemStack(ModItems.ALKAHESTRY_TOME.get()), (width - 16) / 2, ((height - 179) / 2) + 145);
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}
		AlkahestryRecipeRegistry.getDrainRecipe().ifPresent(drainRecipe -> {
			drawItemStack(guiGraphics, drainRecipe.getResultItem(), ((width - 16) / 2) - 32, ((height - 179) / 2) + 145);
			drawItemStack(guiGraphics, drainRecipe.getResultItem(), ((width - 16) / 2) + 32, ((height - 179) / 2) + 145);
		});
	}

	private void drawTomeText(GuiGraphicsExtractor guiGraphics, Font font) {
		String values = Language.getInstance().getOrDefault("gui.reliquary.alkahestry_tome.text");
		int y = 36 + font.lineHeight;
		for (String value : values.split("\n")) {
			List<FormattedCharSequence> splitText = font.split(Component.literal(value), 100);
			for (FormattedCharSequence text : splitText) {
				int x = (146 - font.width(text)) / 2;
				guiGraphics.text(font, text, x + 15, y, BLACK_COLOR, false);
				y += font.lineHeight;
			}
		}
	}

	private void drawTitleText(GuiGraphicsExtractor guiGraphics) {
		String values = "Perform basic,\nintermediate or\nadvanced Alkahestry.";
		int count = 1;
		for (String value : values.split("\n")) {
			int x = (146 - font.width(value)) / 2;
			int y = 4 + (count * font.lineHeight);
			guiGraphics.text(font, value, x + 15, y, BLACK_COLOR, false);
			count++;
		}
	}
}
