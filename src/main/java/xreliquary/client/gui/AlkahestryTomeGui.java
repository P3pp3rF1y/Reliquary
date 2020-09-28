package xreliquary.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xreliquary.common.gui.ContainerAlkahestTome;
import xreliquary.crafting.AlkahestryRecipeRegistry;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class AlkahestryTomeGui extends GuiBase<ContainerAlkahestTome> {
	private static final ResourceLocation BOOK_TEX = new ResourceLocation(Reference.MOD_ID, "textures/gui/book.png");

	public AlkahestryTomeGui(ContainerAlkahestTome container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
		drawTitleText(matrixStack);
		drawTomeText(matrixStack, font);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		bindTexture(BOOK_TEX);
		blit(matrixStack, (width - 146) / 2, (height - 179) / 2, 0, 0, 146, 179);
		blit(matrixStack, ((width - 16) / 2) + 19, ((height - 179) / 2) + 148, 0, 180, 10, 10);
		blit(matrixStack, ((width - 16) / 2) - 14, ((height - 179) / 2) + 148, 10, 180, 10, 10);

		drawItemStack(new ItemStack(ModItems.ALKAHESTRY_TOME), (width - 16) / 2, ((height - 179) / 2) + 145);
		drawItemStack(AlkahestryRecipeRegistry.getDrainRecipe().getRecipeOutput(), ((width - 16) / 2) - 32, ((height - 179) / 2) + 145);
		drawItemStack(AlkahestryRecipeRegistry.getDrainRecipe().getRecipeOutput(), ((width - 16) / 2) + 32, ((height - 179) / 2) + 145);
	}

	private void drawTomeText(MatrixStack matrixStack, FontRenderer renderer) {
		String values = "gui.xreliquary.alkahestry_tome.text";
		if (!LanguageHelper.getLocalization(values).equals(values)) {
			values = LanguageHelper.getLocalization(values);
		}
		int count = 1;
		for (String value : values.split(";")) {
			int y = 36 + (count * renderer.FONT_HEIGHT);
			renderer.drawString(matrixStack, value, (float) 16 + 15, y, 0);
			count++;
		}
	}

	private void drawTitleText(MatrixStack matrixStack) {
		String values = "Perform basic,;intermediate or;advanced Alkahestry.";
		if (!LanguageHelper.getLocalization(values).equals(values)) {
			values = LanguageHelper.getLocalization(values);
		}
		int count = 1;
		for (String value : values.split(";")) {
			int x = (146 - font.getStringWidth(value)) / 2;
			int y = 4 + (count * font.FONT_HEIGHT);
			font.drawString(matrixStack, value, (float) x + 15, y, 0);
			count++;
		}
	}
}
