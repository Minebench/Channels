package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class ChannelSubscribeCommand extends AbstractCommand {
	public ChannelSubscribeCommand(CommandSource sender, String[] args) {
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

        // check channel specific permissions
        if (!chatter.hasPermission(chan, "subscribe") && !chan.isTemporary()) {
            Channels.notify(sender, "channels.permission.subscribe-channel", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
            return;
        }

        // channel is password protected
        if (!chan.getPassword().isEmpty()) {
            if (args.length < 3) {
                Channels.notify(sender, "channels.chatter.channel-has-password", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
                return;
            } else if (!chan.getPassword().equals(args[2])) {
                Channels.notify(sender, "channels.chatter.wrong-password");
                return;
            }
        }

        // subscribe
        chatter.subscribe(chan);
        Channels.notify(sender, "channels.command.channel-subscribed", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
    }

	public boolean validateInput() {
		return args.length > 1;
	}
}
