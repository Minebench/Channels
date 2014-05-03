package net.zaiyers.Channels.config;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import net.md_5.bungee.config.Configuration;
import net.zaiyers.Channels.Channels;

public class MongoDBConnection {
	private MongoClient mongo;
	private DBCollection channels;
	private DBCollection chatters;
	
	public MongoDBConnection(Configuration cfg) {
		if (!mongoDBConnect(cfg)) {
			mongo.close();
			mongo = null;
		}
	}
	
	private boolean mongoDBConnect(Configuration cfg) {
		String mongoHost = cfg.getString("mongo.host", "localhost");
		int mongoPort = cfg.getInt("mongo.port", 27017);
		try {
			mongo = new MongoClient(mongoHost, mongoPort);
			// try auth
			if (cfg.getString("mongo.user") != null && !cfg.getString("mongo.user").isEmpty()) {
				if (cfg.getString("mongo.authdb") == null || cfg.getString("mongo.pwd") == null || cfg.getString("mongo.authdb").isEmpty()) {
					Channels.getInstance().getLogger().severe("Invalid configuration for mongoauth"); return false;
				}
				
				DB authdb = mongo.getDB(cfg.getString("mongo.authdb"));
				if (!authdb.authenticate(cfg.getString("mongo.user"), cfg.getString("mongo.pwd").toCharArray())) {
					Channels.getInstance().getLogger().severe("MongoDB failed authentication"); return false;
				}
				DB db = mongo.getDB(cfg.getString("mongo.db"));
				channels = db.getCollection(cfg.getString("mongo.channelCollection"));
				chatters = db.getCollection(cfg.getString("mongo.chatterCollection"));
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * mongo configuration is available
	 * @return
	 */
	public boolean isAvilable() {
		return mongo != null;
	}
	
	public DBCollection getChatters() {
		return chatters;
	}
	
	public DBCollection getChannels() {
		return channels;
	}
}
