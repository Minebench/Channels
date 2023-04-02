package net.zaiyers.Channels.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.md_5.bungee.config.Configuration;
import org.bson.Document;

public class MongoDBConnection {
	private MongoClient mongo;
	private MongoDatabase db;
	private MongoCollection<Document> channels;
	private MongoCollection<Document> chatters;
	
	public MongoDBConnection(Configuration cfg) {
		if (!mongoDBConnect(cfg)) {
			mongo.close();
			mongo = null;
		}
	}
	
	private boolean mongoDBConnect(Configuration cfg) {
		String mongoHost = cfg.getString("mongo.host", "localhost");
		int mongoPort = cfg.getInt("mongo.port", 27017);
		ServerApi serverApi = ServerApi.builder()
				.version(ServerApiVersion.V1)
				.build();

		MongoClientSettings.Builder settings = MongoClientSettings.builder()
				.applyConnectionString(new ConnectionString("mongodb://" + mongoHost + ":" + mongoPort + "/"))
				.serverApi(serverApi);
		if (cfg.getString("mongo.user") != null && !cfg.getString("mongo.pass").isEmpty()) {
			if (cfg.getString("mongo.authdb") == null || cfg.getString("mongo.pwd") == null || cfg.getString("mongo.authdb").isEmpty()) {
				throw new MongoException("Invalid configuration for mongoauth! To not use mongoauth leave the user empty!");
			}
			settings.credential(MongoCredential.createPlainCredential(cfg.getString("mongo.user"), cfg.getString("mongo.authdb"), cfg.getString("mongo.pass").toCharArray()));
		}

		try (MongoClient mongo = MongoClients.create(settings.build())) {
			this.mongo = mongo;
			// try auth
			db = mongo.getDatabase(cfg.getString("mongo.db"));
			
			channels = db.getCollection(cfg.getString("mongo.channelCollection"));
			chatters = db.getCollection(cfg.getString("mongo.chatterCollection"));
		} catch (Exception e) {
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
	
	public MongoCollection<Document> getChatters() {
		return chatters;
	}
	
	public MongoCollection<Document> getChannels() {
		return channels;
	}
	
	public MongoDatabase getDB() {
		return db;
	}
}
