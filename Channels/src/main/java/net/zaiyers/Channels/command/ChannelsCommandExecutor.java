package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.zaiyers.Channels.Channels;

public class ChannelsCommandExecutor extends Command {
	String command;
	
	public ChannelsCommandExecutor(String name, String permission,	String[] aliases) {
		super(name, permission, aliases);
		
		command = name;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		ChannelsCommand cmd;
		
		if (args.length > 0) {
			String cmdName = args[0].toLowerCase();
			if (command.matches("^pm|msg|tell$")) {
				cmd = new PMCommand(sender, args);
			} else if (command.matches("^r|reply$")) {
				cmd = new ReplyCommand(sender, args);
			} else if (command.equalsIgnoreCase("afk")) {
				cmd = new AFKCommand(sender, args);
			} else if (cmdName.matches("^subscribe|join$")) {
				cmd = new ChannelSubscribeCommand(sender, args);
			} else if (cmdName.matches("^unsubscribe|quit$")) {
				cmd = new ChannelUnsubscribeCommand(sender, args);
			} else if (cmdName.matches("^speak|say$")) {
				cmd = new ChannelSpeakCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("global")) {
				cmd = new ChannelGlobalCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("addserver")) {
				cmd = new ChannelAddServerCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("removeserver")) {
				cmd = new ChannelRemoveServerCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("open")) {
				cmd = new ChannelOpenCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("addmod")) {
				cmd = new ChannelAddModCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("removemod")) {
				cmd = new ChannelRemoveModCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("autojoin")) {
				cmd = new ChannelAutojoinCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("ban")) {
				cmd = new ChannelBanCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("kick")) {
				cmd = new ChannelKickCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("unban")) {
				cmd = new ChannelUnbanCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("color")) {
				cmd = new ChannelColorCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("mute")) {
				cmd = new ChannelMuteCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("unmute")) {
				cmd = new ChannelUnmuteCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("prefix")) {
				cmd = new ChannelPrefixCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("suffix")) {
				cmd = new ChannelSuffixCommand(sender, args);
			} else if (cmdName.equalsIgnoreCase("serverdefaultchannel")) {
				cmd = new ServerDefaultChannelCommand(sender, args);
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
				Channels.notify(sender, "channels.usage."+cmd.getClass().getName());
			}
		}
	}
}
