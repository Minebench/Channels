package net.zaiyers.Channels.command;

public interface ChannelsCommand {
	/**
	 * get the required permission to run this command
	 * @return
	 */
	public String getPermission();
	
	/**
	 * run the command
	 */
	public void execute();
	
	/**
	 * validate user input
	 */
	public boolean validateInput();
}
