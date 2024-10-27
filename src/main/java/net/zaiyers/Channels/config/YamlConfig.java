package net.zaiyers.Channels.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.zaiyers.Channels.Channels;

public abstract class YamlConfig implements Config {
	protected Configuration cfg;
	private final Configuration defaultCfg;
	
	protected File configFile; 
	
	/**
	 * read configuration into memory
	 * @param configFile
	 * @throws IOException
	 */
	public YamlConfig(File configFile) throws IOException {
		this.configFile = configFile;
		InputStream stream = Channels.getInstance().getResourceAsStream(configFile.getName());
		if (stream != null) {
			defaultCfg = new Configuration().load(new InputStreamReader(stream));
		} else {
			defaultCfg = null;
		}
		load();
	}
	
	/**
	 * load configuration from disk
	 */
	public void load() throws IOException {
		if (!configFile.exists()) {
			if (!configFile.getParentFile().exists()) {
				configFile.getParentFile().mkdirs();
			}
			configFile.createNewFile();
			cfg = new Configuration(defaultCfg).load(configFile);
			
			createDefaultConfig();
		} else {
			cfg = new Configuration(defaultCfg).load(configFile);
		}
	}
	
	/**
	 * save configuration to disk
	 */
	public void save() {
		try {
			cfg.save(configFile);
		} catch (IOException e) {
			Channels.getInstance().getLogger().severe("Unable to save configuration at "+configFile.getAbsolutePath());
			e.printStackTrace();
		}
	}
	
	/**
	 * deletes configuration file
	 */
	public void removeConfig() {
		configFile.delete();
	}
}
