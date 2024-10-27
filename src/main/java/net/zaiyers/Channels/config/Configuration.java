package net.zaiyers.Channels.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;

public class Configuration {
	private static final Pattern PATH_PATTERN = Pattern.compile("\\.");

	private final Configuration defaultCfg;
	private CommentedConfigurationNode config;

	public Configuration() {
		defaultCfg = null;
	}

	public Configuration(Configuration defaultCfg) {
		this.defaultCfg = defaultCfg;
	}

	public Configuration load(File configFile) throws ConfigurateException {
		YamlConfigurationLoader configLoader = YamlConfigurationLoader.builder()
				.indent(4)
				.path(configFile.toPath())
				.nodeStyle(NodeStyle.BLOCK)
				.build();
		config = configLoader.load();
		return this;
	}

	public Configuration load(InputStreamReader stream) throws ConfigurateException {
		BufferedReader reader = new BufferedReader(stream);
		YamlConfigurationLoader configLoader = YamlConfigurationLoader.builder()
				.indent(4)
				.source(() -> reader)
				.nodeStyle(NodeStyle.BLOCK)
				.build();
		config = configLoader.load();
		return this;
	}

	public String getString(String path, String def) {
		if (config == null) {
			return def;
		}
		return config.node(splitPath(path)).getString(def);
	}

	public String getString(String path) {
		return getString(path, defaultCfg != null ? defaultCfg.getString(path) : null);
	}

	public int getInt(String path, int def) {
		if (config == null) {
			return def;
		}
		return config.node(splitPath(path)).getInt(def);
	}

	public List<String> getStringList(String path) {
		if (config == null) {
			return List.of();
		}
		try {
			return config.node(splitPath(path)).getList(String.class);
		} catch (SerializationException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean getBoolean(String path, boolean def) {
		if (config == null) {
			return def;
		}
		return config.node(splitPath(path)).getBoolean(def);
	}

	public boolean getBoolean(String path) {
		return getBoolean(path, defaultCfg != null && defaultCfg.getBoolean(path));
	}

	public void set(String path, Object value) {
		try {
			config.node(splitPath(path)).set(value);
		} catch (SerializationException e) {
			throw new RuntimeException(e);
		}
	}

	private static Object[] splitPath(String key) {
		return PATH_PATTERN.split(key);
	}

	public void save(File configFile) throws IOException {
		YamlConfigurationLoader.builder()
				.indent(4)
				.path(configFile.toPath())
				.nodeStyle(NodeStyle.BLOCK)
				.build()
				.save(config);
	}
}
