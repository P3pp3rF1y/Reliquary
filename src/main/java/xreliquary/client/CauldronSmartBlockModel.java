/*
package xreliquary.client;


import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import java.util.List;


public class CauldronSmartBlockModel implements ISmartBlockModel
{
	public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation( Reference.DOMAIN + Names.apothecary_mortar, "inventory");
	private IBakedModel existingModel;

	public CauldronSmartBlockModel( IBakedModel existingModel )
	{

		this.existingModel = existingModel;
	}

	@Override
	public IBakedModel handleBlockState( IBlockState state )
	{
		IExtendedBlockState extendedState = (IExtendedBlockState) state;

		if (extendedState != null) {
			extendedState.getUnlistedProperties();
		}
		return null;
	}

	@Override public List<BakedQuad> getFaceQuads( EnumFacing p_177551_1_ )
	{
		return null;
	}

	@Override public List<BakedQuad> getGeneralQuads()
	{
		return null;
	}

	@Override public boolean isAmbientOcclusion()
	{
		return false;
	}

	@Override public boolean isGui3d()
	{
		return false;
	}

	@Override public boolean isBuiltInRenderer()
	{
		return false;
	}

	@Override public TextureAtlasSprite getParticleTexture()
	{
		return null;
	}

	@Override public ItemCameraTransforms getItemCameraTransforms()
	{
		return null;
	}
}
*/
