package net.zaiyers.Channels.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

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
	private final MongoCollection<Document> col;

	/**
	 * load config from collection, select by uuid
	 * @param c
	 * @param string
	 */
	protected MongoConfig (MongoCollection<Document> c, String string) {
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
		col.deleteMany(Filters.eq("uuid", uuid));
	}
}
