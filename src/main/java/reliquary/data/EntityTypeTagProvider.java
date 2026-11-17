package reliquary.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityTypes;
import reliquary.Reliquary;
import reliquary.init.ModEntities;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagProvider extends EntityTypeTagsProvider {
	public EntityTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, Reliquary.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ModEntities.NOT_CHARMABLE_TAG).add(EntityTypes.ENDER_DRAGON.builtInRegistryHolder().key(), EntityTypes.WITHER.builtInRegistryHolder().key());
		tag(ModEntities.NOT_PUSHABLE_BY_RENDING_GALE_TAG);
		tag(ModEntities.IMMUNE_TO_ROD_OF_LYSSA_TAG);
		tag(ModEntities.NOT_HUNTABLE_BY_SEEKER_SHOT_TAG);
		tag(ModEntities.IGNORED_BY_INTERDICTION_TORCH_TAG);
	}
}
