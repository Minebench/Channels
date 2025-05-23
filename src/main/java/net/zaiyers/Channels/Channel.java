package net.zaiyers.Channels;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import net.kyori.adventure.text.format.TextColor;
import net.zaiyers.Channels.config.ChannelConfig;
import net.zaiyers.Channels.config.ChannelYamlConfig;
import net.zaiyers.Channels.message.ChannelMessage;
import net.zaiyers.Channels.message.ConsoleMessage;
import net.zaiyers.Channels.message.Message;

public class Channel {
	/**
	 * channel is temporary
	 */
	private boolean temporary = false;
	
	/**
	 * list of subscribers
	 */
	private Set<UUID> subscribers = new HashSet<>();
	
	/**
	 * channel configuration
	 */
	private ChannelConfig cfg;
		
	/**
	 * add subscriber
	 */
	public void subscribe(Chatter chatter) {
		if (cfg.getBans().contains(chatter.getPlayer().getUniqueId().toString())) {
			Channels.notify(chatter.getPlayer(), "channels.chatter.banned-from-channel", ImmutableMap.of("channel", getName(), "channelColor", getColor().toString()));
			chatter.unsubscribe(getUUID());
			return;
		}
		
		if (!subscribers.contains(chatter.getPlayer().getUniqueId())) {
			subscribers.add(chatter.getPlayer().getUniqueId());
		}
	}
	
	/**
	 * remove subscriber
	 */
	public void unsubscribe(Chatter chatter) {
		unsubscribe(chatter.getPlayer().getUniqueId());
	}

	private void unsubscribe(UUID subscriberId) {
		subscribers.remove(subscriberId);
		
		// delete empty and temporary channels
		if (temporary && subscribers.size() == 0) {
			Channels.getInstance().removeChannel(cfg.getUUID());
		}
	}
	
