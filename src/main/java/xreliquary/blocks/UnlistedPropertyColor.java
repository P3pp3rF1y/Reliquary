package xreliquary.blocks;


import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;


public class UnlistedPropertyColor implements IUnlistedProperty<Integer>
{
	@Override public String getName()
	{
		return "UnlistedPropertyColor";
	}

	@Override public boolean isValid( Integer value )
	{
		return true;
	}

	@Override public Class<Integer> getType()
	{
		return Integer.class;
	}

	@Override public String valueToString( Integer value )
	{
		return value.toString();
	}
}
