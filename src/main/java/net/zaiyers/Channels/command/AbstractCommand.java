package net.zaiyers.Channels.command;

import com.velocitypowered.api.command.CommandSource;

public abstract class AbstractCommand implements ChannelsCommand {	
	/**
	 * command sender
	 */
	protected CommandSource sender;
	
	/**
	 * arguments to this command
	 */
	protected String[] args;
		
	/**
	 * constructor :)
	 * 
	 * @param sender
	 * @param args
	 */
	public AbstractCommand(CommandSource sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
}
