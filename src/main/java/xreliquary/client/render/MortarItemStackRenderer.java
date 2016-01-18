package xreliquary.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.init.ModBlocks;

public class MortarItemStackRenderer extends TileEntityItemStackRenderer {
    private TileEntityMortar mortartRender = new TileEntityMortar();

    @Override
    public void renderByItem(ItemStack itemStack) {
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block == ModBlocks.apothecaryMortar) {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.mortartRender, 0.0D, 0.0D, 0.0D, 0.0F);
        }else {
            super.renderByItem(itemStack);
        }
}
}
