package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import reliquary.Reliquary;
import reliquary.init.ModFluids;

import java.util.concurrent.CompletableFuture;

public class ReliquaryFluidTagProvider extends FluidTagsProvider {
	public ReliquaryFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, Reliquary.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ModFluids.EXPERIENCE_TAG).add(ModFluids.XP_STILL.get());
	}
}
