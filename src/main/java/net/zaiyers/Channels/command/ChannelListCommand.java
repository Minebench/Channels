package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
                    TextComponent chatterList = new TextComponent();
					for (int i=0; i<uuids.length; i++) {
                        Chatter subscriber = Channels.getInstance().getChatter(uuids[i]);
                        if (i > 0) {
                            chatterList.addExtra(new TextComponent(TextComponent.fromLegacyText(ChatColor.WHITE + ", ")));
                        }
                        chatterList.addExtra(new TextComponent(TextComponent.fromLegacyText(subscriber.getPrefix()+ChatColor.WHITE+subscriber.getName()+subscriber.getSuffix())));
                        if (subscriber.isAFK()) {
                            TextComponent afkComponent = new TextComponent(TextComponent.fromLegacyText(ChatColor.GRAY + " (AFK)"));
                            if (subscriber.getAFKMessage() != null) {
                                afkComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(subscriber.getAFKMessage())));
                            }
                            chatterList.addExtra(afkComponent);
                        }
                        if (subscriber.isDND()) {
                            TextComponent dndComponent = new TextComponent(TextComponent.fromLegacyText(ChatColor.GRAY + " (DND)"));
                            if (subscriber.getDNDMessage() != null) {
                                dndComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(subscriber.getDNDMessage())));
                            }
                            chatterList.addExtra(dndComponent);
                        }
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
