package org.github.trainerguy22.jtoml.impl;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.github.trainerguy22.jtoml.Getter;
import org.github.trainerguy22.jtoml.Util;

public class TomlWrapper implements Getter {
	
    /** The instance context map holding key/values parsed from a TOML String or File */
    protected Map<String, Object> context;
	
    /** A matcher to retrieve the path to a key. */
    protected final Matcher keyPathMatcher = Pattern.compile("((\\w+[.])+).*").matcher("");
	
    public TomlWrapper(Map<String, Object> context) {
    	this.context = context;
    }
    
    @Override
    public Object get(String key) {
        if (key == null || "".equals(key.trim())) {
            return context;
        } else if (key.contains(".")) {
            String keyPath = keyPath(key);
            return findContext(context, keyPath).get(key.replace(keyPath + ".", ""));
        } else {
            return context.get(key);
        }
    }

    @Override
    public String getString(String key) {
        return get(key, String.class);
    }

    @Override
    public Integer getInt(String key) {
        return get(key, Integer.class);
    }

    @Override
    public Double getDouble(String key) {
        return get(key, Double.class);
    }

    @Override
    public Calendar getDate(String key) {
        return get(key, Calendar.class);
    }

    @Override
    public List<Object> getList(String key) {
        return get(key, List.class);
    }

    @Override
    public Map<String, Object> getMap(String key) {
        return get(key, Map.class);
    }

    @Override
    public Boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

    /**
     * Get a new instance of the given Class filled with the value that can be found
     * in the current context at the given key.
     *
     * @param key the key where the value is located
     * @param clazz the class of the resulting object
     * @param <T> the resulting object type
     * @return the value whose key is the given parameter
     */
    @Override
    public <T> T getAs(String key, Class<T> clazz) {
        try {
            T result = clazz.newInstance();
            for (Field f: clazz.getDeclaredFields()) {
                Class<?> fieldType = f.getType();
                String fieldName = (key == null || "".equals(key.trim())) ? f.getName() : key + "." + f.getName();
                Object fieldValue = Util.Reflection.isTomlSupportedType(fieldType) ? //
                        get(fieldName, fieldType) : getAs(fieldName, fieldType);
                Util.Reflection.setFieldValue(f, result, fieldValue);
            }
            return result;
        } catch (Throwable e) {
            throw new IllegalArgumentException("Could not map value of key `" + key + //
                    "` to Object of class `" + clazz.getName() + "`.", e);
        }
    }
    
    /**
     * Get the value whose key is the given parameter from the context map and cast it to the given class.
     * <p>Returns null if the value is null.</p>
     *
     * @param key the key to search the value for.
     * @param clazz the class of the resulting object
     * @param <T> the resulting object type
     * @return the value whose key is the given parameter, <code>null</code> if not found
     */
    private <T> T get(String key, Class<T> clazz) {
        Object value = get(key);
        if (value == null) {
            return null;
        } else if (clazz.isInstance(value)) {
            return (T) value;
        } else {
            throw illegalArg(key, value, clazz);
        }
    }
    
    /**
     * Get the path to a key.
     * A key can be anything like <code>(\w[.])*\w</code>
     *
     * <p><code>keyPath("foo") -> "foo"</code></p>
     * <p><code>keyPath("foo.bar") -> "foo"</code></p>
     * <p><code>keyPath("foo.bar.bazz") -> "foo.bar"</code></p>
     *
     * @param key the key
     * @return the path leading to the key.
     */
    private String keyPath(String key) {
        if (keyPathMatcher.reset(key).matches()) {
            return keyPathMatcher.group(1).substring(0, keyPathMatcher.group(1).length() - 1);
        } else {
            return key;
        }
    }
    
    /**
     * Creates an IllegalArgumentException with a pre-filled message.
     * @param key the key
     * @param expected the expected type
     * @param value the value
     * @return the exception ready to be thrown
     */
    private IllegalArgumentException illegalArg(String key, Object value, Class<?> expected) {
       return new IllegalArgumentException(String.format("Value for key `%s` is `%s`%s.", //
               key, value, (value == null ? "" : ". Expected type was `" + expected.getName() + "`")));
    }
    
    /**
     * Find the correct level context.
     * findContext({"foo": {"bar": "hello"}}, "foo.bar")
     * -> "hello"
     *
     * @param context the context
     * @param key the key
     * @return the context
     */
    public Map<String, Object> findContext(Map<String, Object> context, String key) {
        Map<String, Object> visitor = context;
        for (String part: key.split("[.]")) {
            if (!visitor.containsKey(part)) {
                return null;
            }
            visitor = (Map<String, Object>)visitor.get(part);
        }
        return visitor;
    }

}
