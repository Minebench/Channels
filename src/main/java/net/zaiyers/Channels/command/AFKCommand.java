package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class AFKCommand extends AbstractCommand {

	public AFKCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (!(sender instanceof Player)) {
			Channels.notify(sender, "channels.command.is-player-command");
			return;
		}
		
		Chatter chatter = Channels.getInstance().getChatter(((Player) sender).getUniqueId());
		
		if (chatter.isAFK() && args.length == 0) {
			chatter.setAFK(false, null);
			Channels.notify(sender, "channels.command.no-longer-afk");
		} else {
			if (chatter.isDND()) {
				chatter.setDND(false, null);
				Channels.notify(sender, "channels.chatter.no-longer-dnd");
			}
			String msg = argsToMessage(args);
			chatter.setAFK(true, msg);
			if (msg == null) {
				Channels.notify(sender, "channels.chatter.now-afk");
			} else {
				Channels.notify(sender, "channels.chatter.now-afk-with-msg", ImmutableMap.of("msg", chatter.getAFKMessage()));
			}
		}
		
	}

	private String argsToMessage(String[] args) {
		String msg = null;
		if (args.length > 0) {
			msg = args[0];
			for (int i=1; i<args.length; i++) {
				msg+=" "+args[i];
			}
		}
		return msg;
	}

	public boolean validateInput() {
		return true;
	}

}
