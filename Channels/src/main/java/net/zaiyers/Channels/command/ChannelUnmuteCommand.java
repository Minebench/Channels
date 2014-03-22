package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class ChannelUnmuteCommand extends AbstractCommand {

	public ChannelUnmuteCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public String getPermission() {
		return "channels.unmute";
	}

	public void execute() {
		ProxiedPlayer player = Channels.getInstance().getProxy().getPlayer(args[1]);
		if (player == null) {
			Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[1]));
			return;
		} else {
			Chatter chatter = Channels.getInstance().getChatter(player.getUUID());
			chatter.setMuted(false);
			Channels.notify(player, "channels.command.chatter-unmuted", ImmutableMap.of("chatter", chatter.getName()));
		}
		
		
	}

	public boolean validateInput() {
		return args.length > 1;
	}

}
