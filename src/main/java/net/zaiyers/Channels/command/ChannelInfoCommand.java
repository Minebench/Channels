package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class ChannelInfoCommand extends AbstractCommand {

	public ChannelInfoCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		boolean isConsoleCommand = !(sender instanceof Player);
		Chatter chatter = null;
		if (!isConsoleCommand) {
			chatter = Channels.getInstance().getChatter(((Player) sender).getUniqueId());
		}
		
		Channel channel = null;
		if (args.length == 2) {
			channel = Channels.getInstance().getChannel(args[1]);
		} else if (args.length == 1 && !isConsoleCommand) {
			channel = Channels.getInstance().getChannel(chatter.getChannel());
		} else {
			Channels.notify(sender, "channels.chatter.has-no-channel");
			return;
		}
		
		if (channel == null) {
			//channel does not exist
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		if (isConsoleCommand || chatter.hasPermission(channel, "info") || channel.getModerators().contains(chatter.getName())) { 
			Channels.notify(sender, "channels.command.channel-info-head", ImmutableMap.of("channel", channel.getName(), "channelColor", channel.getColor().toString()));
		    
			Channels.notify(sender, "channels.command.channel-info-name", ImmutableMap.of("name", channel.getName()));
			Channels.notify(sender, "channels.command.channel-info-tag", ImmutableMap.of("tag", channel.getTag()));
			if (isConsoleCommand || chatter.hasPermission(channel, "debug")) {
				Channels.notify(sender, "channels.command.channel-info-uuid", "uuid", channel.getUUID());
			}
			Channels.notify(sender, "channels.command.channel-info-color", ImmutableMap.of("color", channel.getColor().toString(), "colorName", channel.getColor().toString()));
			Channels.notify(sender, "channels.command.channel-info-password", ImmutableMap.of("password", (channel.getPassword() == null) ? "(none)":channel.getPassword()));
			Channels.notify(sender, "channels.command.channel-info-autofocus", ImmutableMap.of("autofocus", channel.doAutofocus() ? "true":"false"));
			Channels.notify(sender, "channels.command.channel-info-autojoin", ImmutableMap.of("autojoin", channel.doAutojoin() ? "true":"false"));
			Channels.notify(sender, "channels.command.channel-info-msgFormat", ImmutableMap.of("format", (channel.getFormat() == null) ? "(none)":channel.getFormat()));
			Channels.notify(sender, "channels.command.channel-info-global", ImmutableMap.of("global", channel.isGlobal() ? "true":"false"));
            Channels.notify(sender, "channels.command.channel-info-backend", ImmutableMap.of("backend", channel.isBackend() ? "true":"false"));
			StringBuilder servers = new StringBuilder();
			if (!channel.getServers().isEmpty()) {
				servers = new StringBuilder(channel.getServers().get(0));
				for (int i = 1; i<channel.getServers().size(); i++) {
					servers.append(", ").append(channel.getServers().get(i));
				}
			}
			Channels.notify(sender, "channels.command.channel-info-servers", ImmutableMap.of("servers", servers.toString()));
			Channels.notify(sender, "channels.command.channel-info-temp", ImmutableMap.of("temporary", channel.isTemporary() ? "true":"false"));
			StringBuilder moderators = new StringBuilder();
			if (!channel.getModerators().isEmpty()) {
				moderators = new StringBuilder(Channels.getPlayerName(channel.getModerators().get(0)));
				for (int i = 1; i<channel.getModerators().size(); i++) {
					moderators.append(", ").append(Channels.getPlayerName(channel.getModerators().get(i)));
				}
			}
			Channels.notify(sender, "channels.command.channel-info-moderators", ImmutableMap.of("moderators", moderators.toString()));
			StringBuilder bans = new StringBuilder();
			if (!channel.getBans().isEmpty()) {
				bans = new StringBuilder(Channels.getPlayerName(channel.getBans().get(0)));
				for (int i = 1; i<channel.getBans().size(); i++) {
					bans.append(", ").append(Channels.getPlayerName(channel.getBans().get(i)));
				}
			}
			Channels.notify(sender, "channels.command.channel-info-bans", ImmutableMap.of("bans", bans.toString()));
			Channels.notify(sender, "channels.command.channel-info-subscribers", ImmutableMap.of("subscribers", ""+channel.getSubscriberUUIDs().size()));
		} else {
			Channels.notify(sender, "channels.permission.info-channel", ImmutableMap.of("channel", channel.getName(), "channelColor", channel.getColor().toString()));
		}
	}

	public boolean validateInput() {
		return true;
	}
}
