package xreliquary.init;

import net.minecraft.block.BlockCompressedPowered;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.Loader;
import xreliquary.items.ItemHarvestRod;
import xreliquary.items.ItemRendingGale;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.pedestal.wrappers.*;
import xreliquary.reference.Compatibility;

public class PedestalItems {
	public static void init() {
		PedestalRegistry.registerItemWrapper(ItemSword.class, PedestalMeleeWeaponWrapper.class);
		PedestalRegistry.registerItemWrapper(ItemBucket.class, PedestalBucketWrapper.class);
		PedestalRegistry.registerItemWrapper(ItemShears.class, PedestalShearsWrapper.class);
		PedestalRegistry.registerItemWrapper(ItemRendingGale.class, PedestalRendingGaleWrapper.class);
		PedestalRegistry.registerItemWrapper(ItemHarvestRod.class, PedestalHarvestRodWrapper.class);
		PedestalRegistry.registerItemWrapper(ItemRedstone.class, PedestalRedstoneWrapper.Toggleable.class);
		PedestalRegistry.registerItemBlockWrapper(BlockCompressedPowered.class, PedestalRedstoneWrapper.AlwaysOn.class);
		PedestalRegistry.registerItemBlockWrapper(BlockCompressedPowered.class, PedestalRedstoneWrapper.AlwaysOn.class);
		PedestalRegistry.registerItemWrapper(ItemFishingRod.class, PedestalFishingRodWrapper.class);

		if(Loader.isModLoaded(Compatibility.MOD_ID.TINKERS_CONSTRUCT)) {
			//TODO readd with TiCon
			//PedestalRegistry.registerItemWrapper(SwordCore.class, PedestalMeleeWeaponWrapper.class);
			//PedestalRegistry.registerItemWrapper(Scythe.class, PedestalMeleeWeaponWrapper.class);

			//not implemented currently in TiCon
			//PedestalRegistry.registerItemWrapper(BattleAxe.class, new PedestalMeleeWeaponWrapper());
		}
/* TODO readd with blood magic
		if (Loader.isModLoaded(Compatibility.MOD_ID.BLOOD_MAGIC)) {
			PedestalRegistry.registerItemWrapper(ItemDaggerOfSacrifice.class, PedestalMeleeWeaponWrapper.class);
		}
*/
	}
}
