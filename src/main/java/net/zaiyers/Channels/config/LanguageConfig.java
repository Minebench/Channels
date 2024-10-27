package net.zaiyers.Channels.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import net.zaiyers.Channels.Channels;
import org.spongepowered.configurate.ConfigurateException;

public class LanguageConfig extends YamlConfig {
	public LanguageConfig(File configFile) throws IOException {
		super(configFile);
	}

	public void createDefaultConfig() {
		InputStream defaultConfig = Channels.getInstance().getResourceAsStream(configFile.getName());
		if (defaultConfig == null) {
			defaultConfig = Channels.getInstance().getResourceAsStream("lang.en.yml");
		}
		cfg = new Configuration();
		try {
			cfg.load(new InputStreamReader(defaultConfig));
		} catch (ConfigurateException e) {
			Channels.getInstance().getLogger().log(Level.WARNING, "Unable to load default language configuration " + configFile.getName() + " from jar file!", e);
		}

		save();
	}
	
	/**
	 * get translation from config
	 */
	public String getTranslation(String key) {
		if (cfg.getString(key, "").isEmpty()) {
			return "Unknown language key: " + key;
		} else {
			return cfg.getString(key);
		}
	}
	
	/**
	 * get translation as a base component from the config with replacements
	 */
	public Component getTranslationComponent(String key, String... replacements) {
		return new MineDown(getTranslation(key)).replace(replacements).toComponent();
	}
	
	/**
	 * get translation as a base component from the config with replacements
	 */
	public Component getTranslationComponent(String key, Map<String, String> replacements) {
		return new MineDown(getTranslation(key)).replace(replacements).replaceFirst(true).toComponent();
	}
}
