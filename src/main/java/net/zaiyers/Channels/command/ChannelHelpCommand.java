package net.zaiyers.Channels.command;

import java.util.ArrayList;

import com.velocitypowered.api.command.CommandSource;
import net.zaiyers.Channels.Channels;

public class ChannelHelpCommand extends AbstractCommand implements ChannelsCommand {

	public ChannelHelpCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (args.length < 2) {
			Channels.notify(sender, "channels.usage.ChannelHelpCommand");
		} else if (args.length > 1) {
			if (args[1].equalsIgnoreCase("admin")) {
				// admin commands
				ArrayList<String> adminCommands = new ArrayList<String>();
				if (sender.hasPermission(CommandPermission.ChannelAddServerCommand.toString())) {
					adminCommands.add("ChannelAddServerCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelAutofocusCommand.toString())) {
					adminCommands.add("ChannelAutofocusCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelAutojoinCommand.toString())) {
					adminCommands.add("ChannelAutojoinCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelBackendCommand.toString())) {
					adminCommands.add("ChannelBackendCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelCreateCommand.toString())) {
					adminCommands.add("ChannelCreateCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelFormatCommand.toString())) {
					adminCommands.add("ChannelFormatCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelGlobalCommand.toString())) {
					adminCommands.add("ChannelGlobalCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelMuteCommand.toString())) {
					adminCommands.add("ChannelMuteCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelPrefixCommand.toString())) {
					adminCommands.add("ChannelPrefixCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelRemoveCommand.toString())) {
					adminCommands.add("ChannelRemoveCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelRemoveServerCommand.toString())) {
					adminCommands.add("ChannelRemoveServerCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelSuffixCommand.toString())) {
					adminCommands.add("ChannelSuffixCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelUnmuteCommand.toString())) {
					adminCommands.add("ChannelUnmuteCommand");
				}
				if (sender.hasPermission(CommandPermission.ServerDefaultChannelCommand.toString())) {
					adminCommands.add("ServerDefaultChannelCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelReloadCommand.toString())) {
					adminCommands.add("ChannelReloadCommand");
				}
				Channels.notify(sender, "channels.usage.help-adminCommands");
				for (String command: adminCommands) {
					Channels.notify(sender, "channels.usage.help-"+command);
					Channels.notify(sender, "channels.usage.explain-"+command);
				}
			} else if (args[1].equalsIgnoreCase("mod")) {
				// mod commands
				ArrayList<String> modCommands = new ArrayList<String>();
				if (sender.hasPermission(CommandPermission.ChannelAddModCommand.toString())) {
					modCommands.add("ChannelAddModCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelBanCommand.toString())) {
					modCommands.add("ChannelBanCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelColorCommand.toString())) {
					modCommands.add("ChannelColorCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelInfoCommand.toString())) {
					modCommands.add("ChannelInfoCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelKickCommand.toString())) {
					modCommands.add("ChannelKickCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelOpenCommand.toString())) {
					modCommands.add("ChannelOpenCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelPasswordCommand.toString())) {
					modCommands.add("ChannelPasswordCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelRemoveModCommand.toString())) {
					modCommands.add("ChannelRemoveModCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelRenameCommand.toString())) {
					modCommands.add("ChannelRenameCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelUnbanCommand.toString())) {
					modCommands.add("ChannelUnbanCommand");
				}
				Channels.notify(sender, "channels.usage.help-modCommands");
				for (String command: modCommands) {
					Channels.notify(sender, "channels.usage.help-"+command);
					Channels.notify(sender, "channels.usage.explain-"+command);
				}
			} else if (args[1].equalsIgnoreCase("user")) {
				// user commands
				ArrayList<String> userCommands = new ArrayList<String>();
				if (sender.hasPermission(CommandPermission.AFKCommand.toString())) {
					userCommands.add("AFKCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelListCommand.toString())) {
					userCommands.add("ChannelListCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelSpeakCommand.toString())) {
					userCommands.add("ChannelSpeakCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelSubscribeCommand.toString())) {
					userCommands.add("ChannelSubscribeCommand");
				}
				if (sender.hasPermission(CommandPermission.ChannelUnsubscribeCommand.toString())) {
					userCommands.add("ChannelUnsubscribeCommand");
				}
				if (sender.hasPermission(CommandPermission.DNDCommand.toString())) {
					userCommands.add("DNDCommand");
				}
				if (sender.hasPermission(CommandPermission.IgnoreCommand.toString())) {
					userCommands.add("IgnoreCommand");
				}
				if (sender.hasPermission(CommandPermission.PMCommand.toString())) {
					userCommands.add("PMCommand");
				}
				if (sender.hasPermission(CommandPermission.ReplyCommand.toString())) {
					userCommands.add("ReplyCommand");
				}
				Channels.notify(sender, "channels.usage.help-userCommands");
				for (String command: userCommands) {
					Channels.notify(sender, "channels.usage.help-"+command);
					Channels.notify(sender, "channels.usage.explain-"+command);
				}
			}
		}		
	}

	public boolean validateInput() {
		return true;
	}
}
