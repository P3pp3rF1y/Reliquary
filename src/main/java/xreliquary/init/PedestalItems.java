package xreliquary.init;

import net.minecraft.block.RedstoneBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.BucketItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.SwordItem;
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
		PedestalRegistry.registerItemBlockWrapper(RedstoneWireBlock.class, PedestalRedstoneWrapper.Toggleable::new);
		PedestalRegistry.registerItemBlockWrapper(RedstoneBlock.class, PedestalRedstoneWrapper.AlwaysOn::new);
		PedestalRegistry.registerItemWrapper(FishingRodItem.class, PedestalFishingRodWrapper::new);
	}
}
