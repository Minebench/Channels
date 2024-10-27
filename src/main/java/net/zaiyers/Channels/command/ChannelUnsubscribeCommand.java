package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class ChannelUnsubscribeCommand extends AbstractCommand implements ChannelsCommand {

	public ChannelUnsubscribeCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (!(sender instanceof Player)) {
			Channels.notify(sender, "channels.command.is-player-command");
			return;
		}
						
		Chatter chatter = Channels.getInstance().getChatter( ((Player) sender).getUniqueId());
		Channel chan = Channels.getInstance().getChannel(args[1]);

		// no such channel
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}

		// has not subscribed
		if (!chatter.getSubscriptions().contains(chan.getUUID())) {
			Channels.notify(sender, "channels.chatter.channel-not-subscribed", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
			return;
		}

		// no permission to unsubscribe
		if (!chatter.hasPermission(chan, "unsubscribe")) {
			Channels.notify(sender, "channels.permission.unsubscribe-channel", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
			return;
		}

		chatter.unsubscribe(chan.getUUID());
		Channels.notify(sender, "channels.command.channel-unsubscribed", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
	}

	public boolean validateInput() {
		return args.length > 1;
	}

}
