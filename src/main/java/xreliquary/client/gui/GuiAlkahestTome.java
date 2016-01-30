package xreliquary.client.gui;

import lib.enderwizards.sandstone.client.gui.GuiBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

public class GuiAlkahestTome extends GuiBase {

    private final ResourceLocation BOOK_TEX = new ResourceLocation(Reference.MOD_ID, "textures/gui/book.png");

    public GuiAlkahestTome(Container container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        this.drawCenteredPositionedString(this.mc.standardGalacticFontRenderer, "Perform basic,;intermediate or;advanced Alkahestry.", 146, 4, 0);
        this.drawPositionedString(this.fontRendererObj, "gui.tome.text", 16, 36, 0);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        this.bindTexture(this.BOOK_TEX);
        this.drawTexturedModalRect((this.width - 146) / 2, (this.height - 179) / 2, 0, 0, 146, 179);
        this.drawTexturedModalRect(((this.width - 16) / 2) + 19, ((this.height - 179) / 2) + 148, 0, 180, 10, 10);
        this.drawTexturedModalRect(((this.width - 16) / 2) - 14, ((this.height - 179) / 2) + 148, 10, 180, 10, 10);

        this.drawItemStack(new ItemStack(ModItems.alkahestryTome), (this.width - 16) / 2, ((this.height - 179) / 2) + 145);
        this.drawItemStack(new ItemStack(Items.redstone), ((this.width - 16) / 2) - 32, ((this.height - 179) / 2) + 145);
        this.drawItemStack(new ItemStack(Blocks.redstone_block), ((this.width - 16) / 2) + 32, ((this.height - 179) / 2) + 145);
    }

}
