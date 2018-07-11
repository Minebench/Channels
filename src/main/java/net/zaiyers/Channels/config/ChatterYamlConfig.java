package net.zaiyers.Channels.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.zaiyers.Channels.Channels;

public class ChatterYamlConfig extends YamlConfig implements ChatterConfig {

	/**
	 * load configuration from disk
	 * 
	 * @param configFile
	 * @throws IOException
	 */
	public ChatterYamlConfig(File configFile) throws IOException {
		super(configFile);
	}

	/**
	 * load a config instance by player uuid
	 * @param uuid
	 * @return
	 */
	public static ChatterYamlConfig load(UUID uuid) {
		File cfgFile = new File(Channels.getInstance().getDataFolder(),
				("chatters" + File.separator
						+ uuid.toString().substring(0,2) + File.separator
						+ uuid.toString().substring(2,4) + File.separator
						+ uuid +".yml"
				).toLowerCase()
		);
		if (cfgFile.exists()) {
			try {
				return new ChatterYamlConfig(cfgFile);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
	
	public List<String> getSubscriptions() {
		ArrayList<String> subs = new ArrayList<String>();
		for (String sub: cfg.getStringList("subscriptions")) {
			subs.add(sub);
		};
		
		return subs;
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

	public void save() {
		try {
			ymlCfg.save(cfg, configFile);
		} catch (IOException e) {
			Channels.getInstance().getLogger().severe("Unable to save chatter configuration '"+configFile.getAbsolutePath()+"'");
			e.printStackTrace();
		}
	}

	public void createDefaultConfig() {
		cfg = ymlCfg.load(
			new InputStreamReader(Channels.getInstance().getResourceAsStream("chatter.yml"))
		);
		
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
		cfg.set("channelUUID", uuid.toString());
	}

	public void setLastSender(String uuid) {
		cfg.set("lastSender", uuid);
	}
}
