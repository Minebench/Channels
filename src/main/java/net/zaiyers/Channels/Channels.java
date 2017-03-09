package net.zaiyers.Channels;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import de.themoep.vnpbungee.VNPBungee;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.zaiyers.Channels.command.ChannelTagCommandExecutor;
import net.zaiyers.Channels.command.ChannelsCommandExecutor;
import net.zaiyers.Channels.config.ChannelsConfig;
import net.zaiyers.Channels.config.LanguageConfig;
import net.zaiyers.Channels.listener.MessageListener;
import net.zaiyers.Channels.listener.PlayerJoinListener;
import net.zaiyers.Channels.listener.PlayerQuitListener;
import net.zaiyers.Channels.listener.ServerSwitchListener;
import net.zaiyers.UUIDDB.bungee.UUIDDB;
import net.zaiyers.UUIDDB.core.UUIDDBPlugin;

public class Channels extends Plugin {
    private static Channels instance;

    /**
	 * List of Chatters
	 */
	private Map<UUID, Chatter> chatters = new HashMap<>();
	
	/**
	 * channels configuration
	 */
	private static ChannelsConfig config;
	
	/**
	 * language configuration
	 */
	private static LanguageConfig lang;
	
	/**
	 * list of channels
	 */
	private Map<String, Channel> channels = new HashMap<>();
	
	/**
	 * command executors for channel tags
	 */
	private Map<String, ChannelTagCommandExecutor> tagCommandExecutors = new HashMap<>();

    /**
     * Soft depend on LuckPerms
     */
    private static LuckPermsApi luckPermsApi = null;

    private static UUIDDBPlugin uuidDb = null;

	private static VNPBungee vnpBungee = null;

