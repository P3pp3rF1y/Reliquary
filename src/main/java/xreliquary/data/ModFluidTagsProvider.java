package xreliquary.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import xreliquary.init.ModFluids;
import xreliquary.reference.Reference;

import javax.annotation.Nullable;

public class ModFluidTagsProvider extends FluidTagsProvider {
	private static final ITag.INamedTag<Fluid> EXPERIENCE = FluidTags.makeWrapperTag(new ResourceLocation("forge", "experience").toString());

	public ModFluidTagsProvider(DataGenerator generatorIn,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(generatorIn, Reference.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerTags() {
		getOrCreateBuilder(EXPERIENCE).add(ModFluids.XP_JUICE_STILL.get());
	}
}
