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
		
		if (command.equals("channel")) {
			if (args.length > 0) {
				String cmdName = args[0].toLowerCase();
				if (cmdName.equals("ignore")) {
					cmd = new IgnoreCommand(sender, args);
				} else if (cmdName.matches("^subscribe|join$")) {
					cmd = new ChannelSubscribeCommand(sender, args);
				} else if (cmdName.matches("^unsubscribe|quit|leave$")) {
					cmd = new ChannelUnsubscribeCommand(sender, args);
				} else if (cmdName.matches("^speak|say$")) {
					cmd = new ChannelSpeakCommand(sender, args);
				} else if (cmdName.equals("global")) {
					cmd = new ChannelGlobalCommand(sender, args);
				} else if (cmdName.equals("addserver")) {
					cmd = new ChannelAddServerCommand(sender, args);
				} else if (cmdName.equals("removeserver")) {
					cmd = new ChannelRemoveServerCommand(sender, args);
				} else if (cmdName.equals("create")) {
					cmd = new ChannelCreateCommand(sender, args);
				} else if (cmdName.equals("open")) {
					cmd = new ChannelOpenCommand(sender, args);
				} else if (cmdName.matches("^remove|close$")) {
					cmd = new ChannelRemoveCommand(sender, args);
				} else if (cmdName.equals("addmod")) {
					cmd = new ChannelAddModCommand(sender, args);
				} else if (cmdName.equals("removemod")) {
					cmd = new ChannelRemoveModCommand(sender, args);
				} else if (cmdName.equals("autojoin")) {
					cmd = new ChannelAutojoinCommand(sender, args);
				} else if (cmdName.equals("ban")) {
					cmd = new ChannelBanCommand(sender, args);
				} else if (cmdName.equals("kick")) {
					cmd = new ChannelKickCommand(sender, args);
				} else if (cmdName.equals("unban")) {
					cmd = new ChannelUnbanCommand(sender, args);
				} else if (cmdName.equals("color")) {
					cmd = new ChannelColorCommand(sender, args);
				} else if (cmdName.equals("mute")) {
					cmd = new ChannelMuteCommand(sender, args);
				} else if (cmdName.equals("unmute")) {
					cmd = new ChannelUnmuteCommand(sender, args);
				} else if (cmdName.equals("prefix")) {
					cmd = new ChannelPrefixCommand(sender, args);
				} else if (cmdName.equals("suffix")) {
					cmd = new ChannelSuffixCommand(sender, args);
				} else if (cmdName.equals("serverdefaultchannel")) {
					cmd = new ServerDefaultChannelCommand(sender, args);
				} else if (cmdName.matches("^list|who$")) {
					cmd = new ChannelListCommand(sender, args);
				} else if (cmdName.equals("info")) {
					cmd = new ChannelInfoCommand(sender, args);
				} else if (cmdName.equals("rename")) {
					cmd = new ChannelRenameCommand(sender, args);
				} else if (cmdName.equals("help")) {
					cmd = new ChannelHelpCommand(sender, args);
				} else if (Channels.getInstance().getChannel(args[0]) != null) {
					String[] shiftedArgs = new String[args.length + 1];
					System.arraycopy(args, 0, shiftedArgs, 1, args.length);
					cmd = new ChannelSpeakCommand(sender, shiftedArgs);
				} else {
					// notify sender and exit
					Channels.notify(sender, "channels.usage.unknown-command", ImmutableMap.of("command", args[0]));
					return;
				}
			} else {
				cmd = new ChannelHelpCommand(sender, args);
			}
		} else if (command.equals("pm")) {
			cmd = new PMCommand(sender, args);
		} else if (command.equals("reply")) {
			cmd = new ReplyCommand(sender, args);
		} else if (command.equals("afk")) {
			cmd = new AFKCommand(sender, args);
		} else if (command.equals("dnd")) {
			cmd = new DNDCommand(sender, args);
		} else if (command.equals("ignore")) {
			cmd = new IgnoreCommand(sender, args);
		} else {
			cmd = new ChannelHelpCommand(sender, args);
		}
		
		// execute command
		if (sender.hasPermission(CommandPermission.valueOf(cmd.getClass().getSimpleName()).toString())) {
			if (cmd.validateInput()) {
				cmd.execute();
			} else {
				Channels.notify(sender, "channels.usage."+cmd.getClass().getSimpleName());
			}
		} else {
			Channels.notify(sender, "channels.permission.insufficient-permission", ImmutableMap.of("permission", CommandPermission.valueOf(cmd.getClass().getSimpleName()).toString()));
		}
	}
}
