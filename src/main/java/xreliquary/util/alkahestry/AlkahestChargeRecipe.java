package xreliquary.util.alkahestry;

import static xreliquary.util.alkahestry.AlkahestRecipeType.*;

public class AlkahestChargeRecipe extends AlkahestRecipe {

	public int charge = 0;

	public AlkahestChargeRecipe(String name, AlkahestRecipeType type, int charge) {
		this(name, charge);

		if (type == META) throw new IllegalArgumentException("This constructor isn't meant for specific meta items");

		this.type = type;
	}

	public AlkahestChargeRecipe(String name, int meta, int charge) {
		this(name, charge);
		this.meta = meta;
	}

	private AlkahestChargeRecipe(String name, int charge) {
		super(name);
		this.charge = charge;
	}
}
