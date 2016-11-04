package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelRemoveCommand extends AbstractCommand {
	public ChannelRemoveCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		Channel channel = Channels.getInstance().getChannel(args[1]);
		if (channel != null) {
			if (sender instanceof ProxiedPlayer && !channel.isMod(((ProxiedPlayer) sender).getUniqueId().toString()) && !sender.hasPermission("channels.remove.foreign")) {
				Channels.notify(sender, "channels.command.channel-no-permission");
				return;
			}
			
			if (Channels.getConfig().getDefaultChannelUUID().equals(channel.getUUID())) {
				Channels.notify(sender, "channels.command.remove-default-channel");
				return;
			}
			
			Channels.getInstance().removeChannel(channel.getUUID());
			Channels.getInstance().unregisterTag(channel.getTag());
			
			Channels.notify(sender, "channels.command.channel-removed", ImmutableMap.of("channel", channel.getName(), "channelColor", channel.getColor().toString()));
			Channels.getInstance().checkSanity(sender, channel.getUUID());
		} else {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
		}
	}

	public boolean validateInput() {
		return args.length > 1;
	}

}
