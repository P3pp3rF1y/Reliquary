package xreliquary.util.alkahestry;

import static xreliquary.util.alkahestry.AlkahestRecipeType.*;

public class AlkahestCraftRecipe extends AlkahestRecipe {

	public int yield = 0;
	public int cost = 0;

	public AlkahestCraftRecipe(String name, AlkahestRecipeType type, int yield, int cost) {
		this(name, yield, cost);

		if (type == META) throw new IllegalArgumentException("This constructor isn't meant for specific meta items");

		this.type = type;
	}

	public AlkahestCraftRecipe(String name, int meta, int yield, int cost) {
		this(name, yield, cost);
		this.meta = meta;
	}

	private AlkahestCraftRecipe(String name, int yield, int cost) {

		super(name);
		this.yield = yield;
		this.cost = cost;
	}
}
