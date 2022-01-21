package reliquary.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import reliquary.init.ModFluids;
import reliquary.reference.Reference;

import javax.annotation.Nullable;

public class ModFluidTagsProvider extends FluidTagsProvider {
	private static final Tag.Named<Fluid> EXPERIENCE = FluidTags.bind(new ResourceLocation("forge", "experience").toString());

	public ModFluidTagsProvider(DataGenerator generatorIn,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(generatorIn, Reference.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(EXPERIENCE).add(ModFluids.XP_JUICE_STILL.get());
	}
}
