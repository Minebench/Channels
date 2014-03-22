package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelAddServerCommand extends AbstractCommand {

	public ChannelAddServerCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public String getPermission() {
		return "channels.addserver";
	}

	public void execute() {
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		if (Channels.getInstance().getProxy().getServerInfo(args[2]) == null) {
			Channels.notify(sender, "channels.command.channel-adding-unknown-server", ImmutableMap.of("server", args[2]));
		}
		
		chan.addServer(args[2]);
		Channels.notify(sender, "channels.command.channel-added-server", ImmutableMap.of("server", args[2], "channel", chan.getName(), "channelColor", chan.getColor().toString()));
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
