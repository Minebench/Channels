package net.zaiyers.Channels.config;

import java.util.List;

import net.md_5.bungee.api.ChatColor;

public interface ChannelConfig extends Config {
	/**
	 * @return channelname
	 */
	public String getName();
	
	/**
	 * @return channeltag
	 */
	public String getTag();
	
	/**
	 * @return messageformat
	 */
	public String getFormat();
	
	/**
	 * @return channelcolor
	 */
	public ChatColor getColor();
	
	/**
	 * @return channelpassword
	 */
	public String getPassword();
	
	/**
	 * servers this channel is distributed to
	 * @return channelservers
	 */
	public List<String> getServers();
	
	/**
	 * channel moderators uuids
	 * @return list of uuids
	 */
	public List<String> getModerators();
	
	/**
	 * banned players uuids
	 * @return list of uuids
	 */
	public List<String> getBans();
	
	/**
	 * set channel name
	 * @param name new name
	 */
	public void setName(String name);
	
	/**
	 * set channel tag
	 * @param tag new tag
	 */
	public void setTag(String tag);
	
	/**
	 * set the channel format
	 * @param format new format
	 */
	public void setFormat(String format);
	
	/**
	 * set channel passwd
	 * @param password new password
	 */
	public void setPassword(String password);
	
	/**
	 * get uuid
	 * @return UUID of this channel
	 */
	public String getUUID();
	
	/**
	 * create a new config
	 */
	public void createDefaultConfig();
	
	/**
	 * add server to distribute list
	 * @param servername
	 */
	public void addServer(String servername);
	
	/**
	 * get format for console messages
	 * @return console format
	 */
	public String getConsoleFormat();
	
	/**
	 * remove server from distribute list
	 * @param servername
	 */
	public void removeServer(String servername);
	
	/**
	 * add moderator
	 * @param uuid moderators uuid
	 */
	public void addModerator(String uuid);
	

	/**
	 * remove moderator
	 * @param modUUID moderators uuid
	 */
	public void removeModerator(String modUUID);
	
	/**
	 * set autojoin behavior
	 * @param autojoin
	 */
	public void setAutojoin(boolean autojoin);
	
	/**
	 * get autojoin behavior
	 * @return true if autojoin is enabled
	 */
	public boolean doAutojoin();
	
	/**
	 * add uuid to bans
	 * @param chatterUUID
	 */
	public void addBan(String chatterUUID);
	
	/**
	 * remove uuid from banlist
	 * @param chatterUUID
	 */
	public void removeBan(String chatterUUID);
	
	/**
	 * set channel color
	 * @param color new color
	 */
	public void setColor(ChatColor color);
	
	/**
	 * make channel global
	 * @param global
	 */
	public void setGlobal(boolean global);
	
	/**
	 * @return true if channel is global
	 */
	public boolean isGlobal();

    /**
     * @return true if channel sends to a backend server
     */
    boolean isBackend();

    /**
     * make channel send to the backend server
     * @param backend
     */
    void setBackend(boolean backend);

	/**
	 * set autofocus behavior
	 * @param autofocus
	 */
	public void setAutofocus(boolean autofocus);

	/**
	 * get autofocus behavior
	 * @return true if autofocus is enabled
	 */
	public boolean doAutofocus();
}
