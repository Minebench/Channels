package net.zaiyers.Channels.command;

import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.bukkit.UUIDDB.UUIDDB;

public class ChannelAddModCommand extends AbstractCommand {
	public ChannelAddModCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public String getPermission() {
		return "channels.addmod";
	}

	public void execute() {
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		String modUUID;
		ProxiedPlayer player = Channels.getInstance().getProxy().getPlayer(args[2]);
		if (player == null) {
			modUUID = UUIDDB.getInstance().getUUIDByName(args[2]);
		} else {
			modUUID = player.getUUID();
		}
		if (modUUID == null) {
			Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[2]));
			return;
		}
		
		chan.addModerator(modUUID);
		Channels.notify(sender, "channels.command.channel-moderator-added", ImmutableMap.of(
				"chatter", UUIDDB.getInstance().getNameByUUID(modUUID),
				"channelColor", chan.getColor().toString(),
				"channel", chan.getName()
		));
		
		// notify player as well 
		if (player != null) {
			Channels.notify(player, "channels.command.channel-moderator-added", ImmutableMap.of(
					"chatter", UUIDDB.getInstance().getNameByUUID(modUUID),
					"channelColor", chan.getColor().toString(),
					"channel", chan.getName()
			));
		}
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
