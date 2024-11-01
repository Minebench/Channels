package net.zaiyers.Channels;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.themoep.vnpvelocity.VNPVelocity;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.zaiyers.Channels.command.AbstractCommandExecutor;
import net.zaiyers.Channels.command.ChannelTagCommandExecutor;
import net.zaiyers.Channels.command.ChannelsCommandExecutor;
import net.zaiyers.Channels.config.ChannelsConfig;
import net.zaiyers.Channels.config.LanguageConfig;
import net.zaiyers.Channels.integration.MiniPlaceholdersIntegration;
import net.zaiyers.Channels.listener.MessageListener;
import net.zaiyers.Channels.listener.PlayerJoinListener;
import net.zaiyers.Channels.listener.PlayerQuitListener;
import net.zaiyers.Channels.listener.ServerSwitchListener;
import net.zaiyers.UUIDDB.core.UUIDDBPlugin;

@Plugin(id = "channels")
public class Channels {
	private static Channels instance;
	/**
	 * Velocity instance
	 */
	private final ProxyServer proxy;

	/**
	 * The plugin logger
	 */
	private final Logger logger;

	/**
	 * Data folder of the plugin
	 */
	private final Path dataFolder;

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
	private static LuckPerms luckPermsApi = null;

	private static UUIDDBPlugin uuidDb = null;

	private static VNPVelocity vnpVelocity = null;

	@Inject
	public Channels(ProxyServer proxy, Logger logger, @DataDirectory Path dataFolder) {
		this.proxy = proxy;
		this.logger = logger;
		this.dataFolder = dataFolder;
	}

	/**
	 * executed on startup
	 */
	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		instance = this;

