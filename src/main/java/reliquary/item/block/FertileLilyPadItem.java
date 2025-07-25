package reliquary.item.block;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import reliquary.init.ModBlocks;

public class FertileLilyPadItem extends BlockItemBase {
	public FertileLilyPadItem(Properties properties) {
		super(ModBlocks.FERTILE_LILY_PAD.get(), properties.rarity(Rarity.EPIC));
	}

	@Override
	public InteractionResult use(Level level, Player playerIn, InteractionHand handIn) {
		BlockHitResult hitResult = getPlayerPOVHitResult(level, playerIn, ClipContext.Fluid.SOURCE_ONLY);
		BlockHitResult hitResultAbove = hitResult.withPosition(hitResult.getBlockPos().above());
		return super.useOn(new UseOnContext(playerIn, handIn, hitResultAbove));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return InteractionResult.PASS;
	}
}
