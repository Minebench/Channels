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
		
		Chatter chatter = Channels.getInstance().getChatter(((ProxiedPlayer) sender).getUniqueId());
		
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
			Chatter recipient = Channels.getInstance().getChatterByName(args[0]);
			if (recipient != null && args.length == 1) {
				chatter.setPrivateRecipient(recipient.getPlayer().getUniqueId().toString());
				Channels.notify(sender, "channels.chatter.recipient-set", ImmutableMap.of("recipient", recipient.getName()));
			} else if (recipient == null) {
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
