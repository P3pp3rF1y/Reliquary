package reliquary.init;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.PoweredBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import reliquary.item.HarvestRodItem;
import reliquary.item.RendingGaleItem;
import reliquary.item.RodOfLyssaItem;
import reliquary.pedestal.PedestalRegistry;
import reliquary.pedestal.wrappers.*;

public class PedestalItems {
	private PedestalItems() {}

	public static void init() {
		PedestalRegistry.registerItemWrapper(SwordItem.class, PedestalMeleeWeaponWrapper::new);
		PedestalRegistry.registerItemWrapper(BucketItem.class, PedestalBucketWrapper::new);
		PedestalRegistry.registerItemWrapper(ShearsItem.class, PedestalShearsWrapper::new);
		PedestalRegistry.registerItemWrapper(RendingGaleItem.class, PedestalRendingGaleWrapper::new);
		PedestalRegistry.registerItemWrapper(HarvestRodItem.class, PedestalHarvestRodWrapper::new);
		PedestalRegistry.registerItemBlockWrapper(RedStoneWireBlock.class, PedestalRedstoneWrapper.Toggleable::new);
		PedestalRegistry.registerItemBlockWrapper(PoweredBlock.class, PedestalRedstoneWrapper.AlwaysOn::new);
		PedestalRegistry.registerItemWrapper(FishingRodItem.class, PedestalFishingRodWrapper::new);
		PedestalRegistry.registerItemWrapper(RodOfLyssaItem.class, PedestalFishingRodWrapper::new);
	}
}
