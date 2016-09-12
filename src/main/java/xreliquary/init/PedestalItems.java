package xreliquary.init;

import WayofTime.bloodmagic.item.ItemDaggerOfSacrifice;
import net.minecraft.block.BlockCompressedPowered;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.Loader;
import xreliquary.items.ItemHarvestRod;
import xreliquary.items.ItemRendingGale;
import xreliquary.reference.Compatibility;
import xreliquary.util.pedestal.*;

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
			if (classExists("slimeknights.tconstruct.tools.item.BroadSword")) {
				try {
					PedestalRegistry.registerItemWrapper((Class<? extends Item>) Class.forName("slimeknights.tconstruct.tools.item.Cleaver"), PedestalMeleeWeaponWrapper.class);
					PedestalRegistry.registerItemWrapper((Class<? extends Item>) Class.forName("slimeknights.tconstruct.tools.item.BroadSword"), PedestalMeleeWeaponWrapper.class);
				}
				catch(ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else if (classExists("slimeknights.tconstruct.library.tools.SwordCore")) {
				try {
					PedestalRegistry.registerItemWrapper((Class<? extends Item>) Class.forName("slimeknights.tconstruct.library.tools.SwordCore"), PedestalMeleeWeaponWrapper.class);
				}
				catch(ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			//not implemented currently in TiCon
			//PedestalRegistry.registerItemWrapper(BattleAxe.class, new PedestalMeleeWeaponWrapper());
			//PedestalRegistry.registerItemWrapper(Scythe.class, new PedestalMeleeWeaponWrapper());
		}
		if (Loader.isModLoaded(Compatibility.MOD_ID.BLOOD_MAGIC)) {
			PedestalRegistry.registerItemWrapper(ItemDaggerOfSacrifice.class, PedestalMeleeWeaponWrapper.class);
		}
	}

	private static boolean classExists(String className) {
		try  {
			Class.forName(className);
			return true;
		}  catch (final ClassNotFoundException e) {
			return false;
		}
	}
}
