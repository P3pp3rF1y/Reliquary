package lib.enderwizards.sandstone.mod.config;

import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ConfigElement<T> implements IConfigElement<T> {

    private boolean isProperty;

    public String mod_id;

    private String group;
    private String key;

    private Config config;
    private Config def;

    public ConfigElement(String mod_id, String key, Map<String, Object> config, Map<String, Object> def) {
        this(mod_id, "", key, config, def);
    }

    public ConfigElement(String mod_id, String group, String key, Map<String, Object> config, Map<String, Object> def) {
        this.mod_id = mod_id;

        this.group = group;
        this.key = key;

        this.config = new ConfigImpl(null, config);
        this.def = new ConfigImpl(null, def);
        if (this.config.get(group, key) instanceof Map) {
            isProperty = false;
            assert (this.def.get(group, key) instanceof Map);
        } else {
            isProperty = true;
        }
    }

    @Override
    public boolean isProperty() {
        return isProperty;
    }

    @Override
    public Class<? extends IConfigEntry> getConfigEntryClass() {
        return null;
    }

    @Override
    public Class<? extends IArrayEntry> getArrayEntryClass() {
        return null;
    }

    @Override
    public String getName() {
        return key;
    }

    @Override
    public String getQualifiedName() {
        return key;
    }

    @Override
    public String getLanguageKey() {
        return mod_id + ".configgui." + this.getName();
    }

    @Override
    public String getComment() {
        return "";
    }

    @Override
    public List<IConfigElement> getChildElements() {
        List<IConfigElement> elements = new ArrayList<IConfigElement>();
        Map<String, Object> map = config.getGroup(key);
        for (String key1 : map.keySet()) {
            elements.add(getTypedElement(mod_id, key, key1, map, (Map) def.get("", key)));
        }
        return elements;
    }

    @Override
    public ConfigGuiType getType() {
        if (def.get(group, key) instanceof ConfigReference) {
            ConfigGuiType type = ((ConfigReference) def.get(group, key)).type;
            if (type != null) {
                return type;
            }
        }
        return getType(config.get(group, key));
    }

    public static ConfigElement<?> getTypedElement(String mod_id, String key, Map<String, Object> value, Map<String, Object> def) {
        return getTypedElement(mod_id, "", key, value, def);
    }

    public static ConfigElement<?> getTypedElement(String mod_id, String group, String key, Map<String, Object> value, Map<String, Object> def) {
        ConfigGuiType type = getType(value);

        if (type == null) {
            return new ConfigElement<String>(mod_id, key, value, def);
        }

        switch (type) {
            case BOOLEAN:
                return new ConfigElement<Boolean>(mod_id, key, value, def);
            case DOUBLE:
                return new ConfigElement<Double>(mod_id, key, value, def);
            case INTEGER:
                return new ConfigElement<Integer>(mod_id, key, value, def);
            default:
                return new ConfigElement<String>(mod_id, key, value, def);
        }
    }

    public static ConfigGuiType getType(Object object) {
        if (object instanceof Boolean) {
            return ConfigGuiType.BOOLEAN;
        } else if (object instanceof Integer) {
            return ConfigGuiType.INTEGER;
        } else if (object instanceof String) {
            return ConfigGuiType.STRING;
        } else if (object instanceof Map) {
            return ConfigGuiType.CONFIG_CATEGORY;
        }
        return null;
    }

    @Override
    public boolean isList() {
        return get() instanceof List;
    }

    @Override
    public boolean isListLengthFixed() {
        return false;
    }

    @Override
    public int getMaxListLength() {
        return -1;
    }

    @Override
    public boolean isDefault() {
        if (get() instanceof List) {
            return Arrays.deepEquals(((List) get()).toArray(), getDefaults());
        } else {
            return config.get(group, key) == def.get(group, key);
        }
    }

    @Override
    public Object getDefault() {
        return ((ConfigReference) def.get(group, key)).defaultValue;
    }

    @Override
    public Object[] getDefaults() {
        if (get() instanceof List) {
            return ((List) get()).toArray();
        } else {
            return new Object[]{getDefault()};
        }
    }

    @Override
    public void setToDefault() {
        config.set(group, key, ((ConfigReference) def.get(group, key)).defaultValue);
    }

    @Override
    public boolean requiresWorldRestart() {
        return false;
    }

    @Override
    public boolean showInGui() {
        return true;
    }

    @Override
    public boolean requiresMcRestart() {
        return false;
    }

    @Override
    public Object get() {
        return config.get(group, key);
    }

    @Override
    public Object[] getList() {
        return ((List) config.get(group, key)).toArray();
    }

    @Override
    public void set(T value) {
        config.set(group, key, value);
    }

    @Override
    public void set(T[] aVal) {
        if (isProperty) {
            config.set(group, key, aVal);
        }
    }

    @Override
    public String[] getValidValues() {
        return null;
    }

    @Override
    public T getMinValue() {
        return (T) String.valueOf(((ConfigReference) def.get(group, key)).minimum);
    }

    @Override
    public T getMaxValue() {
        return (T) String.valueOf(((ConfigReference) def.get(group, key)).maximum);
    }

    @Override
    public Pattern getValidationPattern() {
        return null;
    }

}
