package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.bungee.UUIDDB.UUIDDB;

public class ChannelAddModCommand extends AbstractCommand {
	public ChannelAddModCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		if (!(sender instanceof ConsoleCommandSender) && !chan.isMod(((ProxiedPlayer) sender).getUniqueId().toString()) && !sender.hasPermission("channels.addmod.foreign")) {
			Channels.notify(sender, "channels.command.channel-no-permission");
			return;
		}
		
		String modUUID;
		ProxiedPlayer player = Channels.getInstance().getProxy().getPlayer(args[2]);
		if (player == null) {
			modUUID = UUIDDB.getInstance().getUUIDByName(args[2]);
			if (modUUID == null) {
				Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[2]));
				return;
			}
		} else {
			modUUID = player.getUniqueId().toString();
		}
		
		chan.addModerator(modUUID);
		Channels.notify(sender, "channels.command.channel-moderator-added", ImmutableMap.of(
				"chatter", UUIDDB.getInstance().getNameByUUID(modUUID),
				"channelColor", chan.getColor().toString(),
				"channel", chan.getName()
		));
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
