package net.zaiyers.Channels.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import com.mongodb.client.MongoCursor;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.message.PrivateMessage;
import net.zaiyers.Channels.message.PrivateMessage.SenderRole;
import org.bson.Document;

public class ChannelsConfig extends YamlConfig {
	/**
	 * mongodb connection
	 */
	private MongoDBConnection mongo;

	/**
	 * load configuration from disk
	 * 
	 * @param configFile
	 * @throws IOException
	 */
	public ChannelsConfig(File configFile) throws IOException {
		super(configFile);
		
		if (cfg.getBoolean("mongo.use")) {
			mongo = new MongoDBConnection(cfg);
			if (!mongo.isAvailable()) {
				throw new IOException("Unable to connect to Mongo DB even though it was enabled?");
			}
		}
	}
	
	/**
	 * get configured channels
	 */
	public List<String> getChannels() {
		List<String> chans = new ArrayList<>();

		File channelConfigDir = new File(configFile.getParentFile(), "channels");
		if (!channelConfigDir.exists()) {
			channelConfigDir.mkdirs();
			// try to import channels from Mongo
			if (mongo != null && mongo.isAvailable()) {
				MongoCursor<Document> cursor = mongo.getChannels().find().cursor();

				if (!cursor.hasNext()) {
					makeDefaultChannel();
				} else {
					while (cursor.hasNext()) {
						Document channelConfig = cursor.next();
						String uuid = (String) channelConfig.get("uuid");
						try {
							ChannelMongoConfig cfg = new ChannelMongoConfig(Channels.getConfig().getMongoDBConnection().getChannels(), uuid);
							Channel channel = new Channel(uuid);

							// import data
							channel.setAutofocus(cfg.doAutofocus());
							channel.setAutojoin(cfg.doAutojoin());
							channel.setColor(cfg.getColor());
							channel.setBackend(cfg.isBackend());
							channel.setName(cfg.getName());
							channel.setFormat(cfg.getFormat());
							channel.setTag(cfg.getTag());
							channel.setPassword(cfg.getPassword());
							channel.setGlobal(cfg.isGlobal());
							for (String server : cfg.getServers()) {
								channel.addServer(server);
							}
							for (String moderator : cfg.getModerators()) {
								channel.addModerator(moderator);
							}
							for (String ban : cfg.getBans()) {
								channel.banChatter(UUID.fromString(ban));
							}

							// write channel to file
							channel.save();

							Channels.getInstance().getLogger().info("Imported channel " + channel.getName() + " (" + uuid + ") from Mongo");

						} catch (IOException e) {
							Channels.getInstance().getLogger().log(Level.SEVERE, "Error while trying to import channel " + uuid, e);
						}
						chans.add((String) channelConfig.get("uuid"));
					}
				}
			} else {
				makeDefaultChannel();
			}
		}
		for (File channelConfigFile : channelConfigDir.listFiles()) {
			if (channelConfigFile.getName().endsWith(".yml")) {
				chans.add(channelConfigFile.getName().substring(0, 36));
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
	 * return whether or not to send messages with sender UUIDs to block them client side
	 * @return true if sender UUIDs should be sent, false if not
	 */
	public boolean shouldSendUuidsInMessages() {
		return cfg.getBoolean("sendUuidsInMessages");
	}

	/**
	 * return whether or not vanished players should be hidden from messages and commands
	 * @return true if vanished players should be hidden, false if they should show up
	 */
	public boolean shouldHideVanished() {
		return cfg.getBoolean("hideVanished");
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
	 * @param id
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
			return cfg.getString("privateMessageFormatSender", "[&bTo %prefix%&b%receiver%%suffix%:](&7%date% %time%) %msg%");
		} else if (role.equals(PrivateMessage.SenderRole.RECEIVER)) {
			return cfg.getString("privateMessageFormatReceiver", "[&dFrom %prefix%&d%sender%%suffix%:](&7%date% %time%) %msg%");
		} else return null;
	}
	
    /**
     * format for time hover
     * @return
     */
    public String getTimeHoverFormat() {
        return cfg.getString("timeHoverFormat", "§7%date% %time%");
    }
	
	/**
	 * format for the %date% placeholder
	 * @return
	 */
	public SimpleDateFormat getDateFormat() {
		return new SimpleDateFormat(cfg.getString("dateFormat","yyyy-MM-dd"));
	}
	
	/**
	 * format for the %time% placeholder
	 * @return
	 */
	public SimpleDateFormat getTimeFormat() {
		return new SimpleDateFormat(cfg.getString("timeFormat","HH:mm:ss"));
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
		cfg.set("serverDefaultChannels." + serverName, string);
		
		// force default channel
		List<String> serverList = cfg.getStringList("forceServerDefaultChannel");
		serverList.remove(serverName);
		if (force) {
			serverList.add(serverName);
		}
		
		cfg.set("forceServerDefaultChannel", serverList);
	}
	
	public String getServerDefaultChannel(String serverName) {
		return cfg.getString("serverDefaultChannels." + serverName);
	}
	
	public boolean forceServerDefaultChannel(String serverName) {
		List<String> serverList = cfg.getStringList("forceServerDefaultChannel");
		return serverList.contains(serverName);
	}
	
	public MongoDBConnection getMongoDBConnection() {
		if (mongo != null) {
			if (mongo.isAvailable()) {
				return mongo;
			}
			Channels.getInstance().getLogger().warning("We wanted to use a mongo connection but it wasn't available?");
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