	/**
	 * executed on startup
	 */
	public void onEnable() {
        instance = this;

		// load configuration
		try {
			config = new ChannelsConfig(getDataFolder()+"/config.yml");
		} catch (IOException e) {
			getLogger().severe("Unable to load configuration! Channels will not be enabled.");
			e.printStackTrace();
			
			return;
		}
		
		// load language
		try {
			lang = new LanguageConfig(getDataFolder()+"/lang."+config.getLanguage()+".yml");
		} catch (IOException e) {
			getLogger().severe("Unable to load language! Channels will not be enabled.");
			e.printStackTrace();
			
			return;
		}

		if (getProxy().getPluginManager().getPlugin("VNPBungee") != null) {
			vnpBungee = VNPBungee.getInstance();
		}

		if (getProxy().getPluginManager().getPlugin("LuckPerms") != null) {
            luckPermsApi = LuckPerms.getApi();
        }

        if (getProxy().getPluginManager().getPlugin("UUIDDB") != null) {
            uuidDb = (UUIDDBPlugin) getProxy().getPluginManager().getPlugin("UUIDDB");
        }

        if (getUuidDb() == null && getLuckPermsApi() == null) {
            getLogger().severe("You need either LuckPerms or UUIDDB installed for Channels to work! It will not be enabled.");
            return;
        }
		
		// load listeners
		MessageListener ml = new MessageListener();
		PlayerJoinListener pjl = new PlayerJoinListener();
		PlayerQuitListener pql = new PlayerQuitListener();
		ServerSwitchListener swl = new ServerSwitchListener();
		
		// enable listeners
		getProxy().getPluginManager().registerListener(this, ml);
		getProxy().getPluginManager().registerListener(this, pjl);
		getProxy().getPluginManager().registerListener(this, pql);
		getProxy().getPluginManager().registerListener(this, swl);
		
		// register command executors
		getProxy().getPluginManager().registerCommand(this, new ChannelsCommandExecutor("channel", "", new String[] {"ch"}));
		getProxy().getPluginManager().registerCommand(this, new ChannelsCommandExecutor("pm", "", new String[] {"tell", "msg"}));
		getProxy().getPluginManager().registerCommand(this, new ChannelsCommandExecutor("reply", "", new String[] {"r"}));
		getProxy().getPluginManager().registerCommand(this, new ChannelsCommandExecutor("afk", "", new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new ChannelsCommandExecutor("dnd", "", new String[] {}));
		getProxy().getPluginManager().registerCommand(this, new ChannelsCommandExecutor("ignore", "", new String[] {}));
		
		// load and register channels
		for (String channelUUID: config.getChannels()) {
			Channel channel;
			try {
				channel = new Channel(channelUUID);
				channels.put(channel.getUUID(), channel);
				registerTag(channel.getTag());
			} catch (IOException e) {
				getLogger().severe("Couldn't load channel "+channelUUID);
				
				e.printStackTrace();
			}
		}
		
		checkSanity(getProxy().getConsole(), null);
	}
	
	/**
	 * executed on shutdown
	 */
	public void onDisable() {
		// save channel configurations
		for (Channel channel: channels.values()) {
			channel.save();
		}
		
		// save chatter configurations
		for (Chatter chatter: chatters.values()) {
			chatter.save();
		}
		
		config.save();
	}
	
	/**
	 * get the configuration
	 * @return
	 */
	public static ChannelsConfig getConfig() {
		return config;
	}
	
	/**
	 * return myself
	 * @return
	 */
	public static Channels getInstance() {
        return instance;
	}

	/**
	 * remove channel from memory
	 * @param uuid The UUID of the channel
	 */
	public void removeChannel(String uuid) {
		Channel chan = channels.get(uuid);
		if (chan != null) {
			chan.removeChannel();
		}
		channels.remove(uuid);
	}

	public Chatter getChatter(ProxiedPlayer player) {
		Chatter chatter = chatters.get(player.getUniqueId());
		if (chatter == null) {
			chatter = createChatter(player);
		}
		if (chatter == null) {
			getLogger().log(Level.WARNING, "Could not get the chatter for " + player.getName() + "/" + player.getUniqueId() + "?");
			return null;
		}
		chatters.put(player.getUniqueId(), chatter);
		return chatter;
	}

	/**
	 * get chatter object
	 * @param playerId The UUID of the player
	 * @return The player's chatter object
	 */
	public Chatter getChatter(String playerId) {
		return getChatter(UUID.fromString(playerId));
	}

	/**
	 * get chatter object
	 * @param playerId The UUID of the player
	 * @return The player's chatter object
	 */
	public Chatter getChatter(UUID playerId) {
		Chatter chatter = chatters.get(playerId);
		if (chatter != null) {
			return chatter;
		}
		ProxiedPlayer player = getProxy().getPlayer(playerId);
		if (player != null) {
			return getChatter(player);
		}
		getLogger().log(Level.WARNING, "Could not create the chatter? The player with the uuid " + playerId + " wasn't found online?");
		return null;
	}

	/**
	 * get chatter by name
	 * @return null if chatter not found
	 */
	public Chatter getChatterByName(String name) {
		ProxiedPlayer player = getProxy().getPlayer(name);
		if (player != null) {
			return getChatter(player);
		}
		name = name.toLowerCase();
		for (Chatter onlinechatter : chatters.values()) {
			if (onlinechatter != null && onlinechatter.getName().toLowerCase().startsWith(name)) {
				return onlinechatter;
			}
		}
		
		return null;
	}
	
	/**
	 * adds a channel
	 */
	public void addChannel(Channel channel) {
		channels.put(channel.getUUID(), channel);
	}
		
	/**
	 * get channel by name or tag
	 * 
	 * @param string    The name or tag of the channel
	 * @return          The channel or <tt>null</tt> if none found
	 */
	public Channel getChannel(String string) {
		if (channels.containsKey(string)) {
			return channels.get(string);
		}
		
		// cycle through channels
		for (Channel channel: channels.values()) {
			if (channel.getTag().equalsIgnoreCase(string) || channel.getName().equalsIgnoreCase(string)) {				
				return channel;
			}
		}
		
		// no channel matches
		return null;
	}

	/**
	 * get a map of all channels
	 * @return  A map of channel names to the channel object
	 */
	public Map<String, Channel> getChannels() {
		return channels;
	}

	/**
	 * unregister chatter from plugin
	 * @param playerId   The uuid of the chatter
	 */
	public void removeChatter(UUID playerId) {
		chatters.remove(playerId);
	}

	/**
	 * makes a string pretty
	 * 
	 * @param string    The string to make pretty
	 * @return          A string with all chat colors/formattings applied
	 */
	public static String addSpecialChars(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	/**
	 * sends a system notification to a chatter
	 * 
	 * @param sender    The sender to notify
	 * @param key       The language key
	 */
	public static void notify(CommandSender sender, String key) {
		notify(sender, key, null);
	}
	
	/**
	 * sends a system notification using text replacements
	 * 
	 * @param sender    The sender to notify
	 * @param key       The language key
	 */
	public static void notify(CommandSender sender, String key, Map<String, String> replacements) {
		String string = Channels.getInstance().getLanguage().getTranslation(key);
		
		// insert replacements
		if (replacements != null) {
			for (Map.Entry<String, String> entry : replacements.entrySet()) {
				string = string.replaceAll("%"+entry.getKey()+"%", entry.getValue());
			}
		}
		
		// add colors
		string = addSpecialChars(string);
		
		sender.sendMessage(TextComponent.fromLegacyText(string));
	}
	

	/**
	 * access translations
	 * 
	 * @return  The used LanguageConfig
	 */
	public LanguageConfig getLanguage() {
		return lang;
	}

	/**
	 * allow chat using the channel tag
	 * @param tag   The tag to register
	 */
	public void registerTag(String tag) {
		ChannelTagCommandExecutor executor = new ChannelTagCommandExecutor(tag.toLowerCase());
		
		getProxy().getPluginManager().registerCommand(this, executor);
		
		tagCommandExecutors.put(tag, executor);
	}

	/**
	 * remove commandexecutor
	 * @param tag   The tag to unregister
	 */
	public void unregisterTag(String tag) {
		if (tagCommandExecutors.containsKey(tag)) {
			getProxy().getPluginManager().unregisterCommand(tagCommandExecutors.get(tag));
			tagCommandExecutors.remove(tag);
		}
	}
		
	public Map<UUID, Chatter> getChatters() {
		return chatters;
	}

	/**
	 * check if users will be able to talk in a channel
	 * @param sender    The sender to check
	 * @param channelId The uuid of the channel
	 */
	public void checkSanity(CommandSender sender, String channelId) {
		Channel chan = channels.get(channelId);
		Channel def = channels.get(config.getDefaultChannelUUID());
		
		// check channel
		if (chan != null && !chan.isGlobal() && chan.getServers().isEmpty()) {
			Channels.notify(sender, "channels.command.channel-has-no-servers", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
		}
		
		// check servers
		for (ServerInfo server: getProxy().getServers().values()) {
			Channel serverDef = channels.get(config.getServerDefaultChannel(server.getName()));
			if (serverDef != null) {
				if (!serverDef.isGlobal() && !serverDef.getServers().contains(server.getName())) {
					Channels.notify(sender, "channels.command.default-channel-unavailable", ImmutableMap.of("server", server.getName()));
				}
				if (!serverDef.doAutojoin()) {
					Channels.notify(sender, "channels.command.default-channel-no-autojoin", ImmutableMap.of("channel", serverDef.getName(), "channelColor", serverDef.getColor().toString()));
				}
			} else if (def == null || !def.isGlobal() && !def.getServers().contains(server.getName())) {
				Channels.notify(sender, "channels.command.default-no-defchannel-available", ImmutableMap.of("server", server.getName()));
			}
		}
	}

	/**
	 * Get the instance of VNPBungee if it is installed
	 * @return The VNPBungee instance or <tt>null</tt> if VNPBungee is not installed
	 */
	public static VNPBungee getVNPBungee() {
		return vnpBungee;
	}

    /**
     * Get the LuckPermsApi if LuckPerms is installed
     * @return The LuckPermsApi or <tt>null</tt> if LuckPerms is not installed
     */
    public static LuckPermsApi getLuckPermsApi() {
        return luckPermsApi;
    }

    /**
     * Get the UUIDDBPlugin api if UUIDDB is installed
     * @return The UUIDDBPlugin or <tt>null</tt> if UUIDDB is not installed
     */
    public static UUIDDBPlugin getUuidDb() {
        return uuidDb;
    }

    /**
     * Get the name of a player by its UUID. Offline lookup requires UUIDDB or LuckPerms
     * @param playerId  The UUID of the player (as a string)
     * @return          The player's name or <tt>null</tt> if none found
     */
    public static String getPlayerName(String playerId) {
        return getPlayerName(UUID.fromString(playerId));
    }

    /**
     * Get the name of a player by its UUID. Offline lookup requires UUIDDB or LuckPerms
     * @param playerId  The UUID of the player
     * @return          The player's name or <tt>null</tt> if none found
     */
    public static String getPlayerName(UUID playerId) {
        String playerName = null;

		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerId);

		if (player != null) {
			playerName = player.getName();
		}

        if (getUuidDb() != null) {
            playerName = getUuidDb().getStorage().getNameByUUID(playerId);
        }

        if (playerName == null && getLuckPermsApi() != null) {
            User lpUser = getLuckPermsApi().getUser(playerId);
            if (lpUser != null) {
                playerName = lpUser.getName();
            }
        }

        return playerName != null ? playerName : "Unknown";
    }

    /**
     * Get the UUID of a player by its name. Offline lookup requires UUIDDB or LuckPerms
     * @param name  The name of the player
     * @return      The player's UUID or <tt>null</tt> if none found
     */
    public static UUID getPlayerId(String name) {
        UUID playerId = null;

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);

        if (player != null) {
            playerId = player.getUniqueId();
        }

        if (playerId == null && getUuidDb() != null) {
			String idStr = getUuidDb().getStorage().getUUIDByName(name, false);
			if (idStr != null) {
				playerId = UUID.fromString(idStr);
			}
        }

        if (playerId == null && getLuckPermsApi() != null) {
            User lpUser = getLuckPermsApi().getUser(name);
            if (lpUser != null) {
                playerId = lpUser.getUuid();
            }
        }

        return playerId;
    }

	private Chatter createChatter(ProxiedPlayer player) {
		try {
			Chatter chatter = new Chatter(player);

			for (String channelUUID: chatter.getSubscriptions()) {
				// channel exists
				if (getChannel(channelUUID) != null) {
					// I'm allowed to join
					if (chatter.hasPermission(Channels.getInstance().getChannel(channelUUID), "subscribe")) {
						getChannel(channelUUID).subscribe(chatter);
					} else {
						// chatter no longer has permission for this channel - remove from chatter config
						chatter.unsubscribe(channelUUID);
						if (channelUUID.equals(Channels.getConfig().getDefaultChannelUUID())) {
							getLogger().warning("Chatter '"+chatter.getName()+"' is not allowed to join the default channel");
						}
					}
				} else {
					// channel has been removed - remove from player config
					chatter.unsubscribe(channelUUID);
				}
			}

			// check for autojoin channels
			for (Channel channel: Channels.getInstance().getChannels().values()) {
				if (channel.doAutojoin() && !channel.isTemporary() && chatter.hasPermission(channel, "subscribe")) {
					// joining twice doesn't matter, caught elsewhere
					chatter.subscribe(channel.getUUID());
				}
			}

			return chatter;
		} catch (IOException e) {
			getLogger().severe("Unable to create Chatter '" + player.getName() + "'/" + player.getUniqueId());
			e.printStackTrace();
		}
		return null;
	}

	private class ChatterNotFoundException extends ExecutionException {
		ChatterNotFoundException(String msg) {
			super(msg);
		}
	}
}
