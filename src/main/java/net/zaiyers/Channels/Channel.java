package net.zaiyers.Channels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.ChatColor;
import net.zaiyers.Channels.config.ChannelConfig;
import net.zaiyers.Channels.config.ChannelMongoConfig;
import net.zaiyers.Channels.config.ChannelYamlConfig;
import net.zaiyers.Channels.message.ChannelMessage;
import net.zaiyers.Channels.message.ConsoleMessage;
import net.zaiyers.bungee.UUIDDB.UUIDDB;

public class Channel {
	/**
	 * channel is temporary
	 */
	private boolean temporary = false;
	
	/**
	 * list of subscribers
	 */
	private ArrayList<String> subscribers = new ArrayList<String>();
	
	/**
	 * channel configuration
	 */
	private ChannelConfig cfg;
		
	/**
	 * add subscriber
	 */
	public void subscribe(Chatter chatter) {
		if (cfg.getBans().contains(chatter.getPlayer().getUniqueId().toString())) {
			Channels.notify(chatter.getPlayer(), "banned-from-channel", ImmutableMap.of("channel", getName(), "channelColor", getColor().toString()));
			chatter.unsubscribe(getUUID());
			return;
		}
		
		if (!subscribers.contains(chatter.getPlayer().getUniqueId().toString())) {
			subscribers.add(chatter.getPlayer().getUniqueId().toString());
		}
	}
	
	/**
	 * remove subscriber
	 */
	public void unsubscribe(Chatter chatter) {
		subscribers.remove(chatter.getPlayer().getUniqueId().toString());
		
		// delete empty and temporary channels
		if (temporary && subscribers.size() == 0) {
			Channels.getInstance().removeChannel(cfg.getUUID());
		}
	}
	
	/**
	 * load channel by uuid
	 * @param name
	 * @throws IOException 
	 */
	public Channel(String uuid) throws IOException {
		String cfgFilePath = Channels.getInstance().getDataFolder().getAbsolutePath()+File.separatorChar+"channels"+File.separatorChar+uuid+".yml";
		if (Channels.getConfig().getMongoDBConnection() != null && Channels.getConfig().getMongoDBConnection().isAvilable()) {
			cfg = new ChannelMongoConfig(Channels.getConfig().getMongoDBConnection().getChannels(), uuid);
		} else {
			cfg = new ChannelYamlConfig(cfgFilePath);
		}
	}
	
	/**
	 * name of this channel
	 * 
	 * @return
	 */
	public String getName() {
		return cfg.getName();
	}

	/**
	 * return channel id
	 * @return
	 */
	public String getUUID() {
		return cfg.getUUID();
	}
	
	/**
	 * send message to subscribers
	 * 
	 * @param channelMessage
	 */
	public void send(ChannelMessage message) {
		Chatter sender = message.getChatter();
		if (sender.isMuted()) {
			// notify and return
			Channels.notify(sender.getPlayer(), "channels.chatter.is-muted");
			return;
		}
		String serverName = sender.getPlayer().getServer().getInfo().getName();
		if (!cfg.isGlobal() && !sender.hasPermission(this, "globalread") && !cfg.getServers().contains(serverName)) {
			Channels.notify(sender.getPlayer(), "channels.command.channel-not-available", ImmutableMap.of("channelColor", getColor().toString(), "channel", getName(), "server", serverName));
			return;
		}
		
		for (String uuid: subscribers) {
			Chatter reciever = Channels.getInstance().getChatter(uuid);
			if (reciever.getIgnores().contains(sender.getPlayer().getUniqueId().toString())) {
				// I don't want to read this message
				continue;
			} else if (!cfg.isGlobal() && !reciever.hasPermission(this, "globalread") && !cfg.getServers().contains(reciever.getPlayer().getServer().getInfo().getName())) {
				// channel is not distributed to this players server
				continue;
			}
			
			// send the message
			reciever.sendMessage(message);
		}
	}
	
	/**
	 * send console message to channel
	 * @param consoleMessage
	 */
	public void send(ConsoleMessage consoleMessage) {
		for (String uuid: subscribers) {
			Chatter reciever = Channels.getInstance().getChatter(uuid);
			if (!cfg.isGlobal() && !reciever.hasPermission(this, "globalread") && !cfg.getServers().contains(reciever.getPlayer().getServer().getInfo().getName())) {
				// channel is not distributed to this players server
				continue;
			}
			
			// send the message
			reciever.sendMessage(consoleMessage);
		}
	}

	/**
	 * get message format
	 * @return
	 */
	public String getFormat() {
		return cfg.getFormat();
	}

	/**
	 * get message color
	 * @return
	 */
	public ChatColor getColor() {
		return cfg.getColor();
	}

	/**
	 * get channel tag
	 * @return
	 */
	public String getTag() {
		return cfg.getTag();
	}

	/**
	 * set new channel name
	 * 
	 * @param string
	 */
	public void setName(String name) {
		cfg.setName(name);
	}
	
	/**
	 * set new channel tag
	 * 
	 * @param string
	 */
	public void setTag(String tag) {
		cfg.setTag(tag);
	}
	
	/**
	 * set new channel password
	 * 
	 * @param String
	 */
	public void setPassword(String password) {
		cfg.setPassword(password);
	}
	
