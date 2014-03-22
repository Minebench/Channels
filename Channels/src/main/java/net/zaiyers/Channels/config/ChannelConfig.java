package net.zaiyers.Channels.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.zaiyers.Channels.Channels;

public class ChannelConfig extends AbstractConfig {
	
	/**
	 * load configuration from disk
	 * 
	 * @param configFilePath
	 * @throws IOException
	 */
	public ChannelConfig(String configFilePath) throws IOException {
		super(configFilePath);
	}
	
	/**
	 * get channel name
	 */
	public String getName() {
		return cfg.getString("name");
	}
	
	/**
	 * get channel tag
	 */
	public String getTag() {
		return cfg.getString("tag");
	}
	
	/**
	 * get message format
	 */
	public String getFormat() {
		return cfg.getString("format");
	}
	
	/**
	 * get channel color
	 */
	public ChatColor getColor() {
		return ChatColor.valueOf(cfg.getString("color"));
	}
	
	/**
	 * get channel password
	 */
	public String getPassword() {
		return cfg.getString("password", "");
	}
	
	/**
	 * get channel servers
	 */
	public List<String> getServers() {
		return cfg.getStringList("servers");
	}
	
	/**
	 * get channel moderators uuids
	 */
	public List<String> getModerators() {
		return cfg.getStringList("moderators");
	}
	
	/**
	 * get banned players uuids
	 */
	public List<String> getBans() {
		return cfg.getStringList("bans");
	}

	/**
	 * set channel name
	 */
	public void setName(String name) {
		cfg.set("name", name);
	}
	
	/**
	 * set channel tag
	 */
	public void setTag(String tag) {
		cfg.set("tag", tag);
	}
	
	/**
	 * set channel passwd
	 */
	public void setPassword(String password) {
		if (password == null) {
			cfg.set("password", "");
		} else {
			cfg.set("password", password);
		}
	}
	
	/**
	 * get uuid
	 * @return 
	 */
	public UUID getUUID() {
		return UUID.fromString(configFile.getName().substring(0, 36));
	}
	
	/**
	 * create a new config
	 */
	public void createDefaultConfig() {
		cfg = ymlCfg.load(new InputStreamReader(Channels.getInstance().getResourceAsStream("channel.yml")));
				
		save();
	}

	/**
	 * get format for console messages
	 * @return
	 */
	public String getConsoleFormat() {
		return cfg.getString("consoleFormat");
	}

	/**
	 * add server to distribute list
	 * @param servername
	 */
	public void addServer(String servername) {
		List<String> servers = getServers();
		servers.add(servername);
		
		cfg.set("servers", servers);
	}

	/**
	 * remove server from distribute list
	 * @param servername
	 */
	public void removeServer(String servername) {
		List<String> servers = getServers();
		servers.remove(servername);
		
		cfg.set("servers", servers);
	}

	/**
	 * add moderator
	 * @param uuid
	 */
	public void addModerator(String uuid) {
		List<String> moderators = getModerators();
		moderators.add(uuid);
		
		cfg.set("moderators", moderators);
	}
}