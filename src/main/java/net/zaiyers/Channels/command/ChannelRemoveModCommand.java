package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

import java.util.UUID;

public class ChannelRemoveModCommand extends AbstractCommand {
	public ChannelRemoveModCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		if (sender instanceof ProxiedPlayer && !chan.isMod(((ProxiedPlayer) sender).getUniqueId().toString()) && !sender.hasPermission("channels.removemod.foreign")) {
			Channels.notify(sender, "channels.command.channel-no-permission");
			return;
		}
		
		UUID modUUID = Channels.getPlayerId(args[2]);
		if (modUUID == null) {
			Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[2]));
			return;
		}
		
		if (!chan.isMod(modUUID.toString())) {
			Channels.notify(sender, "channels.command.chatter-not-mod", ImmutableMap.of("chatter", args[2]));
		} else if (chan.getModerators().size() == 1 && chan.isTemporary()) {
			Channels.notify(sender, "channels.command.channel-last-mod");
		} else {
			chan.removeModerator(modUUID.toString());
			Channels.notify(sender, "channels.command.channel-mod-removed", ImmutableMap.of(
					"chatter", args[2],
					"channelColor", chan.getColor().toString(),
					"channel", chan.getName()
			));
		}
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
