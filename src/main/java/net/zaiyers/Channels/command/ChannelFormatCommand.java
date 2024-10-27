package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;
import com.velocitypowered.api.command.CommandSource;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ChannelFormatCommand extends AbstractCommand {

	public ChannelFormatCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}

		try {
			chan.setFormat(Arrays.stream(args).skip(2).collect(Collectors.joining(" ")));
			Channels.notify(sender, "channels.command.channel-modified", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
		} catch (ClassCastException e) {
			Channels.notify(sender, "channels.usage.backend");
		}
	}

	public boolean validateInput() {
		return args.length > 2;
	}

}
