package net.zaiyers.Channels.config;

public interface Config {
	/**
	 * creates a default configuration
	 */
	public void createDefaultConfig();
	
	/**
	 * save configuration
	 */
	public void save();
	
	/**
	 * remove configuration
	 */
	
	public void removeConfig();
}
