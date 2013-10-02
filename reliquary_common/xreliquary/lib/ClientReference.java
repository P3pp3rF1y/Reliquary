package xreliquary.lib;

import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientReference {
	
    @SideOnly(Side.CLIENT)
	public static final ResourceLocation HANDGUN_TEXTURE = new ResourceLocation(Reference.MOD_ID, Reference.MODEL_TEXTURE_PATH + "handgun.png");
    
    @SideOnly(Side.CLIENT)
	public static final ResourceLocation NEUTRAL = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "neutralShot.png");    
    @SideOnly(Side.CLIENT)
	public static final ResourceLocation EXORCISM = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "exorcismShot.png");    
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation BLAZE = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "blazeShot.png");    
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation ENDER = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "enderShot.png");    
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation CONCUSSIVE = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "concussiveShot.png");    
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation BUSTER = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "busterShot.png");    
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation SEEKER = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "seekerShot.png");    
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation SAND = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "sandShot.png");    
	@SideOnly(Side.CLIENT)
	public static final ResourceLocation STORM = new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + "stormShot.png");   

}
