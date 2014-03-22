package net.zaiyers.Channels.command;

import net.md_5.bungee.api.CommandSender;

public abstract class AbstractCommand implements ChannelsCommand {
	/**
	 * by default all commands require operator permissions
	 */
	protected final static String permission = "channels.op";
	
	/**
	 * command sender
	 */
	protected CommandSender sender;
	
	/**
	 * arguments to this command
	 */
	protected String[] args;
		
	/**
	 * constructor :)
	 * 
	 * @param chatter
	 * @param args
	 */
	public AbstractCommand(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
}
