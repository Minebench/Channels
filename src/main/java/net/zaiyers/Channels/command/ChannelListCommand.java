package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChannelListCommand extends AbstractCommand {

	public ChannelListCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		boolean isConsoleCommand = !(sender instanceof ProxiedPlayer );
		Chatter chatter = null;
		if (!isConsoleCommand) {
			chatter = Channels.getInstance().getChatter(((ProxiedPlayer) sender).getUniqueId());
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
			Channels.notify(sender, "channels.chatter.available-channels");
			for (Channel channel: Channels.getInstance().getChannels().values()) {
				boolean onThisServer = channel.isGlobal() || isConsoleCommand || channel.getServers().contains(chatter.getPlayer().getServer().getInfo().getName());
				if (isConsoleCommand ||
						(
							(
								chatter.hasPermission(channel, "subscribe")
								||
								channel.isTemporary()
							)
							&& onThisServer
						)
				) {
						sender.sendMessage(" - " + channel.getColor() + channel.getTag() + " - " + channel.getName() + ChatColor.WHITE + " (" + ((channel.getPassword().isEmpty()) ? "public":"private") + ")");
				} else if (chatter.hasPermission(channel, "globalread")) {
					sender.sendMessage(" - " + channel.getColor() + channel.getTag() + " - " + channel.getName() + ChatColor.WHITE + " (" + ((channel.getPassword().isEmpty()) ? "public":"private") + ") " + (onThisServer ? " " : " not") + "available on this server");
				}
			}
		} else if (args.length == 2 || (args.length == 1 && args[0].equalsIgnoreCase("who") && !isConsoleCommand)) {
			Channel channel = null;
			if (args.length == 2) {
				channel = Channels.getInstance().getChannel(args[1]);
			} else if (!isConsoleCommand && chatter.getChannel() != null) {
				channel = Channels.getInstance().getChannel(chatter.getChannel());
			} else {
				Channels.notify(sender, "channels.chatter.has-no-channel");
			}
			
			if (channel == null) {
				//channel does not exist
				Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
				return;
			}
			
			if (isConsoleCommand || chatter.hasPermission(channel, "list") || (channel.isTemporary() && channel.getSubscriberUUIDs().contains(chatter.getPlayer().getUniqueId()))) {
				UUID[] uuids = channel.getSubscriberUUIDs().toArray(new UUID[0]);
				Channels.notify(sender, "channels.chatter.channel-list-chatters", ImmutableMap.of("channel", channel.getName(), "channelColor", channel.getColor().toString()));

				List<Chatter> chatters = new ArrayList<>();
				for (UUID uuid : uuids) {
					Chatter subscriber = Channels.getInstance().getChatter(uuid);
					if (subscriber.getPlayer() != null && subscriber.getPlayer().isConnected() && (
							chatter == null
									|| !Channels.getConfig().shouldHideVanished()
									|| Channels.getVNPBungee() == null
									|| Channels.getVNPBungee().canSee(chatter.getPlayer(), subscriber.getPlayer()))
							) {
						chatters.add(subscriber);
					}
				}

				if (chatters.size() > 0) {
					ComponentBuilder chatterList = new ComponentBuilder("");
					for (int i=0; i < chatters.size(); i++) {
						Chatter subscriber = chatters.get(i);
						if (i > 0) {
							chatterList.append(", ").color(ChatColor.WHITE);
						}
						chatterList.append(MineDown.parse(subscriber.getPrefix()))
								.append(subscriber.getName()).color(ChatColor.WHITE)
								.append(MineDown.parse(subscriber.getSuffix()));
						if (subscriber.isAFK()) {
							chatterList.append("(AFK)").color(ChatColor.GRAY);
							if (subscriber.getAFKMessage() != null) {
								chatterList.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(ChatColor.GRAY + "AFK: " + ChatColor.ITALIC + subscriber.getAFKMessage()))));
							}
						}
						if (subscriber.isDND()) {
							chatterList.append("(DND)").color(ChatColor.GRAY);
							if (subscriber.getDNDMessage() != null) {
								chatterList.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(ChatColor.GRAY + "DND: " + ChatColor.ITALIC + subscriber.getDNDMessage()))));
							}
						}
					}
					sender.sendMessage(chatterList.create());
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
