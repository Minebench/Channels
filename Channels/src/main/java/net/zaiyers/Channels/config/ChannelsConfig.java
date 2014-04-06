package net.zaiyers.Channels.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.command.PMCommand;
import net.zaiyers.Channels.message.PrivateMessage;
import net.zaiyers.Channels.message.PrivateMessage.SenderRole;

public class ChannelsConfig extends AbstractConfig {
	
	/**
	 * load configuration from disk
	 * 
	 * @param configFilePath
	 * @throws IOException
	 */
	public ChannelsConfig(String configFilePath) throws IOException {
		super(configFilePath);
	}
	
	/**
	 * get configured channels
	 */
	public List<UUID> getChannels() {
		ArrayList<UUID> chans = new ArrayList<UUID>(); 
		
		File channelConfigDir = new File(configFile.getParentFile()+File.separator+"channels");
		if (!channelConfigDir.exists()) {
			// inital run, create default configuration
			try {
				channelConfigDir.mkdirs();
				
				Channel def;
				
				def = new Channel(getDefaultChannelUUID());
				def.setName("default");
				def.setTag("D");
				def.save();
				
				chans.add(def.getUUID());
			} catch (IOException e) {
				Channels.getInstance().getLogger().severe("Could not create default channel!");
				e.printStackTrace();
			}
		} else {
			for (File channelConfigFile: channelConfigDir.listFiles()) {
				if (channelConfigFile.getName().endsWith(".yml")) {
					chans.add(UUID.fromString(channelConfigFile.getName().substring(0, 36)));
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
	public UUID getDefaultChannelUUID() {
		return UUID.fromString(cfg.getString("defaultChannelUUID"));
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
	 * @param channelUUID
	 */
	public void setServerDefaultChannel(String serverName, UUID channelUUID) {
		@SuppressWarnings("unchecked")
		Map<String, String> serverDefaultChannels = (Map<String, String>) cfg.get("serverDefaultChannels");
		serverDefaultChannels.put(serverName, channelUUID.toString());
		cfg.set("serverDefaultChannels", serverDefaultChannels);
	}

	public UUID getServerDefaultChannel(String serverName) {
		@SuppressWarnings("unchecked")
		Map<String, String> serverDefaultChannels = (Map<String, String>) cfg.get("serverDefaultChannels");
		return (serverDefaultChannels.get(serverName) != null) ? UUID.fromString(serverDefaultChannels.get(serverName)) : null;
	}
}
