package net.zaiyers.Channels;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.zaiyers.Channels.command.ChannelTagCommandExecutor;
import net.zaiyers.Channels.command.ChannelsCommandExecutor;
import net.zaiyers.Channels.config.ChannelsConfig;
import net.zaiyers.Channels.config.LanguageConfig;
import net.zaiyers.Channels.listener.MessageListener;
import net.zaiyers.Channels.listener.PlayerJoinListener;
import net.zaiyers.Channels.listener.PlayerQuitListener;
import net.zaiyers.Channels.listener.ServerSwitchListener;

public class Channels extends Plugin {
	/**
	 * List of Chatters
	 */
	private HashMap<String, Chatter> chatters = new HashMap<String, Chatter>();
	
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
	private HashMap<String, Channel> channels = new HashMap<String, Channel>();
	
	/**
	 * command executors for channel tags
	 */
	private HashMap<String, ChannelTagCommandExecutor> tagCommandExecutors = new HashMap<String, ChannelTagCommandExecutor>();

    /**
     * Soft depend on LuckPerms
     */
    private static LuckPermsApi luckPermsApi = null;

    /**
	 * executed on startup
	 */
	public void onEnable() {
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

		if (getProxy().getPluginManager().getPlugin("LuckPerms") != null) {
            luckPermsApi = LuckPerms.getApi();
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
		return (Channels) ProxyServer.getInstance().getPluginManager().getPlugin("Channels");
	}

	/**
	 * remove channel from memory
	 * @param id
	 */
	public void removeChannel(String uuid) {
		Channel chan = channels.get(uuid);
		if (chan != null) {
			chan.removeChannel();
		}
		channels.remove(uuid);
	}

	/**
	 * get chatter object
	 * @param lastSender
	 * @return
	 */
	public Chatter getChatter(String uuid) {
		return chatters.get(uuid);
	}

	/**
	 * get chatter by name
	 * @return null if chatter not found
	 */
	public Chatter getChatterByName(String name) {
		name = name.toLowerCase();
		for (Chatter onlinechatter: chatters.values().toArray(new Chatter[Channels.getInstance().getChatters().size()])) {
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
	 * @param string
	 * @return
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
	 * get a list of all channels
	 * @return
	 */
	public HashMap<String, Channel> getChannels() {
		return channels;
	}

	/**
	 * register chatter
	 * @param chatter
	 */
	public void addChatter(Chatter chatter) {
		chatters.put(chatter.getPlayer().getUniqueId().toString(), chatter);
	}

	/**
	 * unregister chatter from plugin
	 * @param chatter
	 */
	public void removeChatter(Chatter chatter) {
		chatters.remove(chatter.getPlayer().getUniqueId().toString());
	}

	/**
	 * makes a string pretty
	 * 
	 * @param string
	 * @return
	 */
	public static String addSpecialChars(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	/**
	 * sends a system notification to a chatter
	 * 
	 * @param sender
	 * @param string
	 */
	public static void notify(CommandSender sender, String key) {
		notify(sender, key, null);
	}
	
	/**
	 * sends a system notification using text replacements
	 * 
	 * @param sender
	 * @param string
	 */
	public static void notify(CommandSender sender, String key, Map<String, String> replacements) {
		String string = Channels.getInstance().getLanguage().getTranslation(key);
		
		// insert replacements
		if (replacements != null) {
			for (String variable: replacements.keySet()) {
				string = string.replaceAll("%"+variable+"%", replacements.get(variable));
			}
		}
		
		// add colors
		string = addSpecialChars(string);
		
		sender.sendMessage(TextComponent.fromLegacyText(string));
	}
	

	/**
	 * access translations
	 * 
	 * @return
	 */
	public LanguageConfig getLanguage() {
		return lang;
	}

	/**
	 * allow chat using the channel tag
	 * @param tag
	 */
	public void registerTag(String tag) {
		ChannelTagCommandExecutor executor = new ChannelTagCommandExecutor(tag.toLowerCase());
		
		getProxy().getPluginManager().registerCommand(this, executor);
		
		tagCommandExecutors.put(tag, executor);
	}

	/**
	 * remove commandexecutor
	 * @param tag
	 */
	public void unregisterTag(String tag) {
		if (tagCommandExecutors.containsKey(tag)) {
			getProxy().getPluginManager().unregisterCommand(tagCommandExecutors.get(tag));
			tagCommandExecutors.remove(tag);
		}
	}
		
	public HashMap<String, Chatter> getChatters() {
		return chatters;
	}

	/**
	 * check if users will be able to talk in a chanenl
	 * @param sender
	 * @param string
	 */
	public void checkSanity(CommandSender sender, String string) {
		Channel chan = channels.get(string);
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

    public static LuckPermsApi getLuckPermsApi() {
        return luckPermsApi;
    }
}
