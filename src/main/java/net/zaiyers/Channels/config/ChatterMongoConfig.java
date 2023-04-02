package net.zaiyers.Channels.config;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoCollection;
import net.zaiyers.Channels.Channels;
import org.bson.Document;

public class ChatterMongoConfig extends MongoConfig implements ChatterConfig {
	
	public ChatterMongoConfig(MongoCollection<Document> c, String string) {
		super(c, string);
	}

	public List<String> getSubscriptions() {
		return cfg.getStringList("subscriptions");
	}
	
	public boolean isMuted() {
		return cfg.getBoolean("muted", false);
	}

	public List<String> getIgnores() {
		return cfg.getStringList("ignores");
	}

	public String getPrefix() {
		return cfg.getString("prefix", "");
	}

	public String getSuffix() {
		return cfg.getString("suffix", "");
	}
	
	public String getLastSender() {
		return cfg.getString("lastSender", null);
	}

	public String getChannelUUID() {
		return cfg.getString("channelUUID");
	}

	public void createDefaultConfig() {
		cfg = new MongoConfiguration(Channels.getConfig().getMongoDBConnection().getChatters(), null);
		cfg.load(new InputStreamReader(Channels.getInstance().getResourceAsStream("chatter.yml")));
		cfg.set("uuid", uuid.toString());
		
		// set default channel
		cfg.set("channelUUID", Channels.getConfig().getDefaultChannelUUID().toString());
		
		// subscribe to default channel
		ArrayList<String> subs = new ArrayList<String>();
		subs.add(Channels.getConfig().getDefaultChannelUUID());
		setSubscriptions(subs);
		
		save();
	}

	public void setSubscriptions(List<String> subs) {
		List<String> subscriptions = new ArrayList<String>();
		for (String u: subs) {
			subscriptions.add(u.toString());
		}
		
		cfg.set("subscriptions", subscriptions);
	}

	public void setMuted(boolean b) {
		cfg.set("muted", b);
	}

	public void setPrefix(String prefix) {
		cfg.set("prefix", prefix);
	}

	public void setSuffix(String suffix) {
		cfg.set("prefix", suffix);
	}

	public void removeIgnore(String ignoreUUID) {
		List<String> ignores = cfg.getStringList("ignores");
		ignores.remove(ignoreUUID);
		
		cfg.set("ignores", ignores);
	}

	public void addIgnore(String uuid) {
		List<String> ignores = cfg.getStringList("ignores");
		ignores.add(uuid);
		
		cfg.set("ignores", ignores);
	}

	public void setDefaultChannel(String uuid) {
		cfg.set("channelUUID", uuid);
	}

	public void setLastSender(String uuid) {
		cfg.set("lastSender", uuid);
	}
}
