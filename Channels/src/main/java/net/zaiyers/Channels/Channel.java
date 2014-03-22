package net.zaiyers.Channels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.zaiyers.Channels.config.ChannelConfig;
import net.zaiyers.Channels.message.ChannelMessage;

public class Channel {
	/**
	 * channel is temporary
	 */
	private boolean temporary = false;

	/**
	 * channel is global
	 */
	private boolean global = true;
	
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
		if (!subscribers.contains(chatter.getPlayer().getUUID())) {
			subscribers.add(chatter.getPlayer().getUUID());
		}
	}
	
	/**
	 * remove subscriber
	 */
	public void unsubscribe(Chatter chatter) {
		subscribers.remove(chatter.getPlayer().getUUID());
		
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
	public Channel(UUID uuid) throws IOException {	
		String cfgFilePath = Channels.getInstance().getDataFolder().getAbsolutePath()+File.separatorChar+"channels"+File.separatorChar+uuid+".yml";
		cfg = new ChannelConfig(cfgFilePath);
	}
	
	/**
	 * load channel by config file
	 * @param filename
	 * @throws IOException 
	 */
	public Channel(File cfgFile) throws IOException {	
		cfg = new ChannelConfig(cfgFile.getAbsolutePath());
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
	public UUID getUUID() {
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
		for (String uuid: subscribers) {
			Chatter reciever = Channels.getInstance().getChatter(uuid);
			if (reciever.getIgnores().contains(sender.getPlayer().getUUID())) {
				// I don't want to read this message
				continue;
			} else if (!global && !reciever.hasPermission(this, "globalread") && !cfg.getServers().contains(reciever.getPlayer().getServer().getInfo().getName())) {
				// channel is not distributed to this players server
				continue;
			}
			
			// send the message
			reciever.sendMessage(message);
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
		this.global = true;
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
	public void addModerator(Chatter chatter) {
		cfg.addModerator(chatter.getPlayer().getUUID());
	}
}
