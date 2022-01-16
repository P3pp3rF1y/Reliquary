package xreliquary.reference;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientReference {
	@OnlyIn(Dist.CLIENT)
	public static final ResourceLocation NEUTRAL = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "neutral_shot.png");
	@OnlyIn(Dist.CLIENT)
	public static final ResourceLocation EXORCISM = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "exorcism_shot.png");
	@OnlyIn(Dist.CLIENT)
	public static final ResourceLocation BLAZE = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "blaze_shot.png");
	@OnlyIn(Dist.CLIENT)
	public static final ResourceLocation ENDER = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "ender_shot.png");
	@OnlyIn(Dist.CLIENT)
	public static final ResourceLocation CONCUSSIVE = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "concussive_shot.png");
	@OnlyIn(Dist.CLIENT)
	public static final ResourceLocation BUSTER = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "buster_shot.png");
	@OnlyIn(Dist.CLIENT)
	public static final ResourceLocation SEEKER = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "seeker_shot.png");
	@OnlyIn(Dist.CLIENT)
	public static final ResourceLocation SAND = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "sand_shot.png");
	@OnlyIn(Dist.CLIENT)
	public static final ResourceLocation STORM = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "storm_shot.png");

}
