package net.zaiyers.Channels.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.mongodb.DBCollection;

import net.md_5.bungee.api.ChatColor;
import net.zaiyers.Channels.Channels;

public class ChannelMongoConfig extends MongoConfig implements ChannelConfig {
	public ChannelMongoConfig(DBCollection col, String uuid) throws IOException {
		super(col, uuid);
	}

	public String getName() {
		return cfg.getString("name");
	}

	public String getTag() {
		return cfg.getString("tag");
	}

	public String getFormat() {
		return cfg.getString("format");
	}

	public ChatColor getColor() {
		return ChatColor.of(cfg.getString("color"));
	}

	public String getPassword() {
		return cfg.getString("password", "");
	}

	public List<String> getServers() {
		return cfg.getStringList("servers");
	}

	public List<String> getModerators() {
		return cfg.getStringList("moderators");
	}

	public List<String> getBans() {
		return cfg.getStringList("bans");
	}

	public void setName(String name) {
		cfg.set("name", name);
	}

	public void setTag(String tag) {
		cfg.set("tag", tag);
	}

	public void setFormat(String format) {
		cfg.set("format", format);
	}

	public void setPassword(String password) {
		if (password == null) {
			cfg.set("password", "");
		} else {
			cfg.set("password", password);
		}
	}

	public String getUUID() {
		return cfg.getString("uuid");
	}

	public void createDefaultConfig() {
		cfg = new MongoConfiguration(Channels.getConfig().getMongoDBConnection().getChannels(), null);
		cfg.load(new InputStreamReader(Channels.getInstance().getResourceAsStream("channel.yml")));
		cfg.set("uuid", uuid.toString());

		save();
	}

	public void addServer(String servername) {
		List<String> servers = getServers();
		servers.add(servername);

		cfg.set("servers", servers);
	}

	public void removeServer(String servername) {
		List<String> servers = getServers();
		servers.remove(servername);

		cfg.set("servers", servers);
	}

	public void addModerator(String uuid) {
		List<String> moderators = getModerators();
		moderators.add(uuid);

		cfg.set("moderators", moderators);
	}

	public void removeModerator(String modUUID) {
		List<String> moderators = getModerators();
		moderators.remove(modUUID);

		cfg.set("moderators", moderators);
	}

	public void setAutojoin(boolean b) {
		cfg.set("autojoin", b);
	}

	public boolean doAutojoin() {
		return cfg.getBoolean("autojoin", false);
	}

	public void addBan(String chatterUUID) {
		List<String> bans = getBans();
		bans.add(chatterUUID);

		cfg.set("bans", bans);
	}

	public void removeBan(String chatterUUID) {
		List<String> bans = getBans();
		bans.remove(chatterUUID);

		cfg.set("bans", bans);
	}

	public void setColor(ChatColor color) {
		cfg.set("color", color.name());
	}

	public void setGlobal(boolean global) {
		cfg.set("global", global);
	}

	public boolean isGlobal() {
		return cfg.getBoolean("global", true);
	}

	public void setBackend(boolean backend) {
		cfg.set("backend", backend);
	}

	public boolean isBackend() {
		return cfg.getBoolean("backend", false);
	}

	public void setAutofocus(boolean autofocus) {
		cfg.set("autofocus", autofocus);
	}

	public boolean doAutofocus() {
		return cfg.getBoolean("autofocus", false);
	}
}
