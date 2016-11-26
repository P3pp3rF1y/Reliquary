package xreliquary.reference;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientReference {

	@SideOnly(Side.CLIENT)
	public static final ResourceLocation NEUTRAL = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "neutral_shot.png");
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation EXORCISM = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "exorcism_shot.png");
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation BLAZE = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "blaze_shot.png");
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation ENDER = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "ender_shot.png");
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation CONCUSSIVE = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "concussive_shot.png");
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation BUSTER = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "buster_shot.png");
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation SEEKER = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "seeker_shot.png");
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation SAND = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "sand_shot.png");
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation STORM = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "storm_shot.png");

}
