package xreliquary.init;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.PoweredBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import xreliquary.items.HarvestRodItem;
import xreliquary.items.RendingGaleItem;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.pedestal.wrappers.PedestalBucketWrapper;
import xreliquary.pedestal.wrappers.PedestalFishingRodWrapper;
import xreliquary.pedestal.wrappers.PedestalHarvestRodWrapper;
import xreliquary.pedestal.wrappers.PedestalMeleeWeaponWrapper;
import xreliquary.pedestal.wrappers.PedestalRedstoneWrapper;
import xreliquary.pedestal.wrappers.PedestalRendingGaleWrapper;
import xreliquary.pedestal.wrappers.PedestalShearsWrapper;

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
