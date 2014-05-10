package net.zaiyers.Channels.config;

import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public abstract class MongoConfig implements Config {
	/**
	 * access values using bungees configuration class
	 */
	protected MongoConfiguration cfg;	
	
	/**
	 * uuid of this config
	 */
	protected String uuid;
	
	/**
	 * collection for this config
	 */
	private DBCollection col;
	
	/**
	 * used to load default configs
	 */
	protected final static ConfigurationProvider ymlCfg = ConfigurationProvider.getProvider( YamlConfiguration.class );

	/**
	 * load config from collection, select by uuid
	 * @param c
	 * @param string
	 */
	protected MongoConfig (DBCollection c, String string) {
		this.uuid = string;
		col = c;
		cfg = new MongoConfiguration(c, this.uuid);
		
		if (!cfg.loaded()) {
			createDefaultConfig();
		}
	}

	public void save() {
		MongoConfiguration.save(col, cfg);
	}
	
	public void removeConfig() {
		col.remove(new BasicDBObject("uuid", uuid));
	}
}
