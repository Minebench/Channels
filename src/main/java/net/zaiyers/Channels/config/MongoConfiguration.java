package net.zaiyers.Channels.config;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoConfiguration {
	private Map<String, Object> settings;
	
	public MongoConfiguration(DBCollection col, String uuid) {
		if (uuid != null) {
			DBObject search = new BasicDBObject("uuid", uuid);
			
			DBCursor cursor = col.find(search);
			while (cursor.hasNext()) {
				DBObject setting = cursor.next();
				settings.putAll(setting.toMap());
			}
		} 
		
		if (settings == null) {
			settings = Collections.emptyMap();
		}
	}

	public static void save(DBCollection col, MongoConfiguration cfg) {
		DBObject config = new BasicDBObject();
		config.putAll(cfg.settings);
		
		col.remove(new BasicDBObject("uuid", cfg.settings.get("uuid")));
		col.insert(config);
	}
	
	public List<String> getStringList(String path) {
		List<Object> list = getList(path);
        List<String> stringList = new ArrayList<String>();

        for (Object object: list) {
            if (object instanceof String) {
            	stringList.add((String) object);
            }
        }

        return stringList;
	}
	
	public Boolean getBoolean(String key) {
		Object object = settings.get(key);
		if (object instanceof Boolean) {
			return (Boolean) object;
		}
		
		return null;
	}
	
	public Boolean getBoolean(String key, boolean def) {
		Boolean b = getBoolean(key);
		return (b != null) ? b : def;  
	}
	
	public String getString(String key) {
		Object object = settings.get(key);
		if (object instanceof String) {
			return (String) object;
		}
		
		return null;
	}
	
	public String getString(String key, String def) {
		String s = getString(key);
		return (s != null) ? s : def;  
	}
	
	public void set(String key, Object value) {
		settings.put(key, value);
	}
	
	private List<Object> getList(String path) {
		return (List<Object>) settings.get(path);
	}

	public void load(InputStreamReader io) {
		Yaml yaml = new Yaml();
		settings = yaml.loadAs(io, LinkedHashMap.class);
	}
}
