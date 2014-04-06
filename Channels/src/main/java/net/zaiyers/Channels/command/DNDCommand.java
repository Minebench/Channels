package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class DNDCommand extends AbstractCommand {
	public DNDCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (!(sender instanceof ProxiedPlayer)) {
			Channels.notify(sender, "channels.command.is-player-command");
			return;
		}
		
		Chatter chatter = Channels.getInstance().getChatter(((ProxiedPlayer) sender).getUUID());
		if (chatter.isDND() && args.length == 0) {
			chatter.setDND(false, null);
			Channels.notify(sender, "channels.chatter.no-longer-dnd");
		} else {
			String msg = argsToMessage(args);
			chatter.setDND(true, msg);
			if (chatter.isAFK()) {
				chatter.setAFK(false, "");
				Channels.notify(sender, "channels.chatter.no-longer-afk");
			}
			if (msg == null) {
				Channels.notify(sender, "channels.chatter.now-dnd");
			} else {
				Channels.notify(sender, "channels.chatter.now-dnd-with-msg", ImmutableMap.of("msg", msg));
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
