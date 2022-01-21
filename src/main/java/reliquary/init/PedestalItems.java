package reliquary.init;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.PoweredBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import reliquary.items.HarvestRodItem;
import reliquary.items.RendingGaleItem;
import reliquary.pedestal.PedestalRegistry;
import reliquary.pedestal.wrappers.PedestalBucketWrapper;
import reliquary.pedestal.wrappers.PedestalFishingRodWrapper;
import reliquary.pedestal.wrappers.PedestalHarvestRodWrapper;
import reliquary.pedestal.wrappers.PedestalMeleeWeaponWrapper;
import reliquary.pedestal.wrappers.PedestalRedstoneWrapper;
import reliquary.pedestal.wrappers.PedestalRendingGaleWrapper;
import reliquary.pedestal.wrappers.PedestalShearsWrapper;

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
	}
}
