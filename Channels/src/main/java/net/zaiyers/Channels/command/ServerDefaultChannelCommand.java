package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ServerDefaultChannelCommand extends AbstractCommand {
	public ServerDefaultChannelCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {		
		if (Channels.getInstance().getProxy().getServerInfo(args[1]) == null) {
			Channels.notify(sender, "channels.command.channel-unknown-server");
			return;
		}
		
		Channel chan = Channels.getInstance().getChannel(args[2]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		boolean force = false;
		if (args.length > 3) {
			try {
				force = Boolean.parseBoolean(args[3]);
			} catch (ClassCastException e) {
				Channels.notify(sender, "channels.usage.ServerDefaultChannelCommand");
				return;
			}
		}
		
		Channels.getConfig().setServerDefaultChannel(args[1], chan.getUUID(), force);
		Channels.notify(sender, "channels.command.set-server-default-channel", ImmutableMap.of("server", args[1], "channel", chan.getName(), "channelColor", chan.getColor().toString()));
		Channels.getInstance().checkSanity(sender, chan.getUUID());
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
