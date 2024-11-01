package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

import java.util.UUID;

public class ChannelAddModCommand extends AbstractCommand {
	public ChannelAddModCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		if (sender instanceof Player  && !chan.isMod(((Player) sender).getUniqueId().toString()) && !sender.hasPermission("channels.addmod.foreign")) {
			Channels.notify(sender, "channels.command.channel-no-permission");
			return;
		}
		
		UUID modUUID = Channels.getPlayerId(args[2]);
		if (modUUID == null) {
			Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[2]));
			return;
		}
		
		chan.addModerator(modUUID.toString());
		Channels.notify(sender, "channels.command.channel-moderator-added", ImmutableMap.of(
				"chatter", Channels.getPlayerName(modUUID),
				"channelColor", chan.getColor().toString(),
				"channel", chan.getName()
		));
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
