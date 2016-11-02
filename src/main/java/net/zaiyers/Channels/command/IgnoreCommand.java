package net.zaiyers.Channels.command;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;


public class IgnoreCommand extends AbstractCommand {

	public IgnoreCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (!(sender instanceof ProxiedPlayer)) {
			Channels.notify(sender, "channels.command.is-player-command");
			return;
		}
		
		if (args.length > 0) {
			String ignoreName, ignoreUUID;
			if (args.length > 1 && args[0].equalsIgnoreCase("ignore")) {
				ignoreName = args[1];
			} else {
				ignoreName = args[0];
			}
			
			//sanitize input
			if (!ignoreName.matches("^[a-zA-Z0-9_]+$")) {
				Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", ignoreName));
				return;
			}
			
			Chatter ignore = Channels.getInstance().getChatterByName(ignoreName);
			if (ignore != null) {
				ignoreUUID = ignore.getPlayer().getUniqueId().toString();
				ignoreName = ignore.getName();
			} else {
				// try uuiddb
				ignoreUUID = Channels.getPlayerId(ignoreName);
				if (ignoreUUID == null) {
					Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", ignoreName));
					return; // i don't know a player by that name
				}
			}
			Chatter chatter = Channels.getInstance().getChatter( ((ProxiedPlayer) sender).getUniqueId().toString() );
			// toggle ignore
			if (chatter.getIgnores().contains(ignoreUUID)) {
				chatter.removeIgnore(ignoreUUID);
				Channels.notify(sender, "channels.command.ignore-removed", ImmutableMap.of("chatter", ignoreName));			
			} else {
				chatter.addIgnore(ignoreUUID);
				Channels.notify(sender, "channels.command.ignore-added", ImmutableMap.of("chatter", ignoreName));
			}
		} else {
			// list ignores
			Chatter chatter = Channels.getInstance().getChatter( ((ProxiedPlayer) sender).getUniqueId().toString() );
			List<String> ignores = chatter.getIgnores();
			if (ignores.size() > 0) {
				String ignoreList = Channels.getPlayerName(ignores.get(0));
				for (int i=1; i<ignores.size(); i++) {
					ignoreList+=", "+Channels.getPlayerName(ignores.get(i));
				}
				
				Channels.notify(sender, "channels.command.ignore-list-head");
				chatter.sendMessage(ignoreList);
			} else {
				Channels.notify(sender, "channels.command.ignore-list-empty");
			}
		}
	}

	public boolean validateInput() {
		return true;
	}

}
