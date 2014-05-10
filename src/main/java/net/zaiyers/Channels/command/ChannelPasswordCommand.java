package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelPasswordCommand extends AbstractCommand implements ChannelsCommand {

	public ChannelPasswordCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		String password = "";
		Channel chan = Channels.getInstance().getChannel(args[1]);
		
		// get chan
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		// check perms
		if (!(sender instanceof ConsoleCommandSender) && !chan.isMod(((ProxiedPlayer) sender).getUUID()) && !sender.hasPermission("channels.password.foreign")) {
			Channels.notify(sender, "channels.command.channel-no-permission");
			return;
		}
		
		// check input
		if (args.length > 2 && args[2].matches("^[a-zA-Z0-9_]+$")) {
			password = args[2];
		} else if (args.length > 2) {
			Channels.notify(sender, "channels.usage.channelpassword-disallowed-chars");
			return;
		}
		
		chan.setPassword(password);
		
		if (password.isEmpty()) {
			Channels.notify(sender, "channels.command.password-removed");
		} else {
			Channels.notify(sender, "channels.command.password-set");
		}
	}

	public boolean validateInput() {
		return args.length > 1;
	}
}
