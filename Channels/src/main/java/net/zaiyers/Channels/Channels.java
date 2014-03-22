package net.zaiyers.Channels;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.zaiyers.Channels.command.ChannelTagCommandExecutor;
import net.zaiyers.Channels.command.ChannelsCommandExecutor;
import net.zaiyers.Channels.config.ChannelsConfig;
import net.zaiyers.Channels.config.LanguageConfig;
import net.zaiyers.Channels.listener.MessageListener;
import net.zaiyers.Channels.listener.PlayerJoinListener;
import net.zaiyers.Channels.listener.PlayerQuitListener;

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
	private HashMap<UUID, Channel> channels = new HashMap<UUID, Channel>();
	
	/**
	 * maps channel names and tags to their uuids
	 */
	private HashMap<String, UUID> channelMappings = new HashMap<String, UUID>();
	
	/**
	 * command executors for channel tags
	 */
	private HashMap<String, ChannelTagCommandExecutor> tagCommandExecutors = new HashMap<String, ChannelTagCommandExecutor>();
	
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
		
		// load listeners
		MessageListener ml = new MessageListener();
		PlayerJoinListener pjl = new PlayerJoinListener();
		PlayerQuitListener pql = new PlayerQuitListener();
		
		// enable listeners
		getProxy().getPluginManager().registerListener(this, ml);
		getProxy().getPluginManager().registerListener(this, pjl);
		getProxy().getPluginManager().registerListener(this, pql);
		
		// register command executor
		getProxy().getPluginManager().registerCommand(this, new ChannelsCommandExecutor("channel", "", new String[] {"ch"}));
		
		// load and register channels
		for (UUID channelUUID: config.getChannels()) {
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
		return (Channels) BungeeCord.getInstance().getPluginManager().getPlugin("Channels");
	}

	/**
	 * remove channel from memory
	 * @param id
	 */
	public void removeChannel(UUID uuid) {
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
	 * adds a channel
	 */
	public void addChannel(Channel channel) {
		channels.put(channel.getUUID(), channel);
	}
	
	/**
	 * get channel object
	 * @param channel
	 * @return
	 */
	public Channel getChannel(UUID channelUUID) {
		return channels.get(channelUUID);
	}
	
	/**
	 * get channel by name or tag
	 * 
	 * @param string
	 * @return
	 */
	public Channel getChannel(String string) {
		if (channelMappings.containsKey(string.toLowerCase())) {
			return channels.get(channelMappings.get(string));
		} else {
			
			// cycle through channels
			for (Channel channel: channels.values()) {
				if (channel.getTag().equalsIgnoreCase(string) || channel.getName().equalsIgnoreCase(string)) {
					// remember result
					channelMappings.put(string.toLowerCase(), channel.getUUID());
					
					return channel;
				}
			}
		}
		
		// no channel matches
		return null;
	}

	/**
	 * get a list of all channels
	 * @return
	 */
	public HashMap<UUID, Channel> getChannels() {
		return channels;
	}

	/**
	 * register chatter
	 * @param chatter
	 */
	public void addChatter(Chatter chatter) {
		chatters.put(chatter.getPlayer().getUUID(), chatter);
	}

	/**
	 * unregister chatter from plugin
	 * @param chatter
	 */
	public void removeChatter(Chatter chatter) {
		chatters.remove(chatter.getPlayer().getUUID());
	}

	/**
	 * makes a string pretty
	 * 
	 * @param string
	 * @return
	 */
	public static String addSpecialChars(String string) {
		return string.replaceAll("(&([a-fk-or0-9]))", "\u00A7$2");
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
		String string = lang.getTranslation(key);
		
		// insert replacements
		if (replacements != null) {
			for (String variable: replacements.keySet()) {
				string = string.replaceAll("%"+variable+"%", replacements.get(variable));
			}
		}
		
		// add colors
		string = addSpecialChars(string);
		
		sender.sendMessage(new TextComponent(string));
	}
	

	/**
	 * access translations
	 * 
	 * @return
	 */
	public static LanguageConfig getLanguage() {
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
}
