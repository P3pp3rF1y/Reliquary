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
			try {
				//noinspection unchecked
				PedestalRegistry.registerItemWrapper((Class<? extends Item>) Class.forName("slimeknights.tconstruct.library.tools.SwordCore"), PedestalMeleeWeaponWrapper.class);
			}
			catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
			// TODO add in next iteration with tinkers
			//PedestalRegistry.registerItemWrapper(BattleAxe.class, new PedestalMeleeWeaponWrapper());
			//PedestalRegistry.registerItemWrapper(Scythe.class, new PedestalMeleeWeaponWrapper());
		}
/* TODO readd with blood magic
		if (Loader.isModLoaded(Compatibility.MOD_ID.BLOOD_MAGIC)) {
			PedestalRegistry.registerItemWrapper(ItemDaggerOfSacrifice.class, PedestalMeleeWeaponWrapper.class);
		}
*/
	}
}