	/**
	 * write channel configuration to disk
	 */
	public void save() {
		if (!temporary) {
			cfg.save();
		}
	}

	/**
	 * get channel cleartext password
	 * 
	 * @return
	 */
	public String getPassword() {
		return cfg.getPassword();
	}

	/**
	 * channel is temporary
	 * @return
	 */
	public boolean isTemporary() {
		return temporary;
	}

	/**
	 * format for console messages
	 * @return
	 */
	public String getConsoleFormat() {
		return cfg.getConsoleFormat();
	}

	/**
	 * toggle global status
	 * @param global
	 */
	public void setGlobal(boolean global) {
		cfg.setGlobal(global);
	}
	
	/**
	 * add server to distribute to
	 */
	public void addServer(String servername) {
		cfg.addServer(servername);
	}

	/**
	 * remove server from distribute list
	 * @param servername
	 */
	public void removeServer(String servername) {
		cfg.removeServer(servername);
	}

	/**
	 * toggle temporary status
	 * @param b
	 */
	public void setTemporary(boolean b) {
		temporary = b;
	}

	/**
	 * add chatter as moderator
	 * @param sender
	 */
	public void addModerator(String uuid) {
		cfg.addModerator(uuid);
	}

	/**
	 * check if uuid is in moderators
	 * @param uuid
	 * @return
	 */
	public boolean isMod(String uuid) {
		return cfg.getModerators().contains(uuid);
	}

	/**
	 * get list of moderator uuids
	 * @return
	 */
	public List<String> getModerators() {
		return cfg.getModerators();
	}

	/**
	 * remove moderator from channel
	 * @param modUUID
	 */
	public void removeModerator(String modUUID) {
		cfg.removeModerator(modUUID);
	}

	/**
	 * toggle autojoin behavior
	 * @param b
	 */
	public void setAutojoin(boolean b) {
		cfg.setAutojoin(b);
	}
	
	/**
	 * get autojoin behavior
	 * @return
	 */
	public boolean doAutojoin() {
		return cfg.doAutojoin();
	}

	/**
	 * ban and kick player from channel
	 * @param chatterUUID
	 */
	public void banChatter(String chatterUUID) {
		cfg.addBan(chatterUUID);
		
		Chatter chatter = Channels.getInstance().getChatter(chatterUUID);
		if (chatter != null) {
			chatter.unsubscribe(getUUID());
			Channels.notify(chatter.getPlayer(), "channels.chatter.banned-me-from-channel", ImmutableMap.of("channelColor", getColor().toString(), "channel", getName()));
		}
		
		// announce
		String banned = UUIDDB.getInstance().getNameByUUID(chatterUUID);
		for (String subscriber: subscribers) {
			Channels.notify(Channels.getInstance().getChatter(subscriber).getPlayer(), "channels.chatter.banned-from-channel", ImmutableMap.of(
					"chatter", banned,
					"channelColor", getColor().toString(),
					"channel", getName()
			));
		}
	}
	
	/**
	 * kick chatter from channel
	 * @param chatterUUID
	 */
	public void kickChatter(String chatterUUID) {
		Chatter chatter = Channels.getInstance().getChatter(chatterUUID);
		if (chatter != null) {
			chatter.unsubscribe(getUUID());
			Channels.notify(chatter.getPlayer(), "channels.chatter.kicked-me-from-channel", ImmutableMap.of("channelColor", getColor().toString(), "channel", getName()));
		}
		
		// announce
		for (String subscriber: subscribers) {
			Channels.notify(Channels.getInstance().getChatter(subscriber).getPlayer(), "channels.chatter.kicked-from-channel", ImmutableMap.of(
					"chatter", UUIDDB.getInstance().getNameByUUID(chatterUUID),
					"channelColor", getColor().toString(),
					"channel", getName()
			));
		}
	}

	/**
	 * remove chatter from channel banlist
	 * @param chatterUUID
	 */
	public void unbanChatter(String chatterUUID) {
		cfg.removeBan(chatterUUID);
		
		// announce
		String banned = UUIDDB.getInstance().getNameByUUID(chatterUUID);
		for (String subscriber: subscribers) {
			Channels.notify(Channels.getInstance().getChatter(subscriber).getPlayer(), "channels.chatter.unbanned-from-channel", ImmutableMap.of(
					"chatter", banned,
					"channelColor", getColor().toString(),
					"channel", getName()
				));
		}
	}

	public void setColor(ChatColor color) {
		cfg.setColor(color);
	}

	/**
	 * returns true if channel is global
	 * @return
	 */
	public boolean isGlobal() {
		return cfg.isGlobal();
	}

	public List<String> getServers() {
		return cfg.getServers();
	}

	/**
	 * get list of channel subscriber uuids
	 * @return
	 */
	public List<String> getSubscribers() {
		return subscribers;
	}

	public List<String> getBans() {
		return cfg.getBans();
	}

	public void removeChannel() {
		ImmutableMap<String, String> replacements = ImmutableMap.of("channel", getName(), "channelColor", getColor().toString());
		for (String subscriberUUID: getSubscribers()) {
			Channels.notify(Channels.getInstance().getChatter(subscriberUUID).getPlayer(), "channels.command.channel-removed", replacements);
		}
		cfg.removeConfig();
	}
}
