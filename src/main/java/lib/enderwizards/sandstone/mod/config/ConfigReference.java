package lib.enderwizards.sandstone.mod.config;

import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.relauncher.Side;

public class ConfigReference {

    public ConfigGuiType type;

    public Integer minimum;
    public Integer maximum;

    public Side side;

    public final Object defaultValue;

    public ConfigReference(Object defaultValue) {
        this.defaultValue = defaultValue;
        minimum = Integer.MIN_VALUE;
        maximum = Integer.MAX_VALUE;
        side = Side.SERVER;
    }

    public ConfigReference setType(ConfigGuiType type) {
        this.type = type;
        return this;
    }

    public ConfigReference setMinimumValue(Integer minimum) {
        this.minimum = minimum;
        return this;
    }

    public ConfigReference setMaximumValue(Integer maximum) {
        this.maximum = maximum;
        return this;
    }

    public ConfigReference setClientSide() {
        this.side = Side.CLIENT;
        return this;
    }

}
