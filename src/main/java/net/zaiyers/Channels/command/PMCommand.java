package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.events.ChannelsChatEvent;
import net.zaiyers.Channels.Chatter;
import net.zaiyers.Channels.message.Message;
import net.zaiyers.Channels.message.PrivateMessage;

public class PMCommand extends AbstractCommand {
	public PMCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {	
		if (!(sender instanceof Player)) {
			Channels.notify(sender, "channels.command.is-player-command");
			return;
		}
		
		Chatter chatter = Channels.getInstance().getChatter(((Player) sender));
		
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

		//set recipient
		Chatter recipient = Channels.getInstance().getChatterByName(args[0]);
		if (recipient == null || recipient.getPlayer() == null || !recipient.getPlayer().isActive() || (
				args.length == 1
						&& Channels.getConfig().shouldHideVanished()
						&& Channels.getVNPVelocity() != null
						&& !Channels.getVNPVelocity().canSee(chatter.getPlayer(), recipient.getPlayer()))
				) {
			//nobody matched
			Channels.notify(sender, "channels.command.chatter-not-found", ImmutableMap.of("chatter", args[0]));
		} else if (args.length == 1) {
			chatter.setPrivateRecipient(recipient.getPlayer().getUniqueId().toString());
			Channels.notify(sender, "channels.chatter.recipient-set", ImmutableMap.of("recipient", recipient.getName()));
		} else if (args.length > 1) {
			//send message

			Message msg = new PrivateMessage(chatter, recipient, argsToMessage(args));
			ChannelsChatEvent chatEvent = new ChannelsChatEvent(msg);
			if (!Channels.getInstance().getProxy().getEventManager().fire( chatEvent ).isCancelled()) {
				msg.send(chatEvent.isHidden());
				recipient.setLastPrivateSender(chatter);
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
