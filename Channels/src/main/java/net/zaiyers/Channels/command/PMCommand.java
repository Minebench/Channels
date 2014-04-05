package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.ChannelsChatEvent;
import net.zaiyers.Channels.Chatter;
import net.zaiyers.Channels.message.Message;
import net.zaiyers.Channels.message.PrivateMessage;

public class PMCommand extends AbstractCommand {
	public PMCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {	
		if (!(sender instanceof ProxiedPlayer)) {
			Channels.notify(sender, "channels.command.is-player-command");
			return;
		}
		
		Chatter chatter = Channels.getInstance().getChatter(((ProxiedPlayer) sender).getUUID());
		
		if (args.length == 0) {
			chatter.setPrivateRecipient(null);
			Channels.notify(sender, "channels.command.recipient-unset");
			return;
		}
		//sanitize input
		if (!args[0].matches("^[a-zA-Z0-9_]+$")) {
			Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[0]));
			return;
		}

		if (args.length > 0) {
			//set recipient
			Chatter recipient = Channels.getInstance().getChatter(args[0]);
			for (Chatter onlinechatter: Channels.getInstance().getChatters().values().toArray(new Chatter[Channels.getInstance().getChatters().size()])) {
				if (onlinechatter != null && onlinechatter.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
					chatter.setPrivateRecipient(onlinechatter.getPlayer().getUUID());
					Channels.notify(sender, "channels.command.recipient-set", ImmutableMap.of("recipient", recipient.getName()));
					return;
				}
			}
			
			if (recipient == null) {
				//nobody matched
				Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[0]));
				return;
			}
			
			if (args.length > 1) {
				//send message
				
				Message msg = new PrivateMessage(chatter, recipient, argsToMessage(args));
				ChannelsChatEvent chatEvent = new ChannelsChatEvent(msg);
				if (!Channels.getInstance().getProxy().getPluginManager().callEvent( chatEvent ).isCancelled()) {
					msg.send();
					recipient.setLastPrivateSender(chatter);
				}
			}
		}
	}
	
	public String getPermission() {
		return "channels.pm";
	}

	public boolean validateInput() {
		return true;
	}
	
	/**
	 * generate message from arguments
	 * @return
	 */
	private String argsToMessage(String[] args) {
		String message = args[1];
		for (int i=2; i<args.length; i++) {
			message+=" "+args[i];
		}
		return message;
	}
}
