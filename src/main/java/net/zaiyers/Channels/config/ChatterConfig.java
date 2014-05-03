package net.zaiyers.Channels.config;

import java.util.List;

public interface ChatterConfig extends Config {
	/**
	 * read subscriptions from configuration
	 * @return
	 */
	public List<String> getSubscriptions();
	
	/**
	 * I was a bad boy
	 */
	public boolean isMuted();
	
	/**
	 * these are bad boys
	 */
	public List<String> getIgnores();
	
	/**
	 * prefixes are kewl
	 */
	public String getPrefix();
	
	/**
	 * suffixes are kewl as well
	 */
	public String getSuffix();
	
	/**
	 * I know this guy
	 */
	public String getLastSender();
	
	/**
	 * I like this guy
	 */
	public String getLastRecipient();
	
	/**
	 * I have many friends
	 */
	public String getChannelUUID();
	
	/**
	 * override list of subscriptions
	 * @param subs
	 */
	public void setSubscriptions(List<String> subs);
	
	/**
	 * set muted status
	 * @param b
	 */
	public void setMuted(boolean b);
	
	/**
	 * set chatter prefix
	 * @param prefix
	 */
	public void setPrefix(String prefix);
	
	/**
	 * set chatter suffix
	 * @param string
	 */
	public void setSuffix(String suffix);
	
	/**
	 * remove player from ignores
	 * @param ignoreUUID
	 */
	public void removeIgnore(String ignoreUUID);
	
	/**
	 * add player to ignore list
	 * @param uuid
	 */
	public void addIgnore(String uuid);
	
	/**
	 * set players default channel
	 */
	public void setDefaultChannel(String uuid);
	
	/**
	 * set person who last wrote
	 */
	public void setLastSender(String uuid);
}
