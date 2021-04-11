package xreliquary.items.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;
import xreliquary.init.ModBlocks;

public class FertileLilyPadItem extends BlockItemBase {
	public FertileLilyPadItem() {
		super(ModBlocks.FERTILE_LILY_PAD.get(), new Properties().rarity(Rarity.EPIC));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		BlockRayTraceResult blockraytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
		BlockRayTraceResult blockraytraceresult1 = blockraytraceresult.withPosition(blockraytraceresult.getPos().up());
		ActionResultType actionresulttype = super.onItemUse(new ItemUseContext(playerIn, handIn, blockraytraceresult1));
		return new ActionResult<>(actionresulttype, playerIn.getHeldItem(handIn));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		return ActionResultType.PASS;
	}
}
