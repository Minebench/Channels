package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.config.ChatterConfig;
import net.zaiyers.bungee.UUIDDB.UUIDDB;

public class ChannelUnmuteCommand extends AbstractCommand {

	public ChannelUnmuteCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		String chatterUUID;
		ProxiedPlayer player = Channels.getInstance().getProxy().getPlayer(args[1]);
		if (player == null) {
			chatterUUID = UUIDDB.getInstance().getUUIDByName(args[1]);
			if (chatterUUID == null) {
				Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[1]));
				return;
			}
			
			ChatterConfig cfg = ChatterConfig.load(chatterUUID);
			cfg.setMuted(false);
			cfg.save();
			
			Channels.notify(sender, "channels.command.chatter-unmuted", ImmutableMap.of("chatter", UUIDDB.getInstance().getNameByUUID(chatterUUID)));
		} else {
			chatterUUID = player.getUUID();
			Channels.getInstance().getChatter(chatterUUID).setMuted(false);
			
			Channels.notify(sender, "channels.command.chatter-unmuted", ImmutableMap.of("chatter", player.getName()));
		}
	}

	public boolean validateInput() {
		return args.length > 1;
	}
}
