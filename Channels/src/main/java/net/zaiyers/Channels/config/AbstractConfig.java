package net.zaiyers.Channels.config;

import java.io.File;
import java.io.IOException;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.zaiyers.Channels.Channels;

public abstract class AbstractConfig implements Config {
	protected Configuration cfg;
	protected final static ConfigurationProvider ymlCfg = ConfigurationProvider.getProvider( YamlConfiguration.class );
	
	protected File configFile; 
	
	/**
	 * read configuration into memory
	 * @param configFilePath
	 * @throws IOException 
	 */
	public AbstractConfig(String configFilePath) throws IOException {
			configFile = new File(configFilePath);
			
			if (!configFile.exists()) {
				if (!configFile.getParentFile().exists()) {
					configFile.getParentFile().mkdirs();
				}
				configFile.createNewFile();
				cfg = ConfigurationProvider.getProvider( YamlConfiguration.class ).load( configFile );
				
				createDefaultConfig();
			} else {
				cfg = ConfigurationProvider.getProvider( YamlConfiguration.class ).load( configFile );
			}
	}
	
	/**
	 * save configuration to disk
	 */
	public void save() {
		try {
			ymlCfg.save(cfg, configFile);
		} catch (IOException e) {
			Channels.getInstance().getLogger().severe("Unable to save configuration at "+configFile.getAbsolutePath());
			e.printStackTrace();
		}
	}
}
