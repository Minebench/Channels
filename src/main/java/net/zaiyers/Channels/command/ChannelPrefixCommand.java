package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.config.ChatterConfig;
import net.zaiyers.Channels.config.ChatterMongoConfig;
import net.zaiyers.Channels.config.ChatterYamlConfig;

import java.util.Optional;
import java.util.UUID;

public class ChannelPrefixCommand extends AbstractCommand {

	public ChannelPrefixCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		UUID chatterUUID;
		Optional<Player> player = Channels.getInstance().getProxy().getPlayer(args[1]);
		
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

		if (player == null) {
			chatterUUID = Channels.getPlayerId(args[1]);
			if (chatterUUID == null) {
				Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[1]));
				return;
			}

			ChatterConfig cfg;
			if (Channels.getConfig().getMongoDBConnection() != null && Channels.getConfig().getMongoDBConnection().isAvailable()) {
				cfg = new ChatterMongoConfig(Channels.getConfig().getMongoDBConnection().getChatters(), chatterUUID.toString());
			} else {
				cfg = ChatterYamlConfig.load(chatterUUID);
			}

			if (cfg == null) {
				Channels.notify(sender, "channels.command.chatter-config-not-found", ImmutableMap.of("chatter", args[1]));
				return;
			}
			cfg.setPrefix(value);
			cfg.save();
			
			Channels.notify(sender, "channels.chatter.set-prefix", ImmutableMap.of("chatter", Channels.getPlayerName(chatterUUID), "prefix", value));
		} else {
			chatterUUID = player.get().getUniqueId();
			Channels.getInstance().getChatter(chatterUUID).setPrefix(value);
			
			Channels.notify(sender, "channels.chatter.set-prefix", ImmutableMap.of("chatter", player.get().getUsername(), "prefix", value));
		}
	}

	public boolean validateInput() {
		return args.length > 1;
	}

}
