package net.zaiyers.Channels.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;

import net.kyori.adventure.text.format.TextColor;
import net.zaiyers.Channels.Channels;
import org.spongepowered.configurate.ConfigurateException;

public class ChannelYamlConfig extends YamlConfig implements ChannelConfig {

	/**
	 * load configuration from disk
	 * @param configFile
	 * @throws IOException
	 */
	public ChannelYamlConfig(File configFile) throws IOException {
		super(configFile);
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

	public TextColor getColor() {
		return Channels.parseTextColor(cfg.getString("color"));
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
		return configFile.getName().substring(0, 36);
	}

	public void createDefaultConfig() {
		cfg = new Configuration();
		try {
			cfg.load(new InputStreamReader(Channels.getInstance().getResourceAsStream("channel.yml")));
		} catch (ConfigurateException e) {
			Channels.getInstance().getLogger().log(Level.SEVERE, "Unable to load default channel configuration from jar", e);
		}

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

	public void setColor(TextColor color) {
		cfg.set("color", color.toString());
	}

	public void setGlobal(boolean global) {
		cfg.set("global", global);
	}

	public boolean isGlobal() {
		return cfg.getBoolean("global");
	}

	public void setBackend(boolean backend) {
		cfg.set("backend", backend);
	}

	public boolean isBackend() {
		return cfg.getBoolean("backend");
	}

	public void setAutofocus(boolean autofocus) {
		cfg.set("autofocus", autofocus);
	}

	public boolean doAutofocus() {
		return cfg.getBoolean("autofocus", false);
	}

}