package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class ChannelListCommand extends AbstractCommand {

	public ChannelListCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		boolean isConsoleCommand = (sender instanceof ConsoleCommandSender);
		Chatter chatter = null;
		if (!isConsoleCommand && sender instanceof ProxiedPlayer) {
			chatter = Channels.getInstance().getChatter(((ProxiedPlayer) sender).getUniqueId().toString());
		} else {
			// possible? don't care then ...
			return;
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
			Channels.notify(sender, "channels.chatter.available-channels");
			for (Channel channel: Channels.getInstance().getChannels().values()) {
				if (isConsoleCommand ||
						(
							(
								chatter.hasPermission(channel, "subscribe")
								||
								channel.isTemporary()
							)
							&&
							(
								channel.isGlobal()
								||
								channel.getServers().contains(chatter.getPlayer().getServer().getInfo().getName())
							)
						)
				) {
						chatter.sendMessage(" - " + channel.getColor() + channel.getTag() + " - " + channel.getName() + ChatColor.WHITE + " (" + ((channel.getPassword().isEmpty()) ? "public":"private") + ")");
				}
			}
		} else if (args.length == 2 || (args.length == 1 && args[0].equalsIgnoreCase("who") && !isConsoleCommand)) {
			Channel channel = null;
			if (args.length == 2) {
				channel = Channels.getInstance().getChannel(args[1]);
			} else if (chatter.getChannel() != null) {
				channel = Channels.getInstance().getChannel(chatter.getChannel());
			} else {
				Channels.notify(sender, "channels.chatter.has-no-channel");
			}
			
			if (channel == null) {
				//channel does not exist
				Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
				return;
			}
			
			if (isConsoleCommand || chatter.hasPermission(channel, "list") || (channel.isTemporary() && channel.getSubscribers().contains(chatter.getPlayer().getUniqueId().toString()))) {
				String[] uuids = channel.getSubscribers().toArray(new String[channel.getSubscribers().size()]);
				Channels.notify(sender, "channels.chatter.channel-list-chatters", ImmutableMap.of("channel", channel.getName(), "channelColor", channel.getColor().toString()));
				
				if (uuids.length > 0) {
					Chatter subscriber = Channels.getInstance().getChatter(uuids[0]);
					String chatterList = subscriber.getPrefix()+ChatColor.WHITE+subscriber.getName()+subscriber.getSuffix()+ChatColor.WHITE;
					for (int i=1; i<uuids.length; i++) {
						subscriber = Channels.getInstance().getChatter(uuids[i]);
						chatterList += ", "+subscriber.getPrefix()+ChatColor.WHITE+subscriber.getName()+subscriber.getSuffix()+ChatColor.WHITE;
					}
					chatter.sendMessage(chatterList);
				}
			} else {
				Channels.notify(sender, "channels.permission.list-channel", ImmutableMap.of("channel", channel.getName(), "channelColor", channel.getColor().toString()));
			}
		}
	}

	public boolean validateInput() {
		return true;
	}
}
