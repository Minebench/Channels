package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.CommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelAutofocusCommand extends AbstractCommand {

	public ChannelAutofocusCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}

		try {
			chan.setAutofocus(Boolean.parseBoolean(args[2]));
			Channels.notify(sender, "channels.command.channel-modified", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
			Channels.getInstance().checkSanity(sender, chan.getUUID());
		} catch (ClassCastException e) {
			Channels.notify(sender, "channels.usage.autofocus");
		}
	}

	public boolean validateInput() {
		return args.length > 2;
	}

}
