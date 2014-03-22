package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.zaiyers.Channels.Channels;

public class ChannelsCommandExecutor extends Command {
	public ChannelsCommandExecutor(String name, String permission,	String[] aliases) {
		super(name, permission, aliases);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		ChannelsCommand cmd;
		
		if (args.length > 0) {
			String cmdName = args[0].toLowerCase();
			
			if (cmdName.matches("^subscribe|join$")) {
				cmd = new ChannelSubscribeCommand(sender, args);
			} else if (cmdName.matches("^unsubscribe|quit$")) {
				cmd = new ChannelUnsubscribeCommand(sender, args);
			} else if (cmdName.matches("^speak|say$")) {
				cmd = new ChannelSpeakCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("global")) {
				cmd = new ChannelGlobalCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("addserver")) {
				cmd = new ChannelAddServerCommand(sender, args);
			} else {
				// notify sender and exit
				
				Channels.notify(sender, "channels.usage.unkown-command", ImmutableMap.of("command", args[0]));
				return;
			}
		} else {
			cmd = new ChannelHelpCommand(sender, args);
		}
		
		// execute command
		if (sender.hasPermission(cmd.getPermission())) {
			if (cmd.validateInput()) {
				cmd.execute();
			} else {
				Channels.notify(sender, "channels.usage."+getClass().getName());
			}
		}
	}
}
