package net.zaiyers.Channels.command;

public interface ChannelsCommand {	
	/**
	 * run the command
	 */
	public void execute();
	
	/**
	 * validate user input
	 */
	public boolean validateInput();
}
