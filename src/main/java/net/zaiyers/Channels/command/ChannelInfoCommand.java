package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class ChannelInfoCommand extends AbstractCommand {

	public ChannelInfoCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		boolean isConsoleCommand = !(sender instanceof ProxiedPlayer);
		Chatter chatter = null;
		if (!isConsoleCommand) {
			chatter = Channels.getInstance().getChatter(((ProxiedPlayer) sender).getUniqueId());
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
			Channels.notify(sender, "channels.command.channel-info-color", ImmutableMap.of("color", channel.getColor().toString(), "colorName", channel.getColor().name()));
			Channels.notify(sender, "channels.command.channel-info-password", ImmutableMap.of("password", (channel.getPassword() == null) ? "(none)":channel.getPassword()));
			Channels.notify(sender, "channels.command.channel-info-autojoin", ImmutableMap.of("autojoin", channel.doAutojoin() ? "true":"false"));
			Channels.notify(sender, "channels.command.channel-info-msgFormat", ImmutableMap.of("format", (channel.getFormat() == null) ? "(none)":channel.getFormat()));
			Channels.notify(sender, "channels.command.channel-info-global", ImmutableMap.of("global", channel.isGlobal() ? "true":"false"));
            Channels.notify(sender, "channels.command.channel-info-backend", ImmutableMap.of("backend", channel.isBackend() ? "true":"false"));
			String servers = "";
			if (channel.getServers().size() > 0) {
				servers = channel.getServers().get(0);
				for (int i = 1; i<channel.getServers().size(); i++) {
					servers+=", "+channel.getServers().get(i);
				}
			}
			Channels.notify(sender, "channels.command.channel-info-servers", ImmutableMap.of("servers", servers));
			Channels.notify(sender, "channels.command.channel-info-temp", ImmutableMap.of("temporary", channel.isTemporary() ? "true":"false"));
			String moderators = "";
			if (channel.getModerators().size() > 0) {
				moderators = Channels.getPlayerName(channel.getModerators().get(0));
				for (int i = 1; i<channel.getModerators().size(); i++) {
					moderators+=", "+Channels.getPlayerName(channel.getModerators().get(i));
				}
			}
			Channels.notify(sender, "channels.command.channel-info-moderators", ImmutableMap.of("moderators", moderators));
			String bans = "";
			if (channel.getBans().size() > 0) {
				bans = Channels.getPlayerName(channel.getBans().get(0));
				for (int i = 1; i<channel.getBans().size(); i++) {
					bans+=", "+Channels.getPlayerName(channel.getBans().get(i));
				}
			}
			Channels.notify(sender, "channels.command.channel-info-bans", ImmutableMap.of("bans", bans));
			Channels.notify(sender, "channels.command.channel-info-subscribers", ImmutableMap.of("subscribers", ""+channel.getSubscribers().size()));
		} else {
			Channels.notify(sender, "channels.permission.info-channel", ImmutableMap.of("channel", channel.getName(), "channelColor", channel.getColor().toString()));
		}
	}

	public boolean validateInput() {
		return true;
	}
}
