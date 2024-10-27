package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

import java.util.Optional;

public class ChannelMuteCommand extends AbstractCommand {

	public ChannelMuteCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		Optional<Player> player = Channels.getInstance().getProxy().getPlayer(args[1]);
		if (player.isEmpty()) {
			Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[1]));
		} else {
			Chatter chatter = Channels.getInstance().getChatter(player.get().getUniqueId());
			chatter.setMuted(true);
			Channels.notify(player.get(), "channels.command.chatter-muted", ImmutableMap.of("chatter", chatter.getName()));
		}
	}

	public boolean validateInput() {
		return args.length > 1;
	}

}
