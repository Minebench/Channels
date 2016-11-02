package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.config.ChatterYamlConfig;

public class ChannelPrefixCommand extends AbstractCommand {

	public ChannelPrefixCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		String chatterUUID;
		ProxiedPlayer player = Channels.getInstance().getProxy().getPlayer(args[1]);
		
		String value;
		if (args.length < 3) {
			value = "";
		} else {
			value = args[2];
			if (value.startsWith("\"") || value.startsWith("'") || args.length > 3) {
				for (int i=3; i<args.length; i++) {
					value+=" "+args[i];
				}
				if ((value.startsWith("\"") || value.startsWith("\'")) && value.length() > 1) {
					value = value.substring(1);
				}
				
				if (value.endsWith("\"") || value.endsWith("\'")) {
					value = value.substring(0, value.length()-1);
				}
			}
		}
		value = value.replaceAll("([&#]([a-fk-or0-9]))", "\u00A7$2");
		
		if (player == null) {
			chatterUUID = Channels.getPlayerId(args[1]);
			if (chatterUUID == null) {
				Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[1]));
				return;
			}
			
			ChatterYamlConfig cfg = ChatterYamlConfig.load(chatterUUID);
			cfg.setPrefix(value);
			cfg.save();
			
			Channels.notify(player, "channels.chatter.set-prefix", ImmutableMap.of("chatter", Channels.getPlayerName(chatterUUID), "prefix", value));
		} else {
			chatterUUID = player.getUniqueId().toString();
			Channels.getInstance().getChatter(chatterUUID).setPrefix(value);
			
			Channels.notify(sender, "channels.chatter.set-prefix", ImmutableMap.of("chatter", player.getName(), "prefix", value));
		}
	}

	public boolean validateInput() {
		return args.length > 1;
	}

}
