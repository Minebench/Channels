package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelPasswordCommand extends AbstractCommand implements ChannelsCommand {

	public ChannelPasswordCommand(CommandSource sender, String[] args) {
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
		if (sender instanceof Player && !chan.isMod(((Player) sender).getUniqueId().toString()) && !sender.hasPermission("channels.password.foreign")) {
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
