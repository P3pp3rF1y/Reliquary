package xreliquary.util.alkahestry;

import static xreliquary.util.alkahestry.AlkahestRecipeType.META;

public abstract class AlkahestRecipe {

	public String name = null;
	public int meta = 0;
	public AlkahestRecipeType type = META;

	public AlkahestRecipe(String name) {

		this.name = name;
	}
}
