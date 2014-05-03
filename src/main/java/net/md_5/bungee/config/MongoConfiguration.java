package net.md_5.bungee.config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import net.md_5.bungee.config.Configuration;

public abstract class MongoConfiguration {
	public static Configuration load(DBCollection col, UUID uuid) {
		DBObject search = new BasicDBObject("uuid", uuid.toString());
		
		DBCursor cursor = col.find(search);
		Map<String, Object> configMap = new HashMap<String, Object>();
		while (cursor.hasNext()) {
			DBObject setting = cursor.next();
			configMap.putAll(setting.toMap());
		}
		
		return new Configuration(configMap, null);
	}

	public static void save(DBCollection col, Configuration cfg) {
		DBObject config = new BasicDBObject();
		config.putAll(cfg.self);
		
		col.findAndModify(new BasicDBObject("uuid", ((UUID) cfg.self.get("uuid")).toString() ), config);
	}
}
