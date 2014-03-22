package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelRemoveCommand extends AbstractCommand {
	public ChannelRemoveCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public String getPermission() {
		return "channels.remove";
	}

	public void execute() {
		Channel channel = Channels.getInstance().getChannel(args[1]);
		if (channel != null) {
			Channels.getInstance().removeChannel(channel.getUUID());
			Channels.getInstance().unregisterTag(channel.getTag());
			
			Channels.notify(sender, "channels.command.channel-removed", ImmutableMap.of("channel", channel.getName(), "channelColor", channel.getColor().toString()));
		} else {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
		}
	}

	public boolean validateInput() {
		return args.length > 2;
	}

}
