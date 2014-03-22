package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.config.ChatterConfig;
import net.zaiyers.bungee.UUIDDB.UUIDDB;

public class ChannelPrefixCommand extends AbstractCommand {

	public ChannelPrefixCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public String getPermission() {
		return "channels.prefix";
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
			cfg.setPrefix(args[2]);
			cfg.save();
			
			Channels.notify(player, "channels.chatter.set-prefix", ImmutableMap.of("chatter", UUIDDB.getInstance().getNameByUUID(chatterUUID), "prefix", args[2]));
		} else {
			chatterUUID = player.getUUID();
			Channels.getInstance().getChatter(chatterUUID).setPrefix(args[2]);
			
			Channels.notify(player, "channels.chatter.set-prefix", ImmutableMap.of("chatter", player.getName(), "prefix", args[2]));
		}
	}

	public boolean validateInput() {
		return args.length > 1;
	}

}
