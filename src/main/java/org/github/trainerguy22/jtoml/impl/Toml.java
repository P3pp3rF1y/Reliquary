package org.github.trainerguy22.jtoml.impl;

import org.github.trainerguy22.jtoml.TomlParser;
import org.github.trainerguy22.jtoml.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public class Toml {

    private static final Logger LOGGER = Logger.getLogger(Toml.class.getName());

    /**
     * The default {@link TomlParser} loaded from {@link ServiceLoader}. Defaults to {@link SimpleTomlParser} if none found
     */
    private static TomlParser parser;

    /**
     * Retrieve a TomlParser on classpath.
     */
    static {
        initDefaultParser();
    }

    public static Map<String, Object> parse(String tomlString) {
        return parse(tomlString, null);
    }

    public static Map<String, Object> parse(String tomlString, TomlParser tomlParser) {
        return parseString(tomlString);
    }

    public static Map<String, Object> parse(File file) throws IOException {
        return parse(file, null);
    }

    public static Map<String, Object> parse(File file, TomlParser tomlParser) throws IOException {
        return parseFile(file);
    }

    public static Map<String, Object> parseString(String string) {
        return parser.parse(string);
    }

    public static Map<String, Object> parseFile(File file) throws FileNotFoundException {
        return parseString(Util.FileToString.read(file));
    }

    private static void initDefaultParser() {
        parser = new SimpleTomlParser();
    }

    public static void write(File file, Map<String, Object> config) {
        try {
            (new SimpleTomlWriter(file)).write(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
