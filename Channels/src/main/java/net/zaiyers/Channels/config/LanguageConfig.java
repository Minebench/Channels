package net.zaiyers.Channels.config;

import java.io.IOException;
import java.io.InputStreamReader;

import net.zaiyers.Channels.Channels;

public class LanguageConfig extends AbstractConfig {
	public LanguageConfig(String configFilePath) throws IOException {
		super(configFilePath);
	}

	public void createDefaultConfig() {
		// default is english
		cfg = ymlCfg.load(
				new InputStreamReader(Channels.getInstance().getResourceAsStream("lang.en.yml"))
		);
		
		save();
	}
	
	/**
	 * get translation from config
	 */
	public String getTranslation(String key) {
		if (cfg.getString(key, "").isEmpty()) {
			return "§cUnknown language key: §6"+key;
		} else {
			return cfg.getString(key); 
		}
	}
}
