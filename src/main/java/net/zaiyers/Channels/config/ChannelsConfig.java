package net.zaiyers.Channels.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.message.PrivateMessage;
import net.zaiyers.Channels.message.PrivateMessage.SenderRole;

public class ChannelsConfig extends YamlConfig {
	/**
	 * mongodb connection
	 */
	private MongoDBConnection mongo;

	/**
	 * load configuration from disk
	 * 
	 * @param configFilePath
	 * @throws IOException
	 */
	public ChannelsConfig(String configFilePath) throws IOException {
		super(configFilePath);
		
		if (cfg.getBoolean("mongo.use")) {
			mongo = new MongoDBConnection(cfg);
		}
	}
	
	/**
	 * get configured channels
	 */
	public List<String> getChannels() {
		ArrayList<String> chans = new ArrayList<String>(); 
		
		if (mongo != null && mongo.isAvilable()) {
			DBCursor cursor = mongo.getChannels().find();
			
			if (!cursor.hasNext()) {
				Channel def = makeDefaultChannel();
				if (def != null) {
					chans.add(def.getUUID());
				}
			} else {
				while (cursor.hasNext()) {
					DBObject channelConfig = cursor.next();
					chans.add((String) channelConfig.get("uuid"));
				}
			}
		} else {
			File channelConfigDir = new File(configFile.getParentFile()+File.separator+"channels");
			if (!channelConfigDir.exists()) {
				channelConfigDir.mkdirs();
				Channel def = makeDefaultChannel();
				if (def == null) {
					channelConfigDir.delete();
				} else {
					chans.add(def.getUUID());
				}
			} else {
				for (File channelConfigFile: channelConfigDir.listFiles()) {
					if (channelConfigFile.getName().endsWith(".yml")) {
						chans.add(channelConfigFile.getName().substring(0, 36));
					}
				}
			}
		}
		
		return chans;
	}
	
	/**
	 * save configuration to disk
	 */
	public void save() {
		try {
			ymlCfg.save(cfg, configFile);
		} catch (IOException e) {
			Channels.getInstance().getLogger().warning("Unable to save configuration!");
			e.printStackTrace();
		}
	}

	/**
	 * return name of default channel
	 * @return
	 */
	public String getDefaultChannelUUID() {
		return cfg.getString("defaultChannelUUID");
	}
	
	/**
	 * save name of default channel
	 * @param name
	 */
	public void setDefaultChannelId(int id) {
		cfg.set("defaultChannelUUID", id);
	}

	/**
	 * format for private messages
	 * @param role 
	 * @return
	 */
	public String getPrivateMessageFormat(SenderRole role) {
		if (role.equals(PrivateMessage.SenderRole.SENDER)) {
			return cfg.getString("privateMessageFormatSender", "§bTo %receiver%: %message%");
		} else if (role.equals(PrivateMessage.SenderRole.RECEIVER)) {
			return cfg.getString("privateMessageFormatReceiver", "§dFrom %sender%: %message%");
		} else return null;
	}

	/**
	 * create default configuration
	 */
	public void createDefaultConfig() {		
		cfg = ymlCfg.load(new InputStreamReader(Channels.getInstance().getResourceAsStream("config.yml")));
		cfg.set("defaultChannelUUID", UUID.randomUUID().toString());
		
		save();
	}

	/**
	 * get language name
	 * @return
	 */
	public String getLanguage() {
		return cfg.getString("language");
	}

	/**
	 * set server default channel
	 * @param serverName
	 * @param string
	 */
	public void setServerDefaultChannel(String serverName, String string, boolean force) {
		@SuppressWarnings("unchecked")
		Map<String, String> serverDefaultChannels = (Map<String, String>) cfg.get("serverDefaultChannels");
		serverDefaultChannels.put(serverName, string);
		cfg.set("serverDefaultChannels", serverDefaultChannels);
		
		// force default channel
		List<String> serverList = cfg.getStringList("forceServerDefaultChannel");
		serverList.remove(serverName);
		if (force) {
			serverList.add(serverName);
		}
		
		cfg.set("forceServerDefaultChannel", serverList);
	}

	public String getServerDefaultChannel(String serverName) {
		@SuppressWarnings("unchecked")
		Map<String, String> serverDefaultChannels = (Map<String, String>) cfg.get("serverDefaultChannels");
		return (serverDefaultChannels.get(serverName) != null) ? serverDefaultChannels.get(serverName) : null;
	}
	
	public boolean forceServerDefaultChannel(String serverName) {
		List<String> serverList = cfg.getStringList("forceServerDefaultChannel");
		return serverList.contains(serverName);
	}
	
	public MongoDBConnection getMongoDBConnection() {
		if (mongo != null && mongo.isAvilable()) {
			return mongo;
		}
		
		return null;
	}
	
	private Channel makeDefaultChannel() {
		// inital run, create default configuration
		try {			
			Channel def;
			
			def = new Channel(getDefaultChannelUUID());
			def.setName("default");
			def.setTag("D");
			def.save();
			
			return def;
		} catch (IOException e) {
			Channels.getInstance().getLogger().severe("Could not create default channel!");
			e.printStackTrace();
		}
		
		return null;
	}
}
