package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.config.ChatterYamlConfig;

import java.util.Optional;
import java.util.UUID;

public class ChannelUnmuteCommand extends AbstractCommand {

	public ChannelUnmuteCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		UUID chatterUUID;
		Optional<Player> player = Channels.getInstance().getProxy().getPlayer(args[1]);
		if (player.isEmpty()) {
			chatterUUID = Channels.getPlayerId(args[1]);
			if (chatterUUID == null) {
				Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[1]));
				return;
			}
			
			ChatterYamlConfig cfg = ChatterYamlConfig.load(chatterUUID);
			cfg.setMuted(false);
			cfg.save();
			
			Channels.notify(sender, "channels.command.chatter-unmuted", ImmutableMap.of("chatter", Channels.getPlayerName(chatterUUID)));
		} else {
			chatterUUID = player.get().getUniqueId();
			Channels.getInstance().getChatter(chatterUUID).setMuted(false);
			
			Channels.notify(sender, "channels.command.chatter-unmuted", ImmutableMap.of("chatter", player.get().getUsername()));
		}
	}

	public boolean validateInput() {
		return args.length > 1;
	}
}
