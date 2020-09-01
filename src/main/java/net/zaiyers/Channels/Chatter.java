package net.zaiyers.Channels;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.config.ChatterConfig;
import net.zaiyers.Channels.config.ChatterMongoConfig;
import net.zaiyers.Channels.config.ChatterYamlConfig;
import net.zaiyers.Channels.events.ChatterAfkEvent;
import net.zaiyers.Channels.events.ChatterDndEvent;
import net.zaiyers.Channels.message.Message;

public class Chatter {
	/**
	 * player object of the chatter
	 */
	private ProxiedPlayer player;

	/**
	 * afk status
	 */
	private boolean afk = false;

	/**
	 * afk notice
	 */
	private String afkMessage = "";

	/**
	 * dnd status
	 */
	private boolean dnd = false;

	/**
	 * dnd notice
	 */
	private String dndMessage = "";

	/**
	 * configuration for this guy
	 */
	private ChatterConfig cfg;

	/**
	 * uuid of the recipient for private messages
	 */
	private String privateRecipient = null;

	/**
	 * @param player
	 * @throws IOException
	 */
	public Chatter(ProxiedPlayer player) throws IOException {
		this.player = player;

		// load my preferences
		String uuid = player.getUniqueId().toString();
		if (Channels.getConfig().getMongoDBConnection() != null && Channels.getConfig().getMongoDBConnection().isAvilable()) {
			cfg = new ChatterMongoConfig(Channels.getConfig().getMongoDBConnection().getChatters(), uuid);
		} else {
			cfg = new ChatterYamlConfig(
					new File(Channels.getInstance().getDataFolder(),
							("chatters" + File.separator
									+ uuid.substring(0, 2) + File.separator
									+ uuid.substring(2, 4) + File.separator
									+ uuid + ".yml"
							).toLowerCase()
					)
			);
		}
	}

	/**
	 * return my subscriptions
	 * @return
	 */
	public List<String> getSubscriptions() {
		List<String> subscriptions = cfg.getSubscriptions();

		if (subscriptions.size() == 0) {
			// lets add this poor guy to the default channel
			subscriptions.add(Channels.getConfig().getDefaultChannelUUID());
		}

		return subscriptions;
	}

