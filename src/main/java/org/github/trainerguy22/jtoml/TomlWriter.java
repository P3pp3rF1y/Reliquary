package org.github.trainerguy22.jtoml;

import java.io.IOException;
import java.util.Map;

public interface TomlWriter {
    void write(Map<String, Object> config) throws IOException;
}
