package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

import java.util.UUID;

public class ChannelBanCommand extends AbstractCommand {

	public ChannelBanCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		if (sender instanceof ProxiedPlayer  && !chan.isMod(((ProxiedPlayer) sender).getUniqueId().toString()) && !sender.hasPermission("channels.ban.foreign")) {
			Channels.notify(sender, "channels.command.channel-no-permission");
			return;
		}

		UUID chatterUUID = Channels.getPlayerId(args[2]);
		if (chatterUUID == null) {
			Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[2]));
			return;
		}
		
		chan.banChatter(chatterUUID);
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
