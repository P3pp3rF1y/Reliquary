package reliquary.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
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
	protected void renderLabels(PoseStack matrixStack, int x, int y) {
		drawTitleText(matrixStack);
		drawTomeText(matrixStack, font);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float partialTicks, int x, int y) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		bindTexture(BOOK_TEX);
		blit(poseStack, (width - 146) / 2, (height - 179) / 2, 0, 0, 146, 179);
		blit(poseStack, ((width - 16) / 2) + 19, ((height - 179) / 2) + 148, 0, 180, 10, 10);
		blit(poseStack, ((width - 16) / 2) - 14, ((height - 179) / 2) + 148, 10, 180, 10, 10);

		drawItemStack(new ItemStack(ModItems.ALKAHESTRY_TOME.get()), (width - 16) / 2, ((height - 179) / 2) + 145);
		AlkahestryRecipeRegistry.getDrainRecipe().ifPresent(drainRecipe -> {
			drawItemStack(drainRecipe.getResultItem(), ((width - 16) / 2) - 32, ((height - 179) / 2) + 145);
			drawItemStack(drainRecipe.getResultItem(), ((width - 16) / 2) + 32, ((height - 179) / 2) + 145);
		});
	}

	private void drawTomeText(PoseStack matrixStack, Font renderer) {
		String values = Language.getInstance().getOrDefault("gui.reliquary.alkahestry_tome.text");
		int y = 36 + font.lineHeight;
		for (String value : values.split("\n")) {
			List<FormattedCharSequence> splitText = font.split(Component.literal(value), 100);
			for (FormattedCharSequence text : splitText) {
				int x = (146 - font.width(text)) / 2;
				renderer.draw(matrixStack, text, x + 15, y, 0);
				y += font.lineHeight;
			}
		}
	}

	private void drawTitleText(PoseStack matrixStack) {
		String values = "Perform basic,\nintermediate or\nadvanced Alkahestry.";
		int count = 1;
		for (String value : values.split("\n")) {
			int x = (146 - font.width(value)) / 2;
			int y = 4 + (count * font.lineHeight);
			font.draw(matrixStack, value, (float) x + 15, y, 0);
			count++;
		}
	}
}
