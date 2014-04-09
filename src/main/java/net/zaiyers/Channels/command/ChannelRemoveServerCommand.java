package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelRemoveServerCommand extends AbstractCommand {

	public ChannelRemoveServerCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		chan.removeServer(args[2]);
		if (Channels.getInstance().getProxy().getServerInfo(args[2]) == null) {
			Channels.notify(sender, "channels.command.channel-removed-unknown-server", ImmutableMap.of("channel", args[1]));
		} else {
			Channels.notify(sender, "channels.command.channel-removed-server", ImmutableMap.of("server", args[2], "channel", chan.getName(), "channelColor", chan.getColor().toString()));
		}
		Channels.getInstance().checkSanity(sender, chan.getUUID());
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
