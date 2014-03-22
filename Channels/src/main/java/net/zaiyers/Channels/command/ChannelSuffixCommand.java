package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.config.ChatterConfig;
import net.zaiyers.bungee.UUIDDB.UUIDDB;

public class ChannelSuffixCommand extends AbstractCommand {

	public ChannelSuffixCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public String getPermission() {
		return "channels.suffix";
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
			cfg.setSuffix(args[2]);
			cfg.save();
			
			Channels.notify(player, "channels.chatter.set-suffix", ImmutableMap.of("chatter", UUIDDB.getInstance().getNameByUUID(chatterUUID), "suffix", args[2]));
		} else {
			chatterUUID = player.getUUID();
			Channels.getInstance().getChatter(chatterUUID).setSuffix(args[2]);
			
			Channels.notify(player, "channels.chatter.set-suffix", ImmutableMap.of("chatter", player.getName(), "suffix", args[2]));
		}
	}

	public boolean validateInput() {
		return args.length > 1;
	}

}
