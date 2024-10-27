package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

import java.util.Optional;

public class ChannelKickCommand extends AbstractCommand {

	public ChannelKickCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		Channel chan = Channels.getInstance().getChannel(args[1]);
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		if (sender instanceof Player && !chan.isMod(((Player) sender).getUniqueId().toString()) && !sender.hasPermission("channels.kick.foreign")) {
			Channels.notify(sender, "channels.command.channel-no-permission");
			return;
		}
		
		String chatterUUID;
		Optional<Player> player = Channels.getInstance().getProxy().getPlayer(args[2]);
		if (player.isEmpty()) {
			Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[2]));
			return;
		} else {
			chatterUUID = player.get().getUniqueId().toString();
		}
		
		chan.kickChatter(chatterUUID);
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
