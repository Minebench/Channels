package net.zaiyers.Channels;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.config.ChatterConfig;
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
	 * we talk in this one by default
	 */
	private UUID defaultChannel;
	
	/**
	 * uuid of the recipient for private messages
	 */
	private String privateRecipient = null; 
	
	/**
	 * uuid of chatter who last wrote to me
	 */
	private String lastPrivateSender = null;
	
	/**
	 * 
	 * @param player
	 * @throws IOException
	 */
	public Chatter(ProxiedPlayer player) throws IOException {
		this.player = player;
		
		// load my preferences
		String uuid = player.getUUID();
		
		String cfgPath = Channels.getInstance().getDataFolder()+("/chatters/"+uuid.substring(0,2)+"/"+uuid.substring(2,4)+"/"+uuid+".yml").toLowerCase();
		cfg	= new ChatterConfig(cfgPath);
	}
	
	/**
	 * return my subscriptions
	 * @return
	 */
	public List<UUID> getSubscriptions() {
		List<UUID> subscriptions = cfg.getSubscriptions();
		
		if (subscriptions.size() == 0) {
			// lets add this poor guy to the default channel
			subscriptions.add(Channels.getConfig().getDefaultChannelUUID());
		}
		
		return subscriptions;
	}
	
	/**
	 * subscribes to a channel
	 * @param uuid
	 */
	public void subscribe(UUID uuid) {
		// add subscription to config
		List<UUID> subs = cfg.getSubscriptions();
		subs.add(uuid);
		
		cfg.setSubscriptions(subs);
		
		// subscribe to channel
		Channels.getInstance().getChannel(uuid).subscribe(this);
	}
	
	/**
	 * unsubscribe from a  channel
	 * 
	 * @param uuid
	 */
	public void unsubscribe(UUID uuid) {
		// remove subscription from config
		List<UUID> subs = cfg.getSubscriptions();
		subs.remove(uuid);
		
		cfg.setSubscriptions(subs);
		
		// unsubscribe from channel
		Channels.getInstance().getChannel(uuid).unsubscribe(this);
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
		return cfg.getPrefix();
	}
	
	/**
	 * get my suffix
	 */
	public String getSuffix() {
		return cfg.getSuffix();
	}
	
	/**
	 * person who last wrote me
	 */
	public String getLastSender() {
		String lastSender = cfg.getLastSender();
		
		if (Channels.getInstance().getChatter(lastSender) == null) { lastSender = null; } // not online :(
		return lastSender;
	}
	
	/**
	 * person I last wrote to
	 */
	public String getLastRecipient() {
		String lastRecipient = cfg.getLastRecipient();
		if (Channels.getInstance().getChatter(lastRecipient) == null) { lastRecipient = null; } // not online :(
		
		return lastRecipient;
	}
	
	/**
	 * channel I'm writing in
	 */
	public UUID getChannel() {
		UUID channelUUID = cfg.getChannelUUID();
		if (Channels.getInstance().getChannel(channelUUID) == null) { channelUUID = Channels.getConfig().getDefaultChannelUUID(); } // channel was removed
		
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
	 * 
	 * @param channel
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(Channel channel, String permission) {
		return player.hasPermission("channels."+channel.getName()+"."+permission);
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
		player.sendMessage(message.getProcessedMessage());
	}

	/**
	 * set default channel to speak in
	 * @param uuid
	 */
	public void setDefaultChannelUUID(UUID uuid) {
		defaultChannel = uuid;		
	}
	
	/**
	 * get my default channel
	 * @return
	 */
	public UUID getDefaultChannelUUID() {
		return defaultChannel;
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
		lastPrivateSender = chatter.getPlayer().getUUID();
	}
}