	/**
	 * subscribes to a channel
	 * @param chan
	 */
	public void subscribe(Channel chan) {
		// add subscription to config
		List<String> subs = cfg.getSubscriptions();
		if (!subs.contains(chan.getUUID())) {
			subs.add(chan.getUUID());

			cfg.setSubscriptions(subs);
		}
		// subscribe to channel
		chan.subscribe(this);

		if (chan.doAutofocus() && privateRecipient == null && !chan.getUUID().equals(getChannel()) && !Channels.getInstance().getChannel(getChannel()).doAutofocus()) {
			setDefaultChannelUUID(chan.getUUID());
			Channels.notify(player, "channels.chatter.default-channel-set", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
		}
	}

	/**
	 * unsubscribe from a  channel
	 * @param uuid
	 */
	public void unsubscribe(String uuid) {
		// remove subscription from config
		List<String> subs = cfg.getSubscriptions();
		subs.remove(uuid);

		cfg.setSubscriptions(subs);

		// if removed channel is currently focused one switch to server default channel
		if (uuid.equals(getChannel())) {
			Channel chan = null;
			if (player != null && player.getServer() != null) {
				chan = Channels.getInstance().getChannel(Channels.getConfig().getServerDefaultChannel(player.getServer().getInfo().getName()));
			}

			if (chan == null) {
				// server doesn't have default channel
				for (String channelId : subs) {
					// search for one in his subscription that he can speak in
					chan = Channels.getInstance().getChannel(channelId);
					if (chan.doAutojoin() && !chan.isTemporary() && hasPermission(chan, "speak")) {
						// he can speak in the channel! Yay \o/
						break;
					}
					// can't speak in channel, reset to null
					chan = null;
				}
			}

			if (chan != null && hasPermission(chan, "subscribe")) {
				setDefaultChannelUUID(chan.getUUID());
				subscribe(chan);
				if (getLastRecipient() == null) {
					Channels.notify(getPlayer(), "channels.chatter.default-channel-set", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
				}
			} // player is screwed and wont be able to speak
		}

		// unsubscribe from channel
		if (Channels.getInstance().getChannel(uuid) != null) {
			Channels.getInstance().getChannel(uuid).unsubscribe(this);
		}
	}

	/**
	 * am I muted?
	 * @return
	 */
	public boolean isMuted() {
		return cfg.isMuted();
	}

	/**
	 * people I don't want to read
	 */
	public List<String> getIgnores() {
		return cfg.getIgnores();
	}

	/**
	 * get my prefix
	 */
	public String getPrefix() {
		if (cfg.getPrefix() == null || cfg.getPrefix().isEmpty()) {
			// No prefix in chatter config, try to query from other plugins
			if (Channels.getLuckPermsApi() != null) {
				CachedMetaData metaData = getMetaData();
				if (metaData != null && metaData.getPrefix() != null) {
					return metaData.getPrefix();
				}
			}
		}
		return cfg.getPrefix();
	}

	/**
	 * get my suffix
	 */
	public String getSuffix() {
		if (cfg.getSuffix() == null || cfg.getSuffix().isEmpty()) {
			// No suffix in chatter config, try to query from other plugins
			if (Channels.getLuckPermsApi() != null) {
				CachedMetaData metaData = getMetaData();
				if (metaData != null && metaData.getSuffix() != null) {
					return metaData.getSuffix();
				}
			}
		}
		return cfg.getSuffix();
	}

	private CachedMetaData getMetaData() {
		User lpUser = Channels.getLuckPermsApi().getUserManager().getUser(player.getUniqueId());
		if (lpUser != null) {
			ContextSet contexts = Channels.getLuckPermsApi().getContextManager().getContext(lpUser).orElse(null);
			if (contexts != null) {
				return lpUser.getCachedData().getMetaData(QueryOptions.contextual(contexts));
			}
		}
		return null;
	}

	/**
	 * person who last wrote me
	 */
	public String getLastSender() {
		return cfg.getLastSender();
	}

	/**
	 * person I last wrote to
	 */
	public String getLastRecipient() {
		return privateRecipient;
	}

	/**
	 * channel I'm writing in
	 */
	public String getChannel() {
		String channelUUID = cfg.getChannelUUID();
		if (Channels.getInstance().getChannel(channelUUID) == null) {
			channelUUID = Channels.getConfig().getDefaultChannelUUID();
		} // channel was removed

		return channelUUID;
	}

	/**
	 * get my name
	 */
	public String getName() {
		return player.getName();
	}

	/**
	 * check my permissions for this channel
	 * @param channel
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(Channel channel, String permission) {
		return player.hasPermission("channels." + permission + "." + channel.getTag());
	}

	/**
	 * check if I have this permission
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(String permission) {
		return player.hasPermission(permission);
	}

	/**
	 * this chatters player instance
	 * @return
	 */
	public ProxiedPlayer getPlayer() {
		return player;
	}

	/**
	 * save config to disk
	 */
	public void save() {
		cfg.save();
	}

	/**
	 * sends me a message
	 * @param message
	 */
	public void sendMessage(Message message) {
		player.sendMessage(ChatMessageType.CHAT, message.getProcessedMessage());
	}

	/**
	 * set default channel to speak in
	 * @param string
	 */
	public void setDefaultChannelUUID(String string) {
		cfg.setDefaultChannel(string);
	}

	/**
	 * toggle mute status
	 * @param b
	 */
	public void setMuted(boolean b) {
		cfg.setMuted(b);
	}

	/**
	 * set chatter prefix
	 * @param prefix
	 */
	public void setPrefix(String prefix) {
		cfg.setPrefix(prefix);
	}

	/**
	 * set chatter suffix
	 * @param suffix
	 */
	public void setSuffix(String suffix) {
		cfg.setSuffix(suffix);
	}

	/**
	 * set recipient for private messages
	 * @param recipientUUID
	 */
	public void setPrivateRecipient(String recipientUUID) {
		privateRecipient = recipientUUID;
	}

	/**
	 * set last private message sender
	 * @param chatter
	 */
	public void setLastPrivateSender(Chatter chatter) {
		cfg.setLastSender(chatter.getPlayer().getUniqueId().toString());
	}

	/**
	 * returns true if chatter is afk
	 * @return
	 */
	public boolean isAFK() {
		return afk;
	}

	/**
	 * change chatter afk status
	 * @param isAfk
	 * @param afkMessage
	 */
	public void setAFK(boolean isAfk, String afkMessage) {
		ChatterAfkEvent event = new ChatterAfkEvent(this, isAfk, afkMessage);
		Channels.getInstance().getProxy().getPluginManager().callEvent(event);
		afk = isAfk;
		this.afkMessage = event.getMessage();
	}

	/**
	 * returns true if chatter is in dnd
	 * @return
	 */
	public boolean isDND() {
		return dnd;
	}

	/**
	 * change chatter dnd status
	 * @param isDnd
	 * @param dndMsg
	 */
	public void setDND(boolean isDnd, String dndMsg) {
		ChatterDndEvent event = new ChatterDndEvent(this, isDnd, dndMsg);
		Channels.getInstance().getProxy().getPluginManager().callEvent(event);
		dnd = isDnd;
		dndMessage = event.getMessage();
	}

	/**
	 * get afk message
	 * @return
	 */
	public String getAFKMessage() {
		return afkMessage;
	}

	/**
	 * get dnd message
	 * @return
	 */
	public String getDNDMessage() {
		return dndMessage;
	}

	/**
	 * send raw message
	 * @param string
	 */
	public void sendMessage(String string) {
		player.sendMessage(TextComponent.fromLegacyText(string));
	}

	/**
	 * Send a text component
	 * @param textComponent
	 */
	public void sendMessage(TextComponent textComponent) {
		player.sendMessage(textComponent);
	}

	public void removeIgnore(String ignoreUUID) {
		cfg.removeIgnore(ignoreUUID);
	}

	public void addIgnore(String uuid) {
		cfg.addIgnore(uuid);
	}
}
