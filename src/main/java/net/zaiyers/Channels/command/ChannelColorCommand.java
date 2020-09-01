package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelColorCommand extends AbstractCommand {

	public ChannelColorCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (args.length == 1) {
			//list available colors
			String colorlist = "";
			for (ChatColor color: ChatColor.values()) {
				colorlist+=color+color.name()+ChatColor.RESET+" ";
			}
			
			sender.sendMessage(new TextComponent(colorlist));
			return;
		} else if (args.length < 3) {
			Channels.notify(sender, "channels.usage.ChannelColorCommand"); return;
		}
		
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		if (sender instanceof ProxiedPlayer  && !chan.isMod(((ProxiedPlayer) sender).getUniqueId().toString()) && !sender.hasPermission("channels.setcolor.foreign")) {
			Channels.notify(sender, "channels.command.channel-no-permission");
			return;
		}
		
		try {
			ChatColor color = ChatColor.of(args[2]);
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
