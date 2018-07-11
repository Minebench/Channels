package net.zaiyers.Channels.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.zaiyers.Channels.Channels;

public class LanguageConfig extends YamlConfig {
	public LanguageConfig(File configFile) throws IOException {
		super(configFile);
	}

	public void createDefaultConfig() {
		InputStream defaultConfig = Channels.getInstance().getResourceAsStream(configFile.getName());
		if (defaultConfig == null) {
			defaultConfig = Channels.getInstance().getResourceAsStream("lang.en.yml");
		}
		cfg = ymlCfg.load(new InputStreamReader(defaultConfig));
		
		save();
	}
	
	/**
	 * get translation from config
	 */
	public String getTranslation(String key) {
		if (cfg.getString(key, "").isEmpty()) {
			return ChatColor.RED + "Unknown language key: " + ChatColor.GOLD + key;
		} else {
			return ChatColor.translateAlternateColorCodes('&', cfg.getString(key));
		}
	}
	
	/**
	 * get translation as a base component from the config with replacements
	 */
	public BaseComponent[] getTranslationComponent(String key, String... replacements) {
		return new MineDown(getTranslation(key)).replace(replacements).toComponent();
	}
	
	/**
	 * get translation as a base component from the config with replacements
	 */
	public BaseComponent[] getTranslationComponent(String key, Map<String, String> replacements) {
		return new MineDown(getTranslation(key)).replace(replacements).toComponent();
	}
}
