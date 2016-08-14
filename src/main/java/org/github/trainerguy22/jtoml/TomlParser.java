package org.github.trainerguy22.jtoml;

import java.util.Map;

/**
 * Toml Parser interface.
 * <p/>
 * <p>Every parser should implement that interface.</p>
 * <p/>
 * <p>See: The <a href="https://github.com/mojombo/toml">TOML GitHub</a> project for more information about it.</p>
 *
 * @author <a href="a.grison@gmail.com">$Author: Alexandre Grison$</a>
 */
public interface TomlParser {
    /**
     * Parses the given TOML String.
     *
     * @param tomlString the TOML String
     * @return a Map representing the given TOML structure.
     */
    public Map<String, Object> parse(String tomlString);
}