		// load configuration
		try {
			config = new ChannelsConfig(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			getLogger().severe("Unable to load configuration! Channels will not be enabled.");
			e.printStackTrace();

			return;
		}

		// load language
		try {
			lang = new LanguageConfig(new File(getDataFolder(), "lang." + config.getLanguage() + ".yml"));
		} catch (IOException e) {
			getLogger().severe("Unable to load language! Channels will not be enabled.");
			e.printStackTrace();

			return;
		}

		if (getProxy().getPluginManager().getPlugin("vnpvelocity").isPresent()) {
			getLogger().info("Found VNPVelocity!");
			vnpVelocity = VNPVelocity.getInstance();
		}

		if (getProxy().getPluginManager().getPlugin("luckperms").isPresent()) {
			getLogger().info("Found LuckPerms!");
			luckPermsApi = LuckPermsProvider.get();
		}

		if (getProxy().getPluginManager().getPlugin("uuiddb").isPresent()) {
			getLogger().info("Found UUIDDB!");
			uuidDb = (UUIDDBPlugin) getProxy().getPluginManager().getPlugin("uuiddb").get();
		}

		if (getProxy().getPluginManager().getPlugin("miniplaceholders").isPresent()) {
			getLogger().info("Found MiniPlaceholders!");
			new MiniPlaceholdersIntegration();
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
		getProxy().getEventManager().register(this, ml);
		getProxy().getEventManager().register(this, pjl);
		getProxy().getEventManager().register(this, pql);
		getProxy().getEventManager().register(this, swl);

		// register command executors
		registerCommand(new ChannelsCommandExecutor("channel", "ch", "channels"));
		registerCommand(new ChannelsCommandExecutor("pm", "tell", "msg"));
		registerCommand(new ChannelsCommandExecutor("reply", "r"));
		registerCommand(new ChannelsCommandExecutor("afk"));
		registerCommand(new ChannelsCommandExecutor("dnd"));
		registerCommand(new ChannelsCommandExecutor("ignore"));

		// load and register channels
		for (String channelUUID : config.getChannels()) {
			Channel channel;
			try {
				channel = new Channel(channelUUID);
				channels.put(channel.getUUID(), channel);
				registerTag(channel.getTag());
			} catch (IOException e) {
				getLogger().severe("Couldn't load channel " + channelUUID);

				e.printStackTrace();
			}
		}

		checkSanity(getProxy().getConsoleCommandSource(), null);
	}

	private void registerCommand(AbstractCommandExecutor command) {
		getProxy().getCommandManager().register(getProxy().getCommandManager()
				.metaBuilder(command.getName())
				.aliases(command.getAliases())
				.plugin(this)
				.build(), command);
	}

	/**
	 * Reload the config. Currently it is only possible to reload the language config.
	 */
	public boolean reloadConfig() {
		try {
			lang.load();
		} catch (IOException e) {
			getLogger().severe("Error while loading the language config!");
			e.printStackTrace();

			return false;
		}
		return true;
	}

	/**
	 * executed on shutdown
	 */
	public void onDisable() {
		// save channel configurations
		for (Channel channel : channels.values()) {
			channel.save();
		}

		// save chatter configurations
		for (Chatter chatter : chatters.values()) {
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

	public Chatter getChatter(Player player) {
		Chatter chatter = chatters.get(player.getUniqueId());
		if (chatter == null) {
			chatter = createChatter(player);
		}
		if (chatter == null) {
			getLogger().log(Level.WARNING, "Could not get the chatter for " + player.getUsername() + "/" + player.getUniqueId() + "?");
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
		Optional<Player> player = getProxy().getPlayer(playerId);
		if (player.isPresent()) {
			return getChatter(player.get());
		}
		getLogger().log(Level.WARNING, "Could not create the chatter? The player with the uuid " + playerId + " wasn't found online?");
		return null;
	}

	/**
	 * get chatter by name
	 * @return null if chatter not found
	 */
	public Chatter getChatterByName(String name) {
		Optional<Player> player = getProxy().getPlayer(name);
		if (player.isPresent()) {
			return getChatter(player.get());
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
	 * @param string The name or tag of the channel
	 * @return The channel or <tt>null</tt> if none found
	 */
	public Channel getChannel(String string) {
		if (channels.containsKey(string)) {
			return channels.get(string);
		}

		// cycle through channels
		for (Channel channel : channels.values()) {
			if (channel.getTag().equalsIgnoreCase(string) || channel.getName().equalsIgnoreCase(string)) {
				return channel;
			}
		}

		// no channel matches
		return null;
	}

	/**
	 * get a map of all channels
	 * @return A map of channel names to the channel object
	 */
	public Map<String, Channel> getChannels() {
		return channels;
	}

	/**
	 * unregister chatter from plugin
	 * @param playerId The uuid of the chatter
	 */
	public void removeChatter(UUID playerId) {
		chatters.remove(playerId);
	}

	/**
	 * sends a system notification to a chatter
	 * @param sender The sender to notify
	 * @param key    The language key
	 * @param replacements Replacements array
	 */
	public static void notify(CommandSource sender, String key, String... replacements) {
		sender.sendMessage(Channels.getInstance().getLanguage().getTranslationComponent(key, replacements));
	}

	/**
	 * sends a system notification using text replacements
	 * @param sender The sender to notify
	 * @param key    The language key
	 * @param replacements Replacements map
	 */
	public static void notify(CommandSource sender, String key, Map<String, String> replacements) {
		sender.sendMessage(Channels.getInstance().getLanguage().getTranslationComponent(key, replacements));
	}


	/**
	 * access translations
	 * @return The used LanguageConfig
	 */
	public LanguageConfig getLanguage() {
		return lang;
	}

	/**
	 * allow chat using the channel tag
	 * @param tag The tag to register
	 */
	public void registerTag(String tag) {
		ChannelTagCommandExecutor executor = new ChannelTagCommandExecutor(tag.toLowerCase());

		registerCommand(executor);

		tagCommandExecutors.put(tag, executor);
	}

	/**
	 * remove commandexecutor
	 * @param tag The tag to unregister
	 */
	public void unregisterTag(String tag) {
		if (tagCommandExecutors.containsKey(tag)) {
			getProxy().getCommandManager().unregister(tagCommandExecutors.get(tag).getName());
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
	public void checkSanity(CommandSource sender, String channelId) {
		Channel chan = channels.get(channelId);
		Channel def = channels.get(config.getDefaultChannelUUID());

		// check channel
		if (chan != null && !chan.isGlobal() && chan.getServers().isEmpty()) {
			Channels.notify(sender, "channels.command.channel-has-no-servers", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
		}

		// check servers
		for (RegisteredServer registeredServer : getProxy().getAllServers()) {
			ServerInfo server = registeredServer.getServerInfo();
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
	 * Get the instance of VNPVelocity if it is installed
	 * @return The VNPVelocity instance or <tt>null</tt> if VNPVelocity is not installed
	 */
	public static VNPVelocity getVNPVelocity() {
		return vnpVelocity;
	}

	/**
	 * Get the LuckPermsApi if LuckPerms is installed
	 * @return The LuckPermsApi or <tt>null</tt> if LuckPerms is not installed
	 */
	public static LuckPerms getLuckPermsApi() {
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
	 * @param playerId The UUID of the player (as a string)
	 * @return The player's name or <tt>null</tt> if none found
	 */
	public static String getPlayerName(String playerId) {
		return getPlayerName(UUID.fromString(playerId));
	}

	/**
	 * Get the name of a player by its UUID. Offline lookup requires UUIDDB or LuckPerms
	 * @param playerId The UUID of the player
	 * @return The player's name or <tt>null</tt> if none found
	 */
	public static String getPlayerName(UUID playerId) {
		String playerName = null;

		Optional<Player> player = Channels.getInstance().getProxy().getPlayer(playerId);

		if (player.isPresent()) {
			playerName = player.get().getUsername();
		}

		if (getUuidDb() != null) {
			playerName = getUuidDb().getStorage().getNameByUUID(playerId);
		}

		if (playerName == null && getLuckPermsApi() != null) {
			User lpUser = getLuckPermsApi().getUserManager().getUser(playerId);
			if (lpUser != null) {
				playerName = lpUser.getUsername();
			}
		}

		return playerName != null ? playerName : "Unknown";
	}

	/**
	 * Get the UUID of a player by its name. Offline lookup requires UUIDDB or LuckPerms
	 * @param name The name of the player
	 * @return The player's UUID or <tt>null</tt> if none found
	 */
	public static UUID getPlayerId(String name) {
		UUID playerId = null;

		Optional<Player> player = Channels.getInstance().getProxy().getPlayer(name);

		if (player.isPresent()) {
			playerId = player.get().getUniqueId();
		}

		if (playerId == null && getUuidDb() != null) {
			String idStr = getUuidDb().getStorage().getUUIDByName(name, false);
			if (idStr != null) {
				playerId = UUID.fromString(idStr);
			}
		}

		if (playerId == null && getLuckPermsApi() != null) {
			User lpUser = getLuckPermsApi().getUserManager().getUser(name);
			if (lpUser != null) {
				playerId = lpUser.getUniqueId();
			}
		}

		return playerId;
	}

	/**
	 * Parse a CSS hex string or a named color to a TextColor
	 * @param colorString The color string to parse
	 * @return The parsed TextColor or <tt>null</tt> if none found
	 */
	public static TextColor parseTextColor(String colorString) {
		TextColor color = TextColor.fromCSSHexString(colorString);
		if (color != null) {
			return color;
		}
		return NamedTextColor.NAMES.value(colorString);
	}

	private Chatter createChatter(Player player) {
		try {
			Chatter chatter = new Chatter(player);

			for (String channelUUID : chatter.getSubscriptions()) {
				// channel exists
				if (getChannel(channelUUID) != null) {
					// I'm allowed to join
					if (chatter.hasPermission(getChannel(channelUUID), "subscribe")) {
						getChannel(channelUUID).subscribe(chatter);
					} else {
						// chatter no longer has permission for this channel - remove from chatter config
						chatter.unsubscribe(channelUUID);
						if (channelUUID.equals(Channels.getConfig().getDefaultChannelUUID())) {
							getLogger().warning("Chatter '" + chatter.getName() + "' is not allowed to join the default channel");
						}
					}
				} else {
					// channel has been removed - remove from player config
					chatter.unsubscribe(channelUUID);
				}
			}

			// check for autojoin channels
			for (Channel channel : Channels.getInstance().getChannels().values()) {
				if (channel.doAutojoin() && !channel.isTemporary() && chatter.hasPermission(channel, "subscribe")) {
					// joining twice doesn't matter, caught elsewhere
					chatter.subscribe(channel);
				}
			}

			return chatter;
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Unable to create Chatter '" + player.getUsername() + "'/" + player.getUniqueId(), e);
		}
		return null;
	}

	public InputStream getResourceAsStream(String name) {
		return getClass().getClassLoader().getResourceAsStream(name);
	}

	private class ChatterNotFoundException extends ExecutionException {
		ChatterNotFoundException(String msg) {
			super(msg);
		}
	}

	/**
	 * Get the plugin logger
	 * @return The plugin logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Get the data folder of the plugin
	 * @return The data folder
	 */
	public File getDataFolder() {
		return dataFolder.toFile();
	}

	/**
	 * Get the proxy server
	 * @return The proxy server
	 */
	public ProxyServer getProxy() {
		return proxy;
	}
}
