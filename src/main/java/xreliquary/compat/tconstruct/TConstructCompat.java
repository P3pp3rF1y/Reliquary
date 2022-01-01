package xreliquary.compat.tconstruct;

import slimeknights.tconstruct.tools.item.broad.ScytheTool;
import slimeknights.tconstruct.tools.item.small.SwordTool;
import xreliquary.compat.ICompat;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.pedestal.wrappers.PedestalMeleeWeaponWrapper;

public class TConstructCompat implements ICompat {
	@Override
	public void setup() {
		PedestalRegistry.registerItemWrapper(SwordTool.class, PedestalMeleeWeaponWrapper::new);
		PedestalRegistry.registerItemWrapper(ScytheTool.class, PedestalMeleeWeaponWrapper::new);
	}
}
