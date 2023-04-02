package net.zaiyers.Channels.config;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.yaml.snakeyaml.Yaml;

public class MongoConfiguration {
	private Document settings;
	
	public MongoConfiguration(MongoCollection<Document> col, String uuid) {
		if (uuid != null) {
			MongoCursor<Document> cursor = col.find(Filters.eq("uuid", uuid)).cursor();
			while (cursor.hasNext()) {
				settings = cursor.next();
			}
		}
	}

	public static void save(MongoCollection<Document> col, MongoConfiguration cfg) {
		Document config = new Document();
		config.putAll(cfg.settings);

		col.replaceOne(Filters.eq("uuid", cfg.settings.get("uuid")), config);
	}
	
	public List<String> getStringList(String path) {
        List<String> stringList = new ArrayList<String>();

        for (Object object: getList(path)) {
            if (object instanceof String) {
            	stringList.add((String) object);
            }
        }

        return stringList;
	}
	
	private List<Object> getList(String path) {
		if (settings.get(path) instanceof List) {
			return (List<Object>) settings.get(path);
		} else if (settings.get(path) != null) {
			List<Object> list = new ArrayList<>();
			list.add(settings.get(path));
			return list;
		}
		
		return null;
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
		if (value instanceof ArrayList<?>) {
			List<Object> save = new ArrayList<>();
			save.addAll(((ArrayList<?>) value));
			
			settings.put(key, save);
		} else {
			settings.put(key, value);
		}
	}
	
	public void load(InputStreamReader io) {
		Yaml yaml = new Yaml();
		settings = new Document();
		settings.putAll(yaml.loadAs(io, LinkedHashMap.class));
	}
	
	public boolean loaded() {
		return settings != null;
	}
}
