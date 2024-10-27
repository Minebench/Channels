package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import com.velocitypowered.api.command.CommandSource;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelColorCommand extends AbstractCommand {

	public ChannelColorCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (args.length == 1) {
			//list available colors
			Component colorlist = Component.empty();
			for (NamedTextColor color: NamedTextColor.NAMES.values()) {
				colorlist = colorlist.append(Component.text(color.toString() + " ").color(color));
			}
			
			sender.sendMessage(colorlist);
			return;
		} else if (args.length < 3) {
			Channels.notify(sender, "channels.usage.ChannelColorCommand"); return;
		}
		
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		if (sender instanceof Player && !chan.isMod(((Player) sender).getUniqueId().toString()) && !sender.hasPermission("channels.setcolor.foreign")) {
			Channels.notify(sender, "channels.command.channel-no-permission");
			return;
		}
		
		try {
			TextColor color = Channels.parseTextColor(args[2]);
			if (color == null) {
				throw new IllegalArgumentException();
			}
			chan.setColor(color);
			Channels.notify(sender, "channels.command.channel-modified", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
		} catch (IllegalArgumentException e) {
			Channels.notify(sender, "channels.command.color-not-found", ImmutableMap.of("color", args[2]));
		}
	}

	public boolean validateInput() {
		return args.length > 0;
	}

}
