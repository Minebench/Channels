package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class ChannelMuteCommand extends AbstractCommand {

	public ChannelMuteCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		ProxiedPlayer player = Channels.getInstance().getProxy().getPlayer(args[1]);
		if (player == null) {
			Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[1]));
			return;
		} else {
			Chatter chatter = Channels.getInstance().getChatter(player.getUUID());
			chatter.setMuted(true);
			Channels.notify(player, "channels.command.chatter-muted", ImmutableMap.of("chatter", chatter.getName()));
		}
		
		
	}

	public boolean validateInput() {
		return args.length > 1;
	}

}
