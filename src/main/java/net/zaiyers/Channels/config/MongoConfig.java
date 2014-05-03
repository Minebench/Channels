package net.zaiyers.Channels.config;

import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.MongoConfiguration;
import net.md_5.bungee.config.YamlConfiguration;

public abstract class MongoConfig implements Config {
	/**
	 * access values using bungees configuration class
	 */
	protected Configuration cfg;	
	
	/**
	 * uuid of this config
	 */
	private UUID uuid;
	
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
	 * @param uuid
	 */
	protected MongoConfig (DBCollection c, UUID uuid) {
		this.uuid = uuid;
		col = c;
		cfg = MongoConfiguration.load(c, uuid);
		
		if (cfg.getString("uuid") == null) {
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
