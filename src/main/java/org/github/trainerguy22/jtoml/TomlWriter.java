package org.github.trainerguy22.jtoml;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.github.trainerguy22.jtoml.impl.Toml;

public interface TomlWriter {	
	void write(Map<String, Object> config) throws IOException;
}
