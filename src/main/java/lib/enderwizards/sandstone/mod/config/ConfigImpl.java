package lib.enderwizards.sandstone.mod.config;

import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.github.trainerguy22.jtoml.impl.Toml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigImpl extends Config {

    private Map<String, Object> config;
    private Map<String, Object> defaults = new HashMap<String, Object>();
    private File file;

    public ConfigImpl(File file, Map<String, Object> config) {
        this.file = file;
        this.config = config;
    }

    public Map<String, Object> getGroup(String group) {
        return config.get(group) instanceof Map ? ((Map<String, Object>) config.get(group)) : null;
    }

    public Map<String, Object> getDefaultGroup(String group) {
        return defaults.get(group) instanceof Map ? ((Map<String, Object>) defaults.get(group)) : null;
    }

    public void require(String group, String key, ConfigReference def) {
        if (def.side == Side.CLIENT && FMLCommonHandler.instance().getSide() == Side.SERVER)
            return;

        if (getDefaultGroup(group) == null) {
            if (defaults.get(group) != null) {
                defaults.remove(group);
            }
            defaults.put(group, new HashMap<String, ConfigReference>());
        }
        getDefaultGroup(group).put(key, def);

        if (getGroup(group) == null) {
            if (config.get(group) != null) {
                config.remove(group);
            }
            config.put(group, new HashMap<String, Object>());
        }

        if (getGroup(group).containsKey(key))
            return;
        getGroup(group).put(key, def.defaultValue);
    }

    public Object get(String group, String key) {
        Object object;
        if (getGroup(group) == null) {
            object = config.get(key);
        } else {
            object = getGroup(group).get(key);
        }

        return object;
    }

    public Integer getInt(String group, String key) {
        if (this.get(group, key) instanceof Integer)
            return (Integer) this.get(group, key);
        return null;
    }

    public boolean getBool(String group, String key) {
        return ((Boolean) this.get(group, key));
    }

    @Override
    public List<String> getGroups() {
        List<String> groups = new ArrayList<String>();
        for (String key : config.keySet()) {
            if (getGroup(key) != null)
                groups.add(key);
        }

        return groups;
    }

    @Override
    public List<Object> getKeys(String prefix) {
        List<Object> keys = new ArrayList<Object>();

        if (getGroup(prefix) == null) {
            return keys;
        }

        keys.addAll(getGroup(prefix).values());
        return keys;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void save() {
        Toml.write(file, config);
    }

    @Override
    public List<IConfigElement> toGui(String mod_id) {
        List<IConfigElement> elements = new ArrayList<IConfigElement>();
        for (String key : config.keySet()) {
            if (!defaults.containsKey(key))
                continue;
            try {
                elements.add(ConfigElement.getTypedElement(mod_id, key, config, defaults));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return elements;
    }

    @Override
    public void set(String group, String key, Object value) {
        if (getGroup(group) == null) {
            config.put(key, value);
        } else {
            getGroup(group).put(key, value);
        }
    }

}