	/**
	 * load channel by uuid
	 * @param uuid
	 * @throws IOException 
	 */
	public Channel(String uuid) throws IOException {
		cfg = new ChannelYamlConfig(new File(Channels.getInstance().getDataFolder().getAbsolutePath(), "channels" + File.separatorChar + uuid + ".yml"));
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
	 * @param message
	 * @param hidden
	 */
	public void send(ChannelMessage message, boolean hidden) {
		Chatter sender = message.getChatter();
		if (sender.isMuted()) {
			// notify and return
			Channels.notify(sender.getPlayer(), "channels.chatter.is-muted");
			return;
		}
		if (sender.getPlayer().getCurrentServer().isPresent()) {
			String serverName = sender.getPlayer().getCurrentServer().get().getServerInfo().getName();
			if (!cfg.isGlobal() && !sender.hasPermission(this, "globalread") && !cfg.getServers().contains(serverName)) {
				Channels.notify(sender.getPlayer(), "channels.command.channel-not-available", ImmutableMap.of("channelColor", getColor().toString(), "channel", getName(), "server", serverName));
				return;
			}
		}

		Chatter messageSender = sender.hasPermission("channels.bypass.ignore") ? null: sender;

		send(messageSender, message, hidden);
	}
	
	/**
	 * send console message to channel
	 * @param consoleMessage
	 */
	public void send(ConsoleMessage consoleMessage, boolean hidden) {
		send(null, consoleMessage, hidden);
	}

	/**
	 * send a message to channel
	 * @param messageSender
	 * @param message
	 * @param hidden
	 */
	public void send(Chatter messageSender, Message message, boolean hidden) {
		List<UUID> subCur = new ArrayList<>(subscribers);
		for (UUID uuid : subCur) {
			Chatter receiver = Channels.getInstance().getChatter(uuid);
			if (receiver != null && receiver.getPlayer() != null) {
				if (messageSender != null && receiver.getIgnores().contains(messageSender.getPlayer().getUniqueId().toString())) {
					// I don't want to read this message
					continue;
				} else if (!cfg.isGlobal() && !receiver.hasPermission(this, "globalread")
						&& receiver.getPlayer().getCurrentServer().isPresent()
						&& !cfg.getServers().contains(receiver.getPlayer().getCurrentServer().get().getServerInfo().getName())) {
					// channel is not distributed to this player's server
					continue;
				}

				if (hidden && messageSender != receiver) {
					continue;
				}

				if (receiver.canSeeChat()) {
					// send the message
					receiver.sendMessage(messageSender, message);
				}
			} else {
				unsubscribe(uuid);
			}
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
	public TextColor getColor() {
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
	 * @param name
	 */
	public void setName(String name) {
		cfg.setName(name);
	}
	
	/**
	 * set new channel tag
	 * 
	 * @param tag
	 */
	public void setTag(String tag) {
		cfg.setTag(tag);
	}
	
	/**
	 * set the channel format
	 *
	 * @param format new format
	 */
	public void setFormat(String format) {
		cfg.setFormat(format.replace("\\n", "\n"));
	}
	
	/**
	 * set new channel password
	 * 
	 * @param password
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
	 * toggle global status
	 * @param global
	 */
	public void setGlobal(boolean global) {
		cfg.setGlobal(global);
	}

    /**
     * toggle backend status
     * @param backend
     */
    public void setBackend(boolean backend) {
        cfg.setBackend(backend);
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
	 * @param uuid
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
	 * toggle autofocus behavior
	 * @param b
	 */
	public void setAutofocus(boolean b) {
		cfg.setAutofocus(b);
	}

	/**
	 * get autofocus behavior
	 * @return
	 */
	public boolean doAutofocus() {
		return cfg.doAutofocus();
	}

	/**
	 * ban and kick player from channel
	 * @param chatterUUID
	 */
	public void banChatter(UUID chatterUUID) {
		cfg.addBan(chatterUUID.toString());
		
		Chatter chatter = Channels.getInstance().getChatter(chatterUUID);
		if (chatter != null) {
			chatter.unsubscribe(getUUID());
			Channels.notify(chatter.getPlayer(), "channels.chatter.banned-me-from-channel", ImmutableMap.of("channelColor", getColor().toString(), "channel", getName()));
		}
		
		// announce
		String banned = Channels.getPlayerName(chatterUUID);
		notifySubscribers("channels.chatter.banned-from-channel", ImmutableMap.of(
				"chatter", banned,
				"channelColor", getColor().toString(),
				"channel", getName()
		));
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
		notifySubscribers("channels.chatter.kicked-from-channel", ImmutableMap.of(
				"chatter", Channels.getPlayerName(chatterUUID),
				"channelColor", getColor().toString(),
				"channel", getName()
		));
	}

	/**
	 * remove chatter from channel banlist
	 * @param chatterUUID
	 */
	public void unbanChatter(UUID chatterUUID) {
		cfg.removeBan(chatterUUID.toString());
		
		// announce
		String banned = Channels.getPlayerName(chatterUUID);
		for (UUID subscriber : subscribers) {
			notifySubscribers("channels.chatter.unbanned-from-channel", ImmutableMap.of(
						"chatter", banned,
						"channelColor", getColor().toString(),
						"channel", getName()
				));
		}
	}

	public void setColor(TextColor color) {
		cfg.setColor(color);
	}

	/**
	 * returns true if channel is global
	 * @return
	 */
	public boolean isGlobal() {
		return cfg.isGlobal();
	}

    /**
     * Check if this channel should send to the Server behind the bungee rather then to a Channels channel
     * @return True if we should send there, false if not
     */
    public boolean isBackend() {
        return cfg.isBackend();
    }
    
	public List<String> getServers() {
		return cfg.getServers();
	}

	/**
	 * get list of channel subscriber uuids
	 * @return
	 * @deprecated Use {@link #getSubscriberUUIDs()}
	 */
	@Deprecated
	public List<String> getSubscribers() {
		return subscribers.stream().map(UUID::toString).collect(Collectors.toList());
	}

	/**
	 * get list of channel subscriber uuids
	 * @return
	 */
	public Collection<UUID> getSubscriberUUIDs() {
		return subscribers;
	}

	public List<String> getBans() {
		return cfg.getBans();
	}

	public void removeChannel() {
		notifySubscribers("channels.command.channel-removed", ImmutableMap.of("channel", getName(), "channelColor", getColor().toString()));
		cfg.removeConfig();
	}

	private void notifySubscribers(String messageKey, Map<String, String> replacements) {
		for (UUID subscriberUUID : subscribers) {
			Chatter chatter = Channels.getInstance().getChatter(subscriberUUID);
			if (chatter != null && chatter.getPlayer() != null) {
				Channels.notify(chatter.getPlayer(), messageKey, replacements);
			} else {
				unsubscribe(subscriberUUID);
			}
		}
	}
}
