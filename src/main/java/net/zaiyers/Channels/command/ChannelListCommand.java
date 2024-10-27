package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.format.TextDecoration;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChannelListCommand extends AbstractCommand {

	public ChannelListCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		boolean isConsoleCommand = !(sender instanceof Player );
		Chatter chatter = null;
		if (!isConsoleCommand) {
			chatter = Channels.getInstance().getChatter(((Player) sender).getUniqueId());
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
			Channels.notify(sender, "channels.chatter.available-channels");
			for (Channel channel: Channels.getInstance().getChannels().values()) {
				boolean onThisServer = channel.isGlobal() || isConsoleCommand
						|| channel.getServers().contains(chatter.getPlayer().getCurrentServer().get().getServerInfo().getName());
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
					sender.sendMessage(Component.text(" - ")
							.append(Component.text(channel.getTag()).color(channel.getColor()))
							.append(Component.text(" - "))
							.append(Component.text(channel.getName()).color(channel.getColor()))
							.append(Component.text(" (" + (channel.getPassword().isEmpty() ? "public" : "private")+ ")")));
				} else if (chatter.hasPermission(channel, "globalread")) {
					sender.sendMessage(Component.text(" - ")
							.append(Component.text(channel.getTag()).color(channel.getColor()))
							.append(Component.text(" - "))
							.append(Component.text(channel.getName()).color(channel.getColor()))
							.append(Component.text(" (" + (channel.getPassword().isEmpty() ? "public" : "private")+ ")"))
							.append(Component.text(" " + (onThisServer ? "" : "not") + " on this server").color(NamedTextColor.GRAY)));
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
					if (subscriber.getPlayer() != null && subscriber.getPlayer().isActive() && (
							chatter == null
									|| !Channels.getConfig().shouldHideVanished()
									|| Channels.getVNPVelocity() == null
									|| Channels.getVNPVelocity().canSee(chatter.getPlayer(), subscriber.getPlayer()))
							) {
						chatters.add(subscriber);
					}
				}

				if (chatters.size() > 0) {
					Component chatterList = Component.empty();
					for (int i=0; i < chatters.size(); i++) {
						Chatter subscriber = chatters.get(i);
						if (i > 0) {
							chatterList.append(Component.text(", ").color(NamedTextColor.WHITE));
						}
						chatterList.append(MineDown.parse(subscriber.getPrefix()))
								.append(Component.text(subscriber.getName()).color(NamedTextColor.WHITE))
								.append(MineDown.parse(subscriber.getSuffix()));
						if (subscriber.isAFK()) {
							chatterList.append(Component.text("(AFK)").color(NamedTextColor.GRAY));
							if (subscriber.getAFKMessage() != null) {
								chatterList.hoverEvent(HoverEvent.showText(Component.text()
										.append(Component.text("AFK: ").color(NamedTextColor.GRAY))
										.append(Component.text(subscriber.getAFKMessage()).decorate(TextDecoration.ITALIC))
								));
							}
						}
						if (subscriber.isDND()) {
							chatterList.append(Component.text("(DND)").color(NamedTextColor.GRAY));
							if (subscriber.getAFKMessage() != null) {
								chatterList.hoverEvent(HoverEvent.showText(Component.text()
										.append(Component.text("DND: ").color(NamedTextColor.GRAY))
										.append(Component.text(subscriber.getDNDMessage()).decorate(TextDecoration.ITALIC))
								));
							}
						}
					}
					sender.sendMessage(chatterList);
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
