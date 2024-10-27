package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.config.ChatterYamlConfig;

import java.util.Optional;
import java.util.UUID;


public class ChannelSuffixCommand extends AbstractCommand {

	public ChannelSuffixCommand(CommandSource sender, String[] args) {
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
		
		if (player.isEmpty()) {
			chatterUUID = Channels.getPlayerId(args[1]);
			if (chatterUUID == null) {
				Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[1]));
				return;
			}
			
			ChatterYamlConfig cfg = ChatterYamlConfig.load(chatterUUID);
			cfg.setSuffix(value);
			cfg.save();
			
			Channels.notify(sender, "channels.chatter.set-suffix", ImmutableMap.of("chatter", Channels.getPlayerName(chatterUUID), "suffix", value));
		} else {
			chatterUUID = player.get().getUniqueId();
			Channels.getInstance().getChatter(chatterUUID).setSuffix(value);
			
			Channels.notify(sender, "channels.chatter.set-suffix", ImmutableMap.of("chatter", player.get().getUsername(), "suffix", value));
		}
	}

	public boolean validateInput() {
		return args.length > 1;
	}

}
