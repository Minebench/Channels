package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.CommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelBackendCommand extends AbstractCommand {

	public ChannelBackendCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}

		try {
			chan.setBackend(Boolean.parseBoolean(args[2]));
			Channels.notify(sender, "channels.command.channel-modified");
		} catch (ClassCastException e) {
			Channels.notify(sender, "channels.usage.backend");
		}
	}

	public boolean validateInput() {
		return args.length > 2;
	}

}
