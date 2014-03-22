package net.zaiyers.Channels.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.zaiyers.Channels.Channels;

public class ChatterConfig extends AbstractConfig {

	/**
	 * load configuration from disk
	 * 
	 * @param configFilePath
	 * @throws IOException
	 */
	public ChatterConfig(String configFilePath) throws IOException {
		super(configFilePath);
	}

	/**
	 * load a config instance by player uuid
	 * @param uuid
	 * @return
	 */
	public static ChatterConfig load(String uuid) {
		String configFilePath = Channels.getInstance().getDataFolder()+("/chatters/"+uuid.substring(0,2)+"/"+uuid.substring(2,4)+"/"+uuid+".yml").toLowerCase();
		File cfgFile = new File(configFilePath);
		if (cfgFile.exists()) {
			try {
				return new ChatterConfig(configFilePath);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * read subscriptions from configuration
	 * @return
	 */
	public List<UUID> getSubscriptions() {
		ArrayList<UUID> subs = new ArrayList<UUID>();
		for (String sub: cfg.getStringList("subscriptions")) {
			subs.add(UUID.fromString(sub));
		};
		
		return subs;
	}
	
	/**
	 * I was a bad boy
	 */
	public boolean isMuted() {
		return cfg.getBoolean("muted", false);
	}
	
	/**
	 * these are bad boys
	 */
	public List<String> getIgnores() {
		return cfg.getStringList("ignores");
	}
	
	/**
	 * prefixes are kewl
	 */
	public String getPrefix() {
		return cfg.getString("prefix", "");
	}
	
	/**
	 * suffixes are kewl as well
	 */
	public String getSuffix() {
		return cfg.getString("suffix", "");
	}
	
	/**
	 * I know this guy
	 */
	public String getLastSender() {
		return cfg.getString("lastSender", null);
	}
	
	/**
	 * I like this guy
	 */
	public String getLastRecipient() {
		return cfg.getString("lastRecipient", null);
	}
	
	/**
	 * I have many friends
	 */
	public UUID getChannelUUID() {
		return UUID.fromString(cfg.getString("channelUUID"));
	}

	/**
	 * save configuration to disk
	 */
	public void save() {
		try {
			ymlCfg.save(cfg, configFile);
		} catch (IOException e) {
			Channels.getInstance().getLogger().severe("Unable to save chatter configuration '"+configFile.getAbsolutePath()+"'");
			e.printStackTrace();
		}
	}

	/**
	 * create a default chatter configuration
	 */
	public void createDefaultConfig() {
		cfg = ymlCfg.load(
			new InputStreamReader(Channels.getInstance().getResourceAsStream("chatter.yml"))
		);
		
		// set default channel
		cfg.set("channelUUID", Channels.getConfig().getDefaultChannelUUID().toString());
		
		// subscribe to default channel
		ArrayList<UUID> subs = new ArrayList<UUID>();
		subs.add(Channels.getConfig().getDefaultChannelUUID());
		setSubscriptions(subs);
		
		save();
	}

	/**
	 * override list of subscriptions
	 * @param subs
	 */
	public void setSubscriptions(List<UUID> subs) {
		List<String> subscriptions = new ArrayList<String>();
		for (UUID u: subs) {
			subscriptions.add(u.toString());
		}
		
		cfg.set("subscriptions", subscriptions);
	}

	/**
	 * set muted status
	 * @param b
	 */
	public void setMuted(boolean b) {
		cfg.set("muted", b);
	}

	/**
	 * set chatter prefix
	 * @param prefix
	 */
	public void setPrefix(String prefix) {
		cfg.set("prefix", prefix);
	}

	/**
	 * set chatter suffix
	 * @param string
	 */
	public void setSuffix(String suffix) {
		cfg.set("prefix", suffix);
	}
}
